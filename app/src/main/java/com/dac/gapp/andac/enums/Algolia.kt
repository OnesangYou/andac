package com.dac.gapp.andac.enums

import com.dac.gapp.andac.BuildConfig


enum class Algolia(val value: String) {

    APP_ID_ANDAC_DEBUG("VUNTR162M6"),
    APP_ID_ANDAC_RELEASE("H9Q32H274M"),
    SEARCH_API_KEY_ANDAC_DEBUG("f8eab63beb88f72136b260ea219aa6a4"),
    SEARCH_API_KEY_ANDAC_RELEASE("96c3b64fabaf0a7bdeca95fd7788ace3"),
    APP_ID_CHIDAC_DEBUG("JJP46KUQ53"),
    APP_ID_CHIDAC_RELEASE("8RC9O215JK"),
    SEARCH_API_KEY_CHIDAC_DEBUG("aba7d528204fde1f9b291725c41d4aa0"),
    SEARCH_API_KEY_CHIDAC_RELEASE("f361cf4cdb6687d83f11f2becc03acf4"),

    APP_ID(if(BuildConfig.FLAVOR_type == "andac") if(BuildConfig.BUILD_TYPE == "debug") APP_ID_ANDAC_DEBUG.value else APP_ID_ANDAC_RELEASE.value else if(BuildConfig.BUILD_TYPE == "debug") APP_ID_CHIDAC_DEBUG.value else APP_ID_CHIDAC_RELEASE.value),
    SEARCH_API_KEY(if(BuildConfig.FLAVOR_type == "andac") if(BuildConfig.BUILD_TYPE == "debug") SEARCH_API_KEY_ANDAC_DEBUG.value else SEARCH_API_KEY_ANDAC_RELEASE.value else if(BuildConfig.BUILD_TYPE == "debug")SEARCH_API_KEY_CHIDAC_DEBUG.value else SEARCH_API_KEY_CHIDAC_RELEASE.value),


    INDEX_NAME_HOSPITAL("hospitals"),
    INDEX_NAME_BOARD("boards"),
    INDEX_NAME_COLUMN("columns"),
    INDEX_NAME_EVENT("events"),
    HITS("hits"),
    NAME("name"),
    GEOLOC("_geoloc"),
    LAT("lat"),
    LNG("lng"),
    OBJECT_ID("objectID"),
    ADDRESS1("address1"),
    ADDRESS2("address2"),
    NUMBER("number"),
    OPEN_DATE("openDate"),
    PHONE("phone"),
    STATUS("status"),
    TYPE("type"),

}