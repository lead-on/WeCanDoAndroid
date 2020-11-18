package com.example.wecando


import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ProfileAdapter(val profileList: ArrayList<Profiles>, val nextIntent: Intent) : RecyclerView.Adapter<ProfileAdapter.CustomViewHolder>() {

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
        val img:String = profileList.get(position).imgSrc
        if (img == "black") {
            holder.img.setImageResource(R.drawable.icon_black)
        }

        holder.itemView.setOnClickListener {

            it.context.startActivity(nextIntent)
        }
    }

    class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title = itemView.findViewById<TextView>(R.id.tv_list_title) // 타이틀
        val img = itemView.findViewById<ImageView>(R.id.iv_list_icon) // 이미지
        val detailCnt = itemView.findViewById<TextView>(R.id.tv_list_detail_cnt) //할일 갯수 cnt
    }


}