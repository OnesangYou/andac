package com.dac.gapp.andac.model

import com.dac.gapp.andac.model.firebase.BoardInfo
import com.dac.gapp.andac.model.firebase.HospitalInfo
import com.dac.gapp.andac.model.firebase.UserInfo

data class BoardAdapterData(
        var boardInfos : List<BoardInfo> = listOf(),
        var userInfoMap : Map<String, UserInfo> = mapOf(),
        var hospitalInfoMap : Map<String, HospitalInfo> = mapOf(),
        var likeSet : Set<String> = setOf()
)