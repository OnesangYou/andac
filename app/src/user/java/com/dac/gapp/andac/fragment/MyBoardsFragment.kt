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
import com.dac.gapp.andac.model.firebase.BoardInfo
import com.dac.gapp.andac.model.firebase.UserInfo
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.user.fragment_my_boards.*


class MyBoardsFragment : BaseFragment() {
    val list = mutableListOf<BoardInfo>()
    val map = mutableMapOf<String, UserInfo>()
    private var lastVisible : DocumentSnapshot? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_boards, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // set recyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = BoardRecyclerAdapter(context, list, map)
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
        (context as MyPageActivity).apply {
            getUserBoards()
                    ?.orderBy("createdDate", Query.Direction.DESCENDING)
                    ?.let { query -> lastVisible?.let { query.startAfter(it) } ?: query }   // 쿼리 커서 시작 위치 지정
                    ?.limit(PageListSize)   // 페이지 단위
                    ?.let { getTripleDataTask(it) }
                    ?.addOnSuccessListener {
                        list.addAll(it.first)
                        map.putAll(it.second)
                        lastVisible = it.third
                        recyclerView.adapter.notifyDataSetChanged()
                    }
        }
    }

    private fun getTripleDataTask(query : Query) : Task<Triple<List<BoardInfo>, Map<String, UserInfo>, DocumentSnapshot?>>
    {
        return (context as MyPageActivity).run {
            var lastVisible: DocumentSnapshot? = null

            query.get().continueWithTask {
                lastVisible = it.result.documents.let { it[it.size-1] }
                it.result.map{ getBoard(it.id)?.get() }
                        .let { Tasks.whenAllSuccess<DocumentSnapshot>(it) }
            }.continueWith {
                val boardInfos = it.result.filter { it != null }.map { it.toObject(BoardInfo::class.java)!! }
                val userInfoMap = mapOf(getUid().toString() to userInfo!!)
                Triple(boardInfos, userInfoMap, lastVisible)
            }

        }
    }

}
