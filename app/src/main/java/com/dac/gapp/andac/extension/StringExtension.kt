package com.dac.gapp.andac.extension

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*


@SuppressLint("SimpleDateFormat")
fun String.toDate(format : String): Date? = SimpleDateFormat(format).parse(this)