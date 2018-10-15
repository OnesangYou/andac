package com.dac.gapp.andac.viewholder

import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.View
import com.dac.gapp.andac.ChatActivity
import com.dac.gapp.andac.databinding.ChatRoomRowBinding

class ChatRoomListViewHolder(var context: Context, view: View) : RecyclerView.ViewHolder(view) {
    var binding: ChatRoomRowBinding = DataBindingUtil.bind(view)!!


    fun onClickStartChat(uUid: String?, hUid: String?, roomId: String) {
        val uUid: String = uUid ?: return
        val hUid: String = hUid ?: return

        goChat(hUid, uUid, roomId)
    }

    private fun goChat(hUid: String, uUid: String, roomId: String) {
        context?.startActivity(Intent(context, ChatActivity::class.java).putExtra("roomId", roomId).putExtra("uUid", uUid).putExtra("hUid", hUid))
    }


}