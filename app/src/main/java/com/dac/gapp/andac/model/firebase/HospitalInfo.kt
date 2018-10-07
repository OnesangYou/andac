package com.dac.gapp.andac.model.firebase

import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.ServerTimestamp
import com.google.gson.Gson
import org.json.JSONObject
import java.io.Serializable
import java.util.*

class HospitalInfo : Serializable {
    // static method
    companion object {
        fun create(jo: JSONObject) = Gson().fromJson(jo.toString(), HospitalInfo::class.java)
    }

    var address1: String = ""
    var address2: String = ""
    var name: String = ""
    var number: Int = 0
    var phone: String = ""
    var openDate: String = ""
    var businessHours: String = ""
    var status: String = ""
    var type: String = ""
    var _geoloc: GeoLocation = GeoLocation()


    var description: String = ""
    var email: String = ""
    var cellPhone: String = ""
    var isAgreeAlarm: Boolean = false
    var approval: Boolean = false
    @ServerTimestamp
    var createdDate: Date? = null
    var thumbnail: Int = 0

    var profilePicUrl: String = ""
    var profilePicRef: String = ""

    var bankAccountPicUrl: String = ""
    var bankAccountPicRef: String = ""

    var busiRegiPicUrl: String = ""
    var busiRegiPicRef: String = ""

    var objectID: String = ""

    var busniss_id: String = ""

    var bankName: String = ""
    var bankAccountNumber: String = ""
    var bankAccountMaster: String = ""

    var requestStr: String? = "관리자 승인 요청 중입니다"
    var likeCount : Int = 0

    var isUltraAd : Boolean = false

    fun getLatLng(): LatLng {
        return LatLng(_geoloc.lat!!, _geoloc.lng!!)
    }
}
