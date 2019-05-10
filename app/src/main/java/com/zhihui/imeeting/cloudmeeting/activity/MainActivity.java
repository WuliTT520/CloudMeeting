package com.zhihui.imeeting.cloudmeeting.activity;


import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zhihui.imeeting.cloudmeeting.R;
import com.zhihui.imeeting.cloudmeeting.controller.MyURL;
import com.zhihui.imeeting.cloudmeeting.helper.FileHelper;
import com.zhihui.imeeting.cloudmeeting.helper.Userinfo;
import com.zhihui.imeeting.cloudmeeting.service.MsgService;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity {
    private static final String TAG="MainActivity";
    FrameLayout show;
    LinearLayout home,schedule,add,news,mine;
    ImageView home_pic,schedule_pic,add_pic,news_pic,mine_pic;
    TextView home_text,schedule_text,add_text,news_text,mine_text;
    private SharedPreferences sp;
    private Handler handler;
    private Message msg;
    private List<Integer> id=new ArrayList<>();
    private List<String> name=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        setListener();
        getInfo();
        getInfo2();
        BindService();
    }

    private void BindService() {
        Intent intent=new Intent(MainActivity.this,MsgService.class);
        startService(intent);
//        Toast.makeText(MainActivity.this,"开始服务",Toast.LENGTH_LONG).show();
    }

    public void init(){

        sp=this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 404:
                        Toast.makeText(MainActivity.this,"网络异常",Toast.LENGTH_SHORT).show();
                        break;
                    case 500:
                        Toast.makeText(MainActivity.this,"请先登陆",Toast.LENGTH_SHORT).show();
                        break;
                    case 200:
                        Userinfo userinfo=new Userinfo(MainActivity.this);
                        userinfo.delete();
                        for(int i=0;i<id.size();i++){
                            userinfo.insert(id.get(i),name.get(i));
                        }
                        Log.w(TAG,"同事数据存储完毕");
                        break;
                }
            }
        };
        show=findViewById(R.id.show);
        home=findViewById(R.id.home);
        schedule=findViewById(R.id.schedule);
        add=findViewById(R.id.add);
        news=findViewById(R.id.news);
        mine=findViewById(R.id.mine);

        home_pic=findViewById(R.id.home_pic);
        schedule_pic=findViewById(R.id.schedule_pic);
        add_pic=findViewById(R.id.add_pic);
        news_pic=findViewById(R.id.new_pic);
        mine_pic=findViewById(R.id.mine_pic);

        home_text=findViewById(R.id.home_text);
        schedule_text=findViewById(R.id.schedule_text);
        add_text=findViewById(R.id.add_text);
        news_text=findViewById(R.id.news_text);
        mine_text=findViewById(R.id.mine_text);

        home_pic.setImageResource(R.drawable.main_home_ac);
        home_text.setTextColor(getColor(R.color.colorPrimary));
        FragmentManager manager=getFragmentManager();
        final FragmentTransaction transaction=manager.beginTransaction();
        HomeFragment homeFragment=new HomeFragment();
        transaction.replace(R.id.show,homeFragment);
        transaction.commit();

    }
    public void setListener(){

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeColor(1);
                FragmentManager manager=getFragmentManager();
                final FragmentTransaction transaction=manager.beginTransaction();
                HomeFragment homeFragment=new HomeFragment();
                transaction.replace(R.id.show,homeFragment);
                transaction.commit();
            }
        });

        schedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeColor(2);
                FragmentManager manager=getFragmentManager();
                final FragmentTransaction transaction=manager.beginTransaction();
                ScheduleFragment scheduleFragment=new ScheduleFragment();
                transaction.replace(R.id.show,scheduleFragment);
                transaction.commit();
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                changeColor(3);
//                FragmentManager manager=getFragmentManager();
//                final FragmentTransaction transaction=manager.beginTransaction();
//                AddFragment addFragment=new AddFragment();
//                transaction.replace(R.id.show,addFragment);
//                transaction.commit();
//                Intent intent=new Intent(MainActivity.this, AddActivity.class);
                Intent intent=new Intent(MainActivity.this, AIActivity.class);
                startActivity(intent);
            }
        });

        news.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeColor(4);
                FragmentManager manager=getFragmentManager();
                final FragmentTransaction transaction=manager.beginTransaction();
                NewsFragment newsFragment=new NewsFragment();
                transaction.replace(R.id.show,newsFragment);
                transaction.commit();
            }
        });

        mine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeColor(5);
                FragmentManager manager=getFragmentManager();
                final FragmentTransaction transaction=manager.beginTransaction();
                MineFragment mineFragment=new MineFragment();
                transaction.replace(R.id.show,mineFragment);
                transaction.commit();
            }
        });
    }

    public void changeColor(int i){
        if (i==1){
            home_pic.setImageResource(R.drawable.main_home_ac);
            home_text.setTextColor(getColor(R.color.colorPrimary));
        }else{
            home_pic.setImageResource(R.drawable.main_home_pt);
            home_text.setTextColor(getColor(R.color.text));
        }
        if (i==2){
            schedule_pic.setImageResource(R.drawable.contacts_ac);
            schedule_text.setTextColor(getColor(R.color.colorPrimary));
        }else{
            schedule_pic.setImageResource(R.drawable.contacts_pt);
            schedule_text.setTextColor(getColor(R.color.text));
        }

        if (i==4){
            news_pic.setImageResource(R.drawable.main_news_ac);
            news_text.setTextColor(getColor(R.color.colorPrimary));
        }else{
            news_pic.setImageResource(R.drawable.main_news_pt);
            news_text.setTextColor(getColor(R.color.text));
        }
        if (i==5){
            mine_pic.setImageResource(R.drawable.main_mine_ac);
            mine_text.setTextColor(getColor(R.color.colorPrimary));
        }else{
            mine_pic.setImageResource(R.drawable.main_mine_pt);
            mine_text.setTextColor(getColor(R.color.text));
        }
    }
    public void getInfo(){
        MyURL url=new MyURL();
        final OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .addHeader("cookie", sp.getString("sessionID", ""))
                .url(url.showUser())
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
                    Log.w(TAG, result);
                    JSONObject data = new JSONObject(result);
                    boolean flag = data.getBoolean("status");
                    if (flag){
                        JSONArray users=data.getJSONArray("data").getJSONArray(1);
                        for(int i=0;i<users.length();i++){
                            for(int j=0;j<users.getJSONArray(i).length();j++){
//                                Log.w(TAG,"id="+users.getJSONArray(i).getJSONObject(j).getInt("id")+",name="+users.getJSONArray(i).getJSONObject(j).getString("name"));
                                id.add(users.getJSONArray(i).getJSONObject(j).getInt("id"));
                                name.add(users.getJSONArray(i).getJSONObject(j).getString("name"));
                            }
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
    public void getInfo2(){
        MyURL url=new MyURL();
        final OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .addHeader("cookie", sp.getString("sessionID", ""))
                .url(url.reserveIndex())
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                msg=Message.obtain();
                msg.what=404;
                handler.sendMessage(msg);
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String result = response.body().string();
                    Log.w(TAG, result);
                    JSONObject data = new JSONObject(result);
                    boolean flag = data.getBoolean("status");
                    if (flag){
                        FileHelper helper=new FileHelper();
                        helper.write(MainActivity.this,"meetingInfo.txt",result);
//                        Log.w(TAG,helper.read(MainActivity.this,"meetingInfo.txt"));
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