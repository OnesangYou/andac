package com.dac.gapp.andac.model.firebase

import com.google.firebase.firestore.ServerTimestamp
import java.util.*

data class ChatListInfo(var roomId: String? = "",
                        var lastchat: String? = null,
                        @ServerTimestamp var date : Date? = null)