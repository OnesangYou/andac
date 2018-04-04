package com.dac.gapp.andac.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.dac.gapp.andac.HospitalActivity
import com.dac.gapp.andac.R
import com.dac.gapp.andac.model.SearchHospitalItem
import com.dac.gapp.andac.viewholder.SearchHospitalViewHolder

class SearchHospitalAdapter(private var context: Context, private var itemList: List<SearchHospitalItem>) : RecyclerView.Adapter<SearchHospitalViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): SearchHospitalViewHolder {
        var view = LayoutInflater.from(parent!!.context).inflate(R.layout.row, parent, false)
        return SearchHospitalViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchHospitalViewHolder?, position: Int) {
        val item: SearchHospitalItem = itemList[position]
        holder!!.thumbnail.setImageResource(item.thumbnail)
        holder.title.text = item.title
        holder.address.text = item.address
        holder.description.text = item.description
        holder.itemView.setOnClickListener {
            context.startActivity(HospitalActivity.createIntent(context, item.hospitalId))
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}