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
import com.dac.gapp.andac.util.UiUtil
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
    var currentTask : Task<*>? = null  // task 진행 유무 판단용
    var isListEmpty = false // 리스트가 없는지 확인

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

    }

    override fun onStart() {
        super.onStart()
        // default
        setAdapter()
    }

    private fun setAdapter() {
        // reset data
        resetData()

        binding.recyclerView.adapter.notifyDataSetChanged()

        // add Data
        addDataToRecycler()

        // add event to recycler's last
        binding.recyclerView.setOnScrollListener(onScrollListener{addDataToRecycler()})
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
                binding.recyclerView.setOnScrollListener(null)
            }
        }
    }

    private fun resetData() {
        list.clear()
        map.clear()
        lastVisible = null
    }

    private fun addDataToRecycler() {
        currentTask = getTripleDataTask(getUserEvents().let { it?:return }.orderBy("createdDate", Query.Direction.DESCENDING)
                .let { query ->
                    lastVisible?.let { query.startAfter(it) } ?: query
                }.limit(PageSize.event.value)).let { it?:return }.addOnSuccessListener {
            list.addAll(it.first)
            map.putAll(it.second)
            binding.recyclerView.adapter.notifyDataSetChanged()
        }.addOnCompleteListener { currentTask = null }

    }

    private fun getTripleDataTask(query : Query) : Task<Triple<List<EventInfo>, Map<String, HospitalInfo>, DocumentSnapshot?>>? {
        var infoList : List<EventInfo> = listOf()
        return query.get()
                .continueWithTask { it ->
                    it.result.documents.let { if(it.isNotEmpty()) lastVisible = it[it.size - 1] else isListEmpty = true }
                    it.result.map { getEvent(it.id)?.get() }
                            .let { Tasks.whenAllSuccess<DocumentSnapshot>(it) }
                }
                .continueWith { it -> it.result.mapNotNull { it.toObject(EventInfo::class.java) } }
                .continueWithTask { it ->
                    infoList = it.result
                    infoList.asSequence().groupBy { it.writerUid }
                            .filter { !it.key.isEmpty() }
                            .mapNotNull { getHospital(it.key).get() }.toList()
                            .let { Tasks.whenAllSuccess<DocumentSnapshot>(it) }
                }
                .continueWith { it ->
                    Triple(infoList, it.result.asSequence().filterNotNull()
                            .map { it.id to it.toObject(HospitalInfo::class.java)!!}.toList()
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
