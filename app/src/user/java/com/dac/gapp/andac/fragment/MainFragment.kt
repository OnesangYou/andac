package com.dac.gapp.andac.fragment

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dac.gapp.andac.*
import com.dac.gapp.andac.adapter.AdPagerAdapter
import com.dac.gapp.andac.adapter.BoardListRecyclerViewAdapter
import com.dac.gapp.andac.adapter.ColumnRecyclerAdapter
import com.dac.gapp.andac.adapter.EventRecyclerAdapter
import com.dac.gapp.andac.base.BaseFragment
import com.dac.gapp.andac.databinding.FragmentMainBinding
import com.dac.gapp.andac.dialog.MainPopupDialog
import com.dac.gapp.andac.enums.Ad
import com.dac.gapp.andac.enums.AdCountType
import com.dac.gapp.andac.enums.Extra
import com.dac.gapp.andac.enums.PageSize
import com.dac.gapp.andac.extension.loadImageAny
import com.dac.gapp.andac.extension.random
import com.dac.gapp.andac.model.firebase.*
import com.dac.gapp.andac.util.OnItemClickListener
import com.dac.gapp.andac.util.addOnItemClickListener
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.activity_hospital.*
import org.jetbrains.anko.startActivity
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList


class MainFragment : BaseFragment() {

    companion object {
        const val IS_FIRST_MAIN_POPUP_AD = "isFirstMainPopupAd"
        const val DELAY_MS: Long = 500
        const val PERIOD_MS: Long = 3000

        private val mTextViewMoreEventsClickBehaviorSubject: BehaviorSubject<Int> = BehaviorSubject.create()
        fun observeTextViewClickEvent(): Observable<Int> {
            return mTextViewMoreEventsClickBehaviorSubject.hide()
        }
    }

    private lateinit var binding: FragmentMainBinding

    private var mIsMainPopupAdFirst: Boolean = true

    val list = mutableListOf<EventInfo>()
    val map = mutableMapOf<String, HospitalInfo>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflate(inflater, R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        prepareUi()
        setupEvents()
    }

    private fun prepareUi() {
        binding = getBinding()
        binding.imgviewTodaysHospitalAd.loadImageAny(R.drawable.main_todays_hospital_ad)
        context?.let { context ->
            context.setActionBarLeftImage(R.drawable.mypage)
            context.setActionBarRightImage(R.drawable.bell)
            context.setOnActionBarLeftClickListener(View.OnClickListener {
                context.afterCheckLoginDo { startActivity(Intent(context, MyPageActivity::class.java)) }
            })
            context.setOnActionBarRightClickListener(View.OnClickListener {
                //                MyToast.showShort(context, "TODO: 알림 설정")
            })
            context.getDb().collection(Ad.MAIN_BANNER.collectionName)
                    .whereEqualTo("showingUp", true)
                    .get()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful && task.result.size() > 0) {
                            val adInfoList = ArrayList<AdInfo>()
                            for (document in task.result) {
                                val adInfo = document.toObject(AdInfo::class.java)
                                Timber.d("photoUrl: ${adInfo.photoUrl}")
                                adInfoList.add(adInfo)
                            }
                            binding.viewPagerMainBannerAd.adapter = AdPagerAdapter(context, adInfoList)
                        }
                    }
                    .addOnFailureListener {
                        Timber.e("MAIN_BANNER 광고 로드 실패 : ${it.localizedMessage}")
                    }


