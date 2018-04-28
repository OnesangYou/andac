package com.dac.gapp.andac.user

import android.os.Bundle
import com.algolia.instantsearch.helpers.InstantSearch
import com.algolia.instantsearch.helpers.Searcher
import com.dac.gapp.andac.BaseActivity
import com.dac.gapp.andac.R
import com.dac.gapp.andac.model.Algolia


class HospitalTextSearchActivity : BaseActivity() {

    private lateinit var searcher: Searcher

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hospital_text_search)

        searcher = Searcher.create(Algolia.APP_ID.value, Algolia.SEARCH_API_KEY.value, Algolia.INDEX_NAME_HOSPITAL.value)
        InstantSearch(this, searcher) // Initialize InstantSearch in this activity with searcher
        searcher.search(intent) // Show results for empty query (on app launch) / voice query (from intent)
    }

    override fun onDestroy() {
        searcher.destroy()
        super.onDestroy()
    }

}
