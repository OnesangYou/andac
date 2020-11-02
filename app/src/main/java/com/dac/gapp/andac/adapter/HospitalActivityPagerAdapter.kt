package com.dac.gapp.andac.adapter

import android.content.Context
import androidx.core.app.Fragment
import androidx.core.app.FragmentManager
import androidx.core.app.FragmentPagerAdapter
import com.dac.gapp.andac.R
import com.dac.gapp.andac.fragment.ImageViewFragment

class HospitalActivityPagerAdapter(val context: Context, fm: FragmentManager, images: ArrayList<Any>) : FragmentPagerAdapter(fm) {
    private var fragments: ArrayList<Fragment> = ArrayList()

    init {
        for (image in images) {
            fragments.add(ImageViewFragment.create(image))
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