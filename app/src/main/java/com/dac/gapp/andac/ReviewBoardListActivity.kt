package com.dac.gapp.andac

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.dac.gapp.andac.adapter.BoardRecyclerAdapter
import com.dac.gapp.andac.base.BaseActivity
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
import kotlinx.android.synthetic.main.activity_review_board_list.*

@Suppress("DEPRECATION")
class ReviewBoardListActivity : BaseActivity() {

    val list = mutableListOf<BoardInfo>()
    val map = mutableMapOf<String, UserInfo>()
    private val hospitalInfoMap = mutableMapOf<String, HospitalInfo>()
    private var likeSet = mutableSetOf<String>()
    private var lastVisible: DocumentSnapshot? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review_board_list)

        setActionBarLeftImage(R.drawable.back)
        setActionBarRightImage(R.drawable.defult_img)
        setOnActionBarLeftClickListener(View.OnClickListener { finish() })

        // 병원 전용이기 때문에 병원 ID가 있어야함
        val hospitalId = intent.getStringExtra(OBJECT_KEY)?:return

        // Get HospitalInfo
        getHospitalInfo(hospitalId)?.addOnSuccessListener { hospitalInfo ->
            setActionBarCenterText("${hospitalInfo?.name} 후기게시판 ")
        }

        // 후기 리스트
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.swapAdapter((BoardRecyclerAdapter(this, list, map, hospitalInfoMap, likeSet) { boardInfo, _ ->
            // 로그인 상태 체크
            startActivity(Intent(this, BoardDetailActivity::class.java).putExtra(OBJECT_KEY, boardInfo.objectId))
        }), false)


        // RxBus Listen
        RxBus.listen(ActivityResultEvent::class.java).subscribe { activityResultEvent ->
            activityResultEvent?.apply {
                if (requestCode == RequestCode.OBJECT_ADD.value && resultCode == Activity.RESULT_OK) {
                    setAdapter(hospitalId = hospitalId)
                }
            }
        }.apply { disposables.add(this) }


        setAdapter(hospitalId = hospitalId)

    }

    private fun setAdapter(type: String = getString(R.string.review_board), hospitalId : String) {

        // reset data
        resetData()

        // add Data
        addDataToRecycler(type, hospitalId)

        // add event to recycler's last
        recyclerView.setOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(rv: RecyclerView?, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_SETTLING && recyclerView.canScrollVertically(1)) {
                    addDataToRecycler(type, hospitalId)
                }
            }
        })
    }

    private fun resetData() {
        list.clear()
        map.clear()
        hospitalInfoMap.clear()
        likeSet.clear()
        lastVisible = null
    }

    fun addDataToRecycler(type: String, hospitalId : String) {
        getTripleDataTask(
                getBoards()
                        .whereEqualTo("type", type)
                        .whereEqualTo("hospitalUid", hospitalId)
                        .orderBy("likeCount", Query.Direction.DESCENDING)
                        .let { query ->
                            lastVisible?.let { query.startAfter(it) } ?: query
                        }    // 쿼리 커서 시작 위치 지정
                        .limit(PageSize.board.value)   // 페이지 단위
        )
                ?.addOnSuccessListener {
                    list.addAll(it.boardInfos)
                    map.putAll(it.userInfoMap)
                    hospitalInfoMap.putAll(it.hospitalInfoMap)
                    likeSet.addAll(it.likeSet)
                    recyclerView.adapter.notifyDataSetChanged()

                }
                ?.addOnFailureListener { it.printStackTrace() }
    }

    private fun getTripleDataTask(query: Query): Task<BoardAdapterData>? {
        val data = BoardAdapterData()
        return query.get()
                .continueWith { it ->
                    lastVisible = it.result.documents.let { it[it.size - 1] }
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
                                getHospitalInfo(it.key)?.continueWith { task -> it.key to task.result.apply{this?.objectID = it.key} }
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
