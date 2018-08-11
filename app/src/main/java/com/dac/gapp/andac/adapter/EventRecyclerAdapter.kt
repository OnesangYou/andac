package com.dac.gapp.andac.adapter

import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.dac.gapp.andac.EventActivity
import com.dac.gapp.andac.R
import com.dac.gapp.andac.base.BaseActivity
import com.dac.gapp.andac.model.EventInfo
import com.dac.gapp.andac.model.firebase.HospitalInfo
import kotlinx.android.synthetic.main.event_row.view.*
import org.jetbrains.anko.startActivity

class EventRecyclerAdapter
(private val context : BaseActivity?, private var mDataList: List<EventInfo>, private var writerInfoMap: Map<String, HospitalInfo>) : RecyclerView.Adapter<EventRecyclerAdapter.EventHolder>() {
    override fun getItemCount(): Int {
        return mDataList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventHolder {
        return EventHolder(parent)
    }

    override fun onBindViewHolder(holder: EventHolder, position: Int) {
        val item = mDataList[position]
        val hospital = writerInfoMap[item.writerUid]

        holder.apply{
            titleText.text = item.title
            Glide.with(context).load(item.pictureUrl).into(picture)
            bodyText.text = item.body
            dealKindText.text = item.deal_kind
            priceText.text = item.price
            hospitalName.text = hospital?.name
            buyCount.text = item.buy_count

            context?.apply{
                layout.setOnClickListener {
                    // go to evnet detail
                    startActivity<EventActivity>()
                }
            }

        }
    }


    class EventHolder(parent: ViewGroup) : AndroidExtensionsViewHolder(
            LayoutInflater.from(parent.context)
                    .inflate(R.layout.event_row, parent, false)) {

        val titleText: TextView = itemView.event_title
        val picture: ImageView = itemView.event_image
        val bodyText: TextView = itemView.body
        val dealKindText: TextView = itemView.deal_kind
        val priceText: TextView = itemView.price
        val hospitalName: TextView = itemView.sub_title
        val buyCount: TextView = itemView.buy_count
        val layout: ConstraintLayout = itemView.layout
    }

}
