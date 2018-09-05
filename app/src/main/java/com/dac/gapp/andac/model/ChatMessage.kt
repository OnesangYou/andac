package com.dac.gapp.andac.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.*

/**
 * Created by godueol on 2018. 4. 28..
 */
data class ChatMessage(var text : String = "",
                       var writer : String = "",
                       var img : String? = null,
                       @ServerTimestamp var date : Date? = null
                       )