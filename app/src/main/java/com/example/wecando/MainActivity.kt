package com.example.wecando

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), ItemDragListener {

    var TAG:String = "로그"
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

        val profileList = arrayListOf<ListModel>()

        var selectQuery = "SELECT * FROM t_list ORDER BY l_order ASC"
        var cursor = database.rawQuery(selectQuery, null)

        //DB에서 목록들가져와서 profilelist에 넣음
        while(cursor.moveToNext()) {
            profileList.add(ListModel(cursor.getString(cursor.getColumnIndex("l_bg_tag")), cursor.getString(cursor.getColumnIndex("l_title")), cursor.getInt(cursor.getColumnIndex("l_id")), cursor.getInt(cursor.getColumnIndex("l_order")), 0))
//            Log.d(TAG, "order: ${cursor.getInt(cursor.getColumnIndex("l_order")).toString()}")
        }
        database.close()

        var layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        //일반모드 어댑터
        var adapter = ListAdapter(profileList)

        rv_list.layoutManager = layoutManager
        rv_list.setHasFixedSize(false)
        rv_list.adapter = adapter
//        rv_list.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))

        //목록추가 클릭 이벤트
        tv_add_list.setOnClickListener {
            startActivity(Intent(this, AddListActivity::class.java))
            finish()
        }

        //편집/완료 버튼 클릭이벤트
        tv_header_btn.setOnClickListener {

            if (it.getTag() == "modify") { //편집
                rv_list.removeAllViewsInLayout()
                rv_list.layoutManager =
                    LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                rv_list.setHasFixedSize(true)
                val editAdapter = EditAdapter(profileList, this, this)
                rv_list.adapter = editAdapter
                tv_header_btn.text = "완료"
                tv_header_btn.tag = "complete"

                itemTouchHelper = ItemTouchHelper(ItemTouchHelperCallback(editAdapter))
                itemTouchHelper.attachToRecyclerView(rv_list)

            } else { //완료

                var InsertQuery = getString(R.string.InsertQuery)

                for (i in 0..profileList.size-1) {
                    profileList[i].order = i+1
                    InsertQuery += "('${profileList[i].id}', '${profileList[i].title}', '${profileList[i].bg_tag}', '${profileList[i].order}')"
                    if (i != profileList.size-1) {
                        InsertQuery += ", "
                    }
                }

                var deleteQuery = "DELETE FROM t_list"
                database.execSQL(deleteQuery)
                database.execSQL(InsertQuery)
                database.close()

                rv_list.removeAllViewsInLayout()
                rv_list.layoutManager =
                    LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                rv_list.setHasFixedSize(true)
                rv_list.adapter = adapter
                tv_header_btn.text = "편집"
                tv_header_btn.tag = "modify"
           }
        }
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

}