            if (mIsMainPopupAdFirst) {
                mIsMainPopupAdFirst = false
                context.getDb().collection(Ad.MAIN_POPUP.collectionName)
                        .whereEqualTo("showingUp", true)
                        .get()
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful && task.result.size() > 0) {
                                val adInfoList = ArrayList<AdInfo>()
                                for (document in task.result) {
                                    val adInfo = document.toObject(AdInfo::class.java)
                                    Timber.d("photoUrl: ${adInfo.photoUrl}")
                                    adInfoList.add(adInfo)
                                }

                                val index = (0..adInfoList.lastIndex).random()
                                val dialog = MainPopupDialog(context)
                                val clickListener = View.OnClickListener {
                                    if (adInfoList[index].eventId.isNotEmpty()) {
                                        context.startActivity<EventDetailActivity>(Extra.OBJECT_KEY.name to adInfoList[index].eventId)
                                        context.addCountAdClick(adInfoList[index].hospitalId, AdCountType.POPUP)
                                    }
                                    dialog.dismiss()
                                }
                                dialog
                                        .setImage(if (adInfoList[index].photoUrl.isNotEmpty()) adInfoList[index].photoUrl else R.drawable.main_popup_ad)
                                        .setOnImageClickListener(clickListener)
                                        .setOnCancelListener(clickListener)
                                        .setOnConfirmListener(View.OnClickListener { dialog.dismiss() })
                                        .show()
                            } else {
                                val dialog = MainPopupDialog(requireContext())
                                dialog
                                        .setImage(R.drawable.main_popup_ad)
                                        .setOnCancelListener(View.OnClickListener { dialog.dismiss() })
                                        .setOnConfirmListener(View.OnClickListener { dialog.dismiss() })
                                        .show()

                            }
                        }
                        .addOnFailureListener {
                            Timber.e("MAIN_POPUP 광고 로드 실패 : ${it.localizedMessage}")
                        }
            }

            context.getDb().collection(Ad.MAIN_TODAY_HOSPITAL.collectionName)
                    .whereEqualTo("showingUp", true)
                    .get()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful && task.result.size() > 0) {
                            val adInfoList = ArrayList<AdInfo>()
                            for (document in task.result) {
                                val adInfo = document.toObject(AdInfo::class.java)
                                Timber.d("photoUrl: ${adInfo.photoUrl}")
                                adInfoList.add(adInfo)
                            }
                            val index = (0..adInfoList.lastIndex).random()
                            binding.imgviewTodaysHospitalAd.loadImageAny(adInfoList[index].photoUrl)
                            binding.imgviewTodaysHospitalAd.setOnClickListener {
                                adInfoList[index].hospitalId.let { hospitalId ->
                                    context.getHospitalInfo(hospitalId)?.addOnSuccessListener { hospitalInfo ->
                                        context.startActivity<HospitalActivity>(EXTRA_HOSPITAL_INFO to hospitalInfo?.apply { objectID = hospitalId })
                                        context.addCountAdClick(adInfoList[index].hospitalId, AdCountType.TODAY_HOSPITAL)
                                    }
                                }
                            }
                        }
                    }

            binding.recyclerviewColumn?.apply {
                layoutManager = object : GridLayoutManager(context, 2) {
                    override fun canScrollVertically(): Boolean {
                        return false
                    }
                }
                adapter = ColumnRecyclerAdapter(context, arrayListOf(ColumnInfo(),ColumnInfo(),ColumnInfo(),ColumnInfo()), mapOf())
            }
            // 인기 칼럼
            setAdapter()

            context.getBoards()
                    .orderBy("likeCount", Query.Direction.DESCENDING)
                    .limit(5)   // 페이지 단위
                    .get()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful && task.result.size() > 0) {
                            val boardInfoList = ArrayList<BoardInfo>()
                            for (document in task.result) {
                                val boardInfo = document.toObject(BoardInfo::class.java)
                                Timber.d("title: ${boardInfo.title}")
                                boardInfoList.add(boardInfo)
                            }
                            binding.recyclerviewBoard.apply {
                                layoutManager = object : LinearLayoutManager(context) {
                                    override fun canScrollVertically(): Boolean {
                                        return false
                                    }
                                }
                                adapter = BoardListRecyclerViewAdapter(boardInfoList)
                                addOnItemClickListener(object : OnItemClickListener {
                                    override fun onItemClicked(position: Int, view: View) {
                                        activity?.startActivity<BoardDetailActivity>(Extra.OBJECT_KEY.name to boardInfoList[position].objectId)
                                    }
                                })
                            }
                        }
                    }

            getTripleDataTask(
                    context.getEvents()
                            .orderBy(getString(R.string.buy_count), Query.Direction.DESCENDING)
                            .limit(10)   // 페이지 단위 페이지 갯수
            )
                    ?.addOnSuccessListener {
                        list.addAll(it.first)
                        map.putAll(it.second)
                        binding.recyclerviewEvent.apply {
                            layoutManager = object : LinearLayoutManager(context) {
                                override fun canScrollVertically(): Boolean {
                                    return false
                                }
                            }
                            adapter = EventRecyclerAdapter(context, list, map)

                            addOnItemClickListener(object : OnItemClickListener {
                                override fun onItemClicked(position: Int, view: View) {
                                    // 디테일 뷰
                                    startActivity(Intent(context, EventDetailActivity::class.java).putExtra(context.OBJECT_KEY, list[position].objectId))
                                }
                            })
                        }
                    }

            binding.btnMyConsultationHistory.setOnClickListener { context.afterCheckLoginDo { context.startActivity<ConsultBoardActivity>() } }
        }

    }

    private fun setupEvents() {
        binding.btnConsultationForm.setOnClickListener {
            context?.afterCheckLoginDo { startActivity(Intent(context, RequestConsultActivity::class.java).putExtra("isOpen", true)) }
        }

        binding.btnMyEventHistory.setOnClickListener {
            context?.afterCheckLoginDo { context?.startActivity<UserEventApplyListActivity>() }
        }

        binding.btnEyeHealthChecklist.setOnClickListener {
            context?.startActivity<EyeTestActivity>()
        }

        binding.txtviewMoreColumns.setOnClickListener {
            startActivity(Intent(context, ColumnActivity::class.java))
        }

        binding.txtviewMoreBoards.setOnClickListener {
            mTextViewMoreEventsClickBehaviorSubject.onNext(R.id.navigation_board)
        }

        binding.txtviewMoreEvents.setOnClickListener {
            mTextViewMoreEventsClickBehaviorSubject.onNext(R.id.navigation_event)
        }
    }

    private fun setAdapter() {
        (context as MainActivity).let { activity ->
            activity.getColumns()
                    .whereEqualTo("approval", true) // 승인된 칼럼만 보임
                    .orderBy("writeDate", Query.Direction.DESCENDING).limit(4).get().addOnSuccessListener { querySnapshot ->
                        querySnapshot
                                ?.let { it -> it.map { activity.getColumn(it.id)?.get() } }
                                .let { Tasks.whenAllSuccess<DocumentSnapshot>(it) }
                                .onSuccessTask { it ->
                                    val columnInfos = it?.asSequence()?.filterNotNull()?.map { it.toObject(ColumnInfo::class.java)!! }?.toList()
                                    columnInfos?.groupBy { it.writerUid }
                                            ?.map { activity.getHospital(it.key).get() }
                                            .let { Tasks.whenAllSuccess<DocumentSnapshot>(it) }
                                            .addOnSuccessListener { mutableList ->
                                                mutableList
                                                        .asSequence()
                                                        .filterNotNull()
                                                        .map { it.id to it.toObject(HospitalInfo::class.java) }
                                                        .toList()
                                                        .toMap().also { hospitalInfoMap ->
                                                            binding.recyclerviewColumn.apply {
                                                                adapter = ColumnRecyclerAdapter(activity, columnInfos!!, hospitalInfoMap)
                                                                addOnItemClickListener(object : OnItemClickListener {
                                                                    override fun onItemClicked(position: Int, view: View) {
                                                                        // 디테일
                                                                        activity.startActivity<ColumnDetailActivity>(Extra.OBJECT_KEY.name to columnInfos[position].objectId)
                                                                    }
                                                                })
                                                            }
                                                        }
                                            }
                                }
                                .addOnCompleteListener { activity.hideProgressDialog() }
                    }
        }
    }

    private fun getTripleDataTask(query: Query): Task<Pair<List<EventInfo>, Map<String, HospitalInfo>>>? {
        return context?.run {
            var infos: List<EventInfo> = listOf()
            query.get()
                    .continueWith { it ->
                        it.result.toObjects(EventInfo::class.java)
                    }.continueWithTask { it ->
                        infos = it.result
                        infos.groupBy { it.writerUid }
                                .filter { !it.key.isEmpty() }
                                .mapNotNull { getHospital(it.key).get() }
                                .let { Tasks.whenAllSuccess<DocumentSnapshot>(it) }
                    }.continueWith { it ->
                        Pair(infos, it.result.filterNotNull()
                                .map { it.id to it.toObject(HospitalInfo::class.java)!! }
                                .toMap())
                    }
        }
    }

    private var mTimer: Timer? = null

    override fun onResume() {
        super.onResume()
        /*After setting the adapter use the timer */
        val handler = Handler()
        val adAutoScrollRunnable = Runnable {
            binding.viewPagerMainBannerAd.let {
                val nextItem = it.currentItem + 1
                it.setCurrentItem(if (nextItem < it.childCount) nextItem else 0, true)
            }
        }
        mTimer = Timer()
        mTimer?.schedule(object : TimerTask() {
            override fun run() {
                handler.post(adAutoScrollRunnable)
            }

        }, DELAY_MS, PERIOD_MS)
    }

    override fun onPause() {
        super.onPause()
        mTimer?.cancel()
    }

}

