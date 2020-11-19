package com.example.wecando

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), ItemDragListener  {

    private lateinit var itemTouchHelper: ItemTouchHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var TAG:String = "로그"

        val dbHelper = DBHelper(this, "local_db.db", null, 7)
        val database = dbHelper.writableDatabase

//        var inserQuery = "INSERT INTO t_list('l_title', 'l_img') values('Title_Test', 'Img_Test')"
//        database.execSQL(inserQuery)

//        var DropQuery = "DROP TABLE t_list"
//        database.execSQL(DropQuery)

//        var deleteQuery = "DELETE FROM t_list"
//        database.execSQL(deleteQuery)

        val profileList = arrayListOf<Profiles>()

        var selectQuery = "SELECT * FROM t_list"
        var cursor = database.rawQuery(selectQuery, null)

        while(cursor.moveToNext()) {
            profileList.add(Profiles(cursor.getString(cursor.getColumnIndex("l_bg_tag")), cursor.getString(cursor.getColumnIndex("l_title")), cursor.getInt(cursor.getColumnIndex("l_id")), cursor.getInt(cursor.getColumnIndex("l_order")), 0))
//            Log.d(TAG, "order: ${cursor.getInt(cursor.getColumnIndex("l_order")).toString()}")
        }

        var layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        var adapter = ProfileAdapter(profileList)


        rv_list.layoutManager = layoutManager
        rv_list.setHasFixedSize(true)
        rv_list.adapter = adapter
//        rv_list.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))

        tv_add_list.setOnClickListener {
            startActivity(Intent(this, AddListActivity::class.java))
            finish()
        }

        tv_header_btn.setOnClickListener {
            if (it.getTag() == "modify") {

                Log.d(TAG, "profile size: ${profileList.size}")

                rv_list.removeAllViewsInLayout()
                rv_list.layoutManager =
                    LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                rv_list.setHasFixedSize(true)
                val editAdapter = EditAdapter(profileList, this)
                rv_list.adapter = editAdapter
                tv_header_btn.text = "완료"
                tv_header_btn.tag = "complete"

                itemTouchHelper = ItemTouchHelper(ItemTouchHelperCallback(editAdapter))
                itemTouchHelper.attachToRecyclerView(rv_list)

            } else {
                for (i in 0..profileList.size) {

//                    profileList[i].order =

                }

                tv_header_btn.text = "편집"
                tv_header_btn.tag = "modify"
           }

        }

    }

    override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
        itemTouchHelper.startDrag(viewHolder)
    }

}