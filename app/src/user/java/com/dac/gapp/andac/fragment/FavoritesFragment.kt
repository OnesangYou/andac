package com.dac.gapp.andac.fragment

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dac.gapp.andac.MyPageActivity
import com.dac.gapp.andac.R
import com.dac.gapp.andac.adapter.BoardRecyclerAdapter
import com.dac.gapp.andac.base.BaseFragment
import com.dac.gapp.andac.model.firebase.BoardInfo
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
                    getString(R.string.hospital) -> {} // 병원
                    getString(R.string.event) -> {} // 이벤트
                    getString(R.string.board) -> setBoardRecyclerAdapter()  // 게시물
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
            }
        })

        // set recyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)

    }

    fun setBoardRecyclerAdapter() {
        (context as MyPageActivity).apply {
            showProgressDialog()
            getMyBoards()?.get()?.addOnSuccessListener {querySnapshot ->
                querySnapshot
                        ?.let { it.map { getBoard(it.id).get()} }
                        .let { Tasks.whenAllSuccess<DocumentSnapshot>(it) }
                        .addOnSuccessListener {
                            val boardInfos = it.filter { it != null }.map { it.toObject(BoardInfo::class.java)!! }
                            val userInfoMap = mapOf(getUid().toString() to userInfo)
                            recyclerView.adapter = BoardRecyclerAdapter(context, boardInfos, userInfoMap)
                            recyclerView.adapter.notifyDataSetChanged()
                        }
                        .addOnCompleteListener{hideProgressDialog()}
            }
        }
    }

}
