package com.dac.gapp.andac.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.dac.gapp.andac.R
import com.dac.gapp.andac.model.ColumnTitle
import com.dac.gapp.andac.viewholder.ColumnTitleViewHolder

/**
 * Created by godueol on 2018. 4. 14..
 */
class ColumnTitleRecyclerViewAdapter(var context : Context, var list :List<ColumnTitle>) : RecyclerView.Adapter<ColumnTitleViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColumnTitleViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.column_row, parent, false)
        return ColumnTitleViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }


    override fun onBindViewHolder(holder: ColumnTitleViewHolder, position: Int) {
        val item : ColumnTitle = list.get(position)
        holder.titleText.text = item.title

    }

}