package com.dac.gapp.andac.adapter

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import kotlinx.android.extensions.LayoutContainer

abstract class AndroidExtensionsViewHolder(override val containerView: View)
    : RecyclerView.ViewHolder(containerView), LayoutContainer