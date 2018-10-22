package com.dac.gapp.andac.model.firebase

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp
import java.util.*

data class ChatListInfo(var roomId: String? = "",
                        var lastchat: String? = null,
                        @Exclude var picUrl:String?=null,
                        @Exclude var name:String?=null,
                        @Exclude var hUid:String?=null,
                        @Exclude var uUid:String?=null,
                        @ServerTimestamp var date : Date? = null)