package com.dac.gapp.andac

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.hospital.activity_hospital_event_list.*
import org.jetbrains.anko.startActivity

class HospitalEventListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hospital_event_list)
        addEventBtn.setOnClickListener { startActivity<EventWriteActivity>() }
    }
}
