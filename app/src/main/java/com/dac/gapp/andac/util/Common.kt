package com.dac.gapp.andac.util

import android.content.Context
import android.location.Address
import android.location.Geocoder
import java.text.SimpleDateFormat
import java.util.*

class Common {
    companion object {
        fun getFromLocationName(context: Context?, address: String): Address? {
            val addressList = Geocoder(context).getFromLocationName(address, 5)
            if (addressList.size > 0) {
                return addressList[0]
            }
            return null
        }

        fun getDateFormat(date : Date): String {
            val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.KOREA)
            return sdf.format(date)
        }
    }
}