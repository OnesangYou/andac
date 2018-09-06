package com.dac.gapp.andac.model.firebase

import com.google.firebase.firestore.ServerTimestamp
import java.util.*

data class NoticeInfo(@ServerTimestamp var date: Date? = null,
                      var title: String? = null,
                      var content: String? = null)