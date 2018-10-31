package com.dac.gapp.andac.util

import java.util.*

fun Long.getHourAndMin(callback : (hour : Int, min : Int) -> Unit) = Calendar.getInstance().let {
    it.timeInMillis = this
    val hour = it.get(Calendar.HOUR_OF_DAY)
    val min = it.get(Calendar.MINUTE)
    return@let callback(hour, min)
}
