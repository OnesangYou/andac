package com.dac.gapp.andac.enums

import com.dac.gapp.andac.BuildConfig


enum class Algolia(val value: String) {

    APP_ID(if(BuildConfig.FLAVOR_type == "andac") "H9Q32H274M" else "JJP46KUQ53"),
    SEARCH_API_KEY(if(BuildConfig.FLAVOR_type == "andac") "96c3b64fabaf0a7bdeca95fd7788ace3" else "aba7d528204fde1f9b291725c41d4aa0"),
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