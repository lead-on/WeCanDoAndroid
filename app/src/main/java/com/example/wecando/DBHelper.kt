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
        var sql1 : String = "CREATE TABLE if not exists t_list (" +
                "l_id integer primary key autoincrement," +
                "l_title text," +
                "l_bg_tag String," +
                "l_order integer);";
        db.execSQL(sql1)

        var sql2 = "CREATE TABLE if not exists t_detail (" +
                "d_id integer primary key autoincrement," +
                "t_id integer," +
                "d_title text," +
                "d_status integer," +
                "d_order integer);";
        db.execSQL(sql2)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        val sql1 : String = "DROP TABLE if exists t_list"
        db.execSQL(sql1)
        val sql2 : String = "DROP TABLE if exists t_detail"
        db.execSQL(sql2)

        onCreate(db)
    }

}