package com.dac.gapp.andac.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.dac.gapp.andac.R
import com.dac.gapp.andac.base.BaseFragment
import com.dac.gapp.andac.dialog.PhoneNumberDialog
import com.dac.gapp.andac.enums.Ad
import com.dac.gapp.andac.model.AdReqeustInfo
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
        context?.let {context ->
            context.setActionBarLeftImage(R.drawable.back)
            context.setActionBarCenterText(R.string.apply_for_hospital_ad)
            context.hidActionBarRight()
            context.setOnActionBarLeftClickListener(View.OnClickListener {
                if (context.supportFragmentManager.backStackEntryCount != 0 ) {
                    context.supportFragmentManager.popBackStack()
                } else {
                    context.finish()
                }
            })
        }
    }

    private fun setupEventsOnViewCreated() {
        context?.let { context ->
            btnApplyForCreativeProduction.setOnClickListener {
                val dialog = PhoneNumberDialog(requireContext())
                dialog
                        .setOnCancelListener(View.OnClickListener {
                            dialog.dismiss()
                        })
                        .setOnConfirmListener(View.OnClickListener {
                            context.getUid()?.let { uid ->
                                context.getAdRequests().document(uid).set(AdReqeustInfo(uid, dialog.getText()))
                                        .addOnSuccessListener {
                                            Toast.makeText(context, "번호 저장 성공", Toast.LENGTH_SHORT).show()
                                        }.addOnCanceledListener {
                                            Toast.makeText(context, "번호 저장 실패", Toast.LENGTH_SHORT).show()
                                        }
                            }
                            dialog.dismiss()
                        })
                        .show()
            }
            btnApplyForMainPopupAd.setOnClickListener { context.changeFragment(AdPaymentFragment.newInstanceForPay(Ad.MAIN_POPUP)) }
            btnApplyForMainBannerAd.setOnClickListener { context.changeFragment(AdPaymentFragment.newInstanceForPay(Ad.MAIN_BANNER)) }
            btnApplyForMainTodaysHospitalAd.setOnClickListener { context.changeFragment(AdPaymentFragment.newInstanceForPay(Ad.MAIN_TODAY_HOSPITAL)) }
            btnApplyForLoginBannerAd.setOnClickListener { context.changeFragment(AdPaymentFragment.newInstanceForPay(Ad.LOGIN_BANNER)) }
        }

    }
}
