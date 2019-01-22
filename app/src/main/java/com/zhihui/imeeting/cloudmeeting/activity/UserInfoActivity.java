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
import android.widget.ImageView;
import android.widget.LinearLayout;
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

public class UserInfoActivity extends Activity {

    private static final String TAG="UserInfoActivity";
    private TextView user_name;
    private TextView user_departName;
    private TextView user_positionName;
    private TextView user_worknum;
    private TextView user_phone;
    private TextView user_resume;
    private LinearLayout setPhone;
    private LinearLayout setResume;
    private ImageView back;
    private SharedPreferences sp;
    private Handler handler;
    private Message msg;
    private String name;
    private String departName;
    private String worknum;
    private String phone;
    private String positionName;
    private String resume;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        init();
        setListener();
        getInfo();
    }
    protected void setListener(){
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        setPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(UserInfoActivity.this,SetPhoneActivity.class);
                intent.putExtra("phone",phone);
                startActivity(intent);
            }
        });
        setResume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(UserInfoActivity.this,SetResumeActivity.class);
                intent.putExtra("resume",resume);
                startActivityForResult(intent,100);
            }
        });
    }
    protected void init(){
        user_name=findViewById(R.id.user_name);
        user_departName=findViewById(R.id.user_departName);
        user_positionName=findViewById(R.id.user_positionName);
        user_worknum=findViewById(R.id.user_worknum);
        user_phone=findViewById(R.id.user_phone);
        user_resume=findViewById(R.id.user_resume);
        setPhone=findViewById(R.id.setPhone);
        setResume=findViewById(R.id.setResume);
        back=findViewById(R.id.back);
        sp=this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case 404:
                        Toast.makeText(UserInfoActivity.this,"网络异常",Toast.LENGTH_SHORT).show();
                        break;
                    case 500:
                        Toast.makeText(UserInfoActivity.this,"数据异常",Toast.LENGTH_SHORT).show();
                        break;
                    case 200:
                        user_name.setText(name);
                        user_departName.setText(departName);
                        user_positionName.setText(positionName);
                        user_worknum.setText(worknum);
                        user_phone.setText(phone);
                        user_resume.setText(resume);
                        break;
                }
                super.handleMessage(msg);
            }
        };
    }
    protected void getInfo(){
        MyURL url=new MyURL();
        final OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .addHeader("cookie", sp.getString("sessionID", ""))
                .url(url.showUserinfo())
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
                        JSONObject info=data.getJSONObject("data");
                        name=info.getString("name");
                        resume=info.getString("resume");
                        positionName=info.getString("positionName");
                        phone=info.getString("phone");
                        worknum=info.getString("worknum");
                        departName=info.getString("departName");
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode){
            case 100:
                user_resume.setText(data.getStringExtra("resume"));
                break;
            case 500:
                break;
        }
    }
}
