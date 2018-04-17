package com.dac.gapp.andac.viewholder

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.dac.gapp.andac.R

/**
 * Created by godueol on 2018. 4. 14..
 */
class ColumnTitleViewHolder(view : View) : RecyclerView.ViewHolder(view) {
    var titleText : TextView = view.findViewById(R.id.column_tite)
    var img : ImageView = view.findViewById(R.id.column_image)
}