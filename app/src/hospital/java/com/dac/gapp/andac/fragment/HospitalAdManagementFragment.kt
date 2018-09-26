package com.dac.gapp.andac.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.dac.gapp.andac.R
import com.dac.gapp.andac.base.BaseFragment
import com.dac.gapp.andac.enums.Ad
import com.dac.gapp.andac.extension.visibleOrGone
import com.dac.gapp.andac.model.firebase.AdInfo
import com.dac.gapp.andac.util.UiUtil
import kotlinx.android.synthetic.main.fragment_hospital_ad_management.*
import timber.log.Timber

class HospitalAdManagementFragment : BaseFragment() {

    private lateinit var tripleArray: Array<Triple<Ad, Triple<ViewGroup, ImageView, Triple<TextView, TextView, TextView>>, Int>>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_hospital_ad_management, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        tripleArray = arrayOf(
                Triple(Ad.LOGIN_BANNER, Triple(layoutLoginBannerAd, imgviewLoginBannerAd, Triple(txtviewLoginBannerAd, txtviewLoginBannerAdValidityPeriodDays, txtviewLoginBannerAdClicks)), R.string.login_banner_ad_days),
                Triple(Ad.MAIN_POPUP, Triple(layoutMainPopupAd, imgviewMainPopupAd, Triple(txtviewMainPopupAd, txtviewMainPopupAdValidityPeriodDays, txtviewMainPopupAdClicks)), R.string.main_popup_ad_days),
                Triple(Ad.MAIN_BANNER, Triple(layoutMainBannerAd, imgviewMainBannerAd, Triple(txtviewMainBannerAd, txtviewMainBannerAdValidityPeriodDays, txtviewMainBannerAdClicks)), R.string.main_banner_ad_days),
                Triple(Ad.MAIN_TODAY_HOSPITAL, Triple(layoutMainTodaysHospitalAd, imgviewMainTodaysHospitalAd, Triple(txtviewMainTodaysHospitalAd, txtviewMainTodaysHospitalAdValidityPeriodDays, txtviewMainTodaysHospitalAdClicks)), R.string.main_todays_hosptial_ad_days),
                Triple(Ad.SEARCH_HOSPITAL_BANNER_AD, Triple(layoutSearchHospitalBannerAd, imgviewSearchHospitalBannerAd, Triple(txtviewSearchHospitalBannerAd, txtviewSearchHospitalBannerAdValidityPeriodDays, txtviewSearchHospitalBannerAdClicks)), R.string.search_hosptial_banner_ad_days)
        )
        prepareUi()
        setupEventsOnViewCreated()
    }

    private fun prepareUi() {
        for (triple in tripleArray) {
            if (triple.first == Ad.SEARCH_HOSPITAL_BANNER_AD) {
                triple.second.second.visibleOrGone(false)
            } else {
                UiUtil.visibleOrGone(false, triple.second.first)
            }
        }
        context?.let { context ->
            context.setActionBarLeftImage(R.drawable.back)
            context.setActionBarCenterText(R.string.hospital_ad_management)
            context.hideActionBarRight()
            context.setOnActionBarLeftClickListener(View.OnClickListener {
                if (context.supportFragmentManager.backStackEntryCount != 0) {
                    context.supportFragmentManager.popBackStack()
                } else {
                    context.finish()
                }
            })
            context.getUid()?.let { uid ->
                for (triple in tripleArray) {
                    if (triple.first != Ad.SEARCH_HOSPITAL_BANNER_AD) {
                        context.getDb().collection(triple.first.collectionName).document(uid)
                                .get()
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        val adInfo = task.result.toObject(AdInfo::class.java)
                                        adInfo?.let { adInfo ->
                                            Timber.d("photoUrl: ${adInfo.photoUrl}")
                                            Glide.with(this).load(adInfo.photoUrl).into(triple.second.second)
                                            triple.second.second.setOnClickListener { context.changeFragment(HospitalAdApplicationFragment.newInstanceForEdit(triple.first, adInfo.photoUrl)) }
                                            triple.second.third.first.text = String.format(getString(triple.third), 30)
                                            triple.second.third.second.text = String.format(getString(R.string.ad_validity_period_days), 15)
                                            triple.second.third.third.text = String.format(getString(R.string.ad_clicks), 180)
                                            UiUtil.visibleOrGone(true, triple.second.first)
                                        }
                                    }
                                }
                    }
                }
            }
        }
    }


    private fun setupEventsOnViewCreated() {
    }

}
