package com.dac.gapp.andac.adapter

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.dac.gapp.andac.R
import com.dac.gapp.andac.fragment.EventListFragmentForList

class EventListFragmentPagerAdapter(val context: Context?, fm: FragmentManager) : FragmentPagerAdapter(fm) {
    private var fragments: ArrayList<Fragment> = ArrayList()

    init {
        fragments.add(EventListFragmentForList.create("인기상품"))
        fragments.add(EventListFragmentForList.create("낮은 가격순"))
        fragments.add(EventListFragmentForList.create("높은 가격순"))
        fragments.add(EventListFragmentForList.create("근거리 순"))
    }

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getCount(): Int {
        return fragments.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        val fragment = fragments[position]
        if (fragment is EventListFragmentForList) {
            return fragment.title
        }
        return context!!.getString(R.string.no_title)
    }
}