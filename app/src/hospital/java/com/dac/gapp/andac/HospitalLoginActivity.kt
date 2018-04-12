package com.dac.gapp.andac

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.dac.gapp.andac.user.UserJoinActivity
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
