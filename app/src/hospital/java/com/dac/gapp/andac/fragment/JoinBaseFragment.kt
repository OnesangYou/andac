package com.dac.gapp.andac.fragment

import com.dac.gapp.andac.JoinActivity
import com.dac.gapp.andac.base.BaseFragment

abstract class JoinBaseFragment : BaseFragment(){
    abstract fun onChangeFragment()

    fun getJoinActivity() = activity as JoinActivity

}