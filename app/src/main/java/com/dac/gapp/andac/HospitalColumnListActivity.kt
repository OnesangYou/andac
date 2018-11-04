package com.dac.gapp.andac

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.dac.gapp.andac.adapter.ColumnRecyclerAdapter
import com.dac.gapp.andac.base.BaseActivity
import com.dac.gapp.andac.enums.PageSize
import com.dac.gapp.andac.enums.RequestCode
import com.dac.gapp.andac.model.firebase.ColumnInfo
import com.dac.gapp.andac.model.firebase.HospitalInfo
import com.dac.gapp.andac.util.OnItemClickListener
import com.dac.gapp.andac.util.UiUtil
import com.dac.gapp.andac.util.addOnItemClickListener
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_my_column_list.*
import org.jetbrains.anko.startActivityForResult

@Suppress("DEPRECATION")
class HospitalColumnListActivity : BaseActivity() {

    val list = mutableListOf<ColumnInfo>()
    val map = mutableMapOf<String, HospitalInfo>()
    private var lastVisible : DocumentSnapshot? = null
    var currentTask : Task<*>? = null  // task 진행 유무 판단용
    var isListEmpty = false // 리스트가 없는지 확인

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_column_list)

        setActionBarLeftImage(R.drawable.back)
        setActionBarCenterText("내 칼럼 게시판 보기")
        setActionBarRightText("칼럼 작성")
        setOnActionBarLeftClickListener(View.OnClickListener { finish() })
        setOnActionBarRightClickListener(View.OnClickListener { startActivityForResult<ColumnWriteActivity>(RequestCode.OBJECT_ADD.value) })

        resetData()

        // recyclerView
        recyclerView.layoutManager = GridLayoutManager(this@HospitalColumnListActivity,2)
        recyclerView.adapter = ColumnRecyclerAdapter(this@HospitalColumnListActivity, list, map)
        recyclerView.addOnItemClickListener(object: OnItemClickListener {
            override fun onItemClicked(position: Int, view: View) {
                // 수정하기
                if(list[position].writerUid == getUid()) startActivityForResult<ColumnWriteActivity>(RequestCode.OBJECT_ADD.value, OBJECT_KEY to list[position].objectId)
            }
        })

        setAdapter()
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
        currentTask = getTripleDataTask(getHospitalColumns().let { it?:return }
                .orderBy("createdDate", Query.Direction.DESCENDING)
                .let { query ->
                    lastVisible?.let { query.startAfter(it) } ?: query
                }    // 쿼리 커서 시작 위치 지정
                .limit(PageSize.column.value)).let { it?:return }.addOnSuccessListener { triple ->
            list.addAll(triple.first.sortedBy { it.approval })
            map.putAll(triple.second)
            recyclerView.adapter.notifyDataSetChanged()
            recyclerView.setOnScrollListener(onScrollListener{addDataToRecycler()})
        }.addOnCompleteListener { currentTask = null }

    }

    private fun getTripleDataTask(query : Query) : Task<Triple<List<ColumnInfo>, Map<String, HospitalInfo>, DocumentSnapshot?>>? {
        var infos : List<ColumnInfo> = listOf()
        return query.get()
                .continueWithTask { it ->
                    it.result.documents.let { if(it.isNotEmpty()) lastVisible = it[it.size - 1] else isListEmpty = true }
                    it.result.map { getColumn(it.id)?.get() }
                            .let { Tasks.whenAllSuccess<DocumentSnapshot>(it) }
                }
                .continueWith { it -> infos = it.result.mapNotNull { it.toObject(ColumnInfo::class.java) } }
                .continueWithTask { getHospital()?.get() }
                .continueWith {
                    it.result.toObject(HospitalInfo::class.java)?.let{hospitalInfo ->
                        val uid = getUid()?: error("세션을 가져오지 못했습니다")
                        Triple(infos,  mapOf( uid to hospitalInfo ), lastVisible)
                    }
                }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == RequestCode.OBJECT_ADD.value && resultCode == Activity.RESULT_OK){
            setAdapter()
        }
    }
}
