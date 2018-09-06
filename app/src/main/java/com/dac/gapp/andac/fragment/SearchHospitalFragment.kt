package com.dac.gapp.andac.fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dac.gapp.andac.R
import com.dac.gapp.andac.adapter.SearchHospitalFragmentPagerAdapter
import kotlinx.android.synthetic.main.fragment_search_hospital.*


/**
 * A simple [Fragment] subclass.
 */
class SearchHospitalFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_hospital, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        prepareUi()
        setupEventsOnCreate()
    }

    private fun prepareUi() {
        viewPager.adapter = SearchHospitalFragmentPagerAdapter(context, childFragmentManager)
        viewPager.offscreenPageLimit = 11
        layoutTab.setupWithViewPager(viewPager)
    }

    private fun setupEventsOnCreate() {
    }


}


