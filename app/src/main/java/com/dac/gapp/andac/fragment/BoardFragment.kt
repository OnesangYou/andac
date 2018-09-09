package com.dac.gapp.andac.fragment


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.SCROLL_STATE_SETTLING
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dac.gapp.andac.BoardDetailActivity
import com.dac.gapp.andac.BoardWriteActivity
import com.dac.gapp.andac.MyPageActivity
import com.dac.gapp.andac.R
import com.dac.gapp.andac.adapter.BoardRecyclerAdapter
import com.dac.gapp.andac.base.BaseFragment
import com.dac.gapp.andac.enums.PageSize
import com.dac.gapp.andac.enums.RequestCode
import com.dac.gapp.andac.model.ActivityResultEvent
import com.dac.gapp.andac.model.firebase.BoardInfo
import com.dac.gapp.andac.model.firebase.HospitalInfo
import com.dac.gapp.andac.model.firebase.UserInfo
import com.dac.gapp.andac.util.MyToast
import com.dac.gapp.andac.util.RxBus
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

    var type : String? = null
    private var lastVisible : DocumentSnapshot? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_board, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        prepareUi()
        context?.apply {
            if(isUser()) fabWriteBoard.setOnClickListener { _ ->
                getCurrentUser()?.let{
                    activity?.startActivityForResult(Intent(context, BoardWriteActivity::class.java), RequestCode.OBJECT_ADD.value)
//                    startActivity(Intent(context, BoardWriteActivity::class.java))
                }
                ?:goToLogin()
            }
            else {
                fabWriteBoard.visibility = View.INVISIBLE
            }
        }

        resetData()

        // set recyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.swapAdapter(BoardRecyclerAdapter(context, list, map, hospitalInfoMap) { boardInfo, userInfo ->
            // 로그인 상태 체크
            getCurrentUser()?: return@BoardRecyclerAdapter goToLogin(true)
            context?.startActivity(Intent(context, BoardDetailActivity::class.java).putExtra(context?.OBJECT_KEY, boardInfo.objectId))
        }, false)


        // RxBus Listen
        RxBus.listen(ActivityResultEvent::class.java).subscribe { activityResultEvent ->
            activityResultEvent?.apply {
                if(requestCode == RequestCode.OBJECT_ADD.value && resultCode == Activity.RESULT_OK){
                    type?.let{setAdapter(it)}
                }
            }
        }

        // set tabLayout click listener
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when(tab.text){
                    getString(R.string.free_board) -> setAdapter(getString(R.string.free_board))
                    getString(R.string.review_board) -> setAdapter(getString(R.string.review_board))
                    getString(R.string.question_board) -> setAdapter(getString(R.string.question_board))
                    getString(R.string.hot_board) -> setAdapter(getString(R.string.hot_board))
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
            }
        })

        // default
        setAdapter(getString(R.string.free_board))

    }

    private fun prepareUi() {
        context?.let { context ->
            context.setActionBarLeftImage(R.drawable.mypage)
            context.setActionBarCenterImage(R.drawable.andac_font)
            context.setActionBarRightImage(R.drawable.bell)
            context.setOnActionBarLeftClickListener(View.OnClickListener {
                // 로그인 상태 체크
                if (getCurrentUser() == null) {
                    goToLogin(true)
                } else {
                    startActivity(Intent(context, MyPageActivity::class.java))
                }
            })
            context.setOnActionBarRightClickListener(View.OnClickListener {
                MyToast.showShort(context, "TODO: 알림 설정")
            })
        }
    }

    private fun setAdapter(type : String = getString(R.string.free_board)) {
        this.type = type

        // reset data
        resetData()

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

    fun resetData() {
        list.clear()
        map.clear()
        hospitalInfoMap.clear()
        lastVisible = null
    }

    fun addDataToRecycler(type: String) {
        context?.apply {
            showProgressDialog()
            getTripleDataTask(
                    getBoards()
                            .whereEqualTo("type", type)
                            .orderBy("writeDate", Query.Direction.DESCENDING)
                            .let { query -> lastVisible?.let { query.startAfter(it) } ?: query }    // 쿼리 커서 시작 위치 지정
                            .limit(PageSize.board.value)   // 페이지 단위
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
                    }.continueWithTask { task ->
                                boardInfos = task.result
                                Tasks.whenAllSuccess<Void>(
                                        // set userInfoMap
                                        boardInfos.groupBy { it.writerUid }.filter { !it.key.isEmpty() }.mapNotNull {
                                            getUserInfo(it.key)?.continueWith { task-> it.key to task.result }
                                        }.let {
                                            Tasks.whenAllSuccess<Pair<String, UserInfo>>(it)
                                        }.continueWith {
                                            userInfoMap = it.result.toMap()
                                        }
                                        ,
                                        // set hospitalInfoMap
                                        boardInfos.groupBy { it.hospitalUid }.filter { !it.key.isEmpty() }.mapNotNull {
                                            getHospitalInfo(it.key)?.continueWith { task-> it.key to task.result }
                                        }.let {
                                            Tasks.whenAllSuccess<Pair<String, HospitalInfo>>(it)
                                        }.continueWith {
                                            hospitalInfoMap = it.result.toMap()
                                        }
                                )

                    }
                            .continueWith {
                                Triple(boardInfos,
                                        userInfoMap,
                                        hospitalInfoMap)
                            }
        }
    }

}

