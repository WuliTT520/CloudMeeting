package com.zhihui.imeeting.cloudmeeting.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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

public class SetResumeActivity extends Activity {

    private static final String TAG="SetResumeActivity";
    private EditText resumeTV;
    private ImageView back;
    private TextView change;
    private SharedPreferences sp;
    private Handler handler;
    private Message msg;
    private String resume;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_resume);
        init();
        setListener();
    }
    public void init(){
        resumeTV=findViewById(R.id.resume);
        back=findViewById(R.id.back);
        change=findViewById(R.id.change);
        sp=this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        final Intent integer=getIntent();
        resume =integer.getStringExtra("resume");
        resumeTV.setText(resume);
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case 404:
                        Toast.makeText(SetResumeActivity.this,"网络异常",Toast.LENGTH_SHORT).show();
                        break;
                    case 500:
                        Toast.makeText(SetResumeActivity.this,"数据异常",Toast.LENGTH_SHORT).show();
                        break;
                    case 200:
                        Toast.makeText(SetResumeActivity.this,"修改成功",Toast.LENGTH_SHORT).show();
                        Intent intent=getIntent();
                        intent.putExtra("resume",resume);
                        setResult(100, intent);
                        finish();
                        break;
                }
                super.handleMessage(msg);
            }
        };
    }
    public void setListener(){
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=getIntent();
//                intent.putExtra("resume",resume);
                setResult(500, intent);
                finish();
            }
        });
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resume=resumeTV.getText().toString();
                MyURL url=new MyURL();
                final OkHttpClient client = new OkHttpClient();
                RequestBody body=new FormBody.Builder()
                        .add("resume",resume)
                        .build();
                final Request request = new Request.Builder()
                        .addHeader("cookie", sp.getString("sessionID", ""))
                        .url(url.updateResume())
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
        });
    }

}
