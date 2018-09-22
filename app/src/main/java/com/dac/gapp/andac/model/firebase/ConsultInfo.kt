package com.dac.gapp.andac.model.firebase

import com.google.firebase.firestore.ServerTimestamp
import java.util.*

/**
 * Created by Administrator on 2018-04-13.
 */
data class ConsultInfo(var tag:String = "",
                       var visualacuity:String = "",    // 시력
                       var disease:String? = null,
                       var userId:String? = null,
                       var phone:String = "",
                       var name: String = "",
                       var range:String = "",   // 지역
                       var text:String = "",
                       var age: Int? = null,
                       @ServerTimestamp var writeDate: Date? = null
                       ){
    val endDate : Date?
        get() {
            return writeDate?.let{writeDate ->
                Calendar.getInstance().run{
                    time = writeDate
                    add(Calendar.DAY_OF_YEAR, - 30) // 30일 기준
                    time
                }
            }.let{null}
        }
}
data class SelectConsultInfo(var tag:String = "",
                             var visualacuity:String = "",    // 시력
                             var disease:String? = null,
                             var userId:String? = null,
                             var hospitalInfo: HospitalInfo? = null,
                             var phone:String = "",
                             var name: String = "",
                             var text:String = "",
                             var age: Int? = null,
                             @ServerTimestamp var writeDate: Date? = null
)