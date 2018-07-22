package com.dac.gapp.andac.util

import android.view.View

class UiUtil {
    companion object {
        fun visibleOrGone(visibleOrGone: Boolean, vararg views: View) {
            for (view in views) {
                view.visibility = if (visibleOrGone) View.VISIBLE else View.GONE
            }

        }
    }

}