package com.zhihui.imeeting.cloudmeeting.helper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class Userinfo {
    private DatabaseHelper dbHelper;
    public Userinfo(Context context){
        dbHelper=new DatabaseHelper(context);
    }
    public void insert(int id,String name){
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        String sql="insert into userinfo(id,name) values(?,?)";
        Object obj[]={id,name};
        db.execSQL(sql, obj);
    }
    public String searchById(int id){
        String name="";
        SQLiteDatabase db=dbHelper.getReadableDatabase();
        Cursor cursor=db.rawQuery("select * from userinfo where id =?",new String[]{id+""});
        while (cursor.moveToNext()){
            name=cursor.getString(cursor.getColumnIndex("name"));
        }
        return name;
    }
    public boolean search(int id){
        SQLiteDatabase db=dbHelper.getReadableDatabase();
        Cursor cursor=db.rawQuery("select * from userinfo where id =?",new String[]{id+""});
        return cursor.moveToNext();
    }
    public void update(int id,String name){
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        db.execSQL("update userinfo set name=? where id=?",new Object[] {name,id});
    }
    public void delete(){
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        db.execSQL("delete from userinfo");
    }
}
