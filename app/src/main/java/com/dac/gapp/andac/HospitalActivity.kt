package com.dac.gapp.andac

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.dac.gapp.andac.adapter.BoardListRecyclerViewAdapter
import com.dac.gapp.andac.adapter.EventRecyclerAdapter
import com.dac.gapp.andac.adapter.HospitalActivityPagerAdapter
import com.dac.gapp.andac.base.BaseActivity
import com.dac.gapp.andac.databinding.ActivityHospitalBinding
import com.dac.gapp.andac.enums.Extra
import com.dac.gapp.andac.model.firebase.BoardInfo
import com.dac.gapp.andac.model.firebase.EventInfo
import com.dac.gapp.andac.model.firebase.HospitalInfo
import com.dac.gapp.andac.util.OnItemClickListener
import com.dac.gapp.andac.util.addOnItemClickListener
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapFragment
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import org.jetbrains.anko.startActivity
import timber.log.Timber
import java.util.*

const val EXTRA_HOSPITAL_INFO = "EXTRA_HOSPITAL_INFO"

class HospitalActivity : BaseActivity(), OnMapReadyCallback {

    private lateinit var hospitalInfo: HospitalInfo
    private lateinit var googleMap: GoogleMap
    private lateinit var binding: ActivityHospitalBinding

    // static method
    companion object {
        fun createIntent(context: Context, hospitalInfo: HospitalInfo?): Intent {
            val intent = Intent(context, HospitalActivity::class.java)
            intent.putExtra(EXTRA_HOSPITAL_INFO, hospitalInfo)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hospital)
        binding = getBinding()
        binding.activity = this

        hospitalInfo = intent.getSerializableExtra(EXTRA_HOSPITAL_INFO) as HospitalInfo

        // 병원 카운트 증가
        addCountHospitalVisitants(hospitalInfo.objectID)

        prepareUi()
        setupToolbarItemClickEvents()

        setBoards()
        setEvents()

        binding.txtviewHospitalCommentMore.setOnClickListener { startActivity<ReviewBoardListActivity>(Extra.OBJECT_KEY.name to hospitalInfo.objectID) }
    }

    private fun setEvents() {
        fun getTripleDataTask(query: Query): Task<Pair<List<EventInfo>, Map<String, HospitalInfo>>>? {
            return this.run {
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

        getTripleDataTask(
                getEvents()
                        .whereEqualTo("writerUid", hospitalInfo.objectID)
                        .orderBy("likeCount", Query.Direction.DESCENDING)
                        .limit(5)   // 페이지 단위 페이지 갯수
        )
                ?.addOnSuccessListener {
                    val list = it.first
                    val map = it.second
                    binding.recyclerViewHospitalEvent.apply {
                        layoutManager = object : LinearLayoutManager(context) {
                            override fun canScrollVertically(): Boolean {
                                return false
                            }
                        }
                        swapAdapter(EventRecyclerAdapter(this@HospitalActivity, list, map), false)

                        addOnItemClickListener(object : OnItemClickListener {
                            override fun onItemClicked(position: Int, view: View) {
                                // 디테일 뷰
                                startActivity(Intent(context, EventDetailActivity::class.java).putExtra(OBJECT_KEY, list[position].objectId))
                            }
                        })
                        adapter.notifyDataSetChanged()
                    }
                }?.addOnFailureListener{
                    it.printStackTrace()
                }

    }

    private fun setBoards() {
        getBoards()
                .whereEqualTo("type", getString(R.string.review_board))
                .whereEqualTo("hospitalUid", hospitalInfo.objectID)
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
                        binding.recyclerViewHospitalComment.apply {
                            layoutManager = object : LinearLayoutManager(context) {
                                override fun canScrollVertically(): Boolean {
                                    return false
                                }
                            }
                            adapter = BoardListRecyclerViewAdapter(boardInfoList)
                            addOnItemClickListener(object : OnItemClickListener {
                                override fun onItemClicked(position: Int, view: View) {
                                    startActivity<BoardDetailActivity>(Extra.OBJECT_KEY.name to boardInfoList[position].objectId)
                                }
                            })
                        }
                    } else {
                        task.exception?.printStackTrace()
                    }

                }
    }

