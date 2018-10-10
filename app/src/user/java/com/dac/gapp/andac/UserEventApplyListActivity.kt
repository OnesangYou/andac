package com.dac.gapp.andac

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.dac.gapp.andac.adapter.EventRecyclerAdapter
import com.dac.gapp.andac.base.BaseActivity
import com.dac.gapp.andac.databinding.ActivityUserEventApplyListBinding
import com.dac.gapp.andac.enums.PageSize
import com.dac.gapp.andac.enums.RequestCode
import com.dac.gapp.andac.model.firebase.EventInfo
import com.dac.gapp.andac.model.firebase.HospitalInfo
import com.dac.gapp.andac.util.OnItemClickListener
import com.dac.gapp.andac.util.addOnItemClickListener
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query

@Suppress("DEPRECATION")
class UserEventApplyListActivity : BaseActivity() {
    val list = mutableListOf<EventInfo>()
    val map = mutableMapOf<String, HospitalInfo>()
    private var lastVisible : DocumentSnapshot? = null

    lateinit var binding: ActivityUserEventApplyListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_event_apply_list)
        setActionBarLeftImage(R.drawable.back)
        setActionBarCenterText("내 이벤트 관리")
        hideActionBarRight()
        setOnActionBarLeftClickListener(View.OnClickListener { finish() })

        resetData()

        binding = getBinding()

        // recyclerView
        binding.recyclerView.layoutManager = LinearLayoutManager(this@UserEventApplyListActivity)
        binding.recyclerView.adapter = EventRecyclerAdapter(this@UserEventApplyListActivity, list, map)
        binding.recyclerView.addOnItemClickListener(object: OnItemClickListener {
            override fun onItemClicked(position: Int, view: View) {
                // 디테일 뷰
                startActivity(Intent(this@UserEventApplyListActivity, EventDetailActivity::class.java).putExtra(OBJECT_KEY,list[position].objectId))
            }
        })

        setAdapter()
    }


    private fun setAdapter() {
        // reset data
        resetData()

        binding.recyclerView.adapter.notifyDataSetChanged()

        // add Data
        addDataToRecycler()

        // add event to recycler's last
        binding.recyclerView.setOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(rv: RecyclerView?, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_SETTLING && !binding.recyclerView.canScrollVertically(1)) {
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
        getUserEvents()
                ?.orderBy("createdDate", Query.Direction.DESCENDING)
                .let { query ->
                    lastVisible?.let { query?.startAfter(it) } ?: query
                }    // 쿼리 커서 시작 위치 지정
                ?.limit(PageSize.event.value)  // 페이지 단위
                ?.let { it -> getTripleDataTask(it)}
                ?.addOnSuccessListener {
                    list.addAll(it.first)
                    map.putAll(it.second)
                    binding.recyclerView.adapter.notifyDataSetChanged()
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
                .continueWith { it -> it.result.mapNotNull { it.toObject(EventInfo::class.java) } }
                .continueWithTask { it ->
                    infos = it.result
                    infos.groupBy { it.writerUid }
                            .filter { !it.key.isEmpty() }
                            .mapNotNull { getHospital(it.key).get() }
                            .let { Tasks.whenAllSuccess<DocumentSnapshot>(it) }
                }
                .continueWith { it ->
                    Triple(infos, it.result.filterNotNull()
                            .map { it.id to it.toObject(HospitalInfo::class.java)!!}
                            .toMap(), lastVisible)
                }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == RequestCode.OBJECT_ADD.value && resultCode == Activity.RESULT_OK){
            setAdapter()
//            Handler().postDelayed({ setAdapter() }, 2000)   // 클라우드 펑션으로 생성에 딜레이가 있음, 2초 뒤에 실행
        }
    }

}
