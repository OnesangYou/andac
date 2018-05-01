package com.dac.gapp.andac.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dac.gapp.andac.R
import com.dac.gapp.andac.adapter.SearchHospitalRecyclerViewAdapter
import com.dac.gapp.andac.model.SearchHospitalItem
import kotlinx.android.synthetic.main.fragment_search_hospital_for_list.*

class SearchHospitalFragmentForList : Fragment() {

    var title: String = ""

    // static method
    companion object {
        fun create(title: String): SearchHospitalFragmentForList {
            val f = SearchHospitalFragmentForList()
            f.title = title
            val args = Bundle()
            f.arguments = args
            return f
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_search_hospital_for_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prepareUi()
    }

    private fun prepareUi() {
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        recyclerView.setHasFixedSize(true)
        val lm = LinearLayoutManager(context)
        lm.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = lm
        recyclerView.adapter = SearchHospitalRecyclerViewAdapter(context, getDummyData())
    }

    private fun getDummyData(): List<SearchHospitalItem> {
        val itemList: ArrayList<SearchHospitalItem> = ArrayList()
        for (x in 1..10) {
            val item = SearchHospitalItem(x, R.drawable.uproad_pic, "title $x", "address2 $x", "description $x")
            itemList.add(item)
        }
        return itemList
    }

}