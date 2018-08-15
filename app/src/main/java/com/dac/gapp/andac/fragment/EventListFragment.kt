package com.dac.gapp.andac.fragment


import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dac.gapp.andac.EventDetailActivity
import com.dac.gapp.andac.R
import com.dac.gapp.andac.adapter.EventRecyclerAdapter
import com.dac.gapp.andac.base.BaseFragment
import com.dac.gapp.andac.model.firebase.EventInfo
import com.dac.gapp.andac.model.firebase.HospitalInfo
import com.dac.gapp.andac.util.OnItemClickListener
import com.dac.gapp.andac.util.addOnItemClickListener
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.fragment_event_list.*


@Suppress("DEPRECATION")
/**
 * A simple [Fragment] subclass.
 */
class EventListFragment : BaseFragment() {

    val list = mutableListOf<EventInfo>()
    val map = mutableMapOf<String, HospitalInfo>()
    private var lastVisible : DocumentSnapshot? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_event_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // set recyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = EventRecyclerAdapter(context, list, map)
        recyclerView.addOnItemClickListener(object: OnItemClickListener {
            override fun onItemClicked(position: Int, view: View) {
                // 디테일 뷰
                startActivity(Intent(context, EventDetailActivity::class.java).putExtra(context?.OBJECT_KEY,list[position].objectId))
            }
        })

    }

    override fun onStart() {
        super.onStart()
        // set boardTabGroup
        boardTabGroup.apply {
            // default
            if(checkedRadioButtonId == -1) {
                check(R.id.popular_order)
                setAdapter()
            }

            // set Listener
            setOnCheckedChangeListener{ _ : Any?, checkedId : Int ->
                when(checkedId) {
                    R.id.popular_order     -> setAdapter(getString(R.string.buy_count))
                    R.id.low_price_order   -> setAdapter(getString(R.string.price), Query.Direction.ASCENDING)
                    R.id.high_price_order -> setAdapter(getString(R.string.price), Query.Direction.DESCENDING)
                    R.id.distance_order      -> setAdapter(getString(R.string.distance))  // TODO : 병원의 거리 순으로 변경해야함
                }
            }
        }
    }

    private fun setAdapter(type : String = getString(R.string.buy_count), direction : Query.Direction = Query.Direction.DESCENDING) {

        // reset data
        list.clear()
        map.clear()
        lastVisible = null
        recyclerView.adapter.notifyDataSetChanged()

        // add Data
        addDataToRecycler(type, direction)

        // add event to recycler's last
        recyclerView.setOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(rv: RecyclerView?, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_SETTLING && !recyclerView.canScrollVertically(1)) {
                    addDataToRecycler(type, direction)
                }
            }
        })
    }

    fun addDataToRecycler(type: String, direction : Query.Direction = Query.Direction.DESCENDING) {
        context?.apply {
            showProgressDialog()
            getTripleDataTask(
                    getEvents()
                            .orderBy(type, direction)
                            .let { query -> lastVisible?.let { query.startAfter(it) } ?: query }    // 쿼리 커서 시작 위치 지정
                            .limit(PageListSize)   // 페이지 단위
            )
                    ?.addOnSuccessListener {
                        list.addAll(it.first)
                        map.putAll(it.second)
                        recyclerView.adapter.notifyDataSetChanged()
                    }
                    ?.addOnCompleteListener { hideProgressDialog() }
        }
    }

    private fun getTripleDataTask(query : Query) : Task<Triple<List<EventInfo>, Map<String, HospitalInfo>, DocumentSnapshot?>>? {
        return context?.run {
            var infos : List<EventInfo> = listOf()
            query.get()
                    .continueWith { it ->
                        lastVisible = it.result.documents.let { it[it.size-1] }
                        it.result.toObjects(EventInfo::class.java)
                    }.continueWithTask { it ->
                        infos = it.result
                        infos.groupBy { it.writerUid }
                                .map { getHospital(it.key).get() }
                                .let { Tasks.whenAllSuccess<DocumentSnapshot>(it) }
                    }.continueWith { it ->
                        Triple(infos, it.result.filterNotNull()
                                .map { it.id to it.toObject(HospitalInfo::class.java)!!}
                                .toMap(), lastVisible)
                    }
        }
    }
}// Required empty public constructor
