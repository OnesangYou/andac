package com.dac.gapp.andac

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.dac.gapp.andac.user.LoginActivity
import kotlinx.android.synthetic.main.activity_my_page.*

class MyPageActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_page)

        back.setOnClickListener({ finish() })

        logoutBtn.setOnClickListener {
            getAuth()!!.signOut()
            finish()
        }
    }
}
