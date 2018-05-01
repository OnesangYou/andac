package com.dac.gapp.andac.viewholder

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.dac.gapp.andac.R

class SearchHospitalViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    var thumbnail: ImageView = view.findViewById(R.id.imgview_thumbnail)
    var title: TextView = view.findViewById(R.id.txtview_title)
    var address: TextView = view.findViewById(R.id.txtview_address)
    var description: TextView = view.findViewById(R.id.txtview_phone)
}