package com.dac.gapp.andac.adapter

import android.content.Context
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.dac.gapp.andac.R
import com.dac.gapp.andac.fragment.ImageViewFragment
import com.dac.gapp.andac.fragment.SearchHospitalFragmentForList
import com.dac.gapp.andac.fragment.SearchHospitalFragmentForMap

class HospitalActivityPagerAdapter(val context: Context, fm: FragmentManager, images: ArrayList<String>) : FragmentPagerAdapter(fm) {
    private var fragments: ArrayList<Fragment> = ArrayList()

    init {
        for (image in images) {
            fragments.add(ImageViewFragment.createForUrl(image))
        }
    }

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getCount(): Int {
        return fragments.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return context.getString(R.string.no_title)
    }
}