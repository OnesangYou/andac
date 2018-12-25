package com.dac.gapp.andac

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.EditText
import android.widget.TimePicker
import com.bumptech.glide.Glide
import com.dac.gapp.andac.base.BaseActivity
import com.dac.gapp.andac.extension.setPrice
import com.dac.gapp.andac.model.firebase.BoardInfo
import com.dac.gapp.andac.model.firebase.EventApplyInfo
import com.dac.gapp.andac.model.firebase.EventInfo
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.android.synthetic.main.activity_event.*
import kotlinx.android.synthetic.main.event_request_dialog.view.*
import org.jetbrains.anko.startActivity
import java.sql.Time
import java.text.DateFormat
import java.util.*


class EventDetailActivity : BaseActivity() {


    @SuppressLint("SetTextI18n", "MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event)
        setActionBarLeftImage(R.drawable.back)

        setActionBarRightImage(R.drawable.call)
        setOnActionBarLeftClickListener(View.OnClickListener { finish() })


        // 병원 상세 정보 가져오기
        intent.getStringExtra(OBJECT_KEY)?.also{ objectId ->
            getEvent(objectId)?.addSnapshotListener { snapshot, _ ->
                val eventInfo = snapshot?.toObject(EventInfo::class.java)?:return@addSnapshotListener
                event_title.text = eventInfo.title
                body.text = eventInfo.body
                deal_kind.text = eventInfo.deal_kind
                price.setPrice(eventInfo.price)
                likeCountText.text = eventInfo.likeCount.toString()

                Glide.with(this@EventDetailActivity).load(eventInfo.pictureUrl).into(mainImage)
                Glide.with(this@EventDetailActivity).load(eventInfo.detailPictureUrl).into(detailImage)

                // intent 등록
                intent.putExtra("hospitalId", eventInfo.writerUid)

                // 병원명
                getHospitalInfo(eventInfo.writerUid)?.addOnSuccessListener { it ->
                    it?.also { hospitalInfo ->
                        setActionBarCenterText("${hospitalInfo.name} 병원 이벤트")
                        hospitalNameText.text = hospitalInfo.name

                        // Set hospital Btn
                        hospital.setOnClickListener { startActivity(HospitalActivity.createIntent(this@EventDetailActivity, hospitalInfo.apply { objectID = eventInfo.writerUid })) }

                        setOnActionBarRightClickListener(View.OnClickListener {
                            startActivity(Intent(Intent.ACTION_CALL, Uri.parse("tel:" + hospitalInfo.phone)))
                        })
                    }
                }


            }?.let { addListenerRegistrations(it) }

            checkLikeEvent(objectId)

            event_submit.setOnClickListener {
                afterCheckLoginDo { checkApplyEvent(objectId) }
            }
        }

    }

    // likeBtn
    private fun checkLikeEvent(objectId: String) {

        if (isHospital()) {
            likeBtn.isEnabled = false
            return
        }

        val setLike = {
            likeBtn.isEnabled = false
            val ref = getLikeEvent(objectId)?.get()
            ref?.addOnSuccessListener { documentSnapshot ->
                likeBtn.isChecked = documentSnapshot.exists()
                likeBtn.isEnabled = true
            }
        }

        if(isLogin()) setLike()

        likeBtn.setOnClickListener { _ ->
            if(isLogin()){
                likeBtn.isEnabled = false
                clickEventLikeBtn(objectId, likeBtn.isChecked)
                        ?.addOnSuccessListener { likeBtn.isEnabled = true }
            } else {
                likeBtn.isChecked = false
                goToLogin { setLike() }
            }
        }
    }

    // 내가 신청한 이벤트 인지 알아보기
    private fun checkApplyEvent(objectId: String): Task<DocumentSnapshot>? {
        return getUserEvent(objectId)?.get()?.addOnSuccessListener { documentSnapshot ->

            // event btn click event
            event_submit.setOnClickListener {
                if (!isUser()) {
                    toast("유저가 아니면 이벤트 신청이 불가능 합니다")
                    return@setOnClickListener
                }
                showEventSubmitDialog(objectId) { visibleEventCancelBtn() }
            }
            event_cancel.setOnClickListener { eventCancelDialog(objectId) { visibleEventSubmitBtn() } }

            documentSnapshot.data?.let {
                visibleEventCancelBtn()
            } ?: visibleEventSubmitBtn()
        }
    }

