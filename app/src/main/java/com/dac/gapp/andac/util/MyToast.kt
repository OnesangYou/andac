package com.dac.gapp.andac.util

import android.content.Context
import android.widget.Toast

class MyToast {
    companion object {
        fun show(context: Context?, msg: String) {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    }
}