package com.dac.gapp.andac

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AlertDialog
import com.bumptech.glide.Glide
import com.dac.gapp.andac.base.BaseActivity
import com.dac.gapp.andac.model.firebase.EventApplyInfo
import com.dac.gapp.andac.model.firebase.EventInfo
import com.google.firebase.firestore.SetOptions
import kotlinx.android.synthetic.main.activity_event.*
import kotlinx.android.synthetic.main.event_request_dialog.view.*

class EventDetailActivity : BaseActivity() {


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event)

        // back
        back.setOnClickListener { finish() }


        // 병원 상세 정보 가져오기
        intent.getStringExtra(OBJECT_KEY)?.let{ objectId ->

            showProgressDialog()
            getEvent(objectId)?.get()?.continueWith { it.result.toObject(EventInfo::class.java) }
                    ?.addOnSuccessListener { it -> it?.also { eventInfo ->
                        event_title.text = eventInfo.title
                        sub_title.text = eventInfo.sub_title
                        body.text = eventInfo.body
                        deal_kind.text = eventInfo.deal_kind
                        price.text = eventInfo.price.toString()
                        buy_count.text = eventInfo.buy_count.toString()

                        Glide.with(this@EventDetailActivity).load(eventInfo.pictureUrl).into(mainImage)
                        Glide.with(this@EventDetailActivity).load(eventInfo.detailPictureUrl).into(detailImage)

                        // 병원명
                        getHospitalInfo(eventInfo.writerUid)?.addOnSuccessListener { it ->
                            it?.let { hospitalInfo ->
                            toolbarTitle.text = "${hospitalInfo.name} 병원 이벤트"

                            // Set hospital Btn
                            hospital.setOnClickListener {
                                if(!isUser()) {
                                    toast("유저가 아니면 이벤트 신청이 불가능 합니다")
                                    return@setOnClickListener
                                }
                                startActivity(HospitalActivity.createIntent(this@EventDetailActivity, hospitalInfo))

                            }
                        }}

                    } }
                    ?.addOnCompleteListener { hideProgressDialog() }

            // event_submit
            event_submit.setOnClickListener { showDialog(objectId) }

        }




    }

    @SuppressLint("InflateParams")
    fun showDialog(eventId : String){
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
                        getEventApplicant(eventId)?.set(eventApplyInfo, SetOptions.merge())
                                ?.addOnSuccessListener { toast("이벤트신청이 완료되었습니다.\n내이벤트목록을 확인하세요") }
                                ?.addOnCompleteListener { hideProgressDialog() }


                    }
                    .setNegativeButton("취소") { _, _ ->
                    }.create().show()
        } }


    }

}
