package com.dac.gapp.andac.util

import android.support.v4.app.Fragment
import org.jetbrains.anko.toast

fun Fragment.toast(msg : String) = context?.toast(msg)