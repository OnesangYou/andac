package com.dac.gapp.andac

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.bumptech.glide.Glide
import com.dac.gapp.andac.adapter.EventApplyRecyclerviewAdapter
import com.dac.gapp.andac.base.BaseActivity
import com.dac.gapp.andac.databinding.ActivityHospitalEventApplicantListBinding
import com.dac.gapp.andac.enums.PageSize
import com.dac.gapp.andac.enums.RequestCode
import com.dac.gapp.andac.extension.setPrice
import com.dac.gapp.andac.model.firebase.EventApplyInfo
import com.dac.gapp.andac.model.firebase.EventInfo
import com.dac.gapp.andac.util.UiUtil
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.event_row.*
import org.jetbrains.anko.startActivityForResult

@Suppress("DEPRECATION")
class HospitalEventApplicantListActivity : BaseActivity() {

    val list = mutableListOf<EventApplyInfo>()
    private var lastVisible : DocumentSnapshot? = null
    private lateinit var eventKey : String
    lateinit var binding : ActivityHospitalEventApplicantListBinding
    var currentTask : Task<*>? = null  // task 진행 유무 판단용
    var isListEmpty = false // 리스트가 없는지 확인

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hospital_event_applicant_list)
        binding = getBinding()
        setActionBarLeftImage(R.drawable.back)
        setActionBarCenterText("내 이벤트 보기")
        setActionBarRightText("이벤트 수정  ")
        setOnActionBarLeftClickListener(View.OnClickListener { finish() })


        // 수정 시 데이터 받아서 초기화
        intent.getStringExtra(OBJECT_KEY)?.let{ key ->
            eventKey = key
            showProgressDialog()
            getEvent(key)?.get()?.continueWith { it.result.toObject(EventInfo::class.java) }?.addOnSuccessListener { eventInfo ->
                eventInfo?:return@addOnSuccessListener
                Glide.with(this@HospitalEventApplicantListActivity).load(eventInfo.pictureUrl).into(event_image)
                event_title.text = eventInfo.title
                body.text = eventInfo.body
                deal_kind.text = eventInfo.deal_kind
                price.setPrice(eventInfo.price)
                likeCountText.text = eventInfo.likeCount.toString()
                // sub_title(병원명)
                getHospitalInfo(eventInfo.writerUid)?.addOnSuccessListener { info -> info?.let { sub_title.text = it.name } }
            }
                    ?.addOnCompleteListener { hideProgressDialog() }

            setOnActionBarRightClickListener(View.OnClickListener { startActivityForResult<EventWriteActivity>(RequestCode.OBJECT_ADD.value, OBJECT_KEY to key)  })

            // recyclerView
            binding.recyclerView.layoutManager = LinearLayoutManager(this@HospitalEventApplicantListActivity)
            binding.recyclerView.adapter = EventApplyRecyclerviewAdapter(list)

        }
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
        lastVisible = null
        isListEmpty = false
    }

    private fun addDataToRecycler() {
        currentTask = getEventApplicants(eventKey)
                ?.orderBy("writeDate", Query.Direction.DESCENDING)
                .let { query ->
                    lastVisible?.let { query?.startAfter(it) } ?: query
                }    // 쿼리 커서 시작 위치 지정
                ?.limit(PageSize.event.value)  // 페이지 단위
                ?.let { it -> getTripleDataTask(it)}
                ?.addOnSuccessListener {
                    list.addAll(it)
                    binding.recyclerView.adapter.notifyDataSetChanged()
                    binding.recyclerView.setOnScrollListener(onScrollListener{addDataToRecycler()})
                }
                ?.addOnCompleteListener { currentTask = null }

    }

    private fun getTripleDataTask(query : Query) : Task<List<EventApplyInfo>> {
        return query.get()
                .continueWith { it ->
                    it.result.documents.let { if(it.isNotEmpty()) lastVisible = it[it.size - 1] else isListEmpty = true }
                    it.result.toObjects(EventApplyInfo::class.java).map {eventApplyInfo ->
                        eventApplyInfo.apply { eventKey = intent.getStringExtra(OBJECT_KEY) }
                    }
                }
    }
}
