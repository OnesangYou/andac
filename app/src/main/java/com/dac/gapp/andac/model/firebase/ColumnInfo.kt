package com.dac.gapp.andac.model.firebase

import com.google.firebase.firestore.ServerTimestamp
import java.util.*

data class ColumnInfo(
        var tag: String = "",
        var type: String = "",
        var writerUid: String = "",
        var title: String = "",
        var contents: String = "",
        var pictureUrl: String = "",
        @ServerTimestamp var writeDate: Date? = null,
        var modifyDate: Date? = null,
        var objectId : String = ""
)