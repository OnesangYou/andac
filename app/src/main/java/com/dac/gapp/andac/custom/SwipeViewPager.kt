package com.dac.gapp.andac.custom

import android.annotation.SuppressLint
import android.content.Context
import androidx.core.view.MotionEventCompat.getActionMasked
import androidx.viewpager.widget.ViewPager
import android.util.AttributeSet
import android.view.MotionEvent

@Suppress("DEPRECATED_IDENTITY_EQUALS")
/**
 * Created by gimbyeongjin on 2018. 4. 2..
 */
class SwipeViewPager : ViewPager {
    internal var enabled: Boolean = false

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        if (enabled) {
            return super.onInterceptTouchEvent(ev)
        } else {
            if (getActionMasked(ev) === MotionEvent.ACTION_MOVE) {
                // ignore move action
            } else {
                if (super.onInterceptTouchEvent(ev)) {
                    super.onTouchEvent(ev)
                }
            }
            return false
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent): Boolean {
        return if (enabled) {
            super.onTouchEvent(ev)
        } else {
            getActionMasked(ev) !== MotionEvent.ACTION_MOVE && super.onTouchEvent(ev)
        }
    }

    fun setPagingEnabled(enabled: Boolean) {
        this.enabled = enabled
    }
}
