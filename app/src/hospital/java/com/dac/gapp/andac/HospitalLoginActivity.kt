package com.dac.gapp.andac

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_login.*

class HospitalLoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hospital_login)

        goToJoin.setOnClickListener {
            startActivity(Intent(this, HospitalJoinActivity::class.java))
        }
    }
}
