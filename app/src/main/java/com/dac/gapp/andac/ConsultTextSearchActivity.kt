package com.dac.gapp.andac

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.View
import com.algolia.instantsearch.helpers.InstantSearch
import com.algolia.instantsearch.helpers.Searcher
import com.dac.gapp.andac.base.BaseActivity
import com.dac.gapp.andac.dialog.ConsultContentDialog
import com.dac.gapp.andac.enums.Algolia
import com.dac.gapp.andac.extension.toDate
import com.dac.gapp.andac.model.firebase.ConsultInfo
import com.dac.gapp.andac.util.getDateFormat
import kotlinx.android.synthetic.main.activity_consult_text_search.*
import org.json.JSONArray

class ConsultTextSearchActivity : BaseActivity() {

    private lateinit var searcher: Searcher

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_consult_text_search)
        prepareUI()
        searcher = Searcher.create(Algolia.APP_ID.value, Algolia.SEARCH_API_KEY.value, Algolia.INDEX_NAME_OPEN_CONSULT.value)

                // add date
                .registerResultListener { results, _ ->
                    val indexStr = "date"
                    val jsonArray : JSONArray = results.hits
                    for (i in 0..(jsonArray.length() - 1)) {
                        val item = jsonArray.getJSONObject(i)

                        // Your code here
                        if(!item.has(indexStr)) {
                            item.put(indexStr, item.getString("writeDate").toDate("yyyy-MM-dd'T'HH:mm:ss")?.getDateFormat('/'))
                            //  yyyy-MM-dd'T'HH:mm:ss

//                            item.put(indexStr, Date(item.getLong("writeDate")).getDateFormat('/'))
                        }
                    }
                }

        InstantSearch(this, searcher) // Initialize InstantSearch in this activity with searcher

        searcher.search(intent)

        // 객체를 만들어서 호출한 곳에 보냄
        hits.setOnItemClickListener{ _: RecyclerView, i: Int, _: View ->
            val jo = hits.get(i)

            // 오픈 상담 신청서
            getOpenConsult(jo.getString("userId"))?.get()?.addOnSuccessListener { querySnapshot ->
                val consultInfo = querySnapshot.toObject(ConsultInfo::class.java)
                val dialog = ConsultContentDialog(this@ConsultTextSearchActivity, consultInfo)
                dialog.show()
            }
        }
    }

    private fun prepareUI() {
        // 액션바
        setActionBarLeftImage(R.drawable.back)
        hideActionBarRight()
        setOnActionBarLeftClickListener(View.OnClickListener { finish() })
    }

    override fun onDestroy() {
        searcher.destroy()
        super.onDestroy()
    }


}
