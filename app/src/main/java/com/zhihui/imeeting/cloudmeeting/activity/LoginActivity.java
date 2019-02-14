package com.zhihui.imeeting.cloudmeeting.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
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

public class LoginActivity extends AppCompatActivity {
    TextView forget;
    EditText userCode,password;
    Button login;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    Handler handler;
    Message msg;
    String sessionId;
    int id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sp=this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        init();
    }
    public void init(){
        userCode=findViewById(R.id.userCode);
        password=findViewById(R.id.password);
        login=findViewById(R.id.login);
        forget=findViewById(R.id.forget);
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch(msg.what){
                    case 1:
                        Toast.makeText(LoginActivity.this,"网络异常",Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        Toast.makeText(LoginActivity.this,"用户名或密码错误",Toast.LENGTH_SHORT).show();
                        break;
                    case 3:
                        editor = sp.edit();
                        editor.putString("userCode",userCode.getText().toString());
                        editor.putBoolean("isLogin",true);
                        editor.putString("sessionID",sessionId);
                        editor.putInt("userId",id);
                        editor.commit();

                        Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                        startActivity(intent);
                        /*跳转以后将登陆页面关闭，防止点击返回键返回登陆页面*/
                        LoginActivity.this.finish();
                        break;
                    default:
                        Toast.makeText(LoginActivity.this,"异常错误",Toast.LENGTH_SHORT).show();
                }
                super.handleMessage(msg);
            }
        };
        msg=new Message();
        final OkHttpClient client = new OkHttpClient();
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user=userCode.getText().toString();
                String pwd=password.getText().toString();
//                Toast.makeText(LoginActivity.this,user+","+pwd,Toast.LENGTH_SHORT).show();
                /*后期添加与数据库的对比*/
                RequestBody form=new FormBody.Builder()
                        .add("username",user)
                        .add("password",pwd)
                        .build();
                final Request request=new Request.Builder()
                        .url(new MyURL().login())
                        .post(form)
                        .build();
                Call call = client.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        /*失败*/
//
                        Log.w("fail","错误");
                        msg=Message.obtain();
                        msg.what=1;
                        handler.sendMessage(msg);
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
//                        Looper.prepare();
//                        Toast.makeText(LoginActivity.this,response.code()+"",Toast.LENGTH_SHORT).show();
//                        Looper.loop();
                        Log.w("code",response.code()+"");
                        try {
                            String result = response.body().string();
                            Log.w("success",result);
                            sessionId=response.header("Set-Cookie");
                            JSONObject data =new  JSONObject(result);
                            boolean flag=data.getBoolean("status");
                            if (flag){
                                id=data.getJSONObject("data").getInt("id");
                                Log.w("flag","true");

                                msg=Message.obtain();
                                msg.what=3;
                                handler.sendMessage(msg);

                            }else{
                                Log.w("flag","false");
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
        });

        forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(LoginActivity.this,ForgetActivity.class);
                startActivity(intent);
            }
        });

    }

}