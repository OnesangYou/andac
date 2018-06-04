package com.dac.gapp.andac.model.firebase

import com.dac.gapp.andac.model.Algolia
import com.google.android.gms.maps.model.LatLng
import org.json.JSONObject
import java.io.Serializable
import java.util.*

class HospitalInfo : Serializable {
    // static method
    companion object {
        fun create(jo: JSONObject): HospitalInfo {
            val hospitalInfo = HospitalInfo()
            hospitalInfo.documentId = jo.getString(Algolia.OBJECT_ID.value)
            hospitalInfo.address1 = jo.getString(Algolia.ADDRESS1.value)
            hospitalInfo.address2 = jo.getString(Algolia.ADDRESS2.value)
            hospitalInfo.name = jo.getString(Algolia.NAME.value)
            hospitalInfo.number = jo.getInt(Algolia.NUMBER.value)
            hospitalInfo.openDate = jo.getString(Algolia.OPEN_DATE.value)
            hospitalInfo.phone = jo.getString(Algolia.PHONE.value)
            hospitalInfo.status = jo.getString(Algolia.STATUS.value)
            hospitalInfo.type = jo.getString(Algolia.TYPE.value)
            val geoLocation = GeoLocation()
            geoLocation.lat = jo.getJSONObject(Algolia.GEOLOC.value).getDouble(Algolia.LAT.value)
            geoLocation.lng = jo.getJSONObject(Algolia.GEOLOC.value).getDouble(Algolia.LNG.value)
            hospitalInfo._geoloc = geoLocation
            return hospitalInfo
        }
    }

    var documentId: String = ""
    var address1: String = ""
    var address2: String = ""
    var name: String = ""
    var number: Int = 0
    var openDate: String = ""
    var phone: String = ""
    var status: String = ""
    var type: String = ""
    var _geoloc: GeoLocation = GeoLocation()


    var description: String = ""
    var email: String = ""
    var cellPhone: String = ""
    var isAgreeAlarm: Boolean = false
    var isApproval: Boolean = false
    var createDate: Date? = null
    var thumbnail: Int = 0

    var profilePicUrl: String = ""
    var profilePicRef: String = ""

    var bankAccountPicUrl: String = ""
    var bankAccountPicRef: String = ""

    var busiRegiPicUrl: String = ""
    var busiRegiPicRef: String = ""

    fun getLatLng(): LatLng {
        return LatLng(_geoloc.lat!!, _geoloc.lng!!)
    }
}
