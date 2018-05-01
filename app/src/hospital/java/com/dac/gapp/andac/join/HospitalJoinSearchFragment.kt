package com.dac.gapp.andac.join


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.algolia.instantsearch.helpers.InstantSearch
import com.algolia.instantsearch.helpers.Searcher
import com.dac.gapp.andac.BaseActivity
import com.dac.gapp.andac.HospitalJoinActivity
import com.dac.gapp.andac.R
import com.dac.gapp.andac.model.Algolia
import kotlinx.android.synthetic.hospital.fragment_hospital_join_certi2.*
import kotlinx.android.synthetic.main.activity_hospital_text_search.*

/**
 * A simple [Fragment] subclass.
 */
class HospitalJoinSearchFragment : Fragment() {

    private lateinit var searcher: Searcher

    private val ALGOLIA_INDEX_NAME = "hospitals"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_hospital_join_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        nextBtn.setOnClickListener {

            val hospitalJoinActivity = activity as HospitalJoinActivity

            // 다음 프레그먼트로 넘어감
            hospitalJoinActivity.goToNextView()
        }

        val baseActivity = context as BaseActivity
        searcher = Searcher.create(Algolia.APP_ID.value, Algolia.SEARCH_API_KEY.value, ALGOLIA_INDEX_NAME)
        InstantSearch(baseActivity, searcher) // Initialize InstantSearch in this activity with searcher
        searcher.search(baseActivity.intent) // Show results for empty query (on app launch) / voice query (from intent)

        hits.setOnItemClickListener{ recyclerView: RecyclerView, i: Int, view1: View ->

        }

    }

    override fun onDestroy() {
        searcher.destroy()
        super.onDestroy()
    }

}// Required empty public constructor
