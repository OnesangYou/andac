package com.dac.gapp.andac.model.firebase

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

}