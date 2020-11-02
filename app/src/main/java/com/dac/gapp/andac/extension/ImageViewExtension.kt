package com.dac.gapp.andac.extension

import android.app.Activity
import androidx.databinding.BindingAdapter
import android.widget.ImageView
import com.bumptech.glide.Glide


@BindingAdapter("imageUrl", "thumbUrl", "defaultSrc", requireAll = false)
fun ImageView.loadImage(imageUrl: String?, thumbUrl: String? = null, default: Any? = null) {
    val url = thumbUrl?:imageUrl
    val model = if (url.isNullOrEmpty()) default else url
    model?:return
    loadImageAny(model)
}

fun ImageView.loadImageAny(model: Any) {
    if(this.context is Activity && (this.context as Activity).isFinishing) return
    this.tag = null
    Glide.with(this.context).load(model).into(this)
}