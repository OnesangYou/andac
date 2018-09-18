package com.dac.gapp.andac

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.View
import com.bumptech.glide.Glide
import com.dac.gapp.andac.base.BaseActivity
import com.dac.gapp.andac.model.firebase.EventApplyInfo
import com.dac.gapp.andac.model.firebase.EventInfo
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.android.synthetic.main.activity_event.*
import kotlinx.android.synthetic.main.event_request_dialog.view.*

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

            showProgressDialog()
            getEvent(objectId)?.get()?.continueWith { it.result.toObject(EventInfo::class.java) }
                    ?.addOnSuccessListener { it -> it?.also { eventInfo ->
                        event_title.text = eventInfo.title
                        hospitalNameText.text = eventInfo.sub_title
                        body.text = eventInfo.body
                        deal_kind.text = eventInfo.deal_kind
                        price.text = eventInfo.price.toString()
                        buy_count.text = eventInfo.buy_count.toString()

                        Glide.with(this@EventDetailActivity).load(eventInfo.pictureUrl).into(mainImage)
                        Glide.with(this@EventDetailActivity).load(eventInfo.detailPictureUrl).into(detailImage)

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

                    } }
                    ?.addOnCompleteListener { hideProgressDialog() }


            // 내가 신청한 이벤트 인지 알아보기
            getUserEvent(objectId)?.get()?.addOnSuccessListener { documentSnapshot ->

                // event btn click event
                event_submit.setOnClickListener {
                    if(!isUser()) {
                        toast("유저가 아니면 이벤트 신청이 불가능 합니다")
                        return@setOnClickListener
                    }
                    showEventSubmitDialog(objectId) {visibleEventCancelBtn()}
                }
                event_cancel.setOnClickListener { eventCancelDialog(objectId) {visibleEventSubmitBtn()} }

                documentSnapshot.data?.let {
                    visibleEventCancelBtn()
                }?: visibleEventSubmitBtn()
            }
        }

    }

    @SuppressLint("InflateParams")
    fun showEventSubmitDialog(eventId: String, function: () -> Unit){
        getUserInfo()?.addOnSuccessListener { it ->
            it?.let{ userInfo ->
            val builder = AlertDialog.Builder(this)
            val dialogView = layoutInflater.inflate(R.layout.event_request_dialog, null)
                    // 유저 정보 가져와서 기본값 셋팅
                    .apply {
                        event_name.setText(userInfo.nickName)
                        event_phone.setText(userInfo.cellPhone)
                    }

            builder.setView(dialogView)
                    .setPositiveButton("확인") { _, _ ->

                        if(!dialogView.agreePersonalInfoCheckBox.isChecked) {
                            toast("개인정보 이용 동의하지 않으면 신청 불가능합니다")
                            return@setPositiveButton
                        }

                        val eventApplyInfo = EventApplyInfo(
                            name = dialogView.event_name.text.toString(),
                            phone = dialogView.event_phone.text.toString(),
                            possibleTime = dialogView.event_time.text.toString(),
                            writerUid = getUid()!!
                        ).apply { objectId = getUid()!! }

                        showProgressDialog()

                        FirebaseFirestore.getInstance().batch().run {
                            getEventApplicant(eventId)?.let { set(it, eventApplyInfo, SetOptions.merge()) }
                            getUserEvent(eventId)?.let { set(it, mapOf("createdDate" to FieldValue.serverTimestamp()), SetOptions.merge()) }
                            commit()
                        }
                                .addOnSuccessListener { toast("이벤트신청이 완료되었습니다.\n내이벤트목록을 확인하세요"); function.invoke() }
                                .addOnCompleteListener { hideProgressDialog() }


                    }
                    .setNegativeButton("취소") { _, _ ->
                    }.create().show()
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
