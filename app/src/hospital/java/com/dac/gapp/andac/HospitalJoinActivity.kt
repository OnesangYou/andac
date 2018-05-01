package com.dac.gapp.andac

import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.view.View
import com.dac.gapp.andac.custom.SwipeViewPager
import com.dac.gapp.andac.join.*
import com.dac.gapp.andac.model.firebase.HospitalInfo

class HospitalJoinActivity : BaseActivity() {

    var MAX_PAGE = 5
    var current_fragment : HospitalJoinBaseFragment? = null
    var viewPager : SwipeViewPager? = null
    var hospitalInfo = HospitalInfo()
    var hospitalKey : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hospital_join)

        viewPager = findViewById<View>(R.id.viewpager) as SwipeViewPager
        viewPager!!.adapter = Adapter(supportFragmentManager)
    }

    private inner class Adapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): HospitalJoinBaseFragment? {
            if (position < 0 || MAX_PAGE <= position)
                return null
            when (position) {
                0 -> current_fragment = HospitalJoinSearchFragment()
                1 -> current_fragment = HospitalJoinInfoFragment()
                2 -> current_fragment = HospitalJoinCertiFragment()
                3 -> current_fragment = HospitalJoinTermsFragment()
                4 -> current_fragment = HospitalJoinCerti2Fragment()
            }
            return current_fragment
        }

        override fun getCount(): Int {
            return MAX_PAGE
        }
    }

    fun goToNextView(){
        current_fragment!!.onChangeFragment()
        viewPager!!.apply { if(currentItem < MAX_PAGE) currentItem++ }
    }

}
