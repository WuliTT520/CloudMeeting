package com.zhihui.imeeting.cloudmeeting.activity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.util.Log;
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

public class SetPhoneActivity extends Activity {
    private static final String TAG="SetPhoneActivity";
    private ImageView back;
    private TextView bind;
    private EditText newPhone;
    private EditText key;
    private Button getKey;
    private String code;
    private SharedPreferences sp;
    private Handler handler;
    private Message msg;
    private String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_phone);
        init();
        setListener();
    }
    public void init(){
        back=findViewById(R.id.back);
        bind=findViewById(R.id.bind);
        newPhone=findViewById(R.id.newPhone);
        key=findViewById(R.id.key);
        getKey=findViewById(R.id.getkey);
        sp=this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 404:
                        Toast.makeText(SetPhoneActivity.this,"网络错误",Toast.LENGTH_SHORT).show();
                        break;
                    case 500:
                        Toast.makeText(SetPhoneActivity.this,"数据错误",Toast.LENGTH_SHORT).show();
                        break;
                    case 200:
                        Toast.makeText(SetPhoneActivity.this,"绑定成功",Toast.LENGTH_SHORT).show();
                        finish();
                        break;
                    case 201:
                        Toast.makeText(SetPhoneActivity.this,"该手机已经被绑定",Toast.LENGTH_SHORT).show();
                        break;
                    case 202:
                        Toast.makeText(SetPhoneActivity.this,"获取验证码成功",Toast.LENGTH_SHORT).show();
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
        bind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (code==null||phone==""){
                    Toast.makeText(SetPhoneActivity.this,"错误",Toast.LENGTH_SHORT).show();
                }else {
                    if (key.getText().toString().equals(code)){
                        final OkHttpClient client = new OkHttpClient();

                        RequestBody form=new FormBody.Builder()
                                .add("phone",phone)
                                .build();
                        final Request request=new Request.Builder()
                                .addHeader("cookie", sp.getString("sessionID", ""))
                                .url(new MyURL().recordPhone())
                                .post(form)
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
                                        msg.what=201;
                                        handler.sendMessage(msg);
                                    }
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        });
                    }else {
                        Toast.makeText(SetPhoneActivity.this,"验证码输入错误",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        getKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phone=newPhone.getText().toString();
                if (phone.length()==11){
                    final OkHttpClient client = new OkHttpClient();
                    RequestBody form=new FormBody.Builder()
                            .add("phone",phone)
                            .build();
                    final Request request=new Request.Builder()
                            .url(new MyURL().getCode())
                            .post(form)
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
                                    msg.what=202;
                                    handler.sendMessage(msg);
                                    code=data.getString("data");
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
                }else {
                    Toast.makeText(SetPhoneActivity.this,"请确认手机号输入正确",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
