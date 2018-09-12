package com.dac.gapp.andac.extension

import android.databinding.BindingAdapter
import android.widget.ImageView
import com.bumptech.glide.Glide


@BindingAdapter("bind:imageUrl")
fun ImageView.loadImage(url: String?) {
    if (url.isNullOrEmpty()) return
    Glide.with(this.context).load(url).into(this)
}

fun ImageView.loadImageAny(model: Any) {
    Glide.with(this.context).load(model).into(this)
}