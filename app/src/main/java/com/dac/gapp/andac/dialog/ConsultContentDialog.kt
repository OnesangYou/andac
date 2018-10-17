package com.dac.gapp.andac.dialog

import android.app.AlertDialog
import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.LayoutInflater
import com.dac.gapp.andac.R
import com.dac.gapp.andac.databinding.DialogConsultBinding
import com.dac.gapp.andac.model.firebase.ConsultInfo

class ConsultContentDialog(context: Context, private val consultInfo: ConsultInfo?) : AlertDialog(context) {
    var binding: DialogConsultBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_consult, null, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.consultInfo = consultInfo
    }
}