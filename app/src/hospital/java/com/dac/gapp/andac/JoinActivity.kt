package com.dac.gapp.andac

import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.view.View
import com.dac.gapp.andac.base.BaseJoinActivity
import com.dac.gapp.andac.custom.SwipeViewPager
import com.dac.gapp.andac.fragment.*
import com.dac.gapp.andac.model.firebase.HospitalInfo
import android.view.WindowManager



class JoinActivity : BaseJoinActivity() {

    var MAX_PAGE = 6
    var current_fragment : JoinBaseFragment? = null
    var viewPager : SwipeViewPager? = null
    var hospitalInfo = HospitalInfo()
    var hospitalKey : String = ""
    var profilePicUri : Uri? = null
    var bankAccountPicUri: Uri? = null
    var busiRegiPicUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join)

        viewPager = findViewById<View>(R.id.viewpager) as SwipeViewPager
        viewPager!!.adapter = Adapter(supportFragmentManager)

        // 키보드가 에디트 텍스트를 가려서 옵션 넣어줌
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

    }

    private inner class Adapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment? {
            if (position < 0 || MAX_PAGE <= position)
                return null
            when (position) {
                0 -> current_fragment = JoinSearchFragment()
                1 -> current_fragment = JoinInfoFragment()
                2 -> current_fragment = JoinCertiFragment()
                3 -> current_fragment = JoinTermsFragment()
                4 -> current_fragment = JoinCerti2Fragment()
                5 -> current_fragment = JoinFinishFragment()
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
