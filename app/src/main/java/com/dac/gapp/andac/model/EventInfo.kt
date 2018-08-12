package com.dac.gapp.andac.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.*

data class EventInfo(
        var title :String = "",
        var sub_title : String = "",
        var body : String = "",
        var deal_kind : String = "",
        var price : Int = 0,
        var buy_count : Int = 0,
        var writerUid : String = "",
        var pictureUrl : String = "",
        var detailPictureUrl : String = "",
        @ServerTimestamp var writeDate: Date? = null
) {
    val endDate : Date?
    get() {
        return writeDate?.let{writeDate ->
            Calendar.getInstance().run{
                time = writeDate
                add(Calendar.DAY_OF_YEAR, - 30)
                time
            }
        }.let{null}
    }
}