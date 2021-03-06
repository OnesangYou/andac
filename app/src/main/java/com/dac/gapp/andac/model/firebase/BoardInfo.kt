package com.dac.gapp.andac.model.firebase

import com.google.firebase.firestore.ServerTimestamp
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by gimbyeongjin on 2018. 4. 4..
 */
data class BoardInfo (
        var hospitalUid: String = "",
        var tag: String = "",
        var type: String = "",
        var writerUid: String = "",
        var title: String = "",
        var contents: String = "",
        var pictureUrls: ArrayList<String?>? = arrayListOf(null, null, null),
        @ServerTimestamp var writeDate: Date? = null,
        var modifyDate: Date? = null,
        var objectId : String = "",
        var viewCount : Int = 0,
        var likeCount : Int = 0,
        var replyCount : Int = 0
)