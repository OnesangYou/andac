package com.dac.gapp.andac.model.firebase

import com.dac.gapp.andac.enums.AdType
import com.google.firebase.firestore.ServerTimestamp
import java.util.*

/**
 * Created by Administrator on 2018-04-13.
 */
data class AdInfo(
        var photoUrl: String = "",
        var showingUp: Boolean = false,
        @ServerTimestamp var startDate: Date? = null,
        @ServerTimestamp var endDate: Date? = null,
        var eventId: String = ""
)