package com.dac.gapp.andac.viewholder

import android.content.Context
import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.View
import com.dac.gapp.andac.databinding.ConsultBoardRowBinding

class ConsultBoardViewHolder(var context: Context?, var view: View) : RecyclerView.ViewHolder(view) {

    var binding: ConsultBoardRowBinding = DataBindingUtil.bind(view)!!
}