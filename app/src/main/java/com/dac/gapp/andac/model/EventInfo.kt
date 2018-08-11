package com.dac.gapp.andac.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.*
data class EventInfo(var title :String, var sub_title : String, var body : String, var deal_kind : String, var price : String, var buy_count : String, var writerUid : String, var pictureUrl : String, @ServerTimestamp var writeDate: Date)