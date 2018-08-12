package com.dac.gapp.andac

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AlertDialog
import com.bumptech.glide.Glide
import com.dac.gapp.andac.base.BaseActivity
import com.dac.gapp.andac.model.EventInfo
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
                                startActivity(HospitalActivity.createIntent(this@EventDetailActivity, hospitalInfo))
                            }
                        }}

                    } }
                    ?.addOnCompleteListener { hideProgressDialog() }
        }



        // event_submit
        event_submit.setOnClickListener { showDialog() }


    }

    @SuppressLint("InflateParams")
    fun showDialog(){
        val builder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.event_request_dialog, null)
        builder.setView(dialogView)
                .setPositiveButton("확인") { _, _ ->
                    val name = dialogView.event_name.text.toString()
                    val phone = dialogView.event_phone.text.toString()
                    val time = dialogView.event_time.text.toString()
                    toast("이벤트신청이 완료되었습니다.\n내이벤트목록을 확인하세요");
                }
                .setNegativeButton("취소") { _, _ ->
                }.create().show()
    }

}
