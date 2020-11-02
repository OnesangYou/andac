package com.dac.gapp.andac.util.chatUtils


import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Outline
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.annotation.RequiresApi

class ChatMessageDrawable(backgroundColor: Int, radius: Float) : Drawable() {
    private var mPaint: Paint
    private var mBoundsF: RectF
    private var mBoundsI: Rect
    private var mRadius: Float = 0.toFloat()
    private var mPadding: Float = 0.toFloat()
    private var mInsetForPadding: Boolean = false
    private var mInsetForRadius = true

    init {
        mRadius = radius
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG)
        mPaint.color = backgroundColor
        mBoundsF = RectF()
        mBoundsI = Rect()
    }

    internal fun setPadding(padding: Float, insetForPadding: Boolean, insetForRadius: Boolean) {
        if (padding == mPadding && mInsetForPadding == insetForPadding &&
                mInsetForRadius == insetForRadius) {
            return
        }
        mPadding = padding
        mInsetForPadding = insetForPadding
        mInsetForRadius = insetForRadius
        updateBounds(null)
        invalidateSelf()
    }

    internal fun getPadding(): Float {
        return mPadding
    }

    override fun draw(canvas: Canvas) {
        canvas.drawRoundRect(mBoundsF, mRadius, mRadius, mPaint)
    }

    private fun updateBounds(bounds: Rect?) {
        var bounds = bounds
        if (bounds == null) {
            bounds = getBounds()
        }
        mBoundsF.set(bounds!!.left.toFloat(), bounds.top.toFloat(), bounds.right.toFloat(), bounds.bottom.toFloat())
        mBoundsI.set(bounds)
    }

    protected override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)
        updateBounds(bounds)
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    override fun getOutline(outline: Outline) {
        outline.setRoundRect(mBoundsI, mRadius)
    }

    override fun setAlpha(alpha: Int) {
        // not supported because older versions do not support
    }

    override fun setColorFilter(cf: ColorFilter) {
        // not supported because older versions do not support
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

    fun getRadius(): Float {
        return mRadius
    }

    internal fun setRadius(radius: Float) {
        if (radius == mRadius) {
            return
        }
        mRadius = radius
        updateBounds(null)
        invalidateSelf()
    }

    fun setColor(color: Int) {
        mPaint.color = color
        invalidateSelf()
    }

    fun getPaint(): Paint {
        return mPaint
    }
}