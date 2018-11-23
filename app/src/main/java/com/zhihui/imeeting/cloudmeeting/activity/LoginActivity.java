package com.zhihui.imeeting.cloudmeeting.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.arcsoft.face.ErrorInfo;
import com.arcsoft.face.FaceEngine;
import com.zhihui.imeeting.cloudmeeting.R;
import com.zhihui.imeeting.cloudmeeting.common.Constants;

public class LoginActivity extends AppCompatActivity {
    TextView forget;
    EditText userCode,password;
    Button login;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sp=this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        if(sp.getBoolean("isLogin",false)){
            Intent intent=new Intent(LoginActivity.this,MainActivity.class);
            startActivity(intent);
            LoginActivity.this.finish();
        }
        init();
    }
    public void init(){
        userCode=findViewById(R.id.userCode);
        password=findViewById(R.id.password);
        login=findViewById(R.id.login);
        forget=findViewById(R.id.forget);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user=userCode.getText().toString();
                String pwd=password.getText().toString();
//                Toast.makeText(LoginActivity.this,user+","+pwd,Toast.LENGTH_SHORT).show();
                /*后期添加与数据库的对比*/
                if (user.equals("123456")&&pwd.equals("123456")){
                    /*如果登陆成功，将用户名保存好，并再下次打开app时会主动跳转到主界面*/
                    editor = sp.edit();
                    editor.putString("userCode",user);
                    editor.putBoolean("isLogin",true);
                    editor.commit();

                    Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                    startActivity(intent);
                    /*跳转以后将登陆页面关闭，防止点击返回键返回登陆页面*/
                    LoginActivity.this.finish();
                }else {
                    Toast.makeText(LoginActivity.this,"用户名或密码输入错误",Toast.LENGTH_SHORT).show();
                }

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