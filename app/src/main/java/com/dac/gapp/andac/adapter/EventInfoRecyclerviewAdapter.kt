package com.dac.gapp.andac.adapter

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dac.gapp.andac.EventActivity
import com.dac.gapp.andac.R
import com.dac.gapp.andac.model.EventDetail
import com.dac.gapp.andac.model.EventInfo
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
        val item: EventInfo = list.get(position)
        holder.title.text = item.title
        holder.sub_title.text = item.sub_title
        holder.body.text = item.body
        holder.deal_kind.text = item.deal_kind
        holder.price.text = item.price
        holder.buy_count.text = item.buy_count

        var eventDetail = EventDetail(item.title, item.sub_title, item.body, item.deal_kind, item.price, item.buy_count)

        holder.itemView.setOnClickListener({ v: View ->
            val nextIntent = Intent(context, EventActivity::class.java)
            nextIntent.putExtra("EventInfo", eventDetail)
            context!!.startActivity(nextIntent)
        })
    }
}