package com.dac.gapp.andac

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.Toast
import com.dac.gapp.andac.model.EventDetail
import com.yanzhenjie.album.mvp.BaseActivity
import kotlinx.android.synthetic.main.activity_event.*
import kotlinx.android.synthetic.main.event_request_dialog.view.*

class EventActivity : BaseActivity() {
    var dialog : AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event)

        // toolbar
        val toolbar = toolbar
        setSupportActionBar(toolbar)
        // Get the ActionBar here to configure the way it behaves.
        val actionBar = supportActionBar
        actionBar!!.setDisplayShowCustomEnabled(true) //커스터마이징 하기 위해 필요
        actionBar.setDisplayShowTitleEnabled(false)
        actionBar.setDisplayHomeAsUpEnabled(false) // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김


        setInfo()
        setDialog()
        event_submit.setOnClickListener({v: View ->
            dialog!!.show()
        })
        hospital.setOnClickListener({v:View ->
            val intent = Intent(Intent.ACTION_DIAL,Uri.parse("tel:"+"123-4567-1234"));
            startActivity(intent)
            println("dsadsa")
        })
    }

    fun setInfo(){
        if (intent.hasExtra("EventInfo")) {
            var eventDetail = intent.getParcelableExtra<EventDetail>("EventInfo")
            event_title.text = eventDetail.title
            sub_title.text = eventDetail.sub_title
            deal_kind.text = eventDetail.deal_kind
            price.text = eventDetail.price
            buy_count.text = eventDetail.buy_count
        }
    }

    fun setDialog(){
        val builder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.event_request_dialog, null)
        dialog = builder!!.setView(dialogView)
                .setPositiveButton("확인") { dialogInterface, i ->

                    var name = dialogView.event_name.text.toString()
                    var phone = dialogView.event_phone.text.toString()
                    var time = dialogView.event_time.text.toString()
                    Toast.makeText(this,name+"/"+phone+"/"+time,Toast.LENGTH_SHORT ).show()
                }
                .setNegativeButton("취소") { dialogInterface, i ->
                    Toast.makeText(this,"택형귀요미~",Toast.LENGTH_SHORT ).show()
                }.create()
    }
}
