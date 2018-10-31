package com.dac.gapp.andac.model.firebase

import com.google.firebase.firestore.ServerTimestamp
import java.util.*

data class EventApplyInfo(
        var name :String = "",
        var phone : String = "",
        var possibleTime : String = "",
        var possibleTimeStart : Long = -32400000,   // 00:00
        var possibleTimeEnd : Long = 53940000,  // 23:59

        var writerUid : String = "",
        @ServerTimestamp var writeDate: Date? = null,
        var confirmDate: Date? = null
) {
    var objectId: String? = null
}