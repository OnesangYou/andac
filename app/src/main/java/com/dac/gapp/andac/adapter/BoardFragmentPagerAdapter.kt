package com.dac.gapp.andac.adapter

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.dac.gapp.andac.R
import com.dac.gapp.andac.fragment.BoardFragmentForList

class BoardFragmentPagerAdapter(val context: Context?, fm: FragmentManager) : FragmentPagerAdapter(fm) {
    private var fragments: ArrayList<Fragment> = ArrayList()

    init {
        fragments.add(BoardFragmentForList.create(context!!.getString(R.string.free_board)))
        fragments.add(BoardFragmentForList.create(context.getString(R.string.review_board)))
        fragments.add(BoardFragmentForList.create(context.getString(R.string.question_board)))
        fragments.add(BoardFragmentForList.create(context.getString(R.string.hot_board)))
    }

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getCount(): Int {
        return fragments.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        val fragment = fragments[position]
        if (fragment is BoardFragmentForList) {
            return fragment.title
        }
        return context!!.getString(R.string.no_title)
    }
}