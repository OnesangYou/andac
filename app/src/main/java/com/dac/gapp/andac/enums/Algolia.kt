package com.dac.gapp.andac.enums

import com.dac.gapp.andac.BuildConfig

enum class Algolia(val value: String) {
    APP_ID(BuildConfig.ALGOLIA_APP_ID),
    SEARCH_API_KEY(BuildConfig.ALGOLIA_SEARCH_API_KEY),
    INDEX_NAME_HOSPITAL("hospitals"),
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