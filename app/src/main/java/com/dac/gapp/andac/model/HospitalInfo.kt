package com.dac.gapp.andac.model

import com.google.android.gms.maps.model.LatLng

data class HospitalInfo(
        var id: String,
        var lat: Double,
        var lng: Double,
        var address1: String,
        var address2: String,
        var name: String,
        var number: Long,
        var openDate: String,
        var phone: String,
        var status: String,
        var type: String) {
    constructor(id: String, data: Map<String, Any>) :
            this(
                    id,
                    (data["_geoloc"] as Map<String, Any>)["lat"] as Double,
                    (data["_geoloc"] as Map<String, Any>)["lng"] as Double,
                    data["address1"] as String,
                    data["address2"] as String,
                    data["name"] as String,
                    data["number"] as Long,
                    data["openDate"] as String,
                    data["phone"] as String,
                    data["status"] as String,
                    data["type"] as String
            )

    fun getLatLng(): LatLng {
        return LatLng(lat, lng)
    }
}