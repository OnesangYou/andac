package com.dac.gapp.andac

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.dac.gapp.andac.adapter.ColumnRecyclerAdapter
import com.dac.gapp.andac.base.BaseActivity
import com.dac.gapp.andac.model.firebase.ColumnInfo
import com.dac.gapp.andac.model.firebase.HospitalInfo
import com.dac.gapp.andac.util.OnItemClickListener
import com.dac.gapp.andac.util.addOnItemClickListener
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_column.*

@Suppress("DEPRECATION")
class ColumnActivity : BaseActivity() {

    val list = mutableListOf<ColumnInfo>()
    val map = mutableMapOf<String, HospitalInfo>()
    private var lastVisible : DocumentSnapshot? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_column)

        // recyclerView
        recyclerView.layoutManager = GridLayoutManager(this,2)
        recyclerView.adapter = ColumnRecyclerAdapter(this@ColumnActivity, list, map)
        recyclerView.addOnItemClickListener(object: OnItemClickListener {
            override fun onItemClicked(position: Int, view: View) {
                // 디테일
                startActivity(Intent(this@ColumnActivity, ColumnDetailActivity::class.java).putExtra(OBJECT_KEY,list[position].objectId))
            }
        })

        setAdapter()

        // back
        back.setOnClickListener { finish() }
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
        getTripleDataTask(
                getColumns()
                        .orderBy("writeDate", Query.Direction.DESCENDING)
                        .let { query -> lastVisible?.let { query.startAfter(it) } ?: query }    // 쿼리 커서 시작 위치 지정
                        .limit(PageListSize)   // 페이지 단위
        )
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
                .continueWith { it ->
                    lastVisible = it.result.documents.let { it[it.size-1] }
                    it.result.toObjects(ColumnInfo::class.java) }
                .continueWithTask { it ->
                    infos = it.result
                    it.result.groupBy { it.writerUid }
                            .map { getHospital(it.key).get() }
                            .let { Tasks.whenAllSuccess<DocumentSnapshot>(it) }
                }
                .continueWith { it ->
                    Triple(infos, it.result
                            .filterNotNull()
                            .map { it.id to it.toObject(HospitalInfo::class.java)!!}
                            .toMap(), lastVisible)
                }

    }

}
