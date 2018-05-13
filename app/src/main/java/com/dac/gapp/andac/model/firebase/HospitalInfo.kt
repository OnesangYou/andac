package com.dac.gapp.andac.model.firebase

import com.google.android.gms.maps.model.LatLng
import java.util.*

class HospitalInfo {
    var _geoloc: GeoLocation = GeoLocation()
    var address1: String = ""
    var address2: String = ""
    var name: String = ""

    var openDate: String = ""
    var phone: String = ""
    var status: String = ""
    var type: String = ""

    var description: String = ""
    var email: String = ""
    var cellPhone: String = ""
    var isAgreeAlarm: Boolean = false
    var isApproval: Boolean = false
    var createDate: Date? = null
    var thumbnail: Int = 0
    var documentId: String = ""

    public fun getLatLng() : LatLng {
        return LatLng(_geoloc.lat!!, _geoloc.lng!!)
    }
}