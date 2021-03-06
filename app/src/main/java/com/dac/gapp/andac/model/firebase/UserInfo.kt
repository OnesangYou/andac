package com.dac.gapp.andac.model.firebase

import java.util.*

/**
 * Created by gimbyeongjin on 2018. 4. 4..
 */
data class UserInfo(
        var email : String = "",
        var nickName : String = "",
        var phoneNumber : String = "",
        var cellPhone : String = "",
        var profilePicUrl : String = "",
        var isAgreeAlarm : Boolean = false,
        var sex : String = "남자",
        var birthDate : Date? = null

)