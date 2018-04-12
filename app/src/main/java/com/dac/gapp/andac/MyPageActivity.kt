package com.dac.gapp.andac

import android.os.Bundle
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
