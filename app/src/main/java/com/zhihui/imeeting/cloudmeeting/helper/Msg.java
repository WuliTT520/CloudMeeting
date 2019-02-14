package com.zhihui.imeeting.cloudmeeting.helper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class Msg {
    private DatabaseHelper dbHelper;
    public Msg(Context context){
        dbHelper=new DatabaseHelper(context);
    }
    public void insert(int id,String message,int receive_id,String time, int meeting_id){
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        String sql="insert into msg(id,message,receive_id,time,meeting_id) values(?,?,?,?,?)";
        Object obj[]={id,message,receive_id,time,meeting_id};
        db.execSQL(sql, obj);
    }
    public List<String[]> search(int receive_id){
        List<String[]> ans=new ArrayList<>();
        SQLiteDatabase db=dbHelper.getReadableDatabase();
        Cursor cursor=db.rawQuery("select * from msg where receive_id =?",new String[]{receive_id+""});
        while (cursor.moveToNext()){
            String[] item=new String[4];
            item[0]=cursor.getString(cursor.getColumnIndex("id"));
            item[1]=cursor.getString(cursor.getColumnIndex("message"));
            item[2]=cursor.getString(cursor.getColumnIndex("time"));
            item[3]=cursor.getString(cursor.getColumnIndex("meeting_id"));
            ans.add(item);
        }
        return ans;
    }
    public void delete(int id){
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        db.delete("msg","id = ?",new String[]{id+""});
    }
}
