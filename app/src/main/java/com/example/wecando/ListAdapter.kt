package com.example.wecando


import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item_add.view.*
import kotlin.collections.ArrayList

// git test branch develop

class ListAdapter(val lists: ArrayList<ListModel>, val context: Context ) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    //맨 첨에 실행댐 (뷰 생성시)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View?
        return when(viewType) {

            ListModel.ITEM -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
                ItemHolder(view)
            }

            ListModel.BUTTON -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_add, parent, false)
                ButtonHolder(view, context)
            }

            else -> throw RuntimeException("알 수 없는 뷰 타입 에러")
        }
    }

    override fun getItemCount(): Int {
        return lists.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (lists.get(position).type == ListModel.ITEM) {

            (holder as ItemHolder).title.text = lists.get(position).title
            (holder as ItemHolder).detailCnt.text = lists.get(position).detailCnt.toString()
            var img_tag = lists.get(position).bg_tag

            when (img_tag) {

                "BLACK" -> (holder as ItemHolder).bg_tag.setBackgroundResource(R.drawable.circle_black)
                "RED" -> (holder as ItemHolder).bg_tag.setBackgroundResource(R.drawable.circle_red)
                "ORANGE" -> (holder as ItemHolder).bg_tag.setBackgroundResource(R.drawable.circle_orange)
                "SALGU" -> (holder as ItemHolder).bg_tag.setBackgroundResource(R.drawable.circle_salgu)
                "LIGHT_GREEN" -> (holder as ItemHolder).bg_tag.setBackgroundResource(R.drawable.circle_light_green)
                "GREEN" -> (holder as ItemHolder).bg_tag.setBackgroundResource(R.drawable.circle_green)
                "SKYBLUE" -> (holder as ItemHolder).bg_tag.setBackgroundResource(R.drawable.circle_skyblue)
                "BLUE" -> (holder as ItemHolder).bg_tag.setBackgroundResource(R.drawable.circle_blue)
                "PURPLE" -> (holder as ItemHolder).bg_tag.setBackgroundResource(R.drawable.circle_purple)
                "GREY" -> (holder as ItemHolder).bg_tag.setBackgroundResource(R.drawable.circle_grey)
                "LIGHT_BROWN" -> (holder as ItemHolder).bg_tag.setBackgroundResource(R.drawable.circle_light_brown)
                "BROWN" -> (holder as ItemHolder).bg_tag.setBackgroundResource(R.drawable.circle_brown)

                else -> (holder as ItemHolder).bg_tag.setBackgroundResource(R.drawable.circle_black)
            }

            (holder as ItemHolder).itemView.setOnClickListener {
                val nextIntent = Intent(it.context, DetailActivity::class.java)
                var t_id = lists.get(position).id
                nextIntent.putExtra("t_id", t_id)

                //it.context랑 (context as MainActivity) 뭐가 다른지 몰겠어서 둘다써놈 (it은 finish나 멤버함수 접근안댐댐)
               it.context.startActivity(nextIntent)
                (context as MainActivity).finish()
            }
        } else {

            (holder as ButtonHolder).button.setOnClickListener {
                //여기서 액티비티 전환
                val nextIntent = Intent(it.context, AddListActivity::class.java)
                it.context.startActivity(nextIntent)

            }
        }


    }

    override fun getItemViewType(position: Int): Int {
        return lists[position].type
    }

    inner class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnLongClickListener {
        val title = itemView.findViewById<TextView>(R.id.tv_list_title) // 타이틀
        val bg_tag = itemView.findViewById<ImageView>(R.id.iv_list_icon) // 이미지
        val detailCnt = itemView.findViewById<TextView>(R.id.tv_list_detail_cnt) //할일 갯수 cnt

        init {
            itemView.setOnLongClickListener(this)
        }
        override fun onLongClick(p0: View?): Boolean {
            //롱클릭 이벤트 리스너
            (context as MainActivity).LongClicked()
            return true
        }
    }

    inner class ButtonHolder(itemView: View, context: Context) : RecyclerView.ViewHolder(itemView) {
        val button = itemView.findViewById<TextView>(R.id.tv_add_list)
    }



}