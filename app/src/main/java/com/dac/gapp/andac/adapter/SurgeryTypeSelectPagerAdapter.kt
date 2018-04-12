package com.dac.gapp.andac.adapter


import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.dac.gapp.andac.fragment.DesignationConsultingFragment
import com.dac.gapp.andac.fragment.OpenConsultingFragment

/**
 * Created by Administrator on 2018-04-12.
 */

class SurgeryTypeSelectPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    override fun getCount(): Int {
        return 2
    }

    override fun getItem(position: Int): Fragment {
        var selectedFragment: Fragment? = null
        when (position) {
            0 -> selectedFragment = OpenConsultingFragment()
            1 -> selectedFragment = DesignationConsultingFragment()
        }

        return selectedFragment!!
    }

}
