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
import com.dac.gapp.andac.*
import com.dac.gapp.andac.adapter.BoardRecyclerAdapter
import com.dac.gapp.andac.base.BaseFragment
import com.dac.gapp.andac.databinding.FragmentBoardBinding
import com.dac.gapp.andac.enums.PageSize
import com.dac.gapp.andac.enums.RequestCode
import com.dac.gapp.andac.model.ActivityResultEvent
import com.dac.gapp.andac.model.BoardAdapterData
import com.dac.gapp.andac.model.firebase.BoardInfo
import com.dac.gapp.andac.model.firebase.HospitalInfo
import com.dac.gapp.andac.model.firebase.UserInfo
import com.dac.gapp.andac.util.RxBus
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import org.jetbrains.anko.startActivity


@Suppress("DEPRECATION")
class BoardFragment : BaseFragment() {

    val list = mutableListOf<BoardInfo>()
    val map = mutableMapOf<String, UserInfo>()
    private val hospitalInfoMap = mutableMapOf<String, HospitalInfo>()
    private var likeSet = mutableSetOf<String>()


    var type: String? = null
    private var lastVisible: DocumentSnapshot? = null

    private lateinit var binding: FragmentBoardBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflate(inflater, R.layout.fragment_board, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        prepareUi()
        context?.apply {
            if (isUser()) binding.fabWriteBoard.setOnClickListener { _ ->
                afterCheckLoginDo { activity?.startActivityForResult(Intent(context, BoardWriteActivity::class.java), RequestCode.OBJECT_ADD.value) }
            }
            else {
                binding.fabWriteBoard.visibility = View.INVISIBLE
            }
        }

        lastVisible = null

        // set recyclerView
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.swapAdapter((BoardRecyclerAdapter(context, list, map, hospitalInfoMap, likeSet) { boardInfo, _ ->
            // 로그인 상태 체크
            context?.startActivity(Intent(context, BoardDetailActivity::class.java).putExtra(context?.OBJECT_KEY, boardInfo.objectId))
        }), false)


        // RxBus Listen
        RxBus.listen(ActivityResultEvent::class.java).subscribe { activityResultEvent ->
            activityResultEvent?.apply {
                if (requestCode == RequestCode.OBJECT_ADD.value && resultCode == Activity.RESULT_OK) {
                    type?.let { setAdapter(it) }
                }
            }
        }.apply { context?.disposables?.add(this) }

        // set tabLayout click listener
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.text) {
                    getString(R.string.free_board) -> setAdapter(getString(R.string.free_board))
                    getString(R.string.review_board) -> setAdapter(getString(R.string.review_board))
                    getString(R.string.question_board) -> setAdapter(getString(R.string.question_board))
                    getString(R.string.hot_board_s) -> setAdapter(getString(R.string.hot_board))
                    getString(R.string.certification_board) -> setAdapter(getString(R.string.certification_board))
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
            }
        })

        // default
        setAdapter(getString(R.string.hot_board))

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
                // 게시판 검색
                context.startActivity<BoardTextSearchActivity>()

            })
        }
    }

    private fun setAdapter(type: String = getString(R.string.hot_board)) {
        this.type = type

        // reset data
        lastVisible = null

        // add Data
        addDataToRecycler(type)

        // add event to recycler's last
        binding.recyclerView.setOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(rv: RecyclerView?, newState: Int) {
                if (newState == SCROLL_STATE_SETTLING && !binding.recyclerView.canScrollVertically(1)) {
                    addDataToRecycler(type)
                }
            }
        })
    }

    private fun resetData() {
        list.clear()
        map.clear()
        hospitalInfoMap.clear()
        likeSet.clear()
    }

    fun addDataToRecycler(type: String) {
        var needClear = false
        context?.apply {
            showProgressDialog()
            getTripleDataTask(
                    getBoards()
                            .let{boardRef->
                                if(type == getString(R.string.hot_board)){
                                    boardRef.orderBy("likeCount", Query.Direction.DESCENDING)
                                } else {
                                    boardRef.whereEqualTo("type", type)
                                            .orderBy("writeDate", Query.Direction.DESCENDING)
                                }
                            }
                            .let { query ->
                                lastVisible?.let { query.startAfter(it) } ?: let{needClear = true; query}
                            }    // 쿼리 커서 시작 위치 지정
                            .limit(PageSize.board.value)   // 페이지 단위
            )
                    ?.addOnSuccessListener {
                        if(needClear) resetData()

                        list.addAll(it.boardInfos)
                        map.putAll(it.userInfoMap)
                        hospitalInfoMap.putAll(it.hospitalInfoMap)
                        likeSet.addAll(it.likeSet)
                        binding.recyclerView.adapter.notifyDataSetChanged()

                    }
                    ?.addOnCompleteListener { hideProgressDialog() }
        }
    }

    private fun getTripleDataTask(query: Query): Task<BoardAdapterData>? {
//        context?:return null
        val data = BoardAdapterData()
        return context?.run {
            query.get()
                    .continueWith { it ->
                        it.result.documents.let { if(it.isNotEmpty()) lastVisible = it[it.size - 1] }
                        it.result.toObjects(BoardInfo::class.java)
                    }.continueWithTask { task ->
                        data.boardInfos = task.result
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
                                    getHospitalInfo(it.key)?.continueWith { task -> it.key to task.result.apply { this?.objectID = it.key } }
                                }.toList().let {
                                    Tasks.whenAllSuccess<Pair<String, HospitalInfo>>(it)
                                }.continueWith {
                                    data.hospitalInfoMap = it.result.toMap()
                                }
                                ,
                                // set likeSet
                                if(isUser() && FirebaseAuth.getInstance().currentUser != null){
                                    getUserLikeBoards()?.get()?.continueWith { task1 ->
                                        data.likeSet = task1.result.mapNotNull { it.id }.toSet()
                                    }
                                } else task.continueWith { data.likeSet = setOf() }
                        )

                    }
                    .continueWith {
                        BoardAdapterData(data.boardInfos,
                                data.userInfoMap,
                                data.hospitalInfoMap,
                                data.likeSet
                        )
                    }
        }
    }

}

