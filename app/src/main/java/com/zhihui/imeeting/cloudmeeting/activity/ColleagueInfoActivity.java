package com.zhihui.imeeting.cloudmeeting.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zhihui.imeeting.cloudmeeting.R;
import com.zhihui.imeeting.cloudmeeting.controller.MyURL;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ColleagueInfoActivity extends Activity {
    private int id;
    private ImageView back;
    private TextView gonghao;
    private TextView xingming;
    private TextView jianjie;
    private TextView dianhua;
    private Button call;
    private Handler handler;
    private Message msg;

    private String worknum;
    private String name;
    private String phone;
    private String resume;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_colleague_info);
        init();
        getInfo();
        setListener();
    }
    public void init(){
        Intent intent=getIntent();
        id=intent.getIntExtra("userId",-1);
//        Toast.makeText(ColleagueInfoActivity.this,id+"",Toast.LENGTH_LONG).show();
        back=findViewById(R.id.back);
        gonghao=findViewById(R.id.gonghao);
        xingming=findViewById(R.id.xingming);
        jianjie=findViewById(R.id.jianjie);
        dianhua=findViewById(R.id.dianhua);
        call=findViewById(R.id.call);
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 200:
                        gonghao.setText(worknum);
                        xingming.setText(name);
                        dianhua.setText(phone);
                        jianjie.setText(resume);
                        break;
                    case 404:
                        Toast.makeText(ColleagueInfoActivity.this,"网络错误",Toast.LENGTH_LONG);
                        break;
                    case 500:
                        Toast.makeText(ColleagueInfoActivity.this,"该人员不存在",Toast.LENGTH_LONG);
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
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.setAction(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+dianhua.getText().toString()));
                startActivity(intent);
            }
        });
    }
    public void getInfo(){
        MyURL url=new MyURL();
        final OkHttpClient client = new OkHttpClient();
        RequestBody body=new FormBody.Builder()
                .add("id",id+"")
                .build();
        final Request request = new Request.Builder()
                .url(url.showOne())
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
//                    Log.w(TAG,result);
                    JSONObject data =new JSONObject(result);
                    boolean flag=data.getBoolean("status");
                    if (flag){
                        JSONObject info=data.getJSONObject("data");
                        worknum=info.getString("worknum");
                        name=info.getString("name");
                        phone=info.getString("phone");
                        resume=info.getString("resume");
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
