package com.dac.gapp.andac.enums

import com.dac.gapp.andac.R

enum class Ad(val adType: AdType, val titleResId: Int, val uploadFileName: String, val collectionName: String) {
    LOGIN_BANNER(AdType.NONE, R.string.login_banner_ad, "login_banner_ad", "loginBannerAd"),
    MAIN_POPUP(AdType.EVENT, R.string.main_popup_ad, "main_popup_ad", "mainPopupAd"),
    MAIN_BANNER(AdType.EVENT, R.string.main_banner_ad, "main_banner_ad", "mainBannerAd"),
    MAIN_TODAY_HOSPITAL(AdType.HOSPITAL, R.string.main_todays_hospital_ad, "main_todays_hospital_ad", "mainTodayHospitalAd"),
    SEARCH_HOSPITAL_BANNER_AD(AdType.HOSPITAL, R.string.search_hospital_banner_ad, "search_hospital_banner_ad", "searchHospitalBannerAd"), ;

    fun isAdTypeEvent(): Boolean {
        return this.adType == AdType.EVENT
    }
}