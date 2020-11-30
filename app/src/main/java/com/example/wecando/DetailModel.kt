package com.example.wecando

class DetailModel(val status: Int, val id: Int, val title: String, val order: Int) {
    companion object {
        const val RUNNING = 0
        const val ADD = 1
        const val FINISH = 2
        const val MODIFY = 3
    }
}