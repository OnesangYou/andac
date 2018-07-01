package com.dac.gapp.andac.model.firebase

import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by gimbyeongjin on 2018. 4. 4..
 */
data class BoardInfo(
        var hospitalUid: String = "",
        var tag: String = "",
        var type: String = "",
        var writerUid: String = "",
        var title: String = "",
        var contents: String = "",
        var pictureUrls: ArrayList<String>? = ArrayList(),
        var writeDate: Date? = null,
        var modifyDate: Date? = null
)