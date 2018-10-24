package com.dac.gapp.andac.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import com.dac.gapp.andac.HospitalActivity
import com.dac.gapp.andac.R
import com.dac.gapp.andac.base.BaseActivity
import com.dac.gapp.andac.enums.AdCountType
import com.dac.gapp.andac.extension.loadImageAny
import com.dac.gapp.andac.model.HeaderInfo
import com.dac.gapp.andac.model.firebase.HospitalInfo

class SearchHospitalRecyclerViewAdapter(private var context: BaseActivity, private var itemList: List<Pair<Any, Int>>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val VIEW_TYPE_HEADER = 0
        const val VIEW_TYPE_CONTENT = 1
    }

    override fun getItemViewType(position: Int): Int {
        return itemList[position].second
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_HEADER -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.hospital_row_header, parent, false)
                SearchHospitalViewHolder2(view)
            }
            VIEW_TYPE_CONTENT -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.hospital_row, parent, false)
                SearchHospitalViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.hospital_row, parent, false)
                SearchHospitalViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = itemList[position].first
        when (holder.itemViewType) {
            VIEW_TYPE_HEADER -> {
                if (holder is SearchHospitalViewHolder2) {
                    if (item is HeaderInfo) {
                        holder.imgviewTitle.loadImageAny(item.image)
                        holder.txtviewTitle.text = item.title
                    }
                }
            }
            VIEW_TYPE_CONTENT -> {
                if (holder is SearchHospitalViewHolder) {
                    if (item is HospitalInfo) {
                        holder.thumbnail.loadImageAny(item.run { if (profilePicUrl.isNotEmpty()) profilePicUrl else if (approval) R.drawable.hospital_profile_default_approval else R.drawable.hospital_profile_default_not_approval })
                        holder.title.text = item.name
                        holder.address.text = if(item.address2.isNotEmpty()) item.address2 else item.address1
                        holder.rate.rating = item.rateAvg
                        holder.likeCntText.text = item.likeCount.toString()
                        holder.itemView.setOnClickListener {
                            if (item.isUltraAd) {
                                context.addCountAdClick(item.objectID, AdCountType.ULTRA_HOSPITAL)
                            }
                            context.startActivity(HospitalActivity.createIntent(context!!, item))
                        }
                    }
                }
            }
        }

    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    class SearchHospitalViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var thumbnail: ImageView = view.findViewById(R.id.imgview_thumbnail)
        var title: TextView = view.findViewById(R.id.txtview_title)
        var address: TextView = view.findViewById(R.id.txtview_address)
        var rate: RatingBar = view.findViewById(R.id.ratingBar)
        var likeCntText: TextView = view.findViewById(R.id.likeCntText)
    }

    class SearchHospitalViewHolder2(view: View) : RecyclerView.ViewHolder(view) {
        var imgviewTitle: ImageView = view.findViewById(R.id.imgviewTitle)
        var txtviewTitle: TextView = view.findViewById(R.id.txtviewTitle)
    }
}