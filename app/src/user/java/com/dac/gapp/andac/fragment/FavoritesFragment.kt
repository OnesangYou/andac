package com.dac.gapp.andac.fragment

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dac.gapp.andac.BoardDetailActivity
import com.dac.gapp.andac.EventDetailActivity
import com.dac.gapp.andac.R
import com.dac.gapp.andac.adapter.BoardRecyclerAdapter
import com.dac.gapp.andac.adapter.EventRecyclerAdapter
import com.dac.gapp.andac.adapter.SearchHospitalRecyclerViewAdapter
import com.dac.gapp.andac.base.BaseFragment
import com.dac.gapp.andac.model.BoardAdapterData
import com.dac.gapp.andac.model.firebase.BoardInfo
import com.dac.gapp.andac.model.firebase.EventInfo
import com.dac.gapp.andac.model.firebase.HospitalInfo
import com.dac.gapp.andac.model.firebase.UserInfo
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.android.synthetic.user.fragment_favorites.*


class FavoritesFragment : BaseFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favorites, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // set tabLayout click listener
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when(tab.text){
                    getString(R.string.hospital) -> setHospitalRecyclerAdapter() // 병원
                    getString(R.string.event) -> setEventRecyclerAdapter() // 이벤트
                    getString(R.string.board) -> setBoardRecyclerAdapter()  // 게시물
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
            }
        })

        // Default Menu
        setHospitalRecyclerAdapter()

    }

    private fun setHospitalRecyclerAdapter() {
        context?.apply {
            val ref = getLikeHospitals() ?: return
            addListenerRegistrations(ref.orderBy("createdDate").addSnapshotListener { snapshot, _ ->
                snapshot?:return@addSnapshotListener
                snapshot.mapNotNull { documentSnapshot -> getHospitalInfo(documentSnapshot.id)?.continueWith { it.result?.apply { objectID = documentSnapshot.id } }}
                        .let { Tasks.whenAllSuccess<HospitalInfo>(it) }
                        .addOnSuccessListener { hospitalInfos ->
                            val mHospitalList = hospitalInfos.map {
                                it to SearchHospitalRecyclerViewAdapter.VIEW_TYPE_CONTENT
                            }
                            recyclerView.removeAllViews()
                            recyclerView.layoutManager = LinearLayoutManager(this)
                            recyclerView.adapter = SearchHospitalRecyclerViewAdapter(context, mHospitalList)
                        }
            }, true)
        }

    }

    private fun setEventRecyclerAdapter() {
        context?.apply {
            val ref = getLikeEvents() ?: return
            addListenerRegistrations(ref.orderBy("createdDate").addSnapshotListener { snapshot, _ ->
                snapshot?:return@addSnapshotListener
                snapshot.mapNotNull { documentSnapshot -> getEvent(documentSnapshot.id)?.get()}
                        .let { Tasks.whenAllSuccess<DocumentSnapshot>(it) }
                        .addOnSuccessListener { list ->
                            val eventInfos = list.asSequence().mapNotNull { it.toObject(EventInfo::class.java)!! }.toList()

                            eventInfos.groupBy { it.writerUid }
                                    .filter { !it.key.isEmpty() }
                                    .mapNotNull { getHospital(it.key).get() }.toList()
                                    .let { Tasks.whenAllSuccess<DocumentSnapshot>(it) }
                                    .addOnSuccessListener { mutableList ->
                                        val hospitalMap = mutableList.filterNotNull().map{ it.id to it.toObject(HospitalInfo::class.java)!! }.toMap()
                                        recyclerView.removeAllViews()
                                        recyclerView.layoutManager = LinearLayoutManager(this)
                                        recyclerView.adapter = EventRecyclerAdapter(this, eventInfos, hospitalMap) {eventInfo, hospitalInfo ->
                                            startActivity(Intent(this@apply, EventDetailActivity::class.java).putExtra(this@apply.OBJECT_KEY, eventInfo.objectId))
                                        }
                                    }
                        }
            }, true)
        }

    }

    fun setBoardRecyclerAdapter() {
        val data = BoardAdapterData()
        context?.apply {
            val ref = getUserLikeBoards()?:return
            addListenerRegistrations(ref.orderBy("createdDate").addSnapshotListener { snapshot, _ ->
                snapshot?:return@addSnapshotListener
                snapshot.mapNotNull { documentSnapshot -> getBoard(documentSnapshot.id)?.get()}
                        .let { Tasks.whenAllSuccess<DocumentSnapshot>(it) }
                        .continueWithTask { task ->
                            data.boardInfos = task.result.mapNotNull { it.toObject(BoardInfo::class.java)!! }.toList()
                            Tasks.whenAllSuccess<Void>(
                                    // set userInfoMap
                                    data.boardInfos.asSequence().groupBy { it.writerUid }.filter { !it.key.isEmpty() }.mapNotNull {
                                        getUserInfo(it.key)?.continueWith { task -> it.key to task.result }
                                    }.toList().let {
                                        Tasks.whenAllSuccess<Pair<String, UserInfo>>(it)
                                    }.continueWith {
                                        data.userInfoMap = it.result.toMap()
                                    }
                                    ,
                                    // set hospitalInfoMap
                                    data.boardInfos.asSequence().groupBy { it.hospitalUid }.filter { !it.key.isEmpty() }.mapNotNull {
                                        getHospitalInfo(it.key)?.continueWith { task -> it.key to task.result }
                                    }.toList().let {
                                        Tasks.whenAllSuccess<Pair<String, HospitalInfo>>(it)
                                    }.continueWith {
                                        data.hospitalInfoMap = it.result.toMap()
                                    }
                                    ,
                                    // set likeSet
                                    getUserLikeBoards()?.get()
                                            ?.continueWith { task1 -> data.likeSet = task1.result.map { it.id }.toHashSet() }
                            )
                        }
                        .continueWith {
                            BoardAdapterData(data.boardInfos,
                                    data.userInfoMap,
                                    data.hospitalInfoMap,
                                    data.likeSet
                            )
                        }
                        .addOnSuccessListener {
                            recyclerView.removeAllViews()
                            recyclerView.layoutManager = LinearLayoutManager(this@apply)
                            recyclerView.adapter = BoardRecyclerAdapter(this@apply, it.boardInfos, it.userInfoMap, it.hospitalInfoMap, it.likeSet){boardInfo, userInfo ->
                                startActivity(Intent(this@apply, BoardDetailActivity::class.java).putExtra(this@apply.OBJECT_KEY, boardInfo.objectId))
                            }
                        }

            }, true)
        }
    }

}
