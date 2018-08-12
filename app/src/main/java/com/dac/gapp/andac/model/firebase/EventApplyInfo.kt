package com.dac.gapp.andac.model.firebase

import com.google.firebase.firestore.ServerTimestamp
import java.util.*

data class EventApplyInfo(
        var name :String = "",
        var phone : String = "",
        var possibleTime : String = "",
        var writerUid : String = "",
        @ServerTimestamp var writeDate: Date? = null
) {
    var objectId: String? = null
}