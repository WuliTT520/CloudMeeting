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

public class FaceManagerActivity extends Activity {
    private static final String TAG="FaceManagerActivity";
    private ImageView back;
    private LinearLayout next;
    private TextView text;
    private TextView node;
    private SharedPreferences sp;
    private Handler handler;
    private Message msg;
    private Intent intent;
//    private LinearLayout delete_face;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_manager);
        init();
        setListener();
        getInfo();
    }
    public void init(){
        back=findViewById(R.id.back);
        next=findViewById(R.id.next);
        node=findViewById(R.id.note);
        text=findViewById(R.id.text);
        sp=this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        intent=new Intent(FaceManagerActivity.this,FaceIDActivity.class);
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case 404:
                        Toast.makeText(FaceManagerActivity.this,"网络错误",Toast.LENGTH_SHORT).show();
                        break;
                    case 500:
                        Toast.makeText(FaceManagerActivity.this,"请先登陆",Toast.LENGTH_SHORT).show();
                        break;
                    case 200:
                        text.setText("添加人脸数据");
                        node.setText("未添加人脸数据");
                        node.setBackgroundColor(getResources().getColor(R.color.dangerous));
                        intent.putExtra("isAdd",true);
                        break;
                    case 201:
                        text.setText("修改人脸数据");
                        node.setText("管理员未审核");
                        node.setBackgroundColor(getResources().getColor(R.color.waring));
                        intent.putExtra("isAdd",false);
                        break;
                    case 202:
                        text.setText("修改人脸数据");
                        node.setText("已通过");
                        node.setBackgroundColor(getResources().getColor(R.color.pass));
                        intent.putExtra("isAdd",false);
                        break;
                    case 203:
                        text.setText("修改人脸数据");
                        node.setText("人脸数据未通过");
                        node.setBackgroundColor(getResources().getColor(R.color.dangerous));
                        intent.putExtra("isAdd",false);

                        break;
                }
                super.handleMessage(msg);
            }
        };
//        delete_face=findViewById(R.id.delete_face);
    }
    public void setListener(){
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent=new Intent(FaceManagerActivity.this,FaceIDActivity.class);
                startActivityForResult(intent,100);
            }
        });

//        delete_face.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(FaceManagerActivity.this,"接口未连接",Toast.LENGTH_SHORT).show();
//            }
//        });
    }
    public void getInfo(){
        final OkHttpClient client = new OkHttpClient();
        final Request request=new Request.Builder()
                .addHeader("cookie", sp.getString("sessionID", ""))
                .url(new MyURL().selectStatus())
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
                try{
                    String result = response.body().string();
                    Log.w(TAG,result);
                    JSONObject data =new JSONObject(result);
                    boolean flag=data.getBoolean("status");
                    if (flag){
                        int code=data.getInt("code");
                        switch (code){
                            case -1:
                                msg=Message.obtain();
                                msg.what=200;
                                handler.sendMessage(msg);
                                break;
                            case 0:
                                msg=Message.obtain();
                                msg.what=201;
                                handler.sendMessage(msg);
                                break;
                            case 1:
                                msg=Message.obtain();
                                msg.what=202;
                                handler.sendMessage(msg);
                                break;
                            case 2:
                                msg=Message.obtain();
                                msg.what=203;
                                handler.sendMessage(msg);
                                break;
                        }
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
        switch (resultCode){
            case 100:
                text.setText("修改人脸数据");
                node.setText("管理员未审核");
                node.setBackgroundColor(getResources().getColor(R.color.waring));
                intent.putExtra("isAdd",false);
                Log.w(TAG,"success");
                break;
            case 500:
                Log.w(TAG,"back");
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
