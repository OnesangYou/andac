package com.dac.gapp.andac

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.View
import com.algolia.instantsearch.helpers.InstantSearch
import com.algolia.instantsearch.helpers.Searcher
import com.dac.gapp.andac.base.BaseActivity
import com.dac.gapp.andac.model.Algolia
import com.dac.gapp.andac.model.firebase.HospitalInfo
import kotlinx.android.synthetic.main.activity_hospital_text_search.*


class HospitalTextSearchActivity : BaseActivity() {

    private lateinit var searcher: Searcher

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hospital_text_search)

        searcher = Searcher.create(Algolia.APP_ID.value, Algolia.SEARCH_API_KEY.value, Algolia.INDEX_NAME_HOSPITAL.value)
        InstantSearch(this, searcher) // Initialize InstantSearch in this activity with searcher
        searcher.search(intent) // Show results for empty query (on app launch) / voice query (from intent)

        // hospitalInfo 객체를 만들어서 호출한 곳에 보냄
        hits.setOnItemClickListener{ _: RecyclerView, i: Int, _: View ->

            val hit = hits.get(i)

            HospitalInfo().apply{
                _geoloc.lat = hit.getJSONObject("_geoloc").getDouble("lat")
                _geoloc.lng = hit.getJSONObject("_geoloc").getDouble("lng")
                address1 = hit.getString("address1")
                address2 = hit.getString("address2")
                name = hit.getString("name")
                openDate = hit.getString("openDate")
                phone = hit.getString("phone")
                status = hit.getString("status")
                type = hit.getString("type")
                objectID = hit.getString("objectID")
            }.let { hospitalInfo ->
                Intent().let{
                    it.putExtra("hospitalInfo", hospitalInfo)
                    setResult(Activity.RESULT_OK, it)
                    finish()
                }
            }
        }
    }

    override fun onDestroy() {
        searcher.destroy()
        super.onDestroy()
    }

}
