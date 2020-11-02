package com.dac.gapp.andac.viewholder

import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import com.dac.gapp.andac.databinding.ChatMineMessageRowBinding

class ChatMineMessageHolder(view : View) : RecyclerView.ViewHolder(view){
    var binding : ChatMineMessageRowBinding =  DataBindingUtil.bind(view)!!
}