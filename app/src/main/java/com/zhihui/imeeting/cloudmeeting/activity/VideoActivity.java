package com.zhihui.imeeting.cloudmeeting.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.zhihui.imeeting.cloudmeeting.R;
import com.zhihui.imeeting.cloudmeeting.adapter.VideoRoomListAdapter;
import com.zhihui.imeeting.cloudmeeting.controller.MyURL;
import com.zhihui.imeeting.cloudmeeting.entity.Equip;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class VideoActivity extends Activity {
    private final static String TAG="VideoActivity";

    private ImageView back;
    private ImageView add;
    private RecyclerView list;

    private SharedPreferences sp;
    private Handler handler;
    private Message msg;

    private int id[];
    private String videoRoomName[];
    private String createTime[];
    private String name[];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        Init();
        getInfo();
        setListener();
    }
    public void Init(){
        sp=this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        back=findViewById(R.id.back);
        add=findViewById(R.id.add);
        list=findViewById(R.id.list);


        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 200:
                        list.setLayoutManager(new LinearLayoutManager(VideoActivity.this));
                        final VideoRoomListAdapter adapter=new VideoRoomListAdapter(VideoActivity.this);
                        adapter.setIds(id);
                        adapter.setVideoRoomNames(videoRoomName);
                        adapter.setCreateTimes(createTime);
                        adapter.setNames(name);
                        adapter.setOnItemClickLitener(new VideoRoomListAdapter.OnItemClickLitener() {
                            @Override
                            public void onItemClick(View view, int position) {







                                Intent intent=new Intent(VideoActivity.this,JoinVideoActivity.class);
                                startActivity(intent);
                            }
                        });
                        list.setAdapter(adapter);
                        break;
                    case 500:
                        Toast.makeText(VideoActivity.this,"数据错误",Toast.LENGTH_LONG).show();
                        break;
                    case 404:
                        Toast.makeText(VideoActivity.this,"连接超时",Toast.LENGTH_LONG).show();
                        break;
                }
            }
        };
    }
    public void getInfo(){
        MyURL url=new MyURL();
        final OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .addHeader("cookie", sp.getString("sessionID", ""))
                .url(url.selectMyVideoRoom())
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
                        JSONArray array=data.getJSONArray("data");
                        id=new int[array.length()];
                        videoRoomName=new String[array.length()];
                        createTime=new String[array.length()];
                        name=new String[array.length()];

                        for(int i=0;i<array.length();i++){
                            JSONObject item=array.getJSONObject(i);
                            id[i]=item.getInt("id");
                            videoRoomName[i]=item.getString("videoRoomName");
                            createTime[i]=item.getString("createTime");
                            name[i]=item.getJSONObject("userinfo").getString("name");
                        }

                        msg=Message.obtain();
                        msg.what=200;
                        handler.sendMessage(msg);
                    }else{
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

    public void setListener(){
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(VideoActivity.this,AddVideoActivity.class);
                startActivity(intent);
            }
        });
    }
}
