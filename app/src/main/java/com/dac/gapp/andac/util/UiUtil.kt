package com.dac.gapp.andac.util

import android.view.View

class UiUtil {
    companion object {
        fun visibleOrGone(visibleOrGone: Boolean, vararg views: View?) {
            for (view in views) {
                view?.visibility = if (visibleOrGone) View.VISIBLE else View.GONE
            }
        }

        val VisionArr = DoubleArray(30) { i->i*0.1}.map { "%.1f".format(it) }.toTypedArray()
        fun getVisionIndex(vision : Double) : Int {
            VisionArr.forEachIndexed { index, s ->
                if(s == "%.1f".format(vision)) return index
            }
            return 10
        }
    }

}