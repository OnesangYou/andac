package com.dac.gapp.andac.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dac.gapp.andac.HospitalActivity
import com.dac.gapp.andac.R
import kotlinx.android.synthetic.main.fragment_search_hospital_for_map.*

class SearchHospitalFragmentForMap : Fragment() {

    var title: String = ""

    // static method
    companion object {
        fun create(title: String): SearchHospitalFragmentForMap {
            val f = SearchHospitalFragmentForMap()
            f.title = title
            val args = Bundle()
            f.arguments = args
            return f
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_search_hospital_for_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupEventsOnCreate()
    }

    private fun setupEventsOnCreate() {
        btn_hospital.setOnClickListener({
            val nextIntent = Intent(context, HospitalActivity::class.java)
            startActivity(nextIntent)
        })
    }
}