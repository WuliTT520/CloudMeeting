package com.zhihui.imeeting.cloudmeeting.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zhihui.imeeting.cloudmeeting.R;
import com.zhihui.imeeting.cloudmeeting.controller.MyURL;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MeetingChange2Activity extends Activity {
    private final static String TAG="MeetingChange2Activity";
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
    private TextView topic_tv;
    private TextView content_tv;
    private TextView beginTime_tv_1;
    private TextView beginTime_tv_2;
    private TextView overTime_tv_1;
    private TextView overTime_tv_2;
    private TextView prepareTime_tv;
    private TextView joinPeopleNum_tv;
    private TextView meetingName_tv;

    private SharedPreferences sp;
    private String message;
    private Handler handler;
    private Message msg;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_change2);
        init();
        setListener();
        getInfo();
    }
    public void init(){
        sp=this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
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
                finish();
                if (topic_tv.getText()==null||content_tv.getText()==null||joinPeopleId.length==0||meetRoomId==0){
                    Toast.makeText(MeetingChange2Activity.this,"请完成填写信息",Toast.LENGTH_LONG).show();
                }else {
                    try {
                        JSONObject jsonObject=new JSONObject();
                        jsonObject.put("beginTime",beginTime_tv_2.getText().toString());
                        jsonObject.put("content",content_tv.getText().toString());
                        JSONArray join=new JSONArray();
                        for(int i=0;i<joinPeopleId.length;i++){
                            join.put(joinPeopleId[i]);
                        }
                        jsonObject.put("joinPeopleId",join);
                        jsonObject.put("meetRoomId",meetRoomId);
                        jsonObject.put("outsideJoinPersons",new JSONArray());
                        jsonObject.put("prepareTime",prepareTime);
                        String s=beginTime_tv_1.getText().toString();
                        String year=s.substring(0,4);
                        String month=s.substring(5,7);
                        String day=s.substring(8,10);
                        jsonObject.put("reserveDate",year+"-"+month+"-"+day);
                        jsonObject.put("topic",topic_tv.getText().toString());

                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date d1 = df.parse(beginTime_tv_1.getText().toString()+" "+beginTime_tv_2.getText().toString()+":00");
                        Date d2 = df.parse(overTime_tv_1.getText().toString()+" "+overTime_tv_2.getText().toString()+":00");
                        long diff = d2.getTime() - d1.getTime();
//                        Toast.makeText(AddActivity.this,diff/(60*1000)+"",Toast.LENGTH_LONG).show();
                        jsonObject.put("lastTime",diff/(60*1000));
                        Log.w(TAG,jsonObject.toString());
                        MyURL url=new MyURL();
                        final OkHttpClient client = new OkHttpClient();
                        RequestBody body = RequestBody.create(JSON, jsonObject.toString());
                        final Request request = new Request.Builder()
                                .addHeader("cookie", sp.getString("sessionID", ""))
                                .url(url.reserveMeeting())
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
                                    JSONObject data = new JSONObject(result);
                                    boolean flag = data.getBoolean("status");
                                    if (flag){
                                        msg=Message.obtain();
                                        msg.what=200;
                                        handler.sendMessage(msg);
                                    }else {
                                        message=data.getString("message");
                                        msg=Message.obtain();
                                        msg.what=500;
                                        handler.sendMessage(msg);
                                    }
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
    public void getInfo(){

    }
}
