package com.dac.gapp.andac.fragment

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dac.gapp.andac.R
import com.dac.gapp.andac.adapter.SearchHospitalRecyclerViewAdapter
import com.dac.gapp.andac.base.BaseFragment
import com.dac.gapp.andac.model.firebase.HospitalInfo
import kotlinx.android.synthetic.main.fragment_search_hospital_for_list.*
import timber.log.Timber

class SearchHospitalFragmentForList : BaseFragment() {

    private var hospitals: HashMap<String, HospitalInfo> = HashMap()

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
//        loadHospitals()
    }

    private fun setupRecyclerView() {
        recyclerView.setHasFixedSize(true)
        val lm = LinearLayoutManager(context)
        lm.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = lm
    }

    private fun loadHospitals() {
        context!!.getHospitals()
                .get()
                .addOnCompleteListener({
                    if (it.isSuccessful) {
                        val itemList: ArrayList<HospitalInfo> = ArrayList()
                        for (document in it.result) {
//                            Timber.d(document.id + " => " + document.data)
                            hospitals[document.id] = document.toObject(HospitalInfo::class.java)
                            hospitals[document.id]!!.documentId = document.id
                            val hospitalInfo = hospitals[document.id]
                            val address = if (hospitalInfo!!.address1 != "") hospitalInfo.address1 else hospitalInfo.address2
//                            Timber.d("${hospitalInfo.name} ${hospitalInfo._geoloc}")
                            itemList.add(hospitalInfo)
                        }
                        recyclerView.adapter = SearchHospitalRecyclerViewAdapter(context, itemList)
                    } else {
                        Timber.w("Error getting documents. ${it.exception}")
                    }
                })
    }

}