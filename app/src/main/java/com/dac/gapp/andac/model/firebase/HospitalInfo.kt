package com.dac.gapp.andac.model.firebase

data class HospitalInfo(var hospitalId: Int, var thumbnail: Int, var title: String, var address: String, var description: String, var _geoloc: GeoLocation = GeoLocation())