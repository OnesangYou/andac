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
import com.dac.gapp.andac.enums.Ad
import com.dac.gapp.andac.enums.Algolia
import com.dac.gapp.andac.model.HeaderInfo
import com.dac.gapp.andac.model.firebase.HospitalInfo
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentSnapshot
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_search_hospital_for_list.*
import timber.log.Timber

class SearchHospitalFragmentForList : BaseFragment() {

    lateinit var title: String
    private var isEtcEnd: Boolean = false
    private val etcList: MutableList<Int> = mutableListOf(R.string.jeju, R.string.gangwon)
    private val mHospitalList = mutableListOf<Pair<Any, Int>>()

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
        if (isEtc()) {
            if (isEtcEnd) {
                recyclerView?.apply {
                    adapter = SearchHospitalRecyclerViewAdapter(context, mHospitalList)
                }
            } else {
                for (etc in etcList) {
                    loadHospitals(getString(etc))
                    if (etc == etcList.last())
                        isEtcEnd = true
                }
            }
        } else {
            if (mHospitalList.size > 0) {
                recyclerView?.apply {
                    adapter = SearchHospitalRecyclerViewAdapter(context, mHospitalList)
                }
            } else {
                loadHospitals(title)
            }
        }
    }

    private fun setupRecyclerView() {
        val lm = LinearLayoutManager(context).apply {
            orientation = LinearLayoutManager.VERTICAL
        }
        recyclerView?.apply {
            setHasFixedSize(true)
            layoutManager = lm
        }
    }

    private fun isEtc(): Boolean {
        return title == getString(R.string.etc)
    }

    private fun loadHospitals(queryString: String) {
        val client = Client(Algolia.APP_ID.value, Algolia.SEARCH_API_KEY.value)
        val index = client.getIndex(Algolia.INDEX_NAME_HOSPITAL.value)
        val query = Query()
        if (queryString != getString(R.string.popularity)) query.query = queryString
        query.setFacets("address1")
        query.hitsPerPage = Integer.MAX_VALUE
//        query.filters = "address1:\"서울\""
//                query.filters = "lat=36.32911"
//            query.filters = "address1: \"seoul\"";
//        query.filters = "number=580"
//                query.filters = "value:\"조안과의원\""
//        query.filters = "openDate:\"1999.10.08\""
//        query.filters = "name:\"${URLEncoder.encode("조안과의원", "utf-8")}\""
//                query.filters = "objectID:\"bNs9gcZFxhEQu1iRVXEs\""
//        query.filters = "address1:전라북도 전주시 덕진구 덕진동1가 1267번지 27호"
        //강원도 삼척시 남양동 55-46번지 / 580 /
        context?.let { context ->
            context.getDb().collection(Ad.SEARCH_HOSPITAL_BANNER_AD.collectionName)
                    .whereEqualTo("showingUp", true)
                    .get()
                    .continueWithTask {
                        it.result.documents
                                .filter { it.id.isNotEmpty() }
                                .mapNotNull { context.getHospital(it.id).get() }
                                .let { Tasks.whenAllSuccess<DocumentSnapshot>(it) }
                    }.addOnSuccessListener { hospitalAdList ->
                        mHospitalList.add(0, HeaderInfo(R.drawable.star_full, "울트라 광고") to SearchHospitalRecyclerViewAdapter.VIEW_TYPE_HEADER)
                        hospitalAdList.forEachIndexed { index, documentSnapshot ->
                            documentSnapshot.toObject(HospitalInfo::class.java)?.let {
                                it.objectID = documentSnapshot.id
                                mHospitalList.add(index + 1, it to SearchHospitalRecyclerViewAdapter.VIEW_TYPE_CONTENT)
                            }
                        }
                        mHospitalList.add(hospitalAdList.size + 1, HeaderInfo(R.drawable.star_empty, "일반 병원") to SearchHospitalRecyclerViewAdapter.VIEW_TYPE_HEADER)

                        recyclerView?.apply {
                            if (isEtc()) {
                                if (isEtcEnd) adapter = SearchHospitalRecyclerViewAdapter(context, mHospitalList)
                            } else {
                                adapter = SearchHospitalRecyclerViewAdapter(context, mHospitalList)
                            }
                        }
                    }
                    .addOnFailureListener {
                        Timber.e("SEARCH_HOSPITAL_BANNER_AD 광고 로드 실패 : ${it.localizedMessage}")
                    }

            index.searchAsync(query) { jsonObject, AlgoliaException ->
                if (jsonObject == null) return@searchAsync
                if (jsonObject.has(Algolia.HITS.value) && jsonObject.getJSONArray(Algolia.HITS.value).length() > 0) {
                    Timber.d("jsonObject: ${jsonObject.getJSONArray(Algolia.HITS.value).getJSONObject(0).getString(Algolia.NAME.value)}")

                    val hits = jsonObject.getJSONArray(Algolia.HITS.value)
                    var i = 0
                    while (i < hits.length()) {
                        val jo = hits.getJSONObject(i)
                        mHospitalList.add(Gson().fromJson(jo.toString(), HospitalInfo::class.java) to SearchHospitalRecyclerViewAdapter.VIEW_TYPE_CONTENT)
                        i++
                    }

                    recyclerView?.apply {
                        if (isEtc()) {
                            if (isEtcEnd) adapter = SearchHospitalRecyclerViewAdapter(context, mHospitalList)
                        } else {
                            adapter = SearchHospitalRecyclerViewAdapter(context, mHospitalList)
                        }
                    }


                }
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