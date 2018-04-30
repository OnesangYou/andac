package com.dac.gapp.andac.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.dac.gapp.andac.R
import com.dac.gapp.andac.model.ChatMessage
import com.dac.gapp.andac.model.columnTitle
import com.dac.gapp.andac.viewholder.ColumnChatListViewHolder
import com.dac.gapp.andac.viewholder.ColumnTitleViewHolder

/**
 * Created by godueol on 2018. 4. 28..
 */


class ColumnChatListRecyclerViewAdapter(var context : Context, var list :List<ChatMessage>) : RecyclerView.Adapter<ColumnChatListViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColumnChatListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.column_row_chat, parent, false)
        return ColumnChatListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }


    override fun onBindViewHolder(holder: ColumnChatListViewHolder, position: Int) {
        val item : ChatMessage = list.get(position)
        holder.titleText.text = item.text

    }

}