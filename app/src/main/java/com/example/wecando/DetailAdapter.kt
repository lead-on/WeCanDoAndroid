package com.example.wecando

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class DetailAdapter(val details: ArrayList<DetailModel>, val listener: ItemDragListener, val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), ItemActionListener {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View?
        return when (viewType) {
            DetailModel.RUNNING -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.detail_item, parent, false)
                RunningViewHolder(view)
            }
            DetailModel.EDIT -> {
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
//            DetailModel.EDIT -> (holder as EditViewHolder).title.text = obj.title
            DetailModel.FINISH -> (holder as FinishViewHolder).title.text = obj.title
        }

        var doubleClick: Boolean? = false
        holder.itemView.setOnClickListener {
            if (doubleClick!!) {

                var id = details.get(position).id
                var title = details.get(position).title
                var order = details.get(position).order
                details.set(position, DetailModel(DetailModel.FINISH, id, title, order))
                notifyDataSetChanged()
                (context as DetailActivity).FinishDetail(id)

            } else {
                //singClick
            }
            doubleClick = true
            Handler().postDelayed({ doubleClick = false }, 300)
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

        var id = details.get(position).id
        (context as DetailActivity).DeleteDetail(id)

        details.removeAt(position)
        notifyItemRemoved(position)

    }

    inner class RunningViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.tv_detail_title)
    }

    @SuppressLint("ClickableViewAccessibility")
    inner class EditViewHolder(itemView: View, listener: ItemDragListener) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.et_detail_title)
        init {
            itemView.setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    listener.onStartDrag(this)
                }
                false
            }
        }
    }
    inner class FinishViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.tv_detail_title_finish)
    }

}

