package com.dac.gapp.andac.model.firebase

import com.dac.gapp.andac.enums.ConsultStatus
import com.google.firebase.firestore.ServerTimestamp
import java.util.*

/**
 * Created by Administrator on 2018-04-13.
 */
data class ConsultInfo(var tag:String = "",
                       var visualacuity:String = "",    // 시력
                       var disease:String? = null,
                       var userId:String? = null,
                       var hospitalId: String? = null,
                       var phone:String = "",
                       var name: String = "",
                       var range:String = "",   // 지역
                       var text:String = "",
                       var age: Int? = null,
                       var status: String = ConsultStatus.APPLY.value,
                       @ServerTimestamp var writeDate: Date? = null
                       )