package com.dac.gapp.andac

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.dac.gapp.andac.adapter.EventRecyclerAdapter
import com.dac.gapp.andac.base.BaseActivity
import com.dac.gapp.andac.enums.RequestCode
import com.dac.gapp.andac.model.firebase.EventInfo
import com.dac.gapp.andac.model.firebase.HospitalInfo
import com.dac.gapp.andac.util.OnItemClickListener
import com.dac.gapp.andac.util.addOnItemClickListener
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.hospital.activity_hospital_event_list.*
import org.jetbrains.anko.startActivityForResult


@Suppress("DEPRECATION")
class HospitalEventListActivity : BaseActivity() {

    val list = mutableListOf<EventInfo>()
    val map = mutableMapOf<String, HospitalInfo>()
    private var lastVisible : DocumentSnapshot? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hospital_event_list)

        back.setOnClickListener { finish() }
        writeEventBtn.setOnClickListener { startActivityForResult<EventWriteActivity>(RequestCode.OBJECT_ADD.value) }

        // recyclerView
        recyclerView.layoutManager = LinearLayoutManager(this@HospitalEventListActivity)
        recyclerView.adapter = EventRecyclerAdapter(this@HospitalEventListActivity, list, map)
        recyclerView.addOnItemClickListener(object: OnItemClickListener {
            override fun onItemClicked(position: Int, view: View) {
                // 수정하기
                if(list[position].writerUid == getUid()) startActivityForResult<EventWriteActivity>(RequestCode.OBJECT_ADD.value, OBJECT_KEY to list[position].objectId)
            }
        })

        setAdapter()
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
        getHospitalEvents()
                ?.orderBy("createdDate", Query.Direction.DESCENDING)
                .let { query ->
                    lastVisible?.let { query?.startAfter(it) } ?: query
                }    // 쿼리 커서 시작 위치 지정
                ?.limit(PageListSize)  // 페이지 단위
                ?.let { it -> getTripleDataTask(it)}
                ?.addOnSuccessListener {
                    list.addAll(it.first)
                    map.putAll(it.second)
                    recyclerView.adapter.notifyDataSetChanged()
                }
                ?.addOnCompleteListener { hideProgressDialog() }

    }

    private fun getTripleDataTask(query : Query) : Task<Triple<List<EventInfo>, Map<String, HospitalInfo>, DocumentSnapshot?>>? {
        var infos : List<EventInfo> = listOf()
        return query.get()
                .continueWithTask { it ->
                    lastVisible = it.result.documents.let { it[it.size - 1] }
                    it.result.map { getEvent(it.id)?.get() }
                            .let { Tasks.whenAllSuccess<DocumentSnapshot>(it) }
                }
                .continueWith { it -> infos = it.result.mapNotNull { it.toObject(EventInfo::class.java) } }
                .continueWithTask { getHospital()?.get() }
                .continueWith {
                    it.result.toObject(HospitalInfo::class.java)?.let{ hospitalInfo ->
                        Triple(infos,  mapOf( getUid().toString() to hospitalInfo ), lastVisible)
                    }
                }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == RequestCode.OBJECT_ADD.value && resultCode == Activity.RESULT_OK){
            Handler().postDelayed({ setAdapter() }, 2000)   // 클라우드 펑션으로 생성에 딜레이가 있음, 2초 뒤에 실행
        }
    }

}
