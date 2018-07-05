package com.dac.gapp.andac.adapter

import android.content.Context
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.PagerAdapter
import android.util.Log
import com.dac.gapp.andac.R
import com.dac.gapp.andac.fragment.BoardFragmentForList

class BoardFragmentPagerAdapter(val context: Context?, fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
    private var titles: ArrayList<String> = ArrayList()

    init {
        context?.apply {
            titles.add(getString(R.string.free_board))
            titles.add(getString(R.string.review_board))
            titles.add(getString(R.string.question_board))
            titles.add(getString(R.string.hot_board))
        }
    }

    override fun getItem(position: Int): BoardFragmentForList {
        Log.d("KBJ", "getItem")

        return BoardFragmentForList.create(titles[position])
    }

    override fun getCount(): Int {
        return titles.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return titles[position]
    }

    override fun getItemPosition(item: Any): Int {
        val fragment = item as BoardFragmentForList
        val title = fragment.title
        val position = titles.indexOf(title)
        getItem(position).getData()
        Log.d("KBJ", "getItemPosition")

        return if (position >= 0) {
            position
        } else {
            PagerAdapter.POSITION_NONE
        }
    }

}