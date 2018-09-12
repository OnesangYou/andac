package com.dac.gapp.andac

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.View
import com.algolia.instantsearch.helpers.InstantSearch
import com.algolia.instantsearch.helpers.Searcher
import com.algolia.search.saas.Query
import com.dac.gapp.andac.base.BaseActivity
import com.dac.gapp.andac.enums.Algolia
import com.dac.gapp.andac.model.firebase.HospitalInfo
import kotlinx.android.synthetic.main.activity_hospital_text_search.*


class HospitalTextSearchActivity : BaseActivity() {

    private lateinit var searcher: Searcher

    companion object {
        fun createIntent(context: Context, isApproval: Boolean): Intent {
            val intent = Intent(context, HospitalTextSearchActivity::class.java)
            if (isApproval)
                intent.putExtra("filterStr", "approval=1")
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hospital_text_search)
        prepareUI()
        searcher = Searcher.create(Algolia.APP_ID.value, Algolia.SEARCH_API_KEY.value, Algolia.INDEX_NAME_HOSPITAL.value)
        InstantSearch(this, searcher) // Initialize InstantSearch in this activity with searcher

        searcher
                .setQuery(Query().setFilters(intent.getStringExtra("filterStr")))
                .search(intent)

        // hospitalInfo 객체를 만들어서 호출한 곳에 보냄
        hits.setOnItemClickListener{ _: RecyclerView, i: Int, _: View ->

            val jo = hits.get(i)

            HospitalInfo.create(jo).let { hospitalInfo ->
                Intent().let {
                    it.putExtra("hospitalInfo", hospitalInfo)
                    setResult(Activity.RESULT_OK, it)
                    finish()
                }
            }
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
