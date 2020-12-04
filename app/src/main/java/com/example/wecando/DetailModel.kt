package com.example.wecando

class DetailModel(var status: Int, val id: Int, var title: String, var order: Int) {
    companion object {
        const val RUNNING = 0
        const val ADD = 1
        const val FINISH = 2
        const val MODIFY = 3
    }
}