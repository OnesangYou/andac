package com.dac.gapp.andac.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.dac.gapp.andac.R
import com.dac.gapp.andac.model.EventInfo
import com.dac.gapp.andac.viewholder.EventInfoViewHolder

class EventInfoRecyclerviewAdapter(var context: Context?, var list: List<EventInfo>) : RecyclerView.Adapter<EventInfoViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventInfoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.event_row, parent, false)
        return EventInfoViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }


    override fun onBindViewHolder(holder: EventInfoViewHolder, position: Int) {
        val item: EventInfo = list.get(position)
        holder.title.text = item.title
        holder.title.text = item.sub_title
        holder.title.text = item.body
        holder.title.text = item.deal_kind
        holder.title.text = item.price
        holder.title.text = item.buy_count
    }

}