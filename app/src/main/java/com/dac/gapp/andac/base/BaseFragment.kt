package com.dac.gapp.andac.base

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dac.gapp.andac.BuildConfig

open class BaseFragment : Fragment() {

    override fun getContext(): BaseActivity? {
        return super.getContext() as BaseActivity?
    }

    val GOTO_MYPAGE = context?.GOTO_MYPAGE

    fun getAuth() = context?.getAuth()

    fun getCurrentUser() = context?.getCurrentUser()

    fun getUid() = context?.getUid()

    fun isUser(): Boolean = BuildConfig.FLAVOR.startsWith("user")
    fun goToLogin(gotoMyPage: Boolean = false) = context?.goToLogin(gotoMyPage)

    private var viewDataBinding: ViewDataBinding? = null

    fun inflate(inflater: LayoutInflater, layoutResID: Int, container: ViewGroup?, attachToParent: Boolean): View? {
        viewDataBinding = DataBindingUtil.inflate(inflater, layoutResID, container, attachToParent)
        return viewDataBinding?.root
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : ViewDataBinding> getBinding(): T {
        return viewDataBinding as T
    }

    override fun onResume() {
        super.onResume()
        activity?.let { context?.getAnalytics()?.setCurrentScreen(it, javaClass.simpleName, javaClass.simpleName) }

    }
}