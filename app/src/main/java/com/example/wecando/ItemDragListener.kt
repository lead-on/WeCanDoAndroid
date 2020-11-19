package com.example.wecando

import androidx.recyclerview.widget.RecyclerView

interface ItemDragListener {
    fun onStartDrag(viewHolder: RecyclerView.ViewHolder)
}