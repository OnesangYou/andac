package com.dac.gapp.andac.extension

import android.annotation.SuppressLint
import android.databinding.BindingAdapter
import android.widget.TextView
import java.text.DecimalFormat


@SuppressLint("SetTextI18n")
@BindingAdapter("bind:likeCnt")
fun TextView.likeCnt(cnt : Int) {
    this.text = "좋아요 $cnt 개"
}

@SuppressLint("SetTextI18n")
@BindingAdapter("bind:replyCnt")
fun TextView.replyCnt(cnt : Int) {
    this.text = "댓글 $cnt 개"
}

@SuppressLint("SetTextI18n")
fun TextView.setPrice(price : Int, default : String = "병원문의"){
    if(price == 0) {
        this.text = default
    } else {
        this.text = DecimalFormat("#,###").format(price) + "원"
    }
}