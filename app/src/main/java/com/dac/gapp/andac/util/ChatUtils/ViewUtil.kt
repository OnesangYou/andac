package com.dac.gapp.andac.util.ChatUtils

import java.util.concurrent.atomic.AtomicInteger

class ViewUtil @Throws(InstantiationException::class)
private constructor() {

    init {
        throw InstantiationException("This class is not for instantiation")
    }

    companion object {

        private val sNextGeneratedId = AtomicInteger(1)


        fun generateViewId(): Int {
            while (true) {
                val result = sNextGeneratedId.get()
                // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
                var newValue = result + 1
                if (newValue > 0x00FFFFFF) newValue = 1 // Roll over to 1, not 0.
                if (sNextGeneratedId.compareAndSet(result, newValue)) {
                    return result
                }
            }
        }
    }
}
