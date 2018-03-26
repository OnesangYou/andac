package com.dac.gapp.andac

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.dac.gapp.andac.user.LoginActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity(LoginActivity.createIntent(this))
        finish()
    }
}