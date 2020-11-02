package com.dac.gapp.andac

import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import com.algolia.instantsearch.helpers.InstantSearch
import com.algolia.instantsearch.helpers.Searcher
import com.algolia.search.saas.Query
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

        val index = intent.getStringExtra("index")?:Algolia.INDEX_NAME_OPEN_CONSULT.value

        searcher = Searcher.create(Algolia.APP_ID.value, Algolia.SEARCH_API_KEY.value, index)

                // add filter
                .also {
                    val hospitalUid = getUid()?:return
                    if(index == Algolia.INDEX_NAME_SELECT_CONSULT.value) it.query = Query().setFilters("hospitalId:$hospitalUid")
                }

                // add date
                .registerResultListener { results, _ ->
                    val indexStr = "date"
                    val jsonArray : JSONArray = results.hits
                    for (i in 0..(jsonArray.length() - 1)) {
                        val item = jsonArray.getJSONObject(i)
                        if(!item.has(indexStr)) {
                            item.put(indexStr, item.getString("writeDate").toDate("yyyy-MM-dd'T'HH:mm:ss")?.getDateFormat('/'))
                        }
                    }
                }

        InstantSearch(this, searcher) // Initialize InstantSearch in this activity with searcher

        searcher.search(intent)

        // 아이템 클릭시 신청서 보기
        hits.setOnItemClickListener{ _: RecyclerView, i: Int, _: View ->
            val jo = hits.get(i)
            val consultTask = if(index == Algolia.INDEX_NAME_OPEN_CONSULT.value) getOpenConsult(jo.getString("userId"))?.get()?.continueWith { it.result.toObject(ConsultInfo::class.java) }
            else getSelectConsultInfo(getUid()?:return@setOnItemClickListener, jo.getString("userId"))
            consultTask?.addOnSuccessListener {
                val dialog = ConsultContentDialog(this@ConsultTextSearchActivity, it?:return@addOnSuccessListener)
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
