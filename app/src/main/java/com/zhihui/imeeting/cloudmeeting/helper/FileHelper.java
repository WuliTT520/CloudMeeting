package com.zhihui.imeeting.cloudmeeting.helper;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
//写入文件会添加数据在原文件后面
import static android.content.Context.MODE_APPEND;
//写入文件会覆盖原文件内容
import static android.content.Context.MODE_PRIVATE;

public class FileHelper {
    //写入文件
    public void write(Context context,String fileName,String msg){

        if(msg == null) return;
        try {

            FileOutputStream fos =context.openFileOutput(fileName, MODE_PRIVATE);

            fos.write(msg.getBytes());

            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //读取文件，返回字符串
    public String read(Context context,String fileName) {
        try {
            FileInputStream inStream = context.openFileInput(fileName);
            byte[] buffer = new byte[1024];
            int hasRead = 0;
            StringBuilder sb = new StringBuilder();
            while ((hasRead = inStream.read(buffer)) != -1) {
                sb.append(new String(buffer, 0, hasRead));
            }

            inStream.close();
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    //判断是否有该文件
    public boolean existsFile(Context context,String fileName){
        String path = context.getFilesDir().getPath()+"//";
        File file = new File(path+fileName);
        if(file.exists()){
            return true;
        }
        return false;
    }
}
