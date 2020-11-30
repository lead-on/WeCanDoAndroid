package com.example.wecando

import android.graphics.drawable.Drawable

class ListModel(val type: Int, val bg_tag: String, val title: String = "NULL", val id: Int, var order: Int, val detailCnt: Int = 0) {
    companion object {
        const val ITEM = 0
        const val BUTTON = 1
    }
}