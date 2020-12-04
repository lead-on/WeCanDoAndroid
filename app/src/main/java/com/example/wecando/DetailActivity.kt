package com.example.wecando

import android.content.Context
import android.content.Intent
import android.graphics.drawable.InsetDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
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
import java.util.*

class DetailActivity : AppCompatActivity(), ItemDragListener {

    var curStatus = "NORMAL"
    var editTitle = ""
    var editPosition = 0

    private lateinit var itemTouchHelperCallback: ItemTouchHelperCallback
    private lateinit var itemTouchHelper: ItemTouchHelper
    private lateinit var adapter: DetailAdapter
    var t_id: Int = 0
    val detailList = arrayListOf<DetailModel>()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        t_id = intent.getIntExtra("t_id", 0)

        val dbHelper = DBHelper(this, "local_db.db", null, 9)
        val database = dbHelper.writableDatabase


        var selectQuery = "SELECT * FROM t_detail WHERE t_id = '${t_id}' ORDER BY d_order ASC"
        var cursor = database.rawQuery(selectQuery, null)

        while (cursor.moveToNext()) {
            detailList.add(DetailModel(cursor.getInt(cursor.getColumnIndex("d_status")), cursor.getInt(cursor.getColumnIndex("d_id")), cursor.getString(cursor.getColumnIndex("d_title")).toString(), cursor.getInt(cursor.getColumnIndex("d_order"))))
        }
        cursor.close()
        database.close()

        var layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        adapter = DetailAdapter(detailList, this, this)

        rv_details.layoutManager = layoutManager
        rv_details.setHasFixedSize(true)
        rv_details.adapter = adapter

        itemTouchHelperCallback = ItemTouchHelperCallback(adapter, true).apply {
            setClamp(200f)
        }

        itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(rv_details)
        rv_details.apply {
            setOnTouchListener { _, _ ->
                itemTouchHelperCallback.removePreviousClamp(this)
                false
            }
        }

