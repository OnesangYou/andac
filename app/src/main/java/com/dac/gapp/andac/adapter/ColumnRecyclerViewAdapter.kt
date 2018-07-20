package com.dac.gapp.andac.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.dac.gapp.andac.R
import com.dac.gapp.andac.base.BaseActivity
import com.dac.gapp.andac.model.firebase.ColumnInfo
import com.dac.gapp.andac.model.firebase.HospitalInfo
import kotlinx.android.synthetic.main.column_item.view.*

class ColumnRecyclerViewAdapter
(var context : BaseActivity, var mDataList :List<ColumnInfo>, private var hospitalInfoMap: Map<String, HospitalInfo?>) : RecyclerView.Adapter<ColumnRecyclerViewAdapter.ColumnHolder>(){
    override fun getItemCount(): Int {
        return mDataList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColumnHolder {
        return ColumnHolder(parent)
    }

    override fun onBindViewHolder(holder: ColumnHolder, position: Int) {
        val item = mDataList[position]
        val hospital = hospitalInfoMap[item.writerUid]

        with(holder){
            titleText.text = item.title
            hospitalName.text = hospital?.name
            viewCount.text = item.viewCount.toString()
            Glide.with(context).load(item.pictureUrl).into(picture)
        }
    }

    class ColumnHolder(parent: ViewGroup) : AndroidExtensionsViewHolder(
            LayoutInflater.from(parent.context)
                    .inflate(R.layout.column_item, parent, false)) {

        val titleText: TextView = itemView.titleText
        val picture: ImageView = itemView.picture
        val hospitalName: TextView = itemView.hospitalName
        val viewCount: TextView = itemView.viewCount
    }

}