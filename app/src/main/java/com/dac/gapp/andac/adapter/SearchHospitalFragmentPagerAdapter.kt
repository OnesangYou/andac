package com.dac.gapp.andac.adapter

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.dac.gapp.andac.R
import com.dac.gapp.andac.fragment.SearchHospitalFragmentForList
import com.dac.gapp.andac.fragment.SearchHospitalFragmentForMap

class SearchHospitalFragmentPagerAdapter(val context: Context, fm: FragmentManager) : FragmentPagerAdapter(fm) {
    private var fragments: ArrayList<Fragment> = ArrayList()

    init {
        fragments.add(SearchHospitalFragmentForMap.create(context.getString(R.string.nearby_hospital)))
        fragments.add(SearchHospitalFragmentForList.create(context.getString(R.string.popularity)))
        fragments.add(SearchHospitalFragmentForList.create(context.getString(R.string.seoul)))
        fragments.add(SearchHospitalFragmentForList.create(context.getString(R.string.gyeonggi)))
        fragments.add(SearchHospitalFragmentForList.create(context.getString(R.string.incheon)))
    }

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getCount(): Int {
        return fragments.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        val fragment = fragments[position]
        if (fragment is SearchHospitalFragmentForMap) {
            return fragment.title
        } else if (fragment is SearchHospitalFragmentForList) {
            return fragment.title
        }
        return context.getString(R.string.no_title)
    }
}