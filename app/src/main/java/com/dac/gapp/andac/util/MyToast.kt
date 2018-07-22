package com.dac.gapp.andac.util

import android.content.Context
import android.widget.Toast
import timber.log.Timber

class MyToast {
    companion object {
        fun showShort(context: Context?, msg: String) {
            if (context == null) {
                Timber.e("context is null!!")
                return
            }
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    }
}