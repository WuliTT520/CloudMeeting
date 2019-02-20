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
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnDismissListener;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.zhihui.imeeting.cloudmeeting.R;
import com.zhihui.imeeting.cloudmeeting.controller.MyURL;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DiaoActivity extends Activity {
    private static final String TAG="QiangActivity";

    private ImageView back;
    private EditText topic;
    private EditText content;
    private TextView prepareTime;
    private LinearLayout prepare;
    private LinearLayout member;
    private TextView memberNum;
    private TextView meetingName;
    private TextView beginTime_tv;
    private TextView overTime_tv;
    private TextView tiao;
    private int prepare_time=0;
    private Handler handler;
    private Message msg;
    private SharedPreferences sp;
    private int timeInterval=15;
    private int[] member_id=new int[0];
    private int boardroomId;
    private String begin;
    private String over;
    private int meetingId;
    private String message;
    private List<String> options3Items=new ArrayList<>();

    private int chixushijian=0;

    private TextView shichang;
    private EditText reason;
    private RadioButton fangfa1;
    private RadioButton fangfa2;

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diao);
        init();
//        getInfo();
        setListener();

    }
    public void init(){
        meetingId=getIntent().getIntExtra("meetingId",-1);
        begin=getIntent().getStringExtra("beginTime");
        over=getIntent().getStringExtra("overTime");
        boardroomId=getIntent().getIntExtra("meetRoomId",-1);

        shichang=findViewById(R.id.shichang);
        reason=findViewById(R.id.reason);
        fangfa1=findViewById(R.id.fangfa1);
        fangfa2=findViewById(R.id.fangfa2);


        back=findViewById(R.id.back);
        topic=findViewById(R.id.topic);
        content=findViewById(R.id.content);
        prepareTime=findViewById(R.id.prepareTime);
        prepare=findViewById(R.id.prepare);
        member=findViewById(R.id.member);
        memberNum=findViewById(R.id.memberNum);
        meetingName=findViewById(R.id.meetingName);
        beginTime_tv=findViewById(R.id.beginTime_tv);
        overTime_tv=findViewById(R.id.overTime_tv);

        tiao=findViewById(R.id.tiao);
        sp=this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        for(int i=0;i<=3*60;i+=timeInterval){
            options3Items.add(i+"");
        }

        meetingName.setText(getIntent().getStringExtra("meetroom"));

        beginTime_tv.setText(begin);
        overTime_tv.setText(over);

        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 404:
                        Toast.makeText(DiaoActivity.this,"网络错误",Toast.LENGTH_LONG).show();
                        break;
                    case 500:
                        Toast.makeText(DiaoActivity.this,message,Toast.LENGTH_LONG).show();
                        break;
                    case 200:
                        Toast.makeText(DiaoActivity.this,"操作成功",Toast.LENGTH_LONG).show();
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
        member.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(DiaoActivity.this,AttendeeActivity.class);
                startActivityForResult(intent,1);
            }
        });
        prepare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OptionsPickerView pvOptions = new OptionsPickerBuilder(DiaoActivity.this, new OnOptionsSelectListener() {
                    @Override
                    public void onOptionsSelect(int options1, int option2, int options3 ,View v) {
                        //返回的分别是三个级别的选中位置
                        String tx = options3Items.get(options1)+"分钟";
                        prepare_time=Integer.parseInt(options3Items.get(options1));
//                        Toast.makeText(AddActivity.this,tx,Toast.LENGTH_LONG).show();
                        prepareTime.setText(tx);
                    }


                }).setLabels("分钟","","")
                        .setCyclic(true, true, true)
                        .isCenterLabel(true)
                        .isDialog(true)
                        .setContentTextSize(18)
                        .setOutSideCancelable(false)
                        .build();
                pvOptions.setPicker(options3Items);
                pvOptions.setOnDismissListener(new OnDismissListener() {
                    @Override
                    public void onDismiss(Object o) {

                    }
                });
                pvOptions.show();
            }
        });

        shichang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OptionsPickerView pvOptions = new OptionsPickerBuilder(DiaoActivity.this, new OnOptionsSelectListener() {
                    @Override
                    public void onOptionsSelect(int options1, int option2, int options3 ,View v) {
                        //返回的分别是三个级别的选中位置
                        String tx = options3Items.get(options1)+"分钟";
                        chixushijian=Integer.parseInt(options3Items.get(options1));
//                        Toast.makeText(AddActivity.this,tx,Toast.LENGTH_LONG).show();
                        shichang.setText(tx);
                    }


                }).setLabels("分钟","","")
                        .setCyclic(true, true, true)
                        .isCenterLabel(true)
                        .isDialog(true)
                        .setContentTextSize(18)
                        .setOutSideCancelable(false)
                        .build();
                pvOptions.setPicker(options3Items);
                pvOptions.setOnDismissListener(new OnDismissListener() {
                    @Override
                    public void onDismiss(Object o) {

                    }
                });
                pvOptions.show();
            }
        });

        tiao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (topic.getText()==null||content.getText()==null||member_id.length==0||boardroomId==0){
                    Toast.makeText(DiaoActivity.this,"请完成填写信息",Toast.LENGTH_LONG).show();
                }else {
                    try {
                        JSONObject jsonObject=new JSONObject();

                        if (fangfa1.isChecked()){
                            jsonObject.put("beforeOrLast",1);
                        }else {
                            jsonObject.put("beforeOrLast",2);
                        }

                        jsonObject.put("beginTime",begin.substring(11,16));

                        jsonObject.put("content",content.getText().toString());
                        JSONArray join=new JSONArray();
                        for(int i=0;i<member_id.length;i++){
                            join.put(member_id[i]);
                        }
                        jsonObject.put("joinPeopleId",join);
                        jsonObject.put("meetRoomId",boardroomId);
                        jsonObject.put("outsideJoinPersons",new JSONArray());
                        jsonObject.put("prepareTime",prepare_time);
//                        String s=beginTime1.getText().toString();
//                        String year=s.substring(0,4);
//                        String month=s.substring(5,7);
//                        String day=s.substring(8,10);
                        jsonObject.put("reserveDate",begin.substring(0,10));
                        jsonObject.put("topic",topic.getText().toString());

//                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                        Date d1 = df.parse(begin+":00");
//                        Date d2 = df.parse(over+":00");
//                        long diff = d2.getTime() - d1.getTime();
//                        Toast.makeText(AddActivity.this,diff/(60*1000)+"",Toast.LENGTH_LONG).show();
//                        jsonObject.put("lastTime",diff/(60*1000));
                        jsonObject.put("lastTime",chixushijian);
                        jsonObject.put("beforeMeetingId",meetingId);
                        jsonObject.put("note",reason.getText().toString());

                        Log.w(TAG,jsonObject.toString());
                        MyURL url=new MyURL();
                        final OkHttpClient client = new OkHttpClient();
                        RequestBody body = RequestBody.create(JSON, jsonObject.toString());
                        final Request request = new Request.Builder()
                                .addHeader("cookie", sp.getString("sessionID", ""))
                                .url(url.coordinateMeeting())
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode){
            case 200:
//                user_resume.setText(data.getStringExtra("resume"));
                member_id=data.getIntArrayExtra("choose");
                memberNum.setText(member_id.length+"人");
                break;
            case 500:
//                Log.w(TAG,"llll");
                break;
        }
    }
}
