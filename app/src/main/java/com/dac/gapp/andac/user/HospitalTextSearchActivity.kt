package com.dac.gapp.andac.user

import android.os.Bundle
import com.algolia.instantsearch.helpers.InstantSearch
import com.algolia.instantsearch.helpers.Searcher
import com.algolia.search.saas.AbstractQuery
import com.algolia.search.saas.Query
import com.dac.gapp.andac.BaseActivity
import com.dac.gapp.andac.R
import timber.log.Timber


class HospitalTextSearchActivity : BaseActivity() {

    private lateinit var searcher: Searcher

    private val ALGOLIA_INDEX_NAME = "hospitals"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hospital_text_search)

        searcher = Searcher.create(ALGOLIA_APP_ID, ALGOLIA_SEARCH_API_KEY, ALGOLIA_INDEX_NAME)
        InstantSearch(this, searcher) // Initialize InstantSearch in this activity with searcher
        searcher.search(intent) // Show results for empty query (on app launch) / voice query (from intent)
        val query = Query()
                .setAroundLatLng(AbstractQuery.LatLng(37.4739738, 126.886727))
                .setAroundRadius(50000)
        searcher.searchable.searchAsync(query, {jsonObject, algoliaException ->
            Timber.d("jsonObject: $jsonObject")
        })
    }

    override fun onDestroy() {
        searcher.destroy()
        super.onDestroy()
    }

}
