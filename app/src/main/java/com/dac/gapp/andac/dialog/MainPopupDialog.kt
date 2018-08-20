package com.dac.gapp.andac.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.dac.gapp.andac.R
import kotlinx.android.synthetic.main.dialog_main_popup.*

class MainPopupDialog(context: Context) : Dialog(context) {

    private var mImageUrl: String? = null
    private var mOnImageClickListener: View.OnClickListener? = null
    private var mOnCancelListener: View.OnClickListener? = null
    private var mOnConfirmListener: View.OnClickListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_main_popup)
        mImageUrl?.let { Glide.with(context).load(mImageUrl).into(imgview) }
        mOnImageClickListener?.let { btnCancel.setOnClickListener(mOnImageClickListener) }
        mOnCancelListener?.let { btnCancel.setOnClickListener(mOnCancelListener) }
        mOnConfirmListener?.let { btnConfirm.setOnClickListener(mOnConfirmListener) }
    }

    fun setImageUrl(imageUrl: String): MainPopupDialog {
        mImageUrl = imageUrl
        return this
    }

    fun setOnImageClickListener(listener: View.OnClickListener): MainPopupDialog {
        mOnCancelListener = listener
        return this
    }

    fun setOnCancelListener(listener: View.OnClickListener): MainPopupDialog {
        mOnCancelListener = listener
        return this
    }

    fun setOnConfirmListener(listener: View.OnClickListener): MainPopupDialog {
        mOnConfirmListener = listener
        return this
    }
}