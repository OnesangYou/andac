package com.dac.gapp.andac.model.firebase

import com.google.firebase.firestore.ServerTimestamp
import java.util.*

/**
 * Created by Administrator on 2018-04-13.
 */
data class AdInfo(
        var hospitalUid: String = "",
        @ServerTimestamp var startDate: Date? = null,
        @ServerTimestamp var endDate: Date? = null
)