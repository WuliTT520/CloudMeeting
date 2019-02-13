package com.zhihui.imeeting.cloudmeeting.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.zhihui.imeeting.cloudmeeting.R;
import com.zhihui.imeeting.cloudmeeting.activity.MainActivity;
import com.zhihui.imeeting.cloudmeeting.controller.MyURL;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MsgService extends Service {
    public static final String TAG = "MsgService";
    SharedPreferences sp;
    Timer timer;
    int id=0;
    public MsgService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public void onCreate() {
        super.onCreate();


        timer=new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                final OkHttpClient client = new OkHttpClient();
                MyURL myURL=new MyURL();
                sp=getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                Log.w(TAG,sp.getString("sessionID",""));
                final Request request=new Request.Builder()
                        .addHeader("cookie",sp.getString("sessionID",""))
                        .url(myURL.getMsg())
                        .build();
                Call call = client.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.w(TAG,"请求错误");
                    }

                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        try {
                            String result = response.body().string();
                            Log.w("success",result);
                            JSONObject data = new JSONObject(result);
                            boolean flag = data.getBoolean("status");
                            if (flag){
                                JSONArray info=data.getJSONArray("data");
                                if (info.length()==0){
                                    return;
                                }
                                for(int i=0;i<info.length();i++){
                                    String message=info.getJSONObject(i).getString("message");
                                    NotificationManager manager=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                    NotificationChannel channel = new NotificationChannel("1",
                                            "动态消息", NotificationManager.IMPORTANCE_DEFAULT);
                                    channel.enableLights(true); //是否在桌面icon右上角展示小红点
                                    channel.setLightColor(R.color.red); //小红点颜色
                                    channel.setShowBadge(true); //是否在久按桌面图标时显示此渠道的通知
                                    manager.createNotificationChannel(channel);

                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0,  intent, PendingIntent.FLAG_CANCEL_CURRENT);

                                    Notification.Builder builder=new Notification.Builder(MsgService.this)
                                            .setSmallIcon(R.drawable.logo144px)
                                            .setContentTitle("会议室管理系统")/*设置标题*/
                                            .setChannelId("1")/*设置渠道*/
                                            .setContentIntent(contentIntent)/*设置跳转页面*/
                                            .setAutoCancel(true)/*点击消失*/
                                            .setContentText(message);
                                    manager.notify(id,builder.build());
                                    id++;
                                }
                            }

                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });
            }
        },1000,1000*10);
    }
}
