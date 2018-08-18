package com.dac.gapp.andac.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import com.dac.gapp.andac.R
import kotlinx.android.synthetic.main.dialog_phone_number.*

class PhoneNumberDialog(context: Context) : Dialog(context) {

    private var mOnCancelListener: View.OnClickListener? = null
    private var mOnConfirmListener: View.OnClickListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_phone_number)
        mOnCancelListener?.let { btnCancel.setOnClickListener(mOnCancelListener) }
        mOnConfirmListener?.let { btnConfirm.setOnClickListener(mOnConfirmListener) }
    }

    fun setOnCancelListener(listener: View.OnClickListener): PhoneNumberDialog {
        mOnCancelListener = listener
        return this
    }

    fun setOnConfirmListener(listener: View.OnClickListener): PhoneNumberDialog {
        mOnConfirmListener = listener
        return this
    }

    fun getText(): String {
        return edittxt?.text.toString()
    }
}