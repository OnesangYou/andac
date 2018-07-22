package com.dac.gapp.andac.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dac.gapp.andac.R
import com.dac.gapp.andac.base.BaseFragment
import com.dac.gapp.andac.util.MyToast
import kotlinx.android.synthetic.main.fragment_ad_payment.*

class AdPaymentFragment : BaseFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_ad_payment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        prepareUi()
        setupEventsOnViewCreated()
    }

    private fun prepareUi() {
        context!!.getToolBar().setTitle(getString(R.string.paying_for_a_hospital_ad))
    }

    private fun setupEventsOnViewCreated() {
        btnUploadPhoto.setOnClickListener(View.OnClickListener { MyToast.showShort(requireContext(), "TODO : 사진 업로드하기") })
        btnPay.setOnClickListener(View.OnClickListener { MyToast.showShort(requireContext(), "TODO : 광고 결제하기") })
    }

}