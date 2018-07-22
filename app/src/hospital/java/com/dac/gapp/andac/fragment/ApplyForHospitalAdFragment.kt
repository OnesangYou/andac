package com.dac.gapp.andac.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dac.gapp.andac.R
import com.dac.gapp.andac.base.BaseFragment
import com.dac.gapp.andac.util.MyToast
import kotlinx.android.synthetic.main.fragment_apply_for_hospital_ad.*

class ApplyForHospitalAdFragment : BaseFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_apply_for_hospital_ad, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        prepareUi()
        setupEventsOnViewCreated()
    }

    private fun prepareUi() {
        context!!.getToolBar().setTitle(getString(R.string.apply_for_hospital_ad))
    }

    private fun setupEventsOnViewCreated() {
        btnApplyForCreativeProduction.setOnClickListener(View.OnClickListener { MyToast.showShort(requireContext(), "TODO : 광고 제작 신청하기") })
        btnApplyForPopupAd.setOnClickListener(View.OnClickListener { context!!.changeFragment(AdPaymentFragment()) })
        btnApplyForBannerAd.setOnClickListener(View.OnClickListener { MyToast.showShort(requireContext(), "TODO : [배너 광고] 신청하기") })
        btnApplyForTodaysHospitalAd.setOnClickListener(View.OnClickListener { MyToast.showShort(requireContext(), "TODO : [오늘의 병원 광고] 신청하기") })
    }

}