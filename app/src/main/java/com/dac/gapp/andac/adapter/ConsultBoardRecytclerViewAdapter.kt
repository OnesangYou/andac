package com.dac.gapp.andac.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.dac.gapp.andac.R
import com.dac.gapp.andac.model.OpenConsultInfo
import com.dac.gapp.andac.viewholder.ConsultBoardViewHolder

class ConsultBoardRecytclerViewAdapter(var context: Context?, var list: List<OpenConsultInfo>) : RecyclerView.Adapter<ConsultBoardViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConsultBoardViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.consult_board_row, parent, false)

        return ConsultBoardViewHolder(context, view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ConsultBoardViewHolder, position: Int) {
        holder.binding.usermodel = list.get(position)
        holder.binding.holder = holder
    }

}