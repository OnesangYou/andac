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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_column_list)

        setActionBarLeftImage(R.drawable.back)
        setActionBarCenterText("내 컬럼 게시판 보기")
        setActionBarRightText("컬럼 작성하기")
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

    private fun setAdapter() {
        // reset data
        resetData()

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

    fun resetData() {
        list.clear()
        map.clear()
        lastVisible = null
    }

    fun addDataToRecycler() {
        showProgressDialog()
        getHospitalColumns()
                ?.orderBy("createdDate", Query.Direction.DESCENDING)
                .let { query ->
                    lastVisible?.let { query?.startAfter(it) } ?: query
                }    // 쿼리 커서 시작 위치 지정
                ?.limit(PageSize.column.value)  // 페이지 단위
                ?.let { it -> getTripleDataTask(it)}
                ?.addOnSuccessListener {
                    list.addAll(it.first.sortedBy { it.approval })
                    map.putAll(it.second)
                    recyclerView.adapter.notifyDataSetChanged()
                }
                ?.addOnCompleteListener { hideProgressDialog() }

    }

    private fun getTripleDataTask(query : Query) : Task<Triple<List<ColumnInfo>, Map<String, HospitalInfo>, DocumentSnapshot?>>? {
        var infos : List<ColumnInfo> = listOf()
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
                        val uid = getUid()?:return@continueWith throw IllegalStateException()
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
