package com.example.gongzhiyao.twonote03.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by 宫智耀 on 2016/4/9.
 */
public class MyDB extends SQLiteOpenHelper {

    final String sq_notes = "create table  notes(" +
            "name Text," +
            "content text," +
            "date Text," +
            "image  TEXT," +
            " summary TEXT," +
            "is_collect TEXT DEFAULT \"false\"," +
            "passwd TEXT DEFAULT \"\"," +
            "_id INTEGER PRIMARY KEY AUTOINCREMENT)";
    final String sq_media = "create table media(" +
            "path TEXT," +
            "title TEXT," +
            "date TEXT," +
            "_id INTEGER PRIMARY KEY AUTOINCREMENT)";
    final String sq_reminds = "create table reminds(" +
            "title TEXT," +
            "date  TEXT  DEFAULT \"\"," +
            "time TEXT  DEFAULT \"\"," +
            "sub_task TEXT  DEFAULT \"\"," +
            "note  TEXT DEFAULT \"\"," +
            " notes_id INTEGER," +
            "task_id  INTEGER,"+
            "is_check TEXT DEFAULT \"false\"," +
            "is_collection TEXT DEFAULT \"false\"," +
            "_id INTEGER PRIMARY KEY AUTOINCREMENT)";

    final String Sq_task="create table sub_tasks("+
            "title TEXT,"+
            "father_id INTEGER ,"+
            "is_check TEXT DEFAULT \"false\","+
            "_id INTEGER PRIMARY KEY AUTOINCREMENT)";


    final String Sq_relate="create table relates("+
            "title TEXT,"+
            "father_id INTEGER,"+
            "relates_id INTEGER,"+
            "_id INTEGER PRIMARY KEY AUTOINCREMENT)";



    public MyDB(Context context) {
        super(context, "database", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(sq_notes);
        db.execSQL(sq_media);
        db.execSQL(sq_reminds);
        db.execSQL(Sq_task);
        db.execSQL(Sq_relate);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
