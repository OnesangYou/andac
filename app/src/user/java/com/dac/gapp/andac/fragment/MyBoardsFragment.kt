package com.dac.gapp.andac.fragment

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dac.gapp.andac.MyPageActivity
import com.dac.gapp.andac.R
import com.dac.gapp.andac.adapter.BoardRecyclerAdapter
import com.dac.gapp.andac.base.BaseFragment
import com.dac.gapp.andac.enums.PageSize
import com.dac.gapp.andac.model.firebase.BoardInfo
import com.dac.gapp.andac.model.firebase.HospitalInfo
import com.dac.gapp.andac.model.firebase.UserInfo
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.user.fragment_my_boards.*


@Suppress("DEPRECATION")
class MyBoardsFragment : BaseFragment() {
    val list = mutableListOf<BoardInfo>()
    val map = mutableMapOf<String, UserInfo>()
    val hospitalInfoMap = mutableMapOf<String, HospitalInfo>()
    private var lastVisible : DocumentSnapshot? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_boards, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        resetData()

        // set recyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = BoardRecyclerAdapter(context, list, map, hospitalInfoMap)
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
        hospitalInfoMap.clear()
        lastVisible = null
    }

    fun addDataToRecycler() {
        (context as MyPageActivity).apply {
            getUserBoards()
                    ?.orderBy("createdDate", Query.Direction.DESCENDING)
                    ?.let { query -> lastVisible?.let { query.startAfter(it) } ?: query }   // 쿼리 커서 시작 위치 지정
                    ?.limit(PageSize.board.value)   // 페이지 단위
                    ?.let { getTripleDataTask(it) }
                    ?.addOnSuccessListener {
                        list.addAll(it.first)
                        map.putAll(it.second)
                        hospitalInfoMap.putAll(it.third)
                        recyclerView?.adapter?.notifyDataSetChanged()
                    }
        }
    }

    private fun getTripleDataTask(query : Query) : Task<Triple<List<BoardInfo>, Map<String, UserInfo>, Map<String, HospitalInfo>>>
    {
         return (context as MyPageActivity).run {

            var boardInfos = listOf<BoardInfo>()
            var userInfoMap = mapOf<String, UserInfo>()
            var hospitalInfoMap = mapOf<String, HospitalInfo>()

            query.get().continueWithTask { task ->
                lastVisible = task.result.documents.let { it[it.size-1] }

                task.result.map{ getBoard(it.id)?.get() }
                        .let { Tasks.whenAllSuccess<DocumentSnapshot>(it) }
            }.continueWithTask { task ->
                boardInfos = task.result.asSequence().filterNotNull().map { it.toObject(BoardInfo::class.java)!! }.toList()
                val uid = getUid()?:return@continueWithTask throw IllegalStateException()
                userInfoMap = mapOf(uid to userInfo!!)

                // set boardInfos
                boardInfos.groupBy { it.hospitalUid }.filter { !it.key.isEmpty() }.mapNotNull {
                    getHospitalInfo(it.key)?.continueWith { task-> it.key to task.result }
                }.let {
                    Tasks.whenAllSuccess<Pair<String, HospitalInfo>>(it)
                }.addOnSuccessListener { hospitalInfoMap = it.toMap() }

            }.continueWith {
                Triple(boardInfos, userInfoMap, hospitalInfoMap)
            }
        }
    }

}
