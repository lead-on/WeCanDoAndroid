package com.example.wecando

class DetailModel(val status: Int, val id: Int, val title: String, val order: Int) {
    companion object {
        const val RUNNING = 0
        const val EDIT = 1
        const val FINISH = 2
    }
}