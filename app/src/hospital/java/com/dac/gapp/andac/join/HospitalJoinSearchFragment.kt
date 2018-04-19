package com.dac.gapp.andac.join


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.algolia.instantsearch.helpers.InstantSearch
import com.algolia.instantsearch.helpers.Searcher
import com.dac.gapp.andac.BaseActivity
import com.dac.gapp.andac.HospitalJoinActivity
import com.dac.gapp.andac.R
import kotlinx.android.synthetic.hospital.fragment_hospital_join_comp.*


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
        nextBtn.setOnClickListener { (activity as HospitalJoinActivity).goToNextView() }

        val baseActivity = context as BaseActivity
        searcher = Searcher.create(baseActivity.ALGOLIA_APP_ID, baseActivity.ALGOLIA_SEARCH_API_KEY, ALGOLIA_INDEX_NAME)
        InstantSearch(baseActivity, searcher) // Initialize InstantSearch in this activity with searcher
        searcher.search(baseActivity.intent) // Show results for empty query (on app launch) / voice query (from intent)

    }

    override fun onDestroy() {
        searcher.destroy()
        super.onDestroy()
    }

}// Required empty public constructor
