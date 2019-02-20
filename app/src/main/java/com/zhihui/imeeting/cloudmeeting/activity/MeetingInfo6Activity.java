package com.zhihui.imeeting.cloudmeeting.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

public class MeetingInfo6Activity extends Activity {

    private static final String TAG="MeetingInfo6Activity";

    private ImageView back;
    private TextView change;
    private TextView topic_tv;
    private TextView beginTime_tv;
    private TextView overTime_tv;
    private TextView prepareTime_tv;
    private TextView meetroom_tv;
    private TextView joinPeopleNum_tv;
    private LinearLayout join_people;
    private TextView content_tv;
    private Button qiang;
    private Button diao;
    private Message msg;
    private Handler handler;

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
    private int meetRoomId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_info6);
        init();
        getInfo();
        setListener();
    }
    public void init(){
        Intent intent=getIntent();
        meetingId=intent.getIntExtra("meetingId",0);
        back=findViewById(R.id.back);
        change=findViewById(R.id.change);
        topic_tv=findViewById(R.id.topic_tv);
        beginTime_tv=findViewById(R.id.beginTime_tv);
        overTime_tv=findViewById(R.id.overTime_tv);
        prepareTime_tv=findViewById(R.id.prepareTime_tv);
        meetroom_tv=findViewById(R.id.meetroom_tv);
        joinPeopleNum_tv=findViewById(R.id.joinPeopleNum_tv);
        join_people=findViewById(R.id.join_people);
        content_tv=findViewById(R.id.content_tv);
        qiang=findViewById(R.id.qiang);
        diao=findViewById(R.id.tiao);
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 404:
                        Toast.makeText(MeetingInfo6Activity.this,"网络错误",Toast.LENGTH_LONG).show();
                        break;
                    case 500:
                        Toast.makeText(MeetingInfo6Activity.this,"数据错误",Toast.LENGTH_LONG).show();
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

                }
            }
        };
    }
    public void setListener(){
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent=getIntent();
//                setResult(500);
                finish();
            }
        });
        join_people.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MeetingInfo6Activity.this,JoinMemberListActivity.class);
                intent.putExtra("joinPeopleId",joinPeopleId);
                intent.putExtra("outsideJoinPersonsId",outsideJoinPersonsId);
                intent.putExtra("outsideJoinPersonsName",outsideJoinPersonsName);
                intent.putExtra("outsideJoinPersonsPhone",outsideJoinPersonsPhone);
                startActivity(intent);
            }
        });
        qiang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(MeetingInfo6Activity.this,QiangActivity.class);
                intent.putExtra("beginTime",beginTime);
                intent.putExtra("overTime",overTime);
                intent.putExtra("meetRoomId",meetRoomId);
                intent.putExtra("meetroom",meetroom);
                startActivity(intent);
            }
        });
        diao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MeetingInfo6Activity.this,DiaoActivity.class);
                intent.putExtra("beginTime",beginTime);
                intent.putExtra("overTime",overTime);
                intent.putExtra("meetRoomId",meetRoomId);
                intent.putExtra("meetroom",meetroom);
                intent.putExtra("meetingId",meetingId);
                startActivity(intent);
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
                        meetRoomId=info.getInt("meetRoomId");
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
