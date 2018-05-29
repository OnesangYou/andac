package com.dac.gapp.andac.viewholder

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.dac.gapp.andac.R

class EventInfoViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    var title: TextView = view.findViewById(R.id.title)
    var sub_title: TextView = view.findViewById(R.id.sub_title)
    var body: TextView = view.findViewById(R.id.body)
    var deal_kind: TextView = view.findViewById(R.id.deal_kind)
    var price: TextView = view.findViewById(R.id.price)
    var buy_count: TextView = view.findViewById(R.id.buy_count)
}