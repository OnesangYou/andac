package com.dac.gapp.andac.extension

import android.databinding.BindingAdapter
import android.widget.ImageView
import com.bumptech.glide.Glide


@BindingAdapter("imageUrl", "defaultSrc", requireAll = false)
fun ImageView.loadImage(url: String?, default: Any? = null) {
    val model = if (url.isNullOrEmpty()) default else url
    model?:return
    loadImageAny(model)
}

fun ImageView.loadImageAny(model: Any) {
    this.tag = null
    Glide.with(this.context).load(model).into(this)
}