package com.example.wecando

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), ItemDragListener {

    var TAG:String = "로그"
    var lists = arrayListOf<ListModel>()

    private lateinit var itemTouchHelper: ItemTouchHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val dbHelper = DBHelper(this, "local_db.db", null, 9)
        val database = dbHelper.writableDatabase

//        var inserQuery = "INSERT INTO t_list('l_title', 'l_img') values('Title_Test', 'Img_Test')"
//        database.execSQL(inserQuery)
//        database.close()

//        var DropQuery = "DROP TABLE t_list"
//        database.execSQL(DropQuery)
//        database.close()

//        var deleteQuery = "DELETE FROM t_list"
//        database.execSQL(deleteQuery)
//        database.close()


        var selectQuery = "SELECT * FROM t_list ORDER BY l_order ASC"
        var cursor = database.rawQuery(selectQuery, null)


        //DB에서 목록들가져와서 profilelist에 넣음

        while(cursor.moveToNext()) {
            var detailCntQuery = "SELECT * FROM t_detail WHERE t_id = '${cursor.getInt(cursor.getColumnIndex("l_id"))}'"
            var d_cursor = database.rawQuery(detailCntQuery, null)
            var d_cnt = if (d_cursor.count > 0) {
                d_cursor.count
            } else {
                0
            }
            lists.add(ListModel(ListModel.ITEM, cursor.getString(cursor.getColumnIndex("l_bg_tag")), cursor.getString(cursor.getColumnIndex("l_title")), cursor.getInt(cursor.getColumnIndex("l_id")), cursor.getInt(cursor.getColumnIndex("l_order")), d_cnt))
        }

        lists.add(ListModel(ListModel.BUTTON,"", "", 60000, 60000, 0))
        cursor.close()
        database.close()

        var layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        //일반모드 어댑터
        var adapter = ListAdapter(lists, this)

        rv_list.layoutManager = layoutManager
        rv_list.setHasFixedSize(false)
        rv_list.adapter = adapter
//        rv_list.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))


        //완료 버튼 클릭이벤트
        tv_header_btn.setOnClickListener {
            val database = dbHelper.writableDatabase


            var deleteQuery = "DELETE FROM t_list"
            database.execSQL(deleteQuery)
            if (lists.size > 0) {
                var InsertQuery = getString(R.string.InsertQuery)
                for (i in 0..lists.size - 1) {
                    lists[i].order = i + 1
                    InsertQuery += "('${lists[i].id}', '${lists[i].title}', '${lists[i].bg_tag}', '${lists[i].order}')"
                    if (i != lists.size - 1) {
                        InsertQuery += ", "
                    }
                }
                database.execSQL(InsertQuery)
            }
            database.close()

            rv_list.removeAllViewsInLayout()
            rv_list.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            rv_list.setHasFixedSize(true)
            lists.add(ListModel(ListModel.BUTTON,"", "", 60000, 60000, 0))
            rv_list.adapter = adapter
            tv_header_btn.visibility = View.INVISIBLE
            tv_header_btn.isClickable = false
            tv_main_header_cancle.visibility = View.INVISIBLE
            tv_main_header_cancle.isClickable = false

        }

        tv_main_header_cancle.setOnClickListener {
            rv_list.removeAllViewsInLayout()
            rv_list.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            rv_list.setHasFixedSize(true)
            lists.add(ListModel(ListModel.BUTTON,"", "", 60000, 60000, 0))
            rv_list.adapter = adapter

            tv_header_btn.visibility = View.INVISIBLE
            tv_header_btn.isClickable = false
            tv_main_header_cancle.visibility = View.INVISIBLE
            tv_main_header_cancle.isClickable = false
        }
    }

    fun LongClicked() {
        rv_list.removeAllViewsInLayout()
        rv_list.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rv_list.setHasFixedSize(true)
        lists.removeAt(lists.size-1)
        val editAdapter = EditAdapter(lists, this, this)
        rv_list.adapter = editAdapter

        tv_header_btn.visibility = View.VISIBLE
        tv_header_btn.isClickable = true
        tv_main_header_cancle.visibility = View.VISIBLE
        tv_main_header_cancle.isClickable = true

        itemTouchHelper = ItemTouchHelper(ItemTouchHelperCallback(editAdapter, false))
        itemTouchHelper.attachToRecyclerView(rv_list)
    }

    //수정버튼 클릭 이벤트
    public fun ModifyItem(title: String, id: Int, tag: String) {
        Log.d(TAG, "title : ${title}")

        val NextIntent = Intent(this, AddListActivity::class.java)
        NextIntent.putExtra("title", title)
        NextIntent.putExtra("id", id)
        NextIntent.putExtra("tag", tag)
        startActivity(NextIntent)

    }

    override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
        itemTouchHelper.startDrag(viewHolder)
    }

    fun finishActivity() {

    }

}