package com.dac.gapp.andac.base

import android.content.Intent
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    private var viewDataBinding: ViewDataBinding? = null

    fun inflate(inflater: LayoutInflater, layoutResID: Int, container: ViewGroup?): View? {
        viewDataBinding = DataBindingUtil.inflate(inflater, layoutResID, container, false)
        return viewDataBinding?.root
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : ViewDataBinding> getBinding(): T {
        return viewDataBinding as T
    }
}