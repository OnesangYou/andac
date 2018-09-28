package com.dac.gapp.andac

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.bumptech.glide.Glide
import com.dac.gapp.andac.adapter.EventApplyRecyclerviewAdapter
import com.dac.gapp.andac.base.BaseActivity
import com.dac.gapp.andac.enums.PageSize
import com.dac.gapp.andac.enums.RequestCode
import com.dac.gapp.andac.extension.setPrice
import com.dac.gapp.andac.model.firebase.EventApplyInfo
import com.dac.gapp.andac.model.firebase.EventInfo
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.hospital.activity_hospital_event_applicant_list.*
import kotlinx.android.synthetic.main.event_row.*
import org.jetbrains.anko.startActivityForResult

@Suppress("DEPRECATION")
class HospitalEventApplicantListActivity : BaseActivity() {

    val list = mutableListOf<EventApplyInfo>()
    private var lastVisible : DocumentSnapshot? = null

    lateinit var eventKey : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hospital_event_applicant_list)
        setActionBarLeftImage(R.drawable.back)
        setActionBarCenterText("내 이벤트 보기")
        setActionBarRightText("이벤트 수정하기")
        setOnActionBarLeftClickListener(View.OnClickListener { finish() })


        // 수정 시 데이터 받아서 초기화
        intent.getStringExtra(OBJECT_KEY)?.let{ key ->
            eventKey = key
            showProgressDialog()
            getEvent(key)?.get()?.continueWith { it.result.toObject(EventInfo::class.java) }?.addOnSuccessListener { eventInfo ->
                eventInfo?.let { eventInfo ->
                Glide.with(this@HospitalEventApplicantListActivity).load(eventInfo.pictureUrl).into(event_image)
                event_title.text = eventInfo.title
                body.text = eventInfo.body
                deal_kind.text = eventInfo.deal_kind
                price.setPrice(eventInfo.price)
                likeCountText.text = eventInfo.likeCount.toString()
                // sub_title(병원명)
                getHospitalInfo(eventInfo.writerUid)?.addOnSuccessListener { info -> info?.let { sub_title.text = it.name } }
            }}
                    ?.addOnCompleteListener { hideProgressDialog() }

            setOnActionBarRightClickListener(View.OnClickListener { startActivityForResult<EventWriteActivity>(RequestCode.OBJECT_ADD.value, OBJECT_KEY to key)  })

            // recyclerView
            recyclerView.layoutManager = LinearLayoutManager(this@HospitalEventApplicantListActivity)
            recyclerView.adapter = EventApplyRecyclerviewAdapter(list)
//            recyclerView.addOnItemClickListener(object: OnItemClickListener {
//                override fun onItemClicked(position: Int, view: View) {
//                    // 이벤트 신청자 리스트
//                    if(list[position].writerUid == getUid()) startActivityForResult<HospitalEventApplicantListActivity>(RequestCode.OBJECT_ADD.value, OBJECT_KEY to list[position].objectId)
//                }
//            })

            setAdapter()

        }
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
        lastVisible = null
    }

    fun addDataToRecycler() {
        showProgressDialog()

        getEventApplicants(eventKey)
                ?.orderBy("writeDate", Query.Direction.DESCENDING)
                .let { query ->
                    lastVisible?.let { query?.startAfter(it) } ?: query
                }    // 쿼리 커서 시작 위치 지정
                ?.limit(PageSize.event.value)  // 페이지 단위
                ?.let { it -> getTripleDataTask(it)}
                ?.addOnSuccessListener {
                    list.addAll(it)
                    recyclerView.adapter.notifyDataSetChanged()
                }
                ?.addOnCompleteListener { hideProgressDialog() }

    }

    private fun getTripleDataTask(query : Query) : Task<List<EventApplyInfo>> {
        return query.get()
                .continueWith { it ->
                    lastVisible = it.result.documents.let { it[it.size - 1] }
                    it.result.toObjects(EventApplyInfo::class.java)
                }
    }
}