    @SuppressLint("InflateParams", "SetTextI18n")
    fun showEventSubmitDialog(eventId: String, function: () -> Unit){
        getUserInfo()?.addOnSuccessListener { it ->
            it?.let{ userInfo ->
            val builder = AlertDialog.Builder(this)
            val dialogView = layoutInflater.inflate(R.layout.event_request_dialog, null)
                    // 유저 정보 가져와서 기본값 셋팅
                    .apply {
                        event_phone.setText(userInfo.cellPhone)

                        fun getTimePickerDialog(view : View) : RangeTimePickerDialog {
                            val calendar = Calendar.getInstance()
                            var hour = calendar.get(Calendar.HOUR_OF_DAY)
                            var min = calendar.get(Calendar.MINUTE)

                            if(view.tag != null) {
                                val pair = view.tag as Pair<*, *>
                                hour = pair.first as Int
                                min = pair.second as Int
                            }

                            return RangeTimePickerDialog(context, TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                                (view as EditText).setText("${"%02d".format(hourOfDay)}:${"%02d".format(minute)}")
                                view.tag = hourOfDay to minute
                            },hour,min,true)

                        }

                        event_time.setOnClickListener { view ->
                            getTimePickerDialog(view)
                                    .also {
                                        if(event_time2.tag == null) return@also
                                        val pair = event_time2.tag as Pair<*, *>
                                        it.setMax(pair.first as Int, pair.second as Int)
                                    }
                                    .show()
                        }

                        event_time2.setOnClickListener { view ->
                            check(event_time.text.isNotEmpty()) {
                                toast("가능한 연락 시간(TO)를 입력해주세요")
                                return@setOnClickListener
                            }
                            getTimePickerDialog(view)
                                    .also {
                                        if(event_time.tag == null) return@also
                                        val pair = event_time.tag as Pair<*, *>
                                        it.setMin(pair.first as Int, pair.second as Int)
                                    }
                                    .show()
                        }

                        eventPolicyBtn.setOnClickListener {
                            context?.startActivity<TermsActivity>("policyFile" to "andac_personal_information_policy_event.txt")
                        }
                    }

            builder.setView(dialogView)
                    .setPositiveButton("확인", null)
                    .setNegativeButton("취소", null).create()
                    .also {dialog ->
                        // 확인 버튼을 눌러도 닫히지 않도록
                        dialog.setOnShowListener{ it ->
                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener { _ ->
                                if(!dialogView.agreePersonalInfoCheckBox.isChecked) {
                                    toast("개인정보 이용 동의하지 않으면 신청 불가능합니다")
                                    return@setOnClickListener
                                }

                                check(dialogView.event_name.text.isNotEmpty()){toast("이름을 입력하세요"); return@setOnClickListener}
                                check(dialogView.event_phone.text.isNotEmpty()){toast("폰번호를 입력하세요"); return@setOnClickListener}
                                check(dialogView.event_time.text.isNotEmpty()){toast("연락 가능한 시간(FROM)을 입력하세요"); return@setOnClickListener}
                                check(dialogView.event_time2.text.isNotEmpty()){toast("연락 가능 시간(TO)을 입력하세요"); return@setOnClickListener}
                                check(dialogView.event_time.text != dialogView.event_time2.text) {toast("가능한 시간의 TO와 FROM이 같습니다. 다시 입력해주세요"); return@setOnClickListener}

                                val eventApplyInfo = EventApplyInfo(
                                        name = dialogView.event_name.text.toString(),
                                        phone = dialogView.event_phone.text.toString(),
                                        possibleTime = dialogView.event_time.text.toString(),
                                        possibleTimeStart = Time.valueOf(dialogView.event_time.text.toString() + ":00").time,
                                        possibleTimeEnd = Time.valueOf(dialogView.event_time2.text.toString() + ":00").time,
                                        writerUid = getUid()!!
                                ).apply { objectId = getUid()!! }

                                showProgressDialog()

                                FirebaseFirestore.getInstance().batch().run {
                                    getEventApplicant(eventId)?.let { set(it, eventApplyInfo, SetOptions.merge()) }
                                    getUserEvent(eventId)?.let { set(it, mapOf("createdDate" to FieldValue.serverTimestamp()), SetOptions.merge()) }
                                    commit()
                                }
                                        .addOnSuccessListener { _ ->
                                            toast("이벤트신청이 완료되었습니다.\n내이벤트목록을 확인하세요"); function.invoke()

                                            val hospitalId = intent.getStringExtra("hospitalId").also { if(it.isEmpty()) return@addOnSuccessListener }
                                            addCountEventApplicant(hospitalId)
                                            dialog.dismiss()

                                            // applicantCount 카운트 증가
                                            runTransaction<EventInfo>(getEvent(eventId)?:throw IllegalStateException()) { eventInfo ->
                                                eventInfo.applicantCount++
                                                if(eventInfo.applicantCount < 0) throw IllegalStateException("applicantCount Count is Zero")
                                            }
                                        }
                                        .addOnCompleteListener { hideProgressDialog() }


                            }
                        }
                    }
                    .show()
        } }


    }

    private fun visibleEventSubmitBtn() {
        event_submit.visibility = View.VISIBLE
        event_cancel.visibility = View.GONE
    }

    private fun visibleEventCancelBtn() {
        event_submit.visibility = View.GONE
        event_cancel.visibility = View.VISIBLE

    }

}

