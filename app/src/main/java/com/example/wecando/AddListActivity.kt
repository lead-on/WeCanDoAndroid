package com.example.wecando

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_add_list.*

class AddListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_list)

        tv_header_cancle.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        tv_header_complete.setOnClickListener {
            val dbHelper = DBHelper(this, "local_db.db", null, 1)
            val database = dbHelper.writableDatabase
            var list_title = et_add_list_title.text.trim()
            if (list_title.length >= 1) {
                var insertQuery = "INSERT INTO t_list('l_title', 'l_img') values('${list_title}', 'black')"
                database.execSQL(insertQuery)

                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
    }
}