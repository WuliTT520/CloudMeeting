package com.zhihui.imeeting.cloudmeeting.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MeetingInfo5Activity extends Activity {

    private static final String TAG="MeetingInfo5Activity";
    private ImageView back;
    private TextView topic_tv;
    private TextView beginTime_tv;
    private TextView overTime_tv;
    private TextView prepareTime_tv;
    private TextView meetroom_tv;
    private TextView joinPeopleNum_tv;
    private LinearLayout join_people;
    private TextView content_tv;
    private Button leave;
    private Message msg;
    private Handler handler;
    private SharedPreferences sp;

    private int meetingId;
    private String topic;
    private String beginTime;
    private String overTime;
    private int prepareTime;
    private String meetroom;
    private int joinPeopleNum;
    private String content;
    private int[] joinPeopleId;
    private int[] outsideJoinPersonsId;
    private String[] outsideJoinPersonsName;
    private String[] outsideJoinPersonsPhone;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_info5);
        init();
        getInfo();
        setListener();
    }
    public void init(){
        Intent intent=getIntent();
        meetingId=intent.getIntExtra("meetingId",0);
        Toast.makeText(MeetingInfo5Activity.this,meetingId+"",Toast.LENGTH_LONG).show();
        back=findViewById(R.id.back);
        topic_tv=findViewById(R.id.topic_tv);
        beginTime_tv=findViewById(R.id.beginTime_tv);
        overTime_tv=findViewById(R.id.overTime_tv);
        prepareTime_tv=findViewById(R.id.prepareTime_tv);
        meetroom_tv=findViewById(R.id.meetroom_tv);
        join_people=findViewById(R.id.join_people);
        joinPeopleNum_tv=findViewById(R.id.joinPeopleNum_tv);
        leave=findViewById(R.id.leave);
        content_tv=findViewById(R.id.content_tv);
        sp=getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 404:
                        Toast.makeText(MeetingInfo5Activity.this,"网络错误",Toast.LENGTH_LONG).show();
                        break;
                    case 500:
                        Toast.makeText(MeetingInfo5Activity.this,"数据错误",Toast.LENGTH_LONG).show();
                        break;
                    case 200:
                        topic_tv.setText(topic);
                        beginTime_tv.setText(beginTime);
                        overTime_tv.setText(overTime);
                        prepareTime_tv.setText(prepareTime+"分钟");
                        meetroom_tv.setText(meetroom);
                        joinPeopleNum_tv.setText(joinPeopleNum+"人");
                        content_tv.setText(content);
                        break;
                    case 201:
                        Toast.makeText(MeetingInfo5Activity.this,"申请成功，请等待审核",Toast.LENGTH_LONG).show();
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
        join_people.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(MeetingInfoActivity.this,"跳转到群组列表",Toast.LENGTH_LONG).show();
                Intent intent=new Intent(MeetingInfo5Activity.this,JoinMemberListActivity.class);
                intent.putExtra("joinPeopleId",joinPeopleId);
                intent.putExtra("outsideJoinPersonsId",outsideJoinPersonsId);
                intent.putExtra("outsideJoinPersonsName",outsideJoinPersonsName);
                intent.putExtra("outsideJoinPersonsPhone",outsideJoinPersonsPhone);
                startActivity(intent);
            }
        });
        leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View dialogview = LayoutInflater.from(MeetingInfo5Activity.this).inflate(R.layout.inputbox, null);
                final EditText note = (EditText)dialogview.findViewById(R.id.input);
                final AlertDialog.Builder builder = new AlertDialog.Builder(MeetingInfo5Activity.this);
                builder.setTitle("请输入请假理由：");
                builder.setView(dialogview);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        MyURL url=new MyURL();
                        final OkHttpClient client = new OkHttpClient();
                        JSONObject jsonObject=new JSONObject();
                        try {
                            jsonObject.put("meetingId",meetingId);
                            if (note.getText().toString().length()==0){
                                jsonObject.put("note","");
                            }else {
                                jsonObject.put("note",note.getText().toString());
                            }

                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        RequestBody body = RequestBody.create(JSON, jsonObject.toString());
//                RequestBody body=new FormBody.Builder()
//                        .add("meetingId",meetingId+"")
//                        .build();
                        final Request request = new Request.Builder()
                                .addHeader("cookie", sp.getString("sessionID", ""))
                                .url(url.sendLeaveInformation())
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
                                        msg.what=201;
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
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });


                builder.show();


            }
        });
    }
    public void getInfo(){
        MyURL url=new MyURL();
        final OkHttpClient client = new OkHttpClient();
        RequestBody body=new FormBody.Builder()
                .add("meetingId",meetingId+"")
                .build();
        final Request request = new Request.Builder()
                .url(url.showOneReserveDetail())
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
                        JSONObject info=data.getJSONArray("data").getJSONObject(0);
                        topic=info.getString("topic");
                        content=info.getString("content");
                        beginTime=info.getString("beginTime");
                        overTime=info.getString("overTime");
                        prepareTime=info.getInt("prepareTime");
                        meetroom=info.getString("meetroom");
                        joinPeopleNum=info.getJSONArray("joinPeopleId").length();
                        joinPeopleId=new int[joinPeopleNum];
                        for(int i=0;i<joinPeopleNum;i++){
                            joinPeopleId[i]=info.getJSONArray("joinPeopleId").getInt(i);
                        }
                        int length=info.getJSONArray("outsideJoinPersons").length();
                        outsideJoinPersonsId=new int[length];
                        outsideJoinPersonsPhone=new String[length];
                        outsideJoinPersonsName=new String[length];
                        for(int i=0;i<length;i++){
                            outsideJoinPersonsId[i]=info.getJSONArray("outsideJoinPersons").getJSONObject(i).getInt("id");
                            outsideJoinPersonsName[i]=info.getJSONArray("outsideJoinPersons").getJSONObject(i).getString("name");
                            outsideJoinPersonsPhone[i]=info.getJSONArray("outsideJoinPersons").getJSONObject(i).getString("phone");
                        }
                        joinPeopleNum+=length;
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
