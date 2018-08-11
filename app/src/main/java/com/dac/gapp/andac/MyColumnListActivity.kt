package com.dac.gapp.andac

import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import com.dac.gapp.andac.adapter.ColumnRecyclerViewAdapter
import com.dac.gapp.andac.base.BaseActivity
import com.dac.gapp.andac.model.firebase.ColumnInfo
import com.dac.gapp.andac.model.firebase.HospitalInfo
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_my_column_list.*
import org.jetbrains.anko.startActivity

@Suppress("DEPRECATION")
class MyColumnListActivity : BaseActivity() {

    val list = mutableListOf<ColumnInfo>()
    val map = mutableMapOf<String, HospitalInfo>()
    private var lastVisible : DocumentSnapshot? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_column_list)

        // recyclerView
        recyclerView.layoutManager = GridLayoutManager(this@MyColumnListActivity,2)
        recyclerView.adapter = ColumnRecyclerViewAdapter(this@MyColumnListActivity, list, map)
        setAdapter()


        back.setOnClickListener { finish() }
        writeColumnBtn.setOnClickListener { startActivity<ColumnWriteActivity>() }
    }

    private fun setAdapter() {
        // reset data
        list.clear()
        map.clear()
        lastVisible = null
        recyclerView.adapter.notifyDataSetChanged()

        // add Data
        addDataToRecycler()

        // add event to recycler's last
        recyclerView.setOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(rv: RecyclerView?, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_SETTLING && !recyclerView.canScrollVertically(1)) {
                    addDataToRecycler()
                }
            }
        })
    }

    fun addDataToRecycler() {
        showProgressDialog()
        getHospitalColumns()
                ?.orderBy("createdDate", Query.Direction.DESCENDING)
                .let { query ->
                    lastVisible?.let { query?.startAfter(it) } ?: query
                }    // 쿼리 커서 시작 위치 지정
                ?.limit(PageListSize)  // 페이지 단위
                ?.let { it -> getTripleDataTask(it)}
                ?.addOnSuccessListener {
                    list.addAll(it.first)
                    map.putAll(it.second)
                    lastVisible = it.third
                    recyclerView.adapter.notifyDataSetChanged()
                }
                ?.addOnCompleteListener { hideProgressDialog() }

    }

    private fun getTripleDataTask(query : Query) : Task<Triple<List<ColumnInfo>, Map<String, HospitalInfo>, DocumentSnapshot?>>? {
        var infos : List<ColumnInfo> = listOf()
        var lastVisible : DocumentSnapshot? = null
        return query.get()
                .continueWithTask { it ->
                    lastVisible = it.result.documents.let { it[it.size - 1] }
                    it.result.map { getColumn(it.id)?.get() }
                            .let { Tasks.whenAllSuccess<DocumentSnapshot>(it) }
                }
                .continueWith { it -> infos = it.result.mapNotNull { it.toObject(ColumnInfo::class.java) } }
                .continueWithTask { getHospital()?.get() }
                .continueWith {
                    it.result.toObject(HospitalInfo::class.java)?.let{hospitalInfo ->
                        Triple(infos,  mapOf( getUid().toString() to hospitalInfo ), lastVisible)
                    }
                }

    }

}
