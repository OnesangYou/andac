package com.dac.gapp.andac

import android.app.AlertDialog
import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.LayoutInflater
import com.dac.gapp.andac.databinding.DialogRatingBarBinding

class RatingBarDialog(context: Context, val confirm : (Float)-> Unit) : AlertDialog(context) {

    var binding : DialogRatingBarBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_rating_bar, null, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.btnConfirm.setOnClickListener {
            confirm.invoke(binding.ratingBar.rating)
            dismiss()
        }
    }
}