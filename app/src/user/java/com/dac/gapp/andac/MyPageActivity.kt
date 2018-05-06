package com.dac.gapp.andac

import android.os.Bundle
import com.dac.gapp.andac.base.BaseMyPageActivity
import kotlinx.android.synthetic.user.activity_my_page.*

open class MyPageActivity : BaseMyPageActivity() {

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
