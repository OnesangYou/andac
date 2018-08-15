package com.dac.gapp.andac.fragment


import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.SCROLL_STATE_SETTLING
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dac.gapp.andac.BoardWriteActivity
import com.dac.gapp.andac.R
import com.dac.gapp.andac.adapter.BoardRecyclerAdapter
import com.dac.gapp.andac.base.BaseFragment
import com.dac.gapp.andac.model.firebase.BoardInfo
import com.dac.gapp.andac.model.firebase.HospitalInfo
import com.dac.gapp.andac.model.firebase.UserInfo
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.fragment_board.*


@Suppress("DEPRECATION")
class BoardFragment : BaseFragment() {

    val list = mutableListOf<BoardInfo>()
    val map = mutableMapOf<String, UserInfo>()
    val hospitalInfoMap = mutableMapOf<String, HospitalInfo>()
    private var lastVisible : DocumentSnapshot? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_board, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        context?.apply {
            if(isUser()) fabWriteBoard.setOnClickListener { _ ->
                getCurrentUser()?.let{ startActivity(Intent(context, BoardWriteActivity::class.java)) }
                ?:goToLogin()
            }
            else {
                fabWriteBoard.visibility = View.INVISIBLE
            }
        }

        // set recyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = BoardRecyclerAdapter(context, list, map, hospitalInfoMap)

    }

    override fun onStart() {
        super.onStart()
        // set boardTabGroup
        boardTabGroup.apply {
            // default
            if(checkedRadioButtonId == -1) {
                check(R.id.free_board)
                setAdapter()
            }

            setOnCheckedChangeListener{ _ : Any?, checkedId : Int ->
                when(checkedId) {
                    R.id.free_board     -> setAdapter(getString(R.string.free_board))
                    R.id.review_board   -> setAdapter(getString(R.string.review_board))
                    R.id.question_board -> setAdapter(getString(R.string.question_board))
                    R.id.hot_board      -> setAdapter(getString(R.string.hot_board))
                }
            }
        }
    }

    private fun setAdapter(type : String = getString(R.string.free_board)) {

        // reset data
        list.clear()
        map.clear()
        hospitalInfoMap.clear()
        lastVisible = null
        recyclerView.adapter.notifyDataSetChanged()

        // add Data
        addDataToRecycler(type)

        // add event to recycler's last
        recyclerView.setOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(rv: RecyclerView?, newState: Int) {
                if (newState == SCROLL_STATE_SETTLING && !recyclerView.canScrollVertically(1)) {
                    addDataToRecycler(type)
                }
            }
        })
    }

    fun addDataToRecycler(type: String) {
        context?.apply {
            showProgressDialog()
            getTripleDataTask(
                    getBoards()
                            .whereEqualTo("type", type)
                            .orderBy("writeDate", Query.Direction.DESCENDING)
                            .let { query -> lastVisible?.let { query.startAfter(it) } ?: query }    // 쿼리 커서 시작 위치 지정
                            .limit(PageListSize)   // 페이지 단위
            )
                    ?.addOnSuccessListener {
                        list.addAll(it.first)
                        map.putAll(it.second)
                        hospitalInfoMap.putAll(it.third)
                        recyclerView.adapter.notifyDataSetChanged()
                    }
                    ?.addOnCompleteListener { hideProgressDialog() }
        }
    }

    private fun getTripleDataTask(query : Query) : Task<Triple<List<BoardInfo>, Map<String, UserInfo>, Map<String, HospitalInfo>>>? {

        var boardInfos = listOf<BoardInfo>()
        var userInfoMap = mapOf<String, UserInfo>()
        var hospitalInfoMap = mapOf<String, HospitalInfo>()

        return context?.run {
                    query.get()
                    .continueWith { it ->
                        lastVisible = it.result.documents.let { it[it.size-1] }
                        it.result.toObjects(BoardInfo::class.java)
                    }.continueWithTask { it ->
                                boardInfos = it.result

                                Tasks.whenAllComplete(
                                        // set userInfoMap
                                        boardInfos.mapNotNull {
                                            getUserInfo(it.writerUid)?.continueWith { task-> it.writerUid to task.result }
                                        }.let {
                                            Tasks.whenAllSuccess<Pair<String, UserInfo>>(it)
                                        }.addOnSuccessListener { userInfoMap = it.toMap() }
                                                ,
                                        // set hospitalInfoMap
                                        boardInfos.mapNotNull {
                                            getHospitalInfo(it.hospitalUid)?.continueWith { task-> it.hospitalUid to task.result }
                                        }.let {
                                            Tasks.whenAllSuccess<Pair<String, HospitalInfo>>(it)
                                        }.addOnSuccessListener { hospitalInfoMap = it.toMap() }
                                )

                    }.continueWith {
                                Triple(boardInfos,
                                        userInfoMap,
                                        hospitalInfoMap)
                    }
        }
    }
}

