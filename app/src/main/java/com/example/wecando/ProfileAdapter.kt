package com.example.wecando


import android.annotation.SuppressLint
import android.content.Intent
import android.service.voice.AlwaysOnHotwordDetector
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import kotlin.collections.ArrayList

// git test branch develop

class ProfileAdapter(val profileList: ArrayList<Profiles> ) : RecyclerView.Adapter<ProfileAdapter.CustomViewHolder>() {

    //맨 첨에 실행댐 (뷰 생성시)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileAdapter.CustomViewHolder {
        val view =  LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return CustomViewHolder(view)
    }

    override fun getItemCount(): Int {
        return profileList.size
    }

    override fun onBindViewHolder(holder: ProfileAdapter.CustomViewHolder, position: Int) {

        holder.title.text = profileList.get(position).title
        holder.detailCnt.text = profileList.get(position).detailCnt.toString()+" >"

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


        holder.itemView.setOnClickListener {

//            it.context.startActivity(nextIntent)
        }
    }


    class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title = itemView.findViewById<TextView>(R.id.tv_list_title) // 타이틀
        val bg_tag = itemView.findViewById<ImageView>(R.id.iv_list_icon) // 이미지
        val detailCnt = itemView.findViewById<TextView>(R.id.tv_list_detail_cnt) //할일 갯수 cnt

        }


}