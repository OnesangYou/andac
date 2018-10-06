package com.dac.gapp.andac.model

import com.dac.gapp.andac.model.firebase.HospitalInfo
import com.dac.gapp.andac.model.firebase.UserInfo
import java.util.*

data class OpenConsultInfo(var user : UserInfo? = null,
                           var hospital : HospitalInfo? = null,
                           val createdTime : Date? = null,
                           var uUid : String? = null,
                           var hUid : String? = null,
                           var isOpen :Boolean? = null)