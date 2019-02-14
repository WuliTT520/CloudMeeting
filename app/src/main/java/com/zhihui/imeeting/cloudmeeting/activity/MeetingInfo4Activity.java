package com.zhihui.imeeting.cloudmeeting.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zhouwei.library.CustomPopWindow;
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

public class MeetingInfo4Activity extends Activity {
    private static final String TAG="MeetingInfo3Activity";

    private ImageView back;
    private TextView more;
    private TextView topic_tv;
    private TextView beginTime_tv;
    private TextView overTime_tv;
    private TextView prepareTime_tv;
    private TextView meetroom_tv;
    private TextView joinPeopleNum_tv;
    private LinearLayout join_people;
    private TextView content_tv;
    private Button cancel;

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
        setContentView(R.layout.activity_meeting_info4);
        init();
        getInfo();
        setListener();
    }
    public void init(){
        Intent intent=getIntent();
        meetingId=intent.getIntExtra("meetingId",0);
        back=findViewById(R.id.back);
        more=findViewById(R.id.more);
        topic_tv=findViewById(R.id.topic_tv);
        beginTime_tv=findViewById(R.id.beginTime_tv);
        overTime_tv=findViewById(R.id.overTime_tv);
        prepareTime_tv=findViewById(R.id.prepareTime_tv);
        meetroom_tv=findViewById(R.id.meetroom_tv);
        joinPeopleNum_tv=findViewById(R.id.joinPeopleNum_tv);
        join_people=findViewById(R.id.join_people);
        content_tv=findViewById(R.id.content_tv);
        cancel=findViewById(R.id.cancel);
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 404:
                        Toast.makeText(MeetingInfo4Activity.this,"网络错误",Toast.LENGTH_LONG).show();
                        break;
                    case 500:
                        Toast.makeText(MeetingInfo4Activity.this,"数据错误",Toast.LENGTH_LONG).show();
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
                        Toast.makeText(MeetingInfo4Activity.this,"提前结束会议成功",Toast.LENGTH_LONG).show();
                        setResult(1);
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
//                Intent intent=getIntent();
                setResult(500);
                finish();
            }
        });
        join_people.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MeetingInfo4Activity.this,JoinMemberListActivity.class);
                intent.putExtra("joinPeopleId",joinPeopleId);
                intent.putExtra("outsideJoinPersonsId",outsideJoinPersonsId);
                intent.putExtra("outsideJoinPersonsName",outsideJoinPersonsName);
                intent.putExtra("outsideJoinPersonsPhone",outsideJoinPersonsPhone);
                startActivity(intent);
            }
        });
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(MeetingInfo3Activity.this,"点击了",Toast.LENGTH_LONG).show();
                View contentView = LayoutInflater.from(MeetingInfo4Activity.this).inflate(R.layout.popwindow,null);
                //处理popWindow 显示内容
                handleLogic(contentView);
                //创建并显示popWindow
                CustomPopWindow mCustomPopWindow= new CustomPopWindow.PopupWindowBuilder(MeetingInfo4Activity.this)
                        .setView(contentView)
                        .create()
                        .showAsDropDown(more,0,0);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(MeetingInfo4Activity.this);
                builder.setTitle("确定要取消会议吗");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
//                        Toast.makeText(MeetingInfo2Activity.this,"取消会议",Toast.LENGTH_LONG).show();
                        MyURL url=new MyURL();
                        final OkHttpClient client = new OkHttpClient();
                        RequestBody body=new FormBody.Builder()
                                .add("meetingId",meetingId+"")
                                .build();
                        final Request request = new Request.Builder()
                                .url(url.cancelMeeting())
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
                final AlertDialog dialog = builder.show();

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

    private void handleLogic(View contentView) {
        contentView.findViewById(R.id.change).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(MeetingInfo3Activity.this,"改变会议",Toast.LENGTH_LONG).show();
                Intent intent=new Intent(MeetingInfo4Activity.this,MeetingChange3Activity.class);
                intent.putExtra("meetingId",meetingId);
                intent.putExtra("topic",topic);
                intent.putExtra("beginTime",beginTime);
                intent.putExtra("overTime",overTime);
                intent.putExtra("prepareTime",prepareTime);
                intent.putExtra("meetroom",meetroom);
                intent.putExtra("joinPeopleNum",joinPeopleNum);
                intent.putExtra("content",content);
                intent.putExtra("joinPeopleId",joinPeopleId);
                intent.putExtra("meetRoomId",meetRoomId);
                startActivityForResult(intent,10);
            }
        });
        contentView.findViewById(R.id.apply).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(MeetingInfo3Activity.this,"申请列表",Toast.LENGTH_LONG).show();
                System.exit(0);
            }
        });
    }
}
