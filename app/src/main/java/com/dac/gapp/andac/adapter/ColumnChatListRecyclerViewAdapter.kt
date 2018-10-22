package com.dac.gapp.andac.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.Request
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.dac.gapp.andac.ChatActivity
import com.dac.gapp.andac.R
import com.dac.gapp.andac.model.firebase.ChatItem
import com.dac.gapp.andac.viewholder.*
import kotlinx.android.synthetic.main.abc_activity_chooser_view.view.*
import timber.log.Timber
import java.util.*

/**
 * Created by godueol on 2018. 4. 28..
 */


class ColumnChatListRecyclerViewAdapter(var list: List<ChatItem>, var onCompleteImage: ChatActivity.OnCompleteImage) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    lateinit var context: Context
    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(mHolder: RecyclerView.ViewHolder, position: Int) {
        val item: ChatItem = list[position]
        Timber.d(list.toString())

        when (item.type) {
            0 -> {
                val holder: ChatMineMessageHolder = mHolder as ChatMineMessageHolder
                holder.binding.chatItem = item
            }
            1 -> {
                val holder: ChatOtherMessageViewHolder = mHolder as ChatOtherMessageViewHolder
                holder.binding.chatItem = item
            }
            2 -> {
                val holder: ChatMineImageViewHolder = mHolder as ChatMineImageViewHolder
                holder.binding.chatItem = item
                Glide.with(context).load(item.imageUrl).listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                        return false
                    }

                    override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        if (item.first) {
                            onCompleteImage.onComplete(position)
                            item.first = false
                        }
                        return false
                    }

                }).into(holder.binding.chatImage)
            }
            3 -> {
                val holder: ChatOtherImageViewHolder = mHolder as ChatOtherImageViewHolder
                holder.binding.chatItem = item

                Glide.with(context).load(item.imageUrl).listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                        return false
                    }

                    override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        if (item.first) {
                            onCompleteImage.onComplete(position)
                            item.first = false
                        }
                        return false
                    }

                }).into(holder.binding.chatImage)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val message = list[position]
        return message.type
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        when (viewType) {
            0 -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.chat_mine_message_row, parent, false)
                return ChatMineMessageHolder(view)
            }
            1 -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.chat_other_message_row, parent, false)
                return ChatOtherMessageViewHolder(view)
            }
            2 -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.chat_mine_image_row, parent, false)
                return ChatMineImageViewHolder(view)
            }
            3 -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.chat_other_image_view, parent, false)
                return ChatOtherImageViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.chat_mine_message_row, parent, false)
                return ChatMineMessageHolder(view)
            }
        }
    }


}