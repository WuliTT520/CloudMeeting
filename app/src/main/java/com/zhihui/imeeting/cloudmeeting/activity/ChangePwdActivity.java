package com.zhihui.imeeting.cloudmeeting.activity;

import android.app.Activity;
import android.content.Context;
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

public class ChangePwdActivity extends Activity {
    private static final String TAG="ChangePwdActivity";
    private ImageView back;
    private TextView change;
    private EditText old_pwd;
    private EditText pwd1;
    private EditText pwd2;
    private SharedPreferences sp;
    private Handler handler;
    private Message msg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pwd);
        init();
        setListener();
    }
    public void init(){
        back=findViewById(R.id.back);
        change=findViewById(R.id.change);
        old_pwd=findViewById(R.id.old_pwd);
        pwd1=findViewById(R.id.pwd1);
        pwd2=findViewById(R.id.pwd2);
        sp=this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 404:
                        Toast.makeText(ChangePwdActivity.this,"网络异常",Toast.LENGTH_SHORT).show();
                        break;
                    case 500:
                        Toast.makeText(ChangePwdActivity.this,"原始密码不正确",Toast.LENGTH_SHORT).show();
                        break;
                    case 200:
                        Toast.makeText(ChangePwdActivity.this,"修改密码成功",Toast.LENGTH_SHORT).show();
                        finish();
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
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.w(TAG,"点击");
                if (pwd1.getText().length()<6){
                    Toast.makeText(ChangePwdActivity.this,"密码长度不少于6位",Toast.LENGTH_SHORT).show();
                }else if (!pwd1.getText().toString().equals(pwd2.getText().toString())){
                    Toast.makeText(ChangePwdActivity.this,"两次密码不相同",Toast.LENGTH_SHORT).show();
                }else {
                    final OkHttpClient client = new OkHttpClient();
                    RequestBody form=new FormBody.Builder()
                            .add("oldPassword",old_pwd.getText().toString())
                            .add("newPassword",pwd1.getText().toString())
                            .build();
                    final Request request=new Request.Builder()
                            .addHeader("cookie", sp.getString("sessionID", ""))
                            .url(new MyURL().changePwd())
                            .post(form)
                            .build();
                    Call call = client.newCall(request);
                    call.enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            msg=Message.obtain();
                            msg.what=404;
                            handler.sendMessage(msg);
                            e.printStackTrace();
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            try{
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

            }
        });
    }
}
