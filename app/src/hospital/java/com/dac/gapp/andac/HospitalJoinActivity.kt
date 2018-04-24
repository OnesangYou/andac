package com.dac.gapp.andac

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.view.View
import android.widget.Toast
import com.dac.gapp.andac.custom.SwipeViewPager
import com.dac.gapp.andac.join.*
import com.dac.gapp.andac.model.firebase.HospitalInfo
import com.dac.gapp.andac.model.firebase.UserInfo
import java.util.regex.Pattern

class HospitalJoinActivity : BaseActivity() {

    var MAX_PAGE = 5
    var cur_fragment = Fragment()
    var viewPager : SwipeViewPager? = null
    var userInfo  = UserInfo()
    var hospitalInfo = HospitalInfo()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hospital_join)

        viewPager = findViewById<View>(R.id.viewpager) as SwipeViewPager
        viewPager!!.adapter = Adapter(supportFragmentManager)
    }

    private inner class Adapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment? {
            if (position < 0 || MAX_PAGE <= position)
                return null
            when (position) {
                0 -> cur_fragment = HospitalJoinSearchFragment()
                1 -> cur_fragment = HospitalJoinInfoFragment()
                2 -> cur_fragment = HospitalJoinCertiFragment()
                3 -> cur_fragment = HospitalJoinCerti2Fragment()
                4 -> cur_fragment = HospitalJoinCompFragment()
            }
            return cur_fragment
        }

        override fun getCount(): Int {
            return MAX_PAGE
        }
    }

    fun goToNextView(){
        viewPager!!.apply { if(currentItem < MAX_PAGE) currentItem++ }
    }
}
