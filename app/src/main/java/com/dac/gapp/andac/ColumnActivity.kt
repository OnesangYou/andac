package com.dac.gapp.andac

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.dac.gapp.andac.adapter.ColumnRecyclerAdapter
import com.dac.gapp.andac.base.BaseActivity
import com.dac.gapp.andac.enums.PageSize
import com.dac.gapp.andac.model.firebase.ColumnInfo
import com.dac.gapp.andac.model.firebase.HospitalInfo
import com.dac.gapp.andac.util.OnItemClickListener
import com.dac.gapp.andac.util.UiUtil
import com.dac.gapp.andac.util.addOnItemClickListener
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_column.*
import org.jetbrains.anko.startActivity

@Suppress("DEPRECATION")
class ColumnActivity : BaseActivity() {

    val list = mutableListOf<ColumnInfo>()
    val map = mutableMapOf<String, HospitalInfo>()
    private var lastVisible : DocumentSnapshot? = null
    var currentTask : Task<*>? = null  // task 진행 유무 판단용
    var isListEmpty = false // 리스트가 없는지 확인

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_column)
        setActionBarLeftImage(R.drawable.back)
        setActionBarCenterText("칼럼 게시판")
        setOnActionBarLeftClickListener(View.OnClickListener { finish() })
        setActionBarRightImage(R.drawable.finder)
        setOnActionBarRightClickListener(View.OnClickListener {
            // 검색 기능
            startActivity<ColumnTextSearchActivity>()
        })

        resetData()

        // recyclerView
        recyclerView.layoutManager = GridLayoutManager(this,2)
        recyclerView.adapter = ColumnRecyclerAdapter(this@ColumnActivity, list, map)
        recyclerView.addOnItemClickListener(object: OnItemClickListener {
            override fun onItemClicked(position: Int, view: View) {
                // 디테일
                startActivity(Intent(this@ColumnActivity, ColumnDetailActivity::class.java).putExtra(OBJECT_KEY,list[position].objectId))
            }
        })

    }

    override fun onStart() {
        super.onStart()
        // default
        setAdapter()
    }

    private fun setAdapter() {
        // reset data
        resetData()

        recyclerView.adapter.notifyDataSetChanged()

        // add Data
        addDataToRecycler()

        // add event to recycler's last
        recyclerView.setOnScrollListener(onScrollListener{addDataToRecycler()})
    }

    private fun onScrollListener(callback : () -> Unit) = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
            recyclerView?:return
            check(!isListEmpty && currentTask == null){return@onScrolled}
            val computeVerticalScrollRange = recyclerView.computeVerticalScrollRange()
            val computeVerticalScrollOffset = recyclerView.computeVerticalScrollOffset()
            val computeVerticalScrollRange80 = (computeVerticalScrollRange* UiUtil.ScrollRefreshTriggerRatio).toInt()
            if(computeVerticalScrollOffset > computeVerticalScrollRange80){
                callback()
                recyclerView.setOnScrollListener(null)
            }
        }
    }

    private fun resetData() {
        list.clear()
        map.clear()
        lastVisible = null
    }

    private fun addDataToRecycler() {
        currentTask = getTripleDataTask(
                getColumns()
                        .whereEqualTo("approval", true) // 승인된 컬럼만 보임
                        .orderBy("writeDate", Query.Direction.DESCENDING)
                        .let { query -> lastVisible?.let { query.startAfter(it) } ?: query }    // 쿼리 커서 시작 위치 지정
                        .limit(PageSize.column.value)   // 페이지 단위
        ).let { it?:return }
                .addOnSuccessListener {
                    list.addAll(it.first)
                    map.putAll(it.second)
                    recyclerView.adapter.notifyDataSetChanged()
                    recyclerView.setOnScrollListener(onScrollListener{addDataToRecycler()})
                }
                .addOnCompleteListener { currentTask = null }
    }

    private fun getTripleDataTask(query : Query) : Task<Triple<List<ColumnInfo>, Map<String, HospitalInfo>, DocumentSnapshot?>>? {
        var infos : List<ColumnInfo> = listOf()
        return query.get()
                .continueWith { it ->
                    it.result.documents.let { if(it.isNotEmpty()) lastVisible = it[it.size - 1] else isListEmpty = true }
                    it.result.toObjects(ColumnInfo::class.java) }
                .continueWithTask { it ->
                    infos = it.result
                    it.result.asSequence().groupBy { it.writerUid }
                            .map { getHospital(it.key).get() }.toList()
                            .let { Tasks.whenAllSuccess<DocumentSnapshot>(it) }
                }
                .continueWith { it ->
                    Triple(infos, it.result
                            .asSequence()
                            .filterNotNull()
                            .map { it.id to it.toObject(HospitalInfo::class.java)!!}
                            .toList()
                            .toMap(), lastVisible)
                }

    }

}
