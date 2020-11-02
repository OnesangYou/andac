package com.dac.gapp.andac.util

import androidx.core.app.Fragment
import org.jetbrains.anko.toast

fun Fragment.toast(msg : String) = context?.toast(msg)