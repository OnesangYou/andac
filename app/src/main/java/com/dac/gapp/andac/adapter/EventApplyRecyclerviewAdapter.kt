package com.dac.gapp.andac.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.ToggleButton
import com.dac.gapp.andac.R
import com.dac.gapp.andac.model.firebase.EventApplyInfo
import com.dac.gapp.andac.util.getDateFormat
import kotlinx.android.synthetic.main.event_apply_row.view.*

class EventApplyRecyclerviewAdapter
(private val mDataList: List<EventApplyInfo>) : RecyclerView.Adapter<EventApplyRecyclerviewAdapter.EventApplyHolder>() {
    override fun getItemCount(): Int {
        return mDataList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventApplyHolder {
        return EventApplyHolder(parent)
    }

    override fun onBindViewHolder(holder: EventApplyHolder, position: Int) {
        val item = mDataList[position]

        holder.apply{
            writeDateText.text = item.writeDate?.getDateFormat('.')
            nameText.text = item.name
            possibleStartTime.text = item.possibleTime
            phoneBtn.text = item.phone
            confirmBtn.isChecked = item.confirmDate != null
        }
    }


    class EventApplyHolder(parent: ViewGroup) : AndroidExtensionsViewHolder(
            LayoutInflater.from(parent.context)
                    .inflate(R.layout.event_apply_row, parent, false)) {

        val writeDateText: TextView = itemView.writeDateText
        val nameText: TextView = itemView.nameText
        val possibleStartTime: TextView = itemView.possibleStartTime
        val possibleEndTime: TextView = itemView.possibleEndTime

        val phoneBtn: Button = itemView.phoneBtn
        val confirmBtn: ToggleButton = itemView.confirmBtn
    }
}