package com.zhihui.imeeting.cloudmeeting.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.zhihui.imeeting.cloudmeeting.R;

public class ForgetActivity extends AppCompatActivity {
    public ImageView fanhui;
    public EditText phoneNum,yzm;
    public Button next,getyzm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget);
        init();
        setListener();
    }
    public void init(){
        fanhui=findViewById(R.id.fanhui);
        phoneNum=findViewById(R.id.phonenum);
        yzm=findViewById(R.id.yzm);
        next=findViewById(R.id.next);
        getyzm=findViewById(R.id.getyzm);

    }
    public void setListener(){
        fanhui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ForgetActivity.this,LoginActivity.class);
                startActivity(intent);
                ForgetActivity.this.finish();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ForgetActivity.this,SetPasswordActivity.class);
                startActivity(intent);
//                ForgetActivity.this.finish();
            }
        });

        getyzm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}
