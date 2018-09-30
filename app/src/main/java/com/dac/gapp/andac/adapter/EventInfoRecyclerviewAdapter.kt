package com.dac.gapp.andac.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.dac.gapp.andac.R
import com.dac.gapp.andac.extension.setPrice
import com.dac.gapp.andac.model.firebase.EventInfo
import com.dac.gapp.andac.viewholder.EventInfoViewHolder

class EventInfoRecyclerviewAdapter(var context: Context?, var list: List<EventInfo>) : RecyclerView.Adapter<EventInfoViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventInfoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.event_row, parent, false)
        return EventInfoViewHolder(context, view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: EventInfoViewHolder, position: Int) {
        val item: EventInfo = list[position]
        holder.title.text = item.title
        holder.sub_title.text = item.sub_title
        holder.body.text = item.body
        holder.deal_kind.text = item.deal_kind
        holder.price.setPrice(item.price)
        holder.likeCountText.text = item.likeCount.toString()
    }
}