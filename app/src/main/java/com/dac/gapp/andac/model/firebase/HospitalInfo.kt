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
            hospitalInfo.documentId = JsonUtil.getString(jo, Algolia.OBJECT_ID.value)
            hospitalInfo.address1 = JsonUtil.getString(jo, Algolia.ADDRESS1.value)
            hospitalInfo.address2 = JsonUtil.getString(jo, Algolia.ADDRESS2.value)
            hospitalInfo.name = JsonUtil.getString(jo, Algolia.NAME.value)
            hospitalInfo.number = JsonUtil.getInt(jo, Algolia.NUMBER.value)
            hospitalInfo.openDate = JsonUtil.getString(jo, Algolia.OPEN_DATE.value)
            hospitalInfo.phone = JsonUtil.getString(jo, Algolia.PHONE.value)
            hospitalInfo.status = JsonUtil.getString(jo, Algolia.STATUS.value)
            hospitalInfo.type = JsonUtil.getString(jo, Algolia.TYPE.value)
            hospitalInfo.profilePicUrl = JsonUtil.getString(jo, "profilePicUrl")
            val geoLocation = GeoLocation()
            val geoJO = JsonUtil.getObject(jo, Algolia.GEOLOC.value)
            geoLocation.lat = JsonUtil.getDouble(geoJO, Algolia.LAT.value)
            geoLocation.lng = JsonUtil.getDouble(geoJO, Algolia.LNG.value)
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

    var requestStr: String = "관리자 승인 요청 중입니다"

    fun getLatLng(): LatLng {
        return LatLng(_geoloc.lat!!, _geoloc.lng!!)
    }
}
