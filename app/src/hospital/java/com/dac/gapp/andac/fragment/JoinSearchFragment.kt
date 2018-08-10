@file:Suppress("UNUSED_ANONYMOUS_PARAMETER")

package com.dac.gapp.andac.fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.algolia.instantsearch.helpers.InstantSearch
import com.algolia.instantsearch.helpers.Searcher
import com.algolia.search.saas.Query
import com.dac.gapp.andac.JoinActivity
import com.dac.gapp.andac.R
import com.dac.gapp.andac.enums.Algolia
import kotlinx.android.synthetic.hospital.fragment_join_certi2.*
import kotlinx.android.synthetic.main.activity_hospital_text_search.*


/**
 * A simple [Fragment] subclass.
 */
class JoinSearchFragment : JoinBaseFragment() {
    override fun onChangeFragment() {
    }

    private lateinit var searcher: Searcher

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_join_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        nextBtn.setOnClickListener {

            val hospitalJoinActivity = activity as JoinActivity

            // 다음 프레그먼트로 넘어감
            hospitalJoinActivity.goToNextView()
        }

        context?.let{context ->
            searcher = Searcher.create(Algolia.APP_ID.value, Algolia.SEARCH_API_KEY.value, Algolia.INDEX_NAME_HOSPITAL.value)
            InstantSearch(context, searcher) // Initialize InstantSearch in this activity with searcher
            searcher
                    .setQuery(Query()
//                            .setFilters("approval!=0 AND approval!=1")) // 승인 안된 병원 목록
                            .setFilters("approval!=0 AND approval!=1")) // 승인 안된 병원 목록
                    .search(context.intent) // Show results for empty query (on app launch) / voice query (from intent)

            hits.setOnItemClickListener{ recyclerView: RecyclerView, i: Int, view1: View ->

                val hit = hits.get(i)

                (activity as JoinActivity).run{

                    hospitalInfo.apply{
                        _geoloc.lat = hit.getJSONObject("_geoloc").getDouble("lat")
                        _geoloc.lng = hit.getJSONObject("_geoloc").getDouble("lng")
                        address1 = hit.getString("address1")
                        address2 = hit.getString("address2")
                        name = hit.getString("name")
                        openDate = hit.getString("openDate")
                        phone = hit.getString("phone")
                        status = hit.getString("status")
                        type = hit.getString("type")
                    }

                    hospitalKey = hit.getString("objectID")

                    goToNextView()
                }

            }
        }



    }

    override fun onDestroy() {
        searcher.destroy()
        super.onDestroy()
    }

}// Required empty public constructor
