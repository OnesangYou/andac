package com.dac.gapp.andac.extension

import android.view.View

fun View.visibleOrGone(visibleOrGone: Boolean) {
    visibility = if (visibleOrGone) View.VISIBLE else View.GONE
}

fun View.visibleOrGone(visibleOrGone: Boolean, vararg views: View?) {
    for (view in views) {
        view?.visibility = if (visibleOrGone) View.VISIBLE else View.GONE
    }
}