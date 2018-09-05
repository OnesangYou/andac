package com.dac.gapp.andac.base

import android.content.Intent
import android.support.v4.app.Fragment
import com.dac.gapp.andac.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

open class BaseFragment : Fragment() {
    val GOTO_MYPAGE = "goToMyPage"

    override fun getContext(): BaseActivity? {
        return super.getContext() as BaseActivity?
    }

    fun getAuth(): FirebaseAuth? = FirebaseAuth.getInstance()

    fun getCurrentUser(): FirebaseUser? = getAuth()?.currentUser
            ?.also {
                it.phoneNumber?.isEmpty()?.let { isEmpty ->
                    if (isEmpty) goToLogin()
                }
            } // 폰등록 안되 있는 경우는 login 이동

    fun getUid(): String? {
        return getCurrentUser()?.uid
    }

    fun goToLogin(gotoMyPage: Boolean = false) {
        Intent(context, LoginActivity::class.java).let {
            if (gotoMyPage) it.putExtra(GOTO_MYPAGE, true)
            startActivity(it)
        }
    }

}