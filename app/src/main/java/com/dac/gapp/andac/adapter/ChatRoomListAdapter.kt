package com.dac.gapp.andac.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.dac.gapp.andac.R
import com.dac.gapp.andac.model.firebase.ChatListInfo
import com.dac.gapp.andac.viewholder.ChatRoomListViewHolder

class ChatRoomListAdapter(var context: Context, private var list : MutableList<ChatListInfo>) :RecyclerView.Adapter<ChatRoomListViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatRoomListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.chat_room_row, parent, false)
        return ChatRoomListViewHolder(context,view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ChatRoomListViewHolder, position: Int) {
       holder.binding.item = list[position]
        holder.binding.holder = holder
    }

}