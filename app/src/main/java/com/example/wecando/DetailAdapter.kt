package com.example.wecando

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Paint
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.detail_item.view.*
import kotlinx.android.synthetic.main.detail_item_finish.view.*

class DetailAdapter(val details: ArrayList<DetailModel>, val listener: ItemDragListener, val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), ItemActionListener {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View?
        return when (viewType) {
            DetailModel.RUNNING -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.detail_item, parent, false)
                RunningViewHolder(view)
            }
            DetailModel.ADD, DetailModel.MODIFY  -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.detail_item_edit, parent, false)
                EditViewHolder(view, listener)
            }
            DetailModel.FINISH -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.detail_item_finish, parent, false)
                FinishViewHolder(view)
            }
            else -> throw RuntimeException("알 수 없는 뷰 타입 에러")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val obj = details[position]
        when (obj.status) {
            DetailModel.RUNNING -> (holder as RunningViewHolder).title.text = obj.title
            DetailModel.MODIFY -> {
                (holder as EditViewHolder).et_title.text = obj.title
                var et = (holder.et_title as EditText)
                et.setSelection(obj.title.length)
            }
            DetailModel.FINISH -> (holder as FinishViewHolder).title.text = obj.title
        }

        var clickCnt = 0

        when(holder::class.java) {
            RunningViewHolder::class.java -> {
                (holder as RunningViewHolder).itemView.tv_delete_detail.setOnClickListener {
                    details.removeAt(position)
                    (context as DetailActivity).DeleteDetail(obj.id)
                    notifyDataSetChanged()
                }
            }
            FinishViewHolder::class.java -> {
                (holder as FinishViewHolder).itemView.tv_delete_finish_detail.setOnClickListener {
                    details.removeAt(position)
                    (context as DetailActivity).DeleteDetail(obj.id)
                    notifyDataSetChanged()
                }
            }
            else -> {

            }
        }

        holder.itemView.setOnClickListener {
            clickCnt++
            if (clickCnt == 1) {
                Handler().postDelayed({
                    if (clickCnt == 1) {
                        var isDuplicated = false
                        for (item in details) {
                            if (item.status == DetailModel.MODIFY) {
                                isDuplicated = true
                            }
                        }
                        if (obj.status == DetailModel.RUNNING && !isDuplicated) {

                            //새로운 일정 추가 버튼 겹치는거 방지
                            (context as DetailActivity).CancleDetailModify()

                            var id = details.get(position).id
                            var order = details.get(position).order
                            var title = details.get(position).title
//                            details.set(position, DetailModel(DetailModel.MODIFY, id, "${title}", order))
//                            notifyDataSetChanged()
                            (context as DetailActivity).ModifyDetail(id, position, title, order)

                        }

                    } else {
                        if (holder::class.java != EditViewHolder::class.java) {
                            var id = details.get(position).id
                            var title = details.get(position).title
                            var order = details.get(position).order
                            var status = details.get(position).status

                            (context as DetailActivity).FinishDetail(id, status)

                            if (status == DetailModel.FINISH) {
                                details.set(position, DetailModel(DetailModel.RUNNING, id, title, order))
                            } else {
                                details.set(position, DetailModel(DetailModel.FINISH, id, title, order))
                            }
                            notifyDataSetChanged()
                        }
                    }
                     clickCnt = 0
                }, 300)
            }

        }

    }

    override fun getItemCount(): Int {
        return details.size
    }

    override fun getItemViewType(position: Int): Int {
        return details[position].status
    }

    override fun onItemMoved(from: Int, to: Int) {
        TODO("Not yet implemented")
    }

    override fun onItemSwiped(position: Int) {

//        var id = details.get(position).id
//        (context as DetailActivity).DeleteDetail(id)
//
//        details.removeAt(position)
//        notifyItemRemoved(position)

    }

    inner class RunningViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.tv_detail_title)
//        val detailTitleWarpper = itemView.findViewById<LinearLayout>(R.id.ll_detail_title_wrapper)
        init {
            itemView.tv_delete_detail
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    inner class EditViewHolder(itemView: View, listener: ItemDragListener) : RecyclerView.ViewHolder(itemView) {
        val et_title: TextView = itemView.findViewById<EditText>(R.id.et_detail_title)
        init {
//            itemView.setOnTouchListener { v, event ->
//                if (event.action == MotionEvent.ACTION_DOWN) {
//                    listener.onStartDrag(this)
//                }
//                false
//            }

        }
    }
    inner class FinishViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.tv_detail_title_finish)
        init {
            title.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG)
        }
    }

}

