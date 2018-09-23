package com.dac.gapp.andac.adapter

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.dac.gapp.andac.R
import com.dac.gapp.andac.base.BaseActivity
import com.dac.gapp.andac.fragment.ConsultBoardFragmentForList

class ConsultBoardFragmentPagerAdapter(val context: Context, fm: FragmentManager) : FragmentPagerAdapter(fm) {
    private var fragments: ArrayList<Fragment> = ArrayList()

    init {
        if( context is BaseActivity && context.isHospital() ){
            fragments.add(ConsultBoardFragmentForList.create(context.getString(R.string.consult_open_board)))
        }
        fragments.add(ConsultBoardFragmentForList.create(context.getString(R.string.consult_seleted_board)))
        fragments.add(ConsultBoardFragmentForList.create(context.getString(R.string.consulting_board)))
        fragments.add(ConsultBoardFragmentForList.create(context.getString(R.string.consulted_board)))
    }

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getCount(): Int {
        return fragments.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        val fragment = fragments[position]
        if (fragment is ConsultBoardFragmentForList) {
            return fragment.title
        }
        return context.getString(R.string.no_title)
    }
}