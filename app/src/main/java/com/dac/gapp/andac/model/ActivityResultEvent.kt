package com.dac.gapp.andac.model

import android.content.Intent

data class ActivityResultEvent (
        val requestCode : Int,
        val resultCode : Int,
        val data : Intent?
)