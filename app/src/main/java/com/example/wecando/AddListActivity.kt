package com.example.wecando

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_add_list.*
import kotlinx.android.synthetic.main.list_item.*

class AddListActivity : AppCompatActivity() {
    val TAG = "로그"
    var bg_color = (R.drawable.circle_black)
    var tag = "BLACK"

    override fun onCreate(savedInstanceState: Bundle?) {
        getResources()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_list)

        tv_header_cancle.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        tv_header_complete.setOnClickListener {
            val dbHelper = DBHelper(this, "local_db.db", null, 7)
            val database = dbHelper.writableDatabase
            var list_title = et_add_list_title.text.trim().toString()
            if (list_title.length >= 1) {

//                var contentValues = ContentValues()
//                contentValues.put("l_title", list_title)
//                contentValues.put("l_bg_tag", tag)
//                database.insert("list_t", null, )

                var selectMaxOrder = "SELECT IFNULL(MAX(l_order)+1, 1) AS cnt FROM t_list;"
                var cursor = database.rawQuery(selectMaxOrder, null)

                while(cursor.moveToNext()) {
                    var maxOrder = cursor.getInt(cursor.getColumnIndex("cnt"))
                    Log.d(TAG, "max Order: ${maxOrder.toString()}")
                    var insertQuery = "INSERT INTO t_list('l_title', 'l_bg_tag', 'l_order') values('${list_title}', '${tag}'," + "(SELECT IFNULL(MAX(l_order)+1, 1) FROM t_list));"
                    database.execSQL(insertQuery)
                }

                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "목록 이름을 확인해주세요!", Toast.LENGTH_LONG).show()
            }
        }

    }

    fun changeColor(view: View) {

        tag = view.getTag().toString()

        when (tag) {
            "BLACK" -> iv_add_list_image.setBackgroundResource(R.drawable.circle_black)
            "RED" -> iv_add_list_image.setBackgroundResource(R.drawable.circle_red)
            "ORANGE" -> iv_add_list_image.setBackgroundResource(R.drawable.circle_orange)
            "SALGU" -> iv_add_list_image.setBackgroundResource(R.drawable.circle_salgu)
            "LIGHT_GREEN" -> iv_add_list_image.setBackgroundResource(R.drawable.circle_light_green)
            "GREEN" -> iv_add_list_image.setBackgroundResource(R.drawable.circle_green)
            "SKYBLUE" -> iv_add_list_image.setBackgroundResource(R.drawable.circle_skyblue)
            "BLUE" -> iv_add_list_image.setBackgroundResource(R.drawable.circle_blue)
            "PURPLE" -> iv_add_list_image.setBackgroundResource(R.drawable.circle_purple)
            "GREY" -> iv_add_list_image.setBackgroundResource(R.drawable.circle_grey)
            "LIGHT_BROWN" -> iv_add_list_image.setBackgroundResource(R.drawable.circle_light_brown)
            "BROWN" -> iv_add_list_image.setBackgroundResource(R.drawable.circle_brown)

            else -> iv_add_list_image.setBackgroundResource(R.drawable.circle_black)
        }
    }

}