package com.dac.gapp.andac.util

import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Geocoder
import java.text.ParseException
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

        fun getDate(year: Int, month: Int, date: Int): Date {
            val calendar = Calendar.getInstance()
            calendar.set(year, month - 1, date, 0, 0, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            return calendar.time
        }
    }
}