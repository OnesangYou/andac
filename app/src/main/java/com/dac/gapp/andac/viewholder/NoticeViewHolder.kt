package com.dac.gapp.andac.viewholder

import androidx.databinding.DataBindingUtil
import android.view.View
import com.dac.gapp.andac.databinding.NoticeRowBinding
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder


class NoticeViewHolder(itemView : View) : ChildViewHolder(itemView) {

    var binding: NoticeRowBinding = DataBindingUtil.bind(itemView)!!

}