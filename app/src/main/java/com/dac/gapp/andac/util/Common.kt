package com.dac.gapp.andac.util

import android.content.Context
import android.location.Address
import android.location.Geocoder

class Common {
    companion object {
        fun getFromLocationName(context: Context?, address: String): Address? {
            val addressList = Geocoder(context).getFromLocationName(address, 5)
            if (addressList.size > 0) {
                return addressList[0]
            }
            return null
        }
    }
}