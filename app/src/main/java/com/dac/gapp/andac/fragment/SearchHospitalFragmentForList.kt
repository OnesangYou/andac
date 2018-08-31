package com.dac.gapp.andac.fragment

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.algolia.search.saas.Client
import com.algolia.search.saas.Query
import com.dac.gapp.andac.R
import com.dac.gapp.andac.adapter.SearchHospitalRecyclerViewAdapter
import com.dac.gapp.andac.base.BaseFragment
import com.dac.gapp.andac.enums.Algolia
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
        loadHospitals()
    }

    private fun setupRecyclerView() {
        recyclerView.setHasFixedSize(true)
        val lm = LinearLayoutManager(context)
        lm.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = lm
    }

    private fun loadHospitals() {
        val client = Client(Algolia.APP_ID.value, Algolia.SEARCH_API_KEY.value)
        val index = client.getIndex(Algolia.INDEX_NAME_HOSPITAL.value)
        // filter 에 number, objectID 칼럼 제외하고 안됨 -> 왜???????
        // _highlightResult 관련이 있나??
        val query = Query(title)
        query.hitsPerPage = Integer.MAX_VALUE
//        query.filters = "address1:\"test\""
//                query.filters = "lat=36.32911"
//        query.filters = "number=580"
//                query.filters = "value:\"조안과의원\""
//        query.filters = "openDate:\"1999.10.08\""
//        query.filters = "name:\"${URLEncoder.encode("조안과의원", "utf-8")}\""
//                query.filters = "objectID:\"bNs9gcZFxhEQu1iRVXEs\""
//        query.filters = "address1:전라북도 전주시 덕진구 덕진동1가 1267번지 27호"
        //강원도 삼척시 남양동 55-46번지 / 580 /
        index.searchAsync(query) { jsonObject, AlgoliaException ->
            if (jsonObject == null) return@searchAsync

            Timber.d("jsonObject: ${jsonObject.toString(4)}")

            if (jsonObject.has(Algolia.HITS.value) && jsonObject.getJSONArray(Algolia.HITS.value).length() > 0) {
                val itemList: ArrayList<HospitalInfo> = ArrayList()
                Timber.d("jsonObject: ${jsonObject.getJSONArray(Algolia.HITS.value).getJSONObject(0).getString(Algolia.NAME.value)}")
                val hits = jsonObject.getJSONArray(Algolia.HITS.value)
                var i = 0
                while (i < hits.length()) {
                    val jo = hits.getJSONObject(i)
                    Timber.d("jsonObject[$i]: ${jo.toString(4)}")
                    itemList.add(HospitalInfo.create(jo))
                    i++
                }

                recyclerView.adapter = SearchHospitalRecyclerViewAdapter(context, itemList)
            }
        }
//        context!!.getHospitals()
//                .get()
//                .addOnCompleteListener({
//                    if (it.isSuccessful) {
//                        val itemList: ArrayList<HospitalInfo> = ArrayList()
//                        for (document in it.result) {
////                            Timber.d(document.id + " => " + document.data)
//                            hospitals[document.id] = document.toObject(HospitalInfo::class.java)
//                            hospitals[document.id]!!.documentId = document.id
//                            val hospitalInfo = hospitals[document.id]
//                            val address = if (hospitalInfo!!.address1 != "") hospitalInfo.address1 else hospitalInfo.address2
////                            Timber.d("${hospitalInfo.name} ${hospitalInfo._geoloc}")
//                            itemList.add(hospitalInfo)
//                        }
//                        recyclerView.adapter = SearchHospitalRecyclerViewAdapter(context, itemList)
//                    } else {
//                        Timber.w("Error getting documents. ${it.exception}")
//                    }
//                })
    }

}