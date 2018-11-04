package com.dac.gapp.andac.fragment


import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dac.gapp.andac.EventDetailActivity
import com.dac.gapp.andac.EventTextSearchActivity
import com.dac.gapp.andac.MyPageActivity
import com.dac.gapp.andac.R
import com.dac.gapp.andac.adapter.EventRecyclerAdapter
import com.dac.gapp.andac.base.BaseFragment
import com.dac.gapp.andac.databinding.FragmentEventListBinding
import com.dac.gapp.andac.enums.PageSize
import com.dac.gapp.andac.model.firebase.EventInfo
import com.dac.gapp.andac.model.firebase.HospitalInfo
import com.dac.gapp.andac.util.OnItemClickListener
import com.dac.gapp.andac.util.addOnItemClickListener
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import org.jetbrains.anko.startActivity


@Suppress("DEPRECATION")
/**
 * A simple [Fragment] subclass.
 */
class EventListFragment : BaseFragment() {

    val list = mutableListOf<EventInfo>()
    val map = mutableMapOf<String, HospitalInfo?>()
    private var lastVisible: DocumentSnapshot? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflate(inflater, R.layout.fragment_event_list, container, false)
    }

    private lateinit var binding: FragmentEventListBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        prepareUi()
        lastVisible = null

        // set recyclerView
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.swapAdapter(EventRecyclerAdapter(context, list, map), false)

        binding.recyclerView.addOnItemClickListener(object : OnItemClickListener {
            override fun onItemClicked(position: Int, view: View) {
                // 디테일 뷰
                startActivity(Intent(context, EventDetailActivity::class.java).putExtra(context?.OBJECT_KEY, list[position].objectId))
            }
        })

        // set tabLayout click listener
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.text) {
                    getString(R.string.popular_order) -> setAdapter(getString(R.string.likeCount))
                    getString(R.string.low_price_order) -> setAdapter(getString(R.string.price), Query.Direction.ASCENDING)
                    getString(R.string.high_price_order) -> setAdapter(getString(R.string.price), Query.Direction.DESCENDING)

                    getString(R.string.lasik) -> setAdapter(tag = getString(R.string.lasik))
                    getString(R.string.insertLens) -> setAdapter(tag = getString(R.string.insertLens))
                    getString(R.string.cataract) -> setAdapter(tag = getString(R.string.cataract))
                    getString(R.string.presbyopia) -> setAdapter(tag = getString(R.string.presbyopia))
                    getString(R.string.eyeDisease) -> setAdapter(tag = getString(R.string.eyeDisease))
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
            }
        })

        // default
        setAdapter(getString(R.string.likeCount))
    }

    private fun prepareUi() {
        binding = getBinding()
        context?.let { context ->
            context.setActionBarLeftImage(R.drawable.mypage)
            context.setActionBarCenterImage(R.drawable.andac_font)
            context.setActionBarRightImage(R.drawable.finder)
            context.setOnActionBarLeftClickListener(View.OnClickListener {
                // 로그인 상태 체크
                if (getCurrentUser() == null) {
                    goToLogin(true)
                } else {
                    startActivity(Intent(context, MyPageActivity::class.java))
                }
            })
            context.setOnActionBarRightClickListener(View.OnClickListener {
                // 검색 기능
                context.startActivity<EventTextSearchActivity>()
            })
        }
    }

    private fun setAdapter(type: String = getString(R.string.likeCount), direction: Query.Direction = Query.Direction.DESCENDING, tag: String? = null) {

        // reset data
        lastVisible = null

        // add Data
        addDataToRecycler(type, direction, tag)

        // add event to recycler's last
        binding.recyclerView.setOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(rv: RecyclerView?, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_SETTLING && !binding.recyclerView.canScrollVertically(1)) {
                    addDataToRecycler(type, direction, tag)
                }
            }
        })
    }

    private fun resetData() {
        list.clear()
        map.clear()
    }

    fun addDataToRecycler(type: String, direction: Query.Direction = Query.Direction.DESCENDING, tag: String?) {
        var needClear = false
        context?.apply {
            showProgressDialog()
            getTripleDataTask(
                    getEvents()
                            .let {
                                // tag 조회면, tag 필더, 최신순
                                reference -> tag?.let { reference.whereEqualTo("tag", it).orderBy("writeDate", Query.Direction.DESCENDING) }
                                    ?:reference.orderBy(type, direction)
                            }
                            .let { query ->
                                lastVisible?.let { query.startAfter(it) } ?: let{needClear = true; query}
                            }    // 쿼리 커서 시작 위치 지정
                            .limit(PageSize.event.value)   // 페이지 단위
            )
                    ?.addOnSuccessListener {
                        if(needClear) resetData()
                        list.addAll(it.first)
                        map.putAll(it.second)
                        binding.recyclerView.adapter.notifyDataSetChanged()
                    }
                    ?.addOnCompleteListener { hideProgressDialog() }
                    ?.addOnFailureListener{
                        it.printStackTrace()
                    }
        }
    }

    private fun getTripleDataTask(query: Query): Task<Triple<List<EventInfo>, Map<String, HospitalInfo?>, DocumentSnapshot?>>? {
        return context?.run {
            var infos: List<EventInfo> = listOf()
            query.get()
                    .continueWith { it ->
                        it.result.documents.let { if(it.isNotEmpty()) lastVisible = it[it.size - 1] }
                        it.result.toObjects(EventInfo::class.java)
                    }.continueWithTask { it ->
                        infos = it.result
                        infos.asSequence().groupBy { it.writerUid }
                                .filter { !it.key.isEmpty() }
                                .mapNotNull { getHospital(it.key).get() }.toList()
                                .let { Tasks.whenAllSuccess<DocumentSnapshot>(it) }
                    }.continueWith { it ->
                        Triple(infos, it.result.asSequence().filterNotNull()
                                .map { it.id to it.toObject(HospitalInfo::class.java) }.toList()
                                .toMap(), lastVisible)
                    }
        }
    }
}// Required empty public constructor
