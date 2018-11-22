package com.dac.gapp.andac.adapter

import android.annotation.SuppressLint
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.dac.gapp.andac.HospitalColumnListActivity
import com.dac.gapp.andac.R
import com.dac.gapp.andac.base.BaseActivity
import com.dac.gapp.andac.enums.Approval
import com.dac.gapp.andac.extension.loadImage
import com.dac.gapp.andac.model.firebase.ColumnInfo
import com.dac.gapp.andac.model.firebase.HospitalInfo
import kotlinx.android.synthetic.main.column_item.view.*
import com.dac.gapp.andac.util.UiUtil


class ColumnRecyclerAdapter
(var context : BaseActivity, private var mDataList :List<ColumnInfo>, private var hospitalInfoMap: Map<String, HospitalInfo?>) : RecyclerView.Adapter<ColumnRecyclerAdapter.ColumnHolder>(){
    override fun getItemCount(): Int {
        return mDataList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColumnHolder {
        return ColumnHolder(parent)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ColumnHolder, position: Int) {
        val item = mDataList[position]
        val hospital = hospitalInfoMap[item.writerUid]

        with(holder){
            titleText.text = item.title
            hospitalName.text = hospital?.name
            viewCount.text = item.viewCount.toString()
            picture.loadImage(item.pictureUrlThumbnail?:item.pictureUrl)
            // 승인, 보류
            if(context is HospitalColumnListActivity) {
                approvalText.visibility = View.VISIBLE
                approvalText.text = (if(!item.approval) Approval.HOLD.value else Approval.APPROVAL.value ) + " \n " + item.requestStr
            } else {
                approvalText.visibility = View.GONE
            }
        }

        // Set the view to fade in
        UiUtil.setFadeAnimation(holder.itemView)
    }

    class ColumnHolder(parent: ViewGroup) : AndroidExtensionsViewHolder(
            LayoutInflater.from(parent.context)
                    .inflate(R.layout.column_item, parent, false)) {
        val titleText: TextView = itemView.titleText
        val picture: ImageView = itemView.pictureImage
        val hospitalName: TextView = itemView.hospitalName
        val viewCount: TextView = itemView.viewCount
        val layout: ConstraintLayout = itemView.layout
        val approvalText : TextView = itemView.approvalText
    }

}