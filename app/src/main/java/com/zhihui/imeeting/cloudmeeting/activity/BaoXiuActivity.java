package com.zhihui.imeeting.cloudmeeting.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.zhihui.imeeting.cloudmeeting.R;
import com.zhihui.imeeting.cloudmeeting.controller.MyURL;
import com.zhihui.imeeting.cloudmeeting.entity.Equip;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class BaoXiuActivity extends Activity {
    private static String TAG="BaoXiuActivity";
    private int MeetingRoomID;
    private SharedPreferences sp;
    private Message msg;
    private Handler handler;
    private Equip[] equips;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bao_xiu);
        init();
        getInfo();
    }
    public void init(){
        Intent intent=getIntent();
        MeetingRoomID=intent.getIntExtra("meetRoomId",-1);
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 200:
                        break;
                    case 404:
                        Toast.makeText(BaoXiuActivity.this,"网络错误",Toast.LENGTH_LONG).show();
                        break;
                    case 500:
                        break;
                }
            }
        };
    }
    public void getInfo(){
        MyURL url=new MyURL();
        final OkHttpClient client = new OkHttpClient();
        RequestBody form=new FormBody.Builder()
                .add("meetRoomId",MeetingRoomID+"")
                .build();
        final Request request = new Request.Builder()
                .addHeader("cookie", sp.getString("sessionID", ""))
                .url(url.getOneRoomEquip())
                .post(form)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                msg=Message.obtain();
                msg.what=404;
                handler.sendMessage(msg);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String result = response.body().string();
//                    Log.w(TAG,result);
                    JSONObject data =new JSONObject(result);
                    boolean flag=data.getBoolean("status");
                    if (flag){
                        JSONArray array=data.getJSONArray("data");
                        equips=new Equip[array.length()];
                        for(int i=0;i<array.length();i++){
                            equips[i]=new Equip(array.getJSONObject(i).getJSONObject("equip").getInt("id"),
                                    array.getJSONObject(i).getJSONObject("equip").getString("name"));
                        }
                        msg=Message.obtain();
                        msg.what=200;
                        handler.sendMessage(msg);
                    }else {
                        msg=Message.obtain();
                        msg.what=500;
                        handler.sendMessage(msg);
                    }
                }catch (Exception e){

                }
            }
        });

    }
}
