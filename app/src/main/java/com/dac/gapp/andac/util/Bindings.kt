package com.dac.gapp.andac.util

import android.annotation.SuppressLint
import android.databinding.BindingConversion
import android.view.View
import java.util.*


object Bindings{
    @SuppressLint("SimpleDateFormat")
    @BindingConversion
    @JvmStatic
    fun convertDateToText(date: Date?): String? {
        return date?.getFullFormat()?:"send"
    }

    @BindingConversion
    @JvmStatic
    fun convertBooleanToVisibility(b : Boolean) : Int{
        return if(b) View.VISIBLE else View.GONE
    }
}