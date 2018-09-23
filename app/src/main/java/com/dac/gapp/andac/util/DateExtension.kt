package com.dac.gapp.andac.util

import java.text.SimpleDateFormat
import java.util.*

fun Date.getFullFormat(): String? = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.KOREA).format(this)
fun Date.getDateFormat(char: Char): String? = SimpleDateFormat("yy${char}MM${char}dd", Locale.KOREA).format(this)
fun Date.getDateFormat(pattern: String): String? = SimpleDateFormat(pattern, Locale.KOREA).format(this)
