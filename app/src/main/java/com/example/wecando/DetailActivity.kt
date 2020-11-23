package com.example.wecando

import android.content.Intent
import android.graphics.drawable.InsetDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.detail_item.*
import kotlinx.android.synthetic.main.detail_item_edit.*
import kotlinx.android.synthetic.main.detail_item_edit.view.*
import java.text.FieldPosition

class DetailActivity : AppCompatActivity(), ItemDragListener {

    private lateinit var itemTouchHelper: ItemTouchHelper
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val dbHelper = DBHelper(this, "local_db.db", null, 9)
        val database = dbHelper.writableDatabase
        var t_id = intent.getIntExtra("t_id", 0)

        val detailList = arrayListOf<DetailModel>()
        var selectQuery = "SELECT * FROM t_detail WHERE t_id = '${t_id}' ORDER BY d_order ASC"
        var cursor = database.rawQuery(selectQuery, null)

        while (cursor.moveToNext()) {
            detailList.add(DetailModel(cursor.getInt(cursor.getColumnIndex("d_status")), cursor.getInt(cursor.getColumnIndex("d_id")), cursor.getString(cursor.getColumnIndex("d_title")).toString(), cursor.getInt(cursor.getColumnIndex("d_order"))))
        }
        cursor.close()
        database.close()

        var layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        var adapter = DetailAdapter(detailList, this, this)

        rv_details.layoutManager = layoutManager
        rv_details.setHasFixedSize(true)
        rv_details.adapter = adapter

        itemTouchHelper = ItemTouchHelper(ItemTouchHelperCallback(adapter))
        itemTouchHelper.attachToRecyclerView(rv_details)

        //목록뒤로가기 클릭 이벤트
        tv_header_back.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        //새로운할일 클릭 이벤트
        layout_add_detail.setOnClickListener {
            var isDuplicated = false
            for (i in detailList) {
                if (i.status == 1) {
                    isDuplicated = true
                }
            }

            //EDIT 상태인 아이템이 없을때만 아이템 추가
            if (!isDuplicated) {
                val database = dbHelper.writableDatabase
                var EditselectQuery = "SELECT IFNULL(MAX(d_id), 1) AS MaxId, IFNULL(MAX(d_order), 1) AS MaxOrder FROM t_detail"
                var cursor = database.rawQuery(EditselectQuery, null)
                while (cursor.moveToNext()) {
                    detailList.add(DetailModel(DetailModel.EDIT,cursor.getInt(cursor.getColumnIndex("MaxId")), "", cursor.getInt(cursor.getColumnIndex("MaxOrder"))))
                }
                cursor.close()
                database.close()

                rv_details.removeAllViewsInLayout()
                rv_details.layoutManager =
                    LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                rv_details.setHasFixedSize(true)
                rv_details.adapter = adapter
                tv_detail_header_btn.text = "추가"
                tv_detail_header_btn.tag = "add"

            } else {
                Toast.makeText(this, "먼저 추가한 일정부터 완성해주세요!", Toast.LENGTH_LONG).show()
            }

        }

        //추가 버튼 클릭
        tv_detail_header_btn.setOnClickListener {

            when (it.tag) {
                "add" -> {
                    val database = dbHelper.writableDatabase

                    var title = rv_details.et_detail_title.text
                    var order = detailList.get(detailList.size - 1).order
                    var insertQuery =
                        "INSERT INTO t_detail(t_id, d_title, d_order, d_status) VALUES(${t_id}, '${title}', '${order}', '0')"
                    database.execSQL(insertQuery)
                    detailList.clear()

                    var c = database.rawQuery(selectQuery, null)
                    while (c.moveToNext()) {
                        detailList.add(
                            DetailModel(
                                c.getInt(c.getColumnIndex("d_status")),
                                c.getInt(c.getColumnIndex("d_id")),
                                c.getString(c.getColumnIndex("d_title")).toString(),
                                c.getInt(c.getColumnIndex("d_order"))
                            )
                        )
                    }
                    c.close()
                    database.close()
                    adapter.notifyDataSetChanged()

                    rv_details.removeAllViewsInLayout()
                    rv_details.layoutManager =
                        LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                    rv_details.setHasFixedSize(true)
                    rv_details.adapter = adapter

                    tv_detail_header_btn.text = "편집"
                    tv_detail_header_btn.tag = "modify"

                }
                "modify" -> {
                    Toast.makeText(this, "편집버튼 클릭", Toast.LENGTH_LONG).show()
                    tv_detail_header_btn.text = "완료"
                    tv_detail_header_btn.tag = "complete"
                }
                "complete" -> {
                    Toast.makeText(this, "완료버튼 클릭", Toast.LENGTH_LONG).show()
                    tv_detail_header_btn.text = "편집"
                    tv_detail_header_btn.tag = "modify"
                }
            }
        }

    }

    fun AddDetail(title: String, itemCnt: Int) {
        val dbHelper = DBHelper(this, "local_db.db", null, 9)
        val database = dbHelper.writableDatabase
        var InsertQuery = "INSERT INTO t_detail(d_title, d_status, d_order) VALUES('${title}', '0'," + "(SELECT IFNULL(MAX(d_order)+1, 1) FROM t_detail));"
        database.execSQL(InsertQuery)
        database.close()
    }

    fun FinishDetail(id: Int) {
        val dbHelper = DBHelper(this, "local_db.db", null, 9)
        val database = dbHelper.writableDatabase
        var updateQuery = "UPDATE t_detail SET d_status = '2' WHERE d_id = '${id}'"
        database.execSQL(updateQuery)
        database.close()
    }

    fun DeleteDetail(id: Int) {
        val dbHelper = DBHelper(this, "local_db.db", null, 9)
        val database = dbHelper.writableDatabase
        var deleteQuery = "DELETE FROM t_detail WHERE d_id = '${id}'"
        database.execSQL(deleteQuery)
        database.close()
    }

    override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
        itemTouchHelper.startDrag(viewHolder)
    }
}