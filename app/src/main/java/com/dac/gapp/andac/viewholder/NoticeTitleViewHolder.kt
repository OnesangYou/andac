package com.dac.gapp.andac.viewholder

import androidx.databinding.DataBindingUtil
import android.view.View
import com.dac.gapp.andac.databinding.NoticeHeaderRowBinding
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder


class NoticeTitleViewHolder(itemView : View) : GroupViewHolder(itemView) {

    var binding: NoticeHeaderRowBinding = DataBindingUtil.bind(itemView)!!

}