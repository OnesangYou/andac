package com.dac.gapp.andac.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import com.dac.gapp.andac.R
import kotlinx.android.synthetic.main.dialog_my.*

class MyDialog(context: Context) : Dialog(context) {

    private var mOnCancelListener: View.OnClickListener? = null
    private var mOnConfirmListener: View.OnClickListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_my)
        if (mOnCancelListener != null) btnCancel.setOnClickListener(mOnCancelListener)
        if (mOnConfirmListener != null) btnConfirm.setOnClickListener(mOnConfirmListener)
    }

    fun setOnCancelListener(listener: View.OnClickListener): MyDialog {
        mOnCancelListener = listener
        return this
    }

    fun setOnConfirmListener(listener: View.OnClickListener): MyDialog {
        mOnConfirmListener = listener
        return this
    }

    fun getText(): String {
        return if (edittxt != null) edittxt.text.toString() else ""
    }
}