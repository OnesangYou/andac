package com.dac.gapp.andac.dialog

import android.app.Dialog
import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.LayoutInflater
import com.dac.gapp.andac.R
import com.dac.gapp.andac.databinding.DialogConsultBinding
import com.dac.gapp.andac.model.firebase.ConsultInfo

class ConsultContentDialog(context: Context, private val consultInfo: ConsultInfo?) : Dialog(context) {
    lateinit var binding: DialogConsultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_consult, null, false)
        setContentView(binding.root)
        binding.consultInfo = consultInfo
    }
}