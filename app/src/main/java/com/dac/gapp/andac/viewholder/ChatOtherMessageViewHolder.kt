package com.dac.gapp.andac.viewholder

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.View
import com.dac.gapp.andac.databinding.ChatOtherMessageRowBinding

class ChatOtherMessageViewHolder(view :View) : RecyclerView.ViewHolder(view) {
        var binding : ChatOtherMessageRowBinding = DataBindingUtil.bind(view)!!
}