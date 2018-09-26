package com.dac.gapp.andac.adapter

import android.content.Context
import android.databinding.DataBindingUtil
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dac.gapp.andac.R
import com.dac.gapp.andac.model.Notice
import com.dac.gapp.andac.viewholder.NoticeTitleViewHolder
import com.dac.gapp.andac.viewholder.NoticeViewHolder
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup


class NoticeRecyclerAdapter(var context : Context, groups : MutableList <ExpandableGroup<*>>? ) : ExpandableRecyclerViewAdapter<NoticeTitleViewHolder, NoticeViewHolder>(groups) {


    override fun onCreateGroupViewHolder(parent: ViewGroup?, viewType: Int): NoticeTitleViewHolder {

        val view = LayoutInflater.from(context).inflate(R.layout.notice_header_row, parent, false)
        return NoticeTitleViewHolder(view)
    }

    override fun onCreateChildViewHolder(parent: ViewGroup?, viewType: Int): NoticeViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.notice_row, parent, false)
        return NoticeViewHolder(view)
    }

    override fun onBindGroupViewHolder(holder: NoticeTitleViewHolder?, flatPosition: Int, group: ExpandableGroup<*>?) {
        holder?.let{ it.binding.title.text = group?.title}
    }

    override fun onBindChildViewHolder(holder: NoticeViewHolder?, flatPosition: Int, group: ExpandableGroup<*>?, childIndex: Int) {
        val artist = (group as Notice).items[childIndex]
        holder?.let { it.binding.text.text = artist.text }
    }




}