package com.dac.gapp.andac.base

import android.support.v4.app.Fragment

open class BaseFragment : Fragment() {

    override fun getContext(): BaseActivity? {
        return super.getContext() as BaseActivity?
    }

}