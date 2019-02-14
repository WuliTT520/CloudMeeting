package com.zhihui.imeeting.cloudmeeting.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhihui.imeeting.cloudmeeting.R;

public class MeetingChangeActivity extends Activity {
    private final static String TAG="MeetingChangeActivity";
    private int meetingId;
    private String topic;
    private String beginTime;
    private String overTime;
    private int prepareTime;
    private String meetroom;
    private int joinPeopleNum;
    private String content;
    private int[] joinPeopleId;
    private int meetRoomId;

    private ImageView back;
    private TextView yes;
    private EditText topic_tv;
    private EditText content_tv;
    private TextView beginTime_tv_1;
    private TextView beginTime_tv_2;
    private TextView overTime_tv_1;
    private TextView overTime_tv_2;
    private TextView prepareTime_tv;
    private TextView joinPeopleNum_tv;
    private TextView meetingName_tv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_change);
        init();
        setListener();
        getInfo();
    }
    public void init(){
        back=findViewById(R.id.back);
        yes=findViewById(R.id.yes);
        topic_tv=findViewById(R.id.topic);
        content_tv=findViewById(R.id.content);
        beginTime_tv_1=findViewById(R.id.beginTime1);
        beginTime_tv_2=findViewById(R.id.beginTime2);
        overTime_tv_1=findViewById(R.id.overTime1);
        overTime_tv_2=findViewById(R.id.overTime2);
        prepareTime_tv=findViewById(R.id.prepareTime);
        joinPeopleNum_tv=findViewById(R.id.memberNum);
        meetingName_tv=findViewById(R.id.meetingName);

        Intent intent=getIntent();
        meetingId=intent.getIntExtra("meetingId",-1);
        topic=intent.getStringExtra("topic");
        beginTime=intent.getStringExtra("beginTime");
        overTime=intent.getStringExtra("overTime");
        prepareTime=intent.getIntExtra("prepareTime",-1);
        meetroom=intent.getStringExtra("meetroom");
        joinPeopleNum=intent.getIntExtra("joinPeopleNum",-1);
        content=intent.getStringExtra("content");
        joinPeopleId=intent.getIntArrayExtra("joinPeopleId");
        meetRoomId=intent.getIntExtra("meetRoomId",-1);

        topic_tv.setText(topic);
        content_tv.setText(content);
        beginTime_tv_1.setText(beginTime.split(" ")[0]);
        beginTime_tv_2.setText(beginTime.split(" ")[1]);
        overTime_tv_1.setText(overTime.split(" ")[0]);
        overTime_tv_2.setText(overTime.split(" ")[1]);
        prepareTime_tv.setText(prepareTime+"分钟");
        joinPeopleNum_tv.setText(joinPeopleNum+"人");
        meetingName_tv.setText(meetroom);

    }
    public void setListener(){
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(500);
                finish();
            }
        });
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
    public void getInfo(){

    }
}
