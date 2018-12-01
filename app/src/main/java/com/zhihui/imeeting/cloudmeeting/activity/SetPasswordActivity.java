package com.zhihui.imeeting.cloudmeeting.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

public class SetPasswordActivity extends AppCompatActivity {

    ImageView fanhui;
    Button ok;
    EditText newpwd,newpwd2;
    Handler handler;
    Message msg;
    String phone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_password);
        init();
        setListener();
    }
    public void init(){
        fanhui=findViewById(R.id.fanhui);
        ok=findViewById(R.id.ok);
        newpwd=findViewById(R.id.newpwd);
        newpwd2=findViewById(R.id.newpwd2);

        Intent intent=getIntent();
        phone=intent.getStringExtra("phone");

        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case 1:
                        Toast.makeText(SetPasswordActivity.this,"网络异常",Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        Toast.makeText(SetPasswordActivity.this,"更改密码失败",Toast.LENGTH_SHORT).show();
                        break;
                    case 3:
                        Intent intent2=new Intent(SetPasswordActivity.this,LoginActivity.class);
                        startActivity(intent2);
                        finish();
                        break;
                }
                super.handleMessage(msg);
            }
        };
    }
    public void setListener(){
        fanhui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pwd=newpwd.getText().toString();
                if(pwd.equals(newpwd2.getText().toString())){
                    if (pwd.length()<6||pwd.length()>32){
                        Toast.makeText(SetPasswordActivity.this,"密码长度必须为6-32位",Toast.LENGTH_SHORT).show();
                    }else{
                        final OkHttpClient client = new OkHttpClient();
                        RequestBody form=new FormBody.Builder()
                                .add("phone",phone)
                                .add("password",pwd)
                                .build();
                        final Request request=new Request.Builder()
                                .url(new MyURL().pwdCode())
                                .post(form)
                                .build();
                        Call call = client.newCall(request);
                        call.enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                msg=Message.obtain();
                                msg.what=1;
                                handler.sendMessage(msg);
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                try {
                                    String result = response.body().string();
                                    JSONObject data =new JSONObject(result);
                                    Boolean flag=data.getBoolean("status");
                                    if (flag){
                                        msg=Message.obtain();
                                        msg.what=3;
                                        handler.sendMessage(msg);
                                    }else{
                                        msg=Message.obtain();
                                        msg.what=2;
                                        handler.sendMessage(msg);
                                    }
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                }else{
                    Toast.makeText(SetPasswordActivity.this,"两次密码不一致",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