    private fun prepareUi() {
        addListenerRegistrations(getHospital(hospitalInfo.objectID).addSnapshotListener { snapshot, exception ->
            val hospitalInfo = snapshot?.toObject(HospitalInfo::class.java).apply { this?.objectID = hospitalInfo.objectID }?:return@addSnapshotListener

            setActionBarLeftImage(R.drawable.back)
            setActionBarCenterText(hospitalInfo.name)
            setActionBarRightImage(R.drawable.call)
            binding.txtvieName.text = hospitalInfo.name
            binding.txtviewAddress.text = hospitalInfo.run { if (address2.isNotEmpty()) address2 else if (address1.isNotEmpty()) address1 else getString(R.string.no_hospital_addres_entered) }
            binding.txtviewBusinessHours.text = hospitalInfo.run { if (businessHours.isNotEmpty()) businessHours else getString(R.string.no_business_hours_entered) }
            binding.txtviewDescription.text = hospitalInfo.description

            binding.viewPager.adapter = HospitalActivityPagerAdapter(this, supportFragmentManager, ArrayList<Any>().also {
                it.add(hospitalInfo.run { if (profilePicUrl.isNotEmpty()) profilePicUrl else if (approval) R.drawable.hospital_profile_default_approval else R.drawable.hospital_profile_default_not_approval })
            })

            val fragmentManager = fragmentManager
            val mapFragment = fragmentManager.findFragmentById(R.id.map) as MapFragment
            mapFragment.getMapAsync(this)

            binding.favoriteCntText.text = hospitalInfo.likeCount.toString()

            // isLike
            binding.imgviewFavorite.isEnabled = hospitalInfo.approval
            if(hospitalInfo.approval){  // 승인된 병원만 좋아요 버튼 허가

                // 좋아요한 병원 셋팅
                val setLike = {
                    binding.imgviewFavorite.isEnabled = false
                    getLikeHospital(hospitalInfo.objectID)?.get()?.addOnSuccessListener { documentSnapshot ->
                        binding.imgviewFavorite.isChecked = documentSnapshot.exists()
                        binding.imgviewFavorite.isEnabled = true
                    }
                }

                if(isLogin()) setLike()

                // 병원 좋아요 클릭 리스너 이벤트 달기
                binding.imgviewFavorite.setOnClickListener { _ ->

                    if (isLogin()) {
                        // 로그인 되어있으면 좋아요 로직
                        binding.imgviewFavorite.isEnabled = false
                        clickHospitalLikeBtn(hospitalInfo.objectID, binding.imgviewFavorite.isChecked)
                                ?.addOnSuccessListener { binding.imgviewFavorite.isEnabled = true }
                    } else {
                        // 로그인이 안되있으면, 로그인 이후 좋아요 출력
                        binding.imgviewFavorite.isChecked = false
                        goToLogin { setLike() }
                    }
                }
            }
        })
    }

    @SuppressLint("MissingPermission")
    private fun setupToolbarItemClickEvents() {
        setOnActionBarLeftClickListener(View.OnClickListener {
            finish()
        })
        setOnActionBarRightClickListener(View.OnClickListener {
            startActivity(Intent(Intent.ACTION_CALL, Uri.parse("tel:" + hospitalInfo.phone)))
        })
    }

    override fun onMapReady(map: GoogleMap?) {
        map?.apply {
            addMarker(MarkerOptions().apply { icon(BitmapDescriptorFactory.fromResource(R.drawable.hospital_on)); position(hospitalInfo.getLatLng()) })
            animateCamera(CameraUpdateFactory.newLatLngZoom(hospitalInfo.getLatLng(), 15f), 1, object : GoogleMap.CancelableCallback {
                override fun onFinish() {
                    Timber.d("animateCamera onFinish()")
                }

                override fun onCancel() {
                    Timber.d("animateCamera onCancel()")
                    animateCamera(CameraUpdateFactory.newLatLngZoom(hospitalInfo.getLatLng(), 15f), 1, this)
                }
            })
            googleMap = this
        }
    }

    fun onClickConsult(view: View) {
        // 병원 계정은 사용 불가
        if(isHospital()) {
            return toast("병원 계정은 상담신청 불가능합니다.")
        }

        // 승인 받은 병원인지 확인
        if(!hospitalInfo.approval) {
            return toast("아직 승인받지 않은 병원입니다.")
        }

        googleMap.moveCamera(CameraUpdateFactory.newLatLng(hospitalInfo.getLatLng()))

        afterCheckLoginDo { startActivity(Intent(applicationContext, RequestSurgeryActivity::class.java).putExtra("isOpen", false).putExtra("hospitalId", hospitalInfo.objectID).putExtra("hospitalName", hospitalInfo.name)) }
    }
}
