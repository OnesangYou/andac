package com.dac.gapp.andac.model.firebase

import com.google.firebase.firestore.ServerTimestamp
import java.util.*

/**
 * Created by gimbyeongjin on 2018. 4. 4..
 */
data class ReplyInfo(
        var writerUid: String = "",
        var contents: String = "",
        @ServerTimestamp var writeDate: Date? = null,
        var modifyDate: Date? = null,
        var objectId : String = "",
        var boardId : String = "",
        var writerType : String = ""
)