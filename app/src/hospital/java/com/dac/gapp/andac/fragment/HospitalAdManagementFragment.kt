package com.dac.gapp.andac.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dac.gapp.andac.R
import com.dac.gapp.andac.base.BaseFragment
import com.dac.gapp.andac.dialog.PhoneNumberDialog
import com.dac.gapp.andac.enums.Ad
import com.dac.gapp.andac.util.MyToast
import kotlinx.android.synthetic.main.fragment_hospital_ad_management.*

class HospitalAdManagementFragment : BaseFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_hospital_ad_management, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        prepareUi()
        setupEventsOnViewCreated()
    }

    private fun prepareUi() {
        context!!.getToolBar().setTitle(getString(R.string.hospital_ad_management))
    }

    private fun setupEventsOnViewCreated() {
        btnApplyForCreativeProduction.setOnClickListener {
            val dialog = PhoneNumberDialog(requireContext())
            dialog
                    .setOnCancelListener(View.OnClickListener {
                        dialog.dismiss()
                    })
                    .setOnConfirmListener(View.OnClickListener {
                        MyToast.showShort(requireContext(), "TODO : 번호 저장 -> " + dialog.getText())
                        dialog.dismiss()
                    })
                    .show()
        }

        MyToast.showShort(requireContext(), "TODO 어느 광고에서 넘어왔는지 수정")
        imgviewPhoto.setOnClickListener { context?.changeFragment(AdPaymentFragment.newInstance(Ad.MAIN_POPUP)) }
    }

}
