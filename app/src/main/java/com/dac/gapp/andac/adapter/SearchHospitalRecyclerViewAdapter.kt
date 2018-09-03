package com.dac.gapp.andac.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.dac.gapp.andac.HospitalActivity
import com.dac.gapp.andac.R
import com.dac.gapp.andac.model.firebase.HospitalInfo
import com.dac.gapp.andac.util.MyToast
import com.dac.gapp.andac.viewholder.SearchHospitalViewHolder

class SearchHospitalRecyclerViewAdapter(private var context: Context?, private var itemList: List<HospitalInfo>) : RecyclerView.Adapter<SearchHospitalViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchHospitalViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row, parent, false)
        return SearchHospitalViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchHospitalViewHolder, position: Int) {
        val item = itemList[position]
        if (item.profilePicUrl.isNotEmpty()) {
            Glide.with(context).load(item.profilePicUrl).into(holder.thumbnail)
        } else {
            Glide.with(context).load(R.drawable.defult_pic_1).into(holder.thumbnail)
        }
        holder.title.text = item.name
        holder.address.text = item.address1
        holder.description.text = item.description
        holder.itemView.setOnClickListener {
            context!!.startActivity(HospitalActivity.createIntent(context!!, item))
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}