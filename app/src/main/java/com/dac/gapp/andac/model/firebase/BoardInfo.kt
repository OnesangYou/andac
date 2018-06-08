package com.dac.gapp.andac.model.firebase

import java.util.*

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
        var pictureUrl: String = "",
        var writeDate: Date? = null,
        var modifyDate: Date? = null
)