        //목록뒤로가기 클릭 이벤트
        tv_header_back.setOnClickListener {
            CloseKeyboard()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        //새로운할일 클릭 이벤트
        layout_add_detail.setOnClickListener {
            var isDuplicated = false
            for (i in detailList) {
                if (i.status == DetailModel.ADD) {
                    isDuplicated = true
                }
            }

            //EDIT 상태인 아이템이 없을때만 아이템 추가
            if (!isDuplicated) {

                curStatus = "ADD"
                UpKeyboard()

                val database = dbHelper.writableDatabase
                var EditselectQuery = "SELECT IFNULL(MAX(d_id), 1) AS MaxId, IFNULL(MAX(d_order), 1) AS MaxOrder FROM t_detail"
                var cursor = database.rawQuery(EditselectQuery, null)
                while (cursor.moveToNext()) {
                    detailList.add(DetailModel(DetailModel.ADD,cursor.getInt(cursor.getColumnIndex("MaxId")), "", cursor.getInt(cursor.getColumnIndex("MaxOrder"))))
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

                tv_detail_header_btn.visibility = View.VISIBLE
                layout_add_detail.visibility = View.INVISIBLE

            } else {
                Toast.makeText(this, "먼저 추가한 일정부터 완성해주세요!", Toast.LENGTH_LONG).show()
            }
        }

        //추가 버튼 클릭
        tv_detail_header_btn.setOnClickListener {
            when (it.tag) {
                "add" -> {
                    val database = dbHelper.writableDatabase
                    var title = rv_details.et_detail_title.text.trim()

                    //title이 빈값이 아닐때
                    if (title.length > 1) {
                        var selectMaxOrder =
                            "SELECT IFNULL(MAX(d_order), 1) AS cnt FROM t_detail;"
                        var C = database.rawQuery(selectMaxOrder, null)
                        var MaxOrder = 0
                        while (C.moveToNext()) {
                            MaxOrder = C.getInt(C.getColumnIndex("cnt"))
                        }
                        var order = MaxOrder +1
                        var isModify = false
                        var ModifyId = 0
                        for (i in detailList) {
                            if (i.status == DetailModel.MODIFY) {
                                isModify = true
                                ModifyId = i.id
                                order = i.order
                            }
                        }

                        if (isModify) {
                            var deleteQuery = "DELETE FROM t_detail WHERE d_id = '${ModifyId}'"
                            database.execSQL(deleteQuery)
                        }

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
//                        adapter.notifyDataSetChanged()
                        rv_details.removeAllViewsInLayout()
                        rv_details.layoutManager =
                            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                        rv_details.setHasFixedSize(true)
                        rv_details.adapter = adapter
                        CloseKeyboard()

                        tv_detail_header_btn.text = ""
                        tv_detail_header_btn.tag = ""
                        tv_detail_header_btn.visibility = View.INVISIBLE
                        layout_add_detail.visibility = View.VISIBLE
                        curStatus = "NORMAL"

                    } else { // title 빈값일때
                        Toast.makeText(this, "제목을 다시 확인해주세요!", Toast.LENGTH_LONG).show()
                    }
                }
                "complete" -> {
                    Toast.makeText(this, "완료버튼 클릭", Toast.LENGTH_LONG).show()
                    tv_detail_header_btn.text = ""
                    tv_detail_header_btn.tag = ""
                    tv_detail_header_btn.visibility = View.INVISIBLE

                }
                else -> {
                    Toast.makeText(this, "상태코드 에러", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    fun FinishDetail(id: Int, status: Int) {
        val dbHelper = DBHelper(this, "local_db.db", null, 9)
        val database = dbHelper.writableDatabase
        var updateQuery = "UPDATE t_detail SET d_status = '2' WHERE d_id = '${id}'"
        if (status == DetailModel.FINISH) {
            updateQuery = "UPDATE t_detail SET d_status = '0' WHERE d_id = '${id}'"
        }
        database.execSQL(updateQuery)
        database.close()
    }

    fun DeleteDetail(id: Int) {
        val dbHelper = DBHelper(this, "local_db.db", null, 9)
        val database = dbHelper.writableDatabase
        var deleteQuery = "DELETE FROM t_detail WHERE d_id = '${id}'"
        database.execSQL(deleteQuery)
        var selectQuery = "SELECT * FROM t_detail WHERE t_id = '${t_id}' ORDER BY d_order ASC"
        var cursor = database.rawQuery(selectQuery, null)

        rv_details.removeAllViewsInLayout()
        detailList.clear()
        while (cursor.moveToNext()) {
            detailList.add(DetailModel(cursor.getInt(cursor.getColumnIndex("d_status")), cursor.getInt(cursor.getColumnIndex("d_id")), cursor.getString(cursor.getColumnIndex("d_title")).toString(), cursor.getInt(cursor.getColumnIndex("d_order"))))
        }

        rv_details.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rv_details.setHasFixedSize(true)
        rv_details.adapter = adapter

        database.close()

        adapter.notifyDataSetChanged()
        CloseKeyboard()
    }

    fun ModifyDetail(id: Int, position: Int, title: String, order: Int) {
        editTitle = title
        editPosition = position
        curStatus = "EDIT"
        tv_detail_header_btn.text = "완료"
        tv_detail_header_btn.tag = "add"
        tv_detail_header_btn.visibility = View.VISIBLE

        val dbHelper = DBHelper(this, "local_db.db", null, 9)
        val database = dbHelper.writableDatabase
        var selectQuery = "SELECT * FROM t_detail WHERE t_id = '${t_id}' ORDER BY d_order ASC"

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

        detailList.get(position).status = DetailModel.MODIFY
        rv_details.removeAllViewsInLayout()
        rv_details.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rv_details.setHasFixedSize(true)
        rv_details.adapter = adapter

        layout_add_detail.visibility = View.INVISIBLE
        UpKeyboard()
    }

    fun CancleDetailModify(view: View = tv_header_back) {
        if (curStatus == "EDIT" || curStatus == "ADD") {
            val dbHelper = DBHelper(this, "local_db.db", null, 9)
            val database = dbHelper.writableDatabase

            var selectQuery = "SELECT * FROM t_detail WHERE t_id = '${t_id}' ORDER BY d_order ASC"
            var cursor = database.rawQuery(selectQuery, null)

            detailList.clear()
            while (cursor.moveToNext()) {
                detailList.add(DetailModel(cursor.getInt(cursor.getColumnIndex("d_status")), cursor.getInt(cursor.getColumnIndex("d_id")), cursor.getString(cursor.getColumnIndex("d_title")).toString(), cursor.getInt(cursor.getColumnIndex("d_order"))))
            }

            cursor.close()
            database.close()
//            adapter.notifyDataSetChanged()
            rv_details.removeAllViewsInLayout()
            rv_details.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            rv_details.setHasFixedSize(true)
            rv_details.adapter = adapter

            tv_detail_header_btn.text = ""
            tv_detail_header_btn.tag = ""
            tv_detail_header_btn.visibility = View.INVISIBLE

            layout_add_detail.visibility = View.VISIBLE
            CloseKeyboard()
            curStatus = "NORMAL"
        }

    }

    fun CloseKeyboard() {
        var view = this.currentFocus

        if(view != null)
        {
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
        itemTouchHelper.startDrag(viewHolder)
    }

    fun UpKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
    }
}