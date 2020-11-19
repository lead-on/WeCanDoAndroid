package com.example.wecando

interface ItemActionListener {
    fun onItemMoved(from: Int, to: Int)
    fun onItemSwiped(position: Int)
}