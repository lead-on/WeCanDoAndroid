package com.example.wecando

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var TAG:String = "로그"

        val dbHelper = DBHelper(this, "local_db.db", null, 1)
        val database = dbHelper.writableDatabase

//        var inserQuery = "INSERT INTO t_list('l_title', 'l_img') values('Title_Test', 'Img_Test')"
//        database.execSQL(inserQuery)

        val profileList = arrayListOf<Profiles>()

        var selectQuery = "SELECT * FROM t_list"
        var cursor = database.rawQuery(selectQuery, null)
        while(cursor.moveToNext()) {
            profileList.add(Profiles("black", cursor.getString(cursor.getColumnIndex("l_title")), 0))
//            Log.d(TAG, cursor.getString(cursor.getColumnIndex("l_img")))
        }

        rv_list.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rv_list.setHasFixedSize(true)
        rv_list.adapter = ProfileAdapter(profileList, Intent(this, MainActivity::class.java))

        tv_add_list.setOnClickListener {
            Log.d(TAG, "click listner 타나 확인")
            startActivity(Intent(this, AddListActivity::class.java))
            finish()
        }

    }
}