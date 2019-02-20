package com.zhihui.imeeting.cloudmeeting.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.zhihui.imeeting.cloudmeeting.R;
import com.zhihui.imeeting.cloudmeeting.controller.MyURL;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SingInfoActivity extends Activity {
    private static final String TAG="SingInfoActivity";
    private ImageView back;
    private ListView list;
    private Message msg;
    private Handler handler;
    private int meetingId;

    private int[] userId;
    private String[] userName;
    private String[] userPhone;
    private String[] status;
    private String[] signTime;

//    private SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sing_info);
        init();
        getInfo();
        setListener();
    }
    public void init(){
        meetingId=getIntent().getIntExtra("meetingId",-1);
        back=findViewById(R.id.back);
        list=findViewById(R.id.list);
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 404:
                        Toast.makeText(SingInfoActivity.this,"数据错误",Toast.LENGTH_LONG).show();
                        break;
                    case 500:
                        Toast.makeText(SingInfoActivity.this,"数据错误",Toast.LENGTH_LONG).show();
                        break;
                    case 200:
                        List mdata=new ArrayList<Map<String,Object>>();
                        for(int i=0;i<userId.length;i++){
                            Map item=new HashMap<String,Object>();

                            item.put("name",userName[i]);
                            item.put("phone","联系方式"+userPhone[i]);
                            if(status[i].equals("未签到")){
                                item.put("status","未签到");
                            }else {
                                item.put("status","签到时间："+signTime[i]);
                            }
                            mdata.add(item);
                        }
                        SimpleAdapter adapter=new SimpleAdapter(SingInfoActivity.this,mdata,R.layout.sing_info_item,
                                new String[] {"name","phone","status"},
                                new int[]{R.id.name,R.id.phone,R.id.status} );
                        list.setAdapter(adapter);
                        break;
                    case 201:
                        Toast.makeText(SingInfoActivity.this,"提醒成功",Toast.LENGTH_LONG).show();
                        break;

                }
            }
        };
    }
    public void setListener(){
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int position, long l) {
                if (status[position].equals("未签到")){
                    final AlertDialog.Builder builder = new AlertDialog.Builder(SingInfoActivity.this);
                    builder.setTitle("是否发送提醒消息");
                    builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
//                        Toast.makeText(MeetingInfo2Activity.this,"取消会议",Toast.LENGTH_LONG).show();
                            MyURL url=new MyURL();
                            final OkHttpClient client = new OkHttpClient();
                            RequestBody body=new FormBody.Builder()
                                    .add("meetingId",meetingId+"")
                                    .add("userId",userId[position]+"")
                                    .build();
                            final Request request = new Request.Builder()
                                    .url(url.remindOne())
                                    .post(body)
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
                                        Log.w(TAG,result);
                                        JSONObject data =new JSONObject(result);
                                        boolean flag=data.getBoolean("status");
                                        if (flag){
                                            msg=Message.obtain();
                                            msg.what=201;
                                            handler.sendMessage(msg);
                                        }else {
                                            msg=Message.obtain();
                                            msg.what=500;
                                            handler.sendMessage(msg);
                                        }
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    });
                    builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    });
                    final AlertDialog dialog = builder.show();
                }
            }
        });
    }
    public void getInfo(){
        final MyURL url=new MyURL();
        final OkHttpClient client = new OkHttpClient();
        RequestBody form=new FormBody.Builder()
                .add("meetingId",meetingId+"")
                .build();
        final Request request = new Request.Builder()
                .url(url.showOneMeeting())
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
                    Log.w(TAG,result);
                    JSONObject data = new JSONObject(result);
                    boolean flag = data.getBoolean("status");
                    if (flag){
                        JSONArray info=data.getJSONArray("data");
                        userId=new int[info.length()];
                        userName=new String[info.length()];
                        userPhone=new String[info.length()];
                        status=new String[info.length()];
                        signTime=new String[info.length()];
                        for(int i=0;i<info.length();i++){
                            userId[i]=info.getJSONObject(i).getInt("userId");
                            userName[i]=info.getJSONObject(i).getString("userName");
                            userPhone[i]=info.getJSONObject(i).getString("userPhone");
                            status[i]=info.getJSONObject(i).getString("status");
                            signTime[i]=info.getJSONObject(i).getString("signTime");
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
                    e.printStackTrace();
                }
            }
        });
    }
}

