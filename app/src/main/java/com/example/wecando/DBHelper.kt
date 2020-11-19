package com.example.wecando

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(
    context: Context?,
    name: String?,
    factory: SQLiteDatabase.CursorFactory?,
    version: Int
) : SQLiteOpenHelper(context, name, factory, version) {

    override fun onCreate(db: SQLiteDatabase) {
        var sql : String = "CREATE TABLE if not exists t_list (" +
                "l_id integer primary key autoincrement," +
                "l_title text," +
                "l_bg_tag String," +
                "l_order integer);";
        db.execSQL(sql)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        val sql : String = "DROP TABLE if exists t_list"

        db.execSQL(sql)
        onCreate(db)
    }

}