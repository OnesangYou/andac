package com.dac.gapp.andac.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.dac.gapp.andac.R
import com.dac.gapp.andac.viewholder.ConsultBoardViewHolder

class ConsultBoardRecytclerViewAdapter(var context: Context?, var list: List<String>) : RecyclerView.Adapter<ConsultBoardViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConsultBoardViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.consult_board_row, parent, false)

        return ConsultBoardViewHolder(context, view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ConsultBoardViewHolder, position: Int) {
        var item = list.get(position);
        holder.date.text = item
    }

}