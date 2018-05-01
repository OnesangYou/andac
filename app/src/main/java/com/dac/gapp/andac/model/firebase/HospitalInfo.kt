package com.dac.gapp.andac.model.firebase

class HospitalInfo {
    var hospitalId: Int = 0
    var thumbnail: Int = 0
    lateinit var title: String
    lateinit var address2: String
    lateinit var description: String
    var _geoloc: GeoLocation = GeoLocation()
    lateinit var email: String
    lateinit var phoneNumber: String
    var isAgreeAlarm: Boolean = false
}