class RangeTimePickerDialog(context: Context, callBack: TimePickerDialog.OnTimeSetListener, hourOfDay: Int, minute: Int, is24HourView: Boolean) : TimePickerDialog(context, callBack, hourOfDay, minute, is24HourView) {

    private var minHour = -1
    private var minMinute = -1

    private var maxHour = 25
    private var maxMinute = 25

    private var currentHour = 0
    private var currentMinute = 0

    private val calendar = Calendar.getInstance()
    private val dateFormat: DateFormat


    init {
        currentHour = hourOfDay
        currentMinute = minute
        dateFormat = DateFormat.getTimeInstance(DateFormat.SHORT)

        try {
            val superclass = javaClass.superclass
            val mTimePickerField = superclass!!.getDeclaredField("mTimePicker")
            mTimePickerField.isAccessible = true
            val mTimePicker = mTimePickerField.get(this) as TimePicker
            mTimePicker.setOnTimeChangedListener(this)
        } catch (e: NoSuchFieldException) {
        } catch (e: IllegalArgumentException) {
        } catch (e: IllegalAccessException) {
        }

    }

    fun setMin(hour: Int, minute: Int) {
        minHour = hour
        minMinute = minute

        if(currentHour<minHour) currentHour = minHour
        if(currentMinute<minMinute) currentMinute = minMinute

        updateTime(currentHour, currentMinute)
    }

    fun setMax(hour: Int, minute: Int) {
        maxHour = hour
        maxMinute = minute

        if(currentHour>maxHour) currentHour = maxHour
        if(currentMinute>maxMinute) currentMinute = maxMinute

        updateTime(currentHour, currentMinute)
    }

    override fun onTimeChanged(view: TimePicker, hourOfDay: Int, minute: Int) {

        var validTime = true
        if (hourOfDay < minHour || hourOfDay == minHour && minute < minMinute) {
            validTime = false
        }

        if (hourOfDay > maxHour || hourOfDay == maxHour && minute > maxMinute) {
            validTime = false
        }

        if (validTime) {
            currentHour = hourOfDay
            currentMinute = minute
        }

        updateTime(currentHour, currentMinute)
        updateDialogTitle(view, currentHour, currentMinute)
    }

    private fun updateDialogTitle(timePicker: TimePicker, hourOfDay: Int, minute: Int) {
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        calendar.set(Calendar.MINUTE, minute)
        val title = dateFormat.format(calendar.time)
        setTitle(title)
    }
}