package com.dac.gapp.andac.fragment

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.dac.gapp.andac.*
import com.dac.gapp.andac.adapter.ColumnRecyclerAdapter
import com.dac.gapp.andac.adapter.AdPagerAdapter
import com.dac.gapp.andac.base.BaseFragment
import com.dac.gapp.andac.dialog.MainPopupDialog
import com.dac.gapp.andac.enums.Ad
import com.dac.gapp.andac.model.firebase.AdInfo
import com.dac.gapp.andac.model.firebase.ColumnInfo
import com.dac.gapp.andac.model.firebase.HospitalInfo
import com.dac.gapp.andac.util.toast
import com.dac.gapp.andac.util.MyToast
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.user.fragment_main.*
import org.jetbrains.anko.startActivity
import timber.log.Timber
import java.util.*


class MainFragment : BaseFragment() {

    companion object {
        const val IS_FIRST_MAIN_POPUP_AD = "isFirstMainPopupAd"
        const val DELAY_MS: Long = 500
        const val PERIOD_MS: Long = 3000
    }

    private var mIsMainPopupAdFirst: Boolean = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        prepareUi()

        requestSurgery.setOnClickListener {
            startActivity(Intent(context,RequestSurgeryActivity::class.java).putExtra("isOpen",true))
        }

        more_calum.setOnClickListener {
            startActivity(Intent(context, ColumnActivity::class.java))
        }

        columnList.layoutManager = GridLayoutManager(context, 2)
        setAdapter()

        main_my_event.setOnClickListener {
            context?.startActivity<UserEventApplyListActivity>()
        }
    }

    private fun prepareUi() {
        context?.let { context ->
            context.getDb().collection(Ad.MAIN_BANNER.collectionName)
                    .get()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val photoUrls = ArrayList<String>()
                            for (document in task.result) {
                                val adInfo = document.toObject(AdInfo::class.java)
                                Timber.d("photoUrl: ${adInfo.photoUrl}")
                                photoUrls.add(adInfo.photoUrl)
                            }
                            viewPagerMainBannerAd.adapter = AdPagerAdapter(context, photoUrls)
                        }
                    }
                    .addOnFailureListener {
                        Timber.e("MAIN_BANNER 광고 로드 실패 : ${it.localizedMessage}")
                    }


            if (mIsMainPopupAdFirst) {
                mIsMainPopupAdFirst = false
                context.getDb().collection(Ad.MAIN_POPUP.collectionName)
                        .get()
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val photoUrls = ArrayList<String>()
                                for (document in task.result) {
                                    val adInfo = document.toObject(AdInfo::class.java)
                                    Timber.d("photoUrl: ${adInfo.photoUrl}")
                                    photoUrls.add(adInfo.photoUrl)
                                }

                                // TODO 팝업 광고 랜덤으로 띄워야됨!!
                                if (photoUrls.size > 0) {
                                    val dialog = MainPopupDialog(requireContext())
                                    dialog
                                            .setImageUrl(photoUrls[0])
                                            .setOnImageClickListener(View.OnClickListener {
                                                MyToast.showShort(context, "TODO: go to event page")
                                                dialog.dismiss()
                                            })
                                            .setOnCancelListener(View.OnClickListener {
                                                MyToast.showShort(context, "TODO: go to event page")
                                                dialog.dismiss()
                                            })
                                            .setOnConfirmListener(View.OnClickListener {
                                                dialog.dismiss()
                                            }).show()
                                }
                            }
                        }
                        .addOnFailureListener {
                            Timber.e("MAIN_POPUP 광고 로드 실패 : ${it.localizedMessage}")
                        }
            }

            context.getDb().collection(Ad.MAIN_TODAY_HOSPITAL.collectionName)
                    .get()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val photoUrls = ArrayList<String>()
                            if(task.result.size() == 0) return@addOnCompleteListener
                            for (document in task.result) {
                                val adInfo = document.toObject(AdInfo::class.java)
                                Timber.d("photoUrl: ${adInfo.photoUrl}")
                                photoUrls.add(adInfo.photoUrl)
                            }
                            Glide.with(context).load(photoUrls[0]).into(imgviewTodaysHospitalAd)
                        }
                    }
        }

    }

    private fun setAdapter() {
        (context as MainActivity).apply {
            showProgressDialog()
            getColumns().orderBy("writeDate", Query.Direction.DESCENDING).limit(4).get().addOnSuccessListener { querySnapshot ->
                querySnapshot
                        ?.let { it -> it.map { getColumn(it.id)?.get() } }
                        .let { Tasks.whenAllSuccess<DocumentSnapshot>(it) }
                        .onSuccessTask { it ->
                            val columnInfos = it?.filterNotNull()?.map { it.toObject(ColumnInfo::class.java)!! }
                            columnInfos?.groupBy { it.writerUid }
                                    ?.map { getHospital(it.key).get() }
                                    .let { Tasks.whenAllSuccess<DocumentSnapshot>(it) }
                                    .addOnSuccessListener { mutableList ->
                                        mutableList
                                                .filterNotNull()
                                                .map { it.id to it.toObject(HospitalInfo::class.java) }
                                                .toMap().also { hospitalInfoMap ->
                                                    columnList.swapAdapter(ColumnRecyclerAdapter(this, columnInfos!!, hospitalInfoMap), false)
                                                    columnList.adapter.notifyDataSetChanged()
                                                }
                                    }
                        }
                        .addOnCompleteListener { hideProgressDialog() }
            }
        }
    }

    private var mTimer: Timer? = null

    override fun onResume() {
        super.onResume()
        /*After setting the adapter use the timer */
        val handler = Handler()
        val adAutoScrollRunnable = Runnable {
            viewPagerMainBannerAd.let {
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

