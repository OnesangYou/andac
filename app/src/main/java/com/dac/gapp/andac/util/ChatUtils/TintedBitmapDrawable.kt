package com.dac.gapp.andac.util.ChatUtils


import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LightingColorFilter
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable

class TintedBitmapDrawable : BitmapDrawable {
    private var tint: Int = 0
    internal var alpha: Int = 0

    constructor(res: Resources, bitmap: Bitmap, tint: Int) : super(res, bitmap) {
        this.tint = tint
        this.alpha = Color.alpha(tint)
    }

    constructor(res: Resources, resId: Int, tint: Int) : super(res, BitmapFactory.decodeResource(res, resId)) {
        this.tint = tint
        this.alpha = Color.alpha(tint)
    }

    override fun setTint(tint: Int) {
        this.tint = tint
        this.alpha = Color.alpha(tint)

        invalidateSelf()
    }

    override fun draw(canvas: Canvas) {
        val paint = paint
        if (paint.colorFilter == null) {
            paint.colorFilter = LightingColorFilter(tint, 0)
            paint.alpha = alpha
        }
        super.draw(canvas)
    }
}