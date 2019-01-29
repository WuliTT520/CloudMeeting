package com.zhihui.imeeting.cloudmeeting.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";
    private static final String DB_NAME = "IMeeting.db"; //数据库名称
    private static final int version = 1; //数据库版本

    DatabaseHelper(Context context) {
        super(context, DB_NAME, null, version);
        // TODO Auto-generated constructor stub
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sql = "create table userinfo( id int(20) primary key not null , name varchar(20) not null );";
//        String sql2="create table ding( _no integer PRIMARY KEY , text varchar(100) not null , time varchar(20) not null);";
//        System.out.println(sql2);
        Log.i(TAG, "create Database------------->");
        sqLiteDatabase.execSQL(sql);
//        sqLiteDatabase.execSQL(sql2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        Log.i(TAG, "update Database------------->");
    }
}
