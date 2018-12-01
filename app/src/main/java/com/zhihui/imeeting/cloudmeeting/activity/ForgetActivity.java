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

public class ForgetActivity extends AppCompatActivity {
    public ImageView fanhui;
    public EditText phoneNum,yzm;
    public Button next,getyzm;
    public String yzm_true;
    Handler handler;
    Message msg;
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

        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 1:
                            /*无法连接*/
                        Toast.makeText(ForgetActivity.this,"网络异常",Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        /*返回错误*/
                        Toast.makeText(ForgetActivity.this,"请求失败，检查手机号是否正确",Toast.LENGTH_SHORT).show();
                        break;
                    case 3:
                        /*返回成功*/
                        Toast.makeText(ForgetActivity.this,yzm_true,Toast.LENGTH_SHORT).show();
                        break;
                        default:
                            Toast.makeText(ForgetActivity.this,"未知错误",Toast.LENGTH_SHORT).show();
                }
            }
        };

    }
    public void setListener(){
        fanhui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ForgetActivity.this.finish();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user_yzm=yzm.getText().toString();
                if (user_yzm.equals(yzm_true)){
                    Intent intent=new Intent(ForgetActivity.this,SetPasswordActivity.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(ForgetActivity.this,"验证码错误",Toast.LENGTH_SHORT).show();
                }
//                ForgetActivity.this.finish();
            }
        });

        getyzm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone =phoneNum.getText().toString();
                if (phone!=""&&phone.length()==11){
                    final OkHttpClient client = new OkHttpClient();
                    RequestBody form=new FormBody.Builder()
                            .add("phone",phone)
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
                            e.printStackTrace();
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            try{
                                String result = response.body().string();
                                JSONObject data =new  JSONObject(result);
                                boolean flag=data.getBoolean("status");
                                if (flag){
                                    yzm_true=data.getString("data");
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
                }else{
                    Toast.makeText(ForgetActivity.this,"手机号输入错误",Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}
