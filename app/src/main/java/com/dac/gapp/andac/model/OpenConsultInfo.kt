package com.dac.gapp.andac.model

import com.dac.gapp.andac.model.firebase.UserInfo
import java.util.*

data class OpenConsultInfo(val user : UserInfo? = null,
                           val createdTime : Date? = null,
                           var uUid : String? = null,
                           var hUid : String? = null,
                           var isOpen :Boolean? = null)