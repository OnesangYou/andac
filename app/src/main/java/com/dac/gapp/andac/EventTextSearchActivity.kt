package com.dac.gapp.andac

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.View
import com.algolia.instantsearch.helpers.InstantSearch
import com.algolia.instantsearch.helpers.Searcher
import com.dac.gapp.andac.base.BaseActivity
import com.dac.gapp.andac.enums.Algolia
import kotlinx.android.synthetic.main.activity_event_text_search.*

class EventTextSearchActivity : BaseActivity() {

    private lateinit var searcher: Searcher

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_text_search)
        prepareUI()
        searcher = Searcher.create(Algolia.APP_ID.value, Algolia.SEARCH_API_KEY.value, Algolia.INDEX_NAME_EVENT.value)
        InstantSearch(this, searcher) // Initialize InstantSearch in this activity with searcher

        searcher.search(intent)

        // 객체를 만들어서 호출한 곳에 보냄
        hits.setOnItemClickListener{ _: RecyclerView, i: Int, _: View ->
            val jo = hits.get(i)
            startActivity(Intent(this, EventDetailActivity::class.java).putExtra(OBJECT_KEY, jo.getString("objectId")))
        }
    }

    private fun prepareUI() {
        setActionBarLeftImage(R.drawable.back)
        setActionBarCenterImage(R.drawable.andac_font)
        setOnActionBarLeftClickListener(View.OnClickListener {
            finish()
        })
        hidActionBarRight()
    }

    override fun onDestroy() {
        searcher.destroy()
        super.onDestroy()
    }

}
