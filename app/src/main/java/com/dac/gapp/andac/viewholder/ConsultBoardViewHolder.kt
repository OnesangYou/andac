package com.dac.gapp.andac.viewholder

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.dac.gapp.andac.R

class ConsultBoardViewHolder(var context : Context?, view : View) : RecyclerView.ViewHolder(view) {

    val date : TextView = view.findViewById(R.id.date)
    val name: TextView = view.findViewById(R.id.name)
    val phone: TextView = view.findViewById(R.id.phone)
    val view_consult: TextView = view.findViewById(R.id.view_consult)
    val start_consult: TextView = view.findViewById(R.id.start_consult)

}