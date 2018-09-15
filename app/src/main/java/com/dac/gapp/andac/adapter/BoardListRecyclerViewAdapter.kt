package com.dac.gapp.andac.adapter

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.dac.gapp.andac.R
import com.dac.gapp.andac.databinding.BoardRowBinding
import com.dac.gapp.andac.model.firebase.BoardInfo
import com.dac.gapp.andac.util.MyToast

class BoardListRecyclerViewAdapter(private var itemList: List<BoardInfo>) : RecyclerView.Adapter<BoardListRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.board_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList[position]
        holder.apply {
            binding?.apply {
                title = item.title
            }
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var binding: BoardRowBinding? = DataBindingUtil.bind(view)
        val title: TextView? = binding?.txtviewTitle
    }
}