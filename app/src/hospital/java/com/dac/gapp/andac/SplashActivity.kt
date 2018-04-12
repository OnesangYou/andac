package com.dac.gapp.andac

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.dac.gapp.andac.user.UserLoginActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity(Intent(this, HospitalLoginActivity::class.java) )
        finish()
    }
}