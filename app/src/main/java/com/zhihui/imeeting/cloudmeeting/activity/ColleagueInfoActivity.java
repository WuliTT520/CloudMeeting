package com.zhihui.imeeting.cloudmeeting.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zhihui.imeeting.cloudmeeting.R;

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
                        break;
                    case 404:
                        break;
                    case 500:
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

    }
}
