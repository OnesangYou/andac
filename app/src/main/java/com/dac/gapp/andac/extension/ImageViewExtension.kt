package com.dac.gapp.andac.extension

import android.databinding.BindingAdapter
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target


@BindingAdapter("bind:imageUrl")
fun ImageView.loadImage(url : String): Target<Drawable> = Glide.with(this.context).load(url).into(this)