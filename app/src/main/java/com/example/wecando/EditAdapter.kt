package com.example.wecando


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.service.voice.AlwaysOnHotwordDetector
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.createDeviceProtectedStorageContext
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item_edit.view.*
import java.util.*
import kotlin.collections.ArrayList

// git test branch develop

val TAG = "로그"

class EditAdapter(val profileList: ArrayList<Profiles>, val listener: ItemDragListener, val context: Context) : RecyclerView.Adapter<EditAdapter.CustomViewHolder>(), ItemActionListener {


    //맨 첨에 실행댐 (뷰 생성시)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EditAdapter.CustomViewHolder {
        val view =  LayoutInflater.from(parent.context).inflate(R.layout.list_item_edit, parent, false)

        return CustomViewHolder(view, listener, context)

    }

    override fun getItemCount(): Int {
        return profileList.size
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {

        holder.title.text = profileList.get(position).title
        var img_tag = profileList.get(position).bg_tag
        when (img_tag) {

            "BLACK" -> holder.bg_tag.setBackgroundResource(R.drawable.circle_black)
            "RED" -> holder.bg_tag.setBackgroundResource(R.drawable.circle_red)
            "ORANGE" -> holder.bg_tag.setBackgroundResource(R.drawable.circle_orange)
            "SALGU" -> holder.bg_tag.setBackgroundResource(R.drawable.circle_salgu)
            "LIGHT_GREEN" -> holder.bg_tag.setBackgroundResource(R.drawable.circle_light_green)
            "GREEN" -> holder.bg_tag.setBackgroundResource(R.drawable.circle_green)
            "SKYBLUE" -> holder.bg_tag.setBackgroundResource(R.drawable.circle_skyblue)
            "BLUE" -> holder.bg_tag.setBackgroundResource(R.drawable.circle_blue)
            "PURPLE" -> holder.bg_tag.setBackgroundResource(R.drawable.circle_purple)
            "GREY" -> holder.bg_tag.setBackgroundResource(R.drawable.circle_grey)
            "LIGHT_BROWN" -> holder.bg_tag.setBackgroundResource(R.drawable.circle_light_brown)
            "BROWN" -> holder.bg_tag.setBackgroundResource(R.drawable.circle_brown)

            else -> holder.bg_tag.setBackgroundResource(R.drawable.circle_black)
        }

        //삭제 버튼
        holder.itemView.iv_btn_delete.setOnClickListener {
            profileList.removeAt(position)
            notifyDataSetChanged()
        }

        holder.itemView.tv_btn_list_modify.setOnClickListener {
            //이거 찾는데 3시간 걸림 ㅋ (왜 메인액티비티에서 컨텍스트 던지고 바로 쓰면 안되는지 몰겠음)
            var title = profileList.get(position).title
            var id = profileList.get(position).id
            var tag = profileList.get(position).bg_tag
            (context as MainActivity).ModifyItem(title, id, tag)
        }

        holder.itemView.setOnClickListener {

//            it.context.startActivity(nextIntent)
        }

    }

    override fun onItemMoved(from: Int, to: Int) {

        if (from == to) {
            return
        }

        val fromItem = profileList.removeAt(from)
        profileList.add(to, fromItem)
        notifyItemMoved(from, to)
    }

    override fun onItemSwiped(position: Int) {
        profileList.removeAt(position)
        notifyItemRemoved(position)
    }



    @SuppressLint("ClickableViewAccessibility")
    class CustomViewHolder(itemView: View, listener: ItemDragListener, context: Context) : RecyclerView.ViewHolder(itemView) {
        val title = itemView.findViewById<TextView>(R.id.tv_list_title) // 타이틀
        val bg_tag = itemView.findViewById<ImageView>(R.id.iv_list_icon) // 이미지

        init {
            itemView.setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    listener.onStartDrag(this)
                }
                false
            }

        }
    }

}