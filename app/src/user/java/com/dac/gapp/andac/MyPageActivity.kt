package com.dac.gapp.andac

import android.os.Bundle
import com.bumptech.glide.Glide
import com.dac.gapp.andac.base.BaseMyPageActivity
import com.dac.gapp.andac.model.firebase.UserInfo
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

        // Set Profile
        getUserInfo().success { userInfo ->
            Glide.with(this@MyPageActivity).load(userInfo.profilePicUrl).into(profilePic)
            nameText.text = userInfo.nickName
        }
    }
}
