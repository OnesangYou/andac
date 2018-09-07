package com.dac.gapp.andac.model.firebase

import com.dac.gapp.andac.enums.Algolia
import com.dac.gapp.andac.util.JsonUtil
import com.google.android.gms.maps.model.LatLng
import org.json.JSONObject
import java.io.Serializable
import java.util.*

class HospitalInfo : Serializable {
    // static method
    companion object {
        fun create(jo: JSONObject): HospitalInfo {
            val hospitalInfo = HospitalInfo()
            hospitalInfo.documentId = JsonUtil.getData(jo, Algolia.OBJECT_ID.value)
            hospitalInfo.address1 = JsonUtil.getData(jo, Algolia.ADDRESS1.value)
            hospitalInfo.address2 = JsonUtil.getData(jo, Algolia.ADDRESS2.value)
            hospitalInfo.name = JsonUtil.getData(jo, Algolia.NAME.value)
            hospitalInfo.number = JsonUtil.getData(jo, Algolia.NUMBER.value)
            hospitalInfo.openDate = JsonUtil.getData(jo, Algolia.OPEN_DATE.value)
            hospitalInfo.phone = JsonUtil.getData(jo, Algolia.PHONE.value)
            hospitalInfo.status = JsonUtil.getData(jo, Algolia.STATUS.value)
            hospitalInfo.type = JsonUtil.getData(jo, Algolia.TYPE.value)
            hospitalInfo.profilePicUrl = JsonUtil.getData(jo, "profilePicUrl")
            val geoLocation = GeoLocation()
            val geoJO = JsonUtil.getObject(jo, Algolia.GEOLOC.value)
            geoLocation.lat = JsonUtil.getData(geoJO, Algolia.LAT.value)
            geoLocation.lng = JsonUtil.getData(geoJO, Algolia.LNG.value)
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

    fun getLatLng(): LatLng {
        return LatLng(_geoloc.lat!!, _geoloc.lng!!)
    }
}
