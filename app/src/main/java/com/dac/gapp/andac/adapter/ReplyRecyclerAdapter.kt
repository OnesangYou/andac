package com.dac.gapp.andac.adapter

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.dac.gapp.andac.R
import com.dac.gapp.andac.databinding.ReplyItemBinding
import com.dac.gapp.andac.model.firebase.ReplyInfo
import com.dac.gapp.andac.model.firebase.SomebodyInfo

class ReplyRecyclerAdapter
(private var mDataList: List<ReplyInfo>, private var somebodyInfoMap: Map<String, SomebodyInfo>) : RecyclerView.Adapter<ReplyRecyclerAdapter.ReplyHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReplyHolder {
        return ReplyHolder(parent)
    }

    override fun onBindViewHolder(holder: ReplyHolder, position: Int) {
        holder.binding?.apply {
            replyInfo = mDataList[position]
            somebodyInfo = somebodyInfoMap[replyInfo?.writerUid]
        }
    }

    override fun getItemCount(): Int {
        return mDataList.size
    }

    class ReplyHolder(parent: ViewGroup) : AndroidExtensionsViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.reply_item, parent, false)) {

        var binding : ReplyItemBinding? = DataBindingUtil.bind(itemView)




    }
}
