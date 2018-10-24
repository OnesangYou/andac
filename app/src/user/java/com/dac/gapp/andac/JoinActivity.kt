package com.dac.gapp.andac

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.view.View
import com.dac.gapp.andac.base.BaseJoinActivity
import com.dac.gapp.andac.custom.SwipeViewPager
import com.dac.gapp.andac.databinding.ActivityJoinBinding
import com.dac.gapp.andac.fragment.JoinFinishFragment
import com.dac.gapp.andac.fragment.JoinInfoFragment
import com.dac.gapp.andac.fragment.JoinTermsFragment
import com.dac.gapp.andac.model.firebase.UserInfo
import com.google.firebase.auth.FirebaseUser
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.debug


@Suppress("NAME_SHADOWING")
class JoinActivity : BaseJoinActivity(), AnkoLogger {

    var MAX_PAGE = 3
    var cur_fragment = Fragment()
    var viewPager : SwipeViewPager? = null
    val mUserInfo = UserInfo()

    private lateinit var binding: ActivityJoinBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join)
        binding = getBinding()
        hideActionBar()
        viewPager = findViewById<View>(R.id.viewpager) as SwipeViewPager
        viewPager!!.adapter = Adapter(supportFragmentManager)

    }

    fun goToNextView(){
        viewPager!!.apply { if(currentItem < MAX_PAGE) currentItem++ }
    }

    fun updateUI(user: FirebaseUser?) {
        user?.let{
            toast("회원가입 완료")
            goToNextView()
//            finish()
        }
    }

    private inner class Adapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment? {
            if (position < 0 || MAX_PAGE <= position)
                return null
            when (position) {
                0 -> cur_fragment = JoinTermsFragment()
                1 -> cur_fragment = JoinInfoFragment()
                2 -> cur_fragment = JoinFinishFragment()
            }
            return cur_fragment
        }

        override fun getCount(): Int {
            return MAX_PAGE
        }
    }

}
