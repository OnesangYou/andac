package com.dac.gapp.andac.fragment

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dac.gapp.andac.*
import com.dac.gapp.andac.adapter.ColumnRecyclerAdapter
import com.dac.gapp.andac.adapter.AdPagerAdapter
import com.dac.gapp.andac.base.BaseFragment
import com.dac.gapp.andac.enums.Ad
import com.dac.gapp.andac.model.firebase.AdInfo
import com.dac.gapp.andac.model.firebase.ColumnInfo
import com.dac.gapp.andac.model.firebase.HospitalInfo
import com.dac.gapp.andac.util.toast
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.user.fragment_main.*
import timber.log.Timber
import java.util.*


class MainFragment : BaseFragment() {

    companion object {
        const val DELAY_MS: Long = 500
        const val PERIOD_MS: Long = 3000
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        prepareUi()

        requestSurgery.setOnClickListener {
            val nextIntent = Intent(context, RequestSurgeryActivity::class.java)
            startActivity(nextIntent)
        }

        column.setOnClickListener {
            val nextIntent = Intent(context, ColumnActivity::class.java)
            startActivity(nextIntent)
        }

        event.setOnClickListener {
            // TODO : 이벤트 신청내역
            toast("이벤트 신청내역")

        }

        findHospitalByText.setOnClickListener {
            startActivity(Intent(context, HospitalTextSearchActivity::class.java).putExtra("filterStr", "approval=1"))
        }

        more_calum.setOnClickListener {
            startActivity(Intent(context, ColumnActivity::class.java))
        }

        columnList.layoutManager = GridLayoutManager(context, 2)
        setAdapter()
    }

    private fun prepareUi() {
        context?.let { baseActivity ->
            baseActivity.getAds()
                    .whereEqualTo("adType", Ad.MAIN_BANNER.adType.name)
                    .get()
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            val photoUrls = ArrayList<String>()
                            for (document in it.result) {
                                val adInfo = document.toObject(AdInfo::class.java)
                                Timber.d("photoUrl: ${adInfo.photoUrl}")
                                photoUrls.add(adInfo.photoUrl)
                            }
                            viewPagerMainBannerAd.adapter = AdPagerAdapter(baseActivity, photoUrls)
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
                                                    columnList.adapter = ColumnRecyclerAdapter(this, columnInfos!!, hospitalInfoMap)
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

