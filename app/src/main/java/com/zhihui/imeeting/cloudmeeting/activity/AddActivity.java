package com.zhihui.imeeting.cloudmeeting.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnDismissListener;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.necer.calendar.MonthCalendar;
import com.necer.entity.NDate;
import com.necer.listener.OnMonthSelectListener;
import com.zhihui.imeeting.cloudmeeting.R;
import com.zhihui.imeeting.cloudmeeting.controller.MyURL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddActivity extends Activity {
    private static final String TAG="AddActivity";
    private ImageView back;
    private TextView book;
    private EditText topic;
    private EditText content;
    private LinearLayout begin;
    private LinearLayout over;
    private TextView beginTime1;
    private TextView beginTime2;
    private TextView overTime1;
    private TextView overTime2;
    private LinearLayout prepare;
    private TextView prepareTime;
    private LinearLayout member;
    private TextView memberNum;
    private LinearLayout meeting;
    private TextView meetingName;
    private int timeInterval=15;
    private List<String> options1Items=new ArrayList<>();
    private List<List<String>> options2Items=new ArrayList<>();
    private List<String> options3Items=new ArrayList<>();
    private int prepare_time=0;
    private Handler handler;
    private Message msg;
    private SharedPreferences sp;
    private String message;
    //当前时间
    private Calendar cal;
    private String current_time;
    private String current_time_year;
    private String current_time_month;
    private String current_time_day;
    private String current_time_hour;
    private String current_time_minute;
    private int[] member_id=new int[0];
    private int boardroomId;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        init();
        setListener();
    }
    public void init(){
        back=findViewById(R.id.back);
        book=findViewById(R.id.book);
        topic=findViewById(R.id.topic);
        content=findViewById(R.id.content);
        begin=findViewById(R.id.begin);
        over=findViewById(R.id.over);
        beginTime1=findViewById(R.id.beginTime1);
        beginTime2=findViewById(R.id.beginTime2);
        overTime1=findViewById(R.id.overTime1);
        overTime2=findViewById(R.id.overTime2);
        prepare=findViewById(R.id.prepare);
        prepareTime=findViewById(R.id.prepareTime);
        member=findViewById(R.id.member);
        memberNum=findViewById(R.id.memberNum);
        meeting=findViewById(R.id.meeting);
        meetingName=findViewById(R.id.meetingName);
        sp=this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 404:
                        Toast.makeText(AddActivity.this,"网络错误",Toast.LENGTH_LONG).show();
                        break;
                    case 500:
                        Toast.makeText(AddActivity.this,message,Toast.LENGTH_LONG).show();
                        break;
                    case 200:
                        Toast.makeText(AddActivity.this,"预订会议成功",Toast.LENGTH_LONG).show();
                        finish();
                        break;
                }
            }
        };
        for (int i=0;i<=23;i++){
            options1Items.add(String.format("%02d", i));
        }
        for(int i=0;i<options1Items.size();i++){
            List<String> item=new ArrayList<>();
            for(int j=0;j<=59;j+=timeInterval){
                item.add(String.format("%02d", j));
            }
            options2Items.add(item);
        }
        for(int i=0;i<=3*60;i+=timeInterval){
            options3Items.add(i+"");
        }
        cal = Calendar.getInstance();
        current_time_year = String.valueOf(cal.get(Calendar.YEAR));
        current_time_month = String.valueOf(cal.get(Calendar.MONTH)+1);
        current_time_day = String.valueOf(cal.get(Calendar.DATE));
        if (cal.get(Calendar.AM_PM) == 0)
            current_time_hour = String.valueOf(cal.get(Calendar.HOUR));
        else
            current_time_hour = String.valueOf(cal.get(Calendar.HOUR)+12);
        current_time_minute = String.valueOf(cal.get(Calendar.MINUTE));
        current_time=current_time_year+"年"+current_time_month+"月"+current_time_day+
                "日"+current_time_hour+":"+current_time_minute;
//        Toast.makeText(AddActivity.this,current_time_year+"年"+current_time_month+"月"+current_time_day+
//                "日"+current_time_hour+"时"+current_time_minute+"分",Toast.LENGTH_LONG).show();
        beginTime1.setText(current_time_year+"年"+current_time_month+"月"+current_time_day+ "日");
        beginTime2.setText((1+Integer.parseInt(current_time_hour))%24+":00");
        overTime1.setText(current_time_year+"年"+current_time_month+"月"+current_time_day+ "日");
        overTime2.setText((2+Integer.parseInt(current_time_hour))%24+":00");
    }
    public void setListener(){
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (topic.getText()==null||content.getText()==null||member_id.length==0||boardroomId==0){
                    Toast.makeText(AddActivity.this,"请完成填写信息",Toast.LENGTH_LONG).show();
                }else {
                    try {
                        JSONObject jsonObject=new JSONObject();
                        jsonObject.put("beginTime",beginTime2.getText().toString());
                        jsonObject.put("content",content.getText().toString());
                        JSONArray join=new JSONArray();
                        for(int i=0;i<member_id.length;i++){
                            join.put(member_id[i]);
                        }
                        jsonObject.put("joinPeopleId",join);
                        jsonObject.put("meetRoomId",boardroomId);
                        jsonObject.put("outsideJoinPersons",new JSONArray());
                        jsonObject.put("prepareTime",prepare_time);
                        String s=beginTime1.getText().toString();
                        String year=s.substring(0,4);
                        String month=s.substring(5,7);
                        String day=s.substring(8,10);
                        jsonObject.put("reserveDate",year+"-"+month+"-"+day);
                        jsonObject.put("topic",topic.getText().toString());

                        DateFormat df = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
                        Date d1 = df.parse(beginTime1.getText().toString()+" "+beginTime2.getText().toString()+":00");
                        Date d2 = df.parse(overTime1.getText().toString()+" "+overTime2.getText().toString()+":00");
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
        begin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(AddActivity.this);
                View dialogview = LayoutInflater.from(AddActivity.this).inflate(R.layout.choose_date, null);
                final TextView month_year=dialogview.findViewById(R.id.month_year);
                MonthCalendar monthCalendar=dialogview.findViewById(R.id.monthCalendar);
                LinearLayout chooseTime=dialogview.findViewById(R.id.chooseTime);
                final TextView time_tv=dialogview.findViewById(R.id.time_tv);
                Button cancel=dialogview.findViewById(R.id.cancel);
                Button next=dialogview.findViewById(R.id.next);
                builder.setView(dialogview);
                final AlertDialog dialog = builder.show();
                monthCalendar.setOnMonthSelectListener(new OnMonthSelectListener() {
                    @Override
                    public void onMonthSelect(NDate date, boolean isClick) {
                        int year=date.localDate.getYear();
                        int month=date.localDate.getMonthOfYear();
                        int day=date.localDate.getDayOfMonth();

                        month_year.setText(year+"年"+String.format("%02d",month)+"月"+String.format("%02d",day)+"日");
                    }
                });

                chooseTime.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        OptionsPickerView pvOptions = new OptionsPickerBuilder(AddActivity.this, new OnOptionsSelectListener() {
                            @Override
                            public void onOptionsSelect(int options1, int option2, int options3 ,View v) {
                                //返回的分别是三个级别的选中位置
                                String tx = options1Items.get(options1)+":"+ options2Items.get(options1).get(option2);
//                                Toast.makeText(AddActivity.this,tx,Toast.LENGTH_LONG).show();
                                time_tv.setText(tx);
                                time_tv.setTextColor(getColor(R.color.colorPrimary));
                                dialog.show();
                            }


                        }).setLabels("时","分","")
                                .setCyclic(true, true, true)
                                .setSelectOptions(Integer.parseInt(current_time_hour)+1,0,0)
                                .isCenterLabel(true)
                                .isDialog(true)
                                .setContentTextSize(22)
                                .setOutSideCancelable(false)
                                .build();
                        pvOptions.setPicker(options1Items, options2Items);
                        pvOptions.setOnDismissListener(new OnDismissListener() {
                            @Override
                            public void onDismiss(Object o) {
                                dialog.show();
                            }
                        });
                        pvOptions.show();
                    }
                });
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                next.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //判断用户输入的时间是否有错，如果没错则保存时间，并前端刷新显示
//                        Log.w(TAG,month_year.getText().toString());
//                        Log.w(TAG,current_time_year+"年"+current_time_month+"月"+current_time_day+ "日");
                        String chooseTime=month_year.getText().toString();
                        if (time_tv.getText().toString().equals("选择具体时间")){
                            chooseTime+="00:00";
                        }else {
                            chooseTime+=time_tv.getText().toString();
                        }
                        if (chooseTime.compareTo(current_time)<0){
                            Toast.makeText(AddActivity.this,"无法预订当前时间之前的会议",Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                            return;
                        }
                        beginTime1.setText(month_year.getText().toString());
                        if (time_tv.getText().toString().equals("选择具体时间")){
                            beginTime2.setText("00:00");
                        }else {
                            beginTime2.setText(time_tv.getText().toString());
                        }
                        dialog.dismiss();
                    }
                });

            }
        });
        over.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(AddActivity.this);
                View dialogview = LayoutInflater.from(AddActivity.this).inflate(R.layout.choose_date, null);
                final TextView month_year=dialogview.findViewById(R.id.month_year);
                MonthCalendar monthCalendar=dialogview.findViewById(R.id.monthCalendar);
                LinearLayout chooseTime=dialogview.findViewById(R.id.chooseTime);
                final TextView time_tv=dialogview.findViewById(R.id.time_tv);
                Button cancel=dialogview.findViewById(R.id.cancel);
                Button next=dialogview.findViewById(R.id.next);
                builder.setView(dialogview);
                final AlertDialog dialog = builder.show();
                monthCalendar.setOnMonthSelectListener(new OnMonthSelectListener() {
                    @Override
                    public void onMonthSelect(NDate date, boolean isClick) {
                        int year=date.localDate.getYear();
                        int month=date.localDate.getMonthOfYear();
                        int day=date.localDate.getDayOfMonth();
                        month_year.setText(year+"年"+String.format("%02d",month)+"月"+String.format("%02d",day)+"日");
                    }
                });

                chooseTime.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        OptionsPickerView pvOptions = new OptionsPickerBuilder(AddActivity.this, new OnOptionsSelectListener() {
                            @Override
                            public void onOptionsSelect(int options1, int option2, int options3 ,View v) {
                                //返回的分别是三个级别的选中位置
                                String tx = options1Items.get(options1)+":"+ options2Items.get(options1).get(option2);
//                                Toast.makeText(AddActivity.this,tx,Toast.LENGTH_LONG).show();
                                time_tv.setText(tx);
                                time_tv.setTextColor(getColor(R.color.colorPrimary));
                                dialog.show();
                            }


                        }).setLabels("时","分","")
                                .setCyclic(true, true, true)
                                .setSelectOptions(Integer.parseInt(current_time_hour)+1,0,0)
                                .isCenterLabel(true)
                                .isDialog(true)
                                .setContentTextSize(22)
                                .setOutSideCancelable(false)
                                .build();
                        pvOptions.setPicker(options1Items, options2Items);
                        pvOptions.setOnDismissListener(new OnDismissListener() {
                            @Override
                            public void onDismiss(Object o) {
                                dialog.show();
                            }
                        });
                        pvOptions.show();
                    }
                });
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                next.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //判断用户输入的时间是否有错，如果没错则保存时间，并前端刷新显示

                        String chooseTime=month_year.getText().toString();
                        if (time_tv.getText().toString().equals("选择具体时间")){
                            chooseTime+="00:00";
                        }else {
                            chooseTime+=time_tv.getText().toString();
                        }
                        if (chooseTime.compareTo(current_time)<0){
                            Toast.makeText(AddActivity.this,"无法预订当前时间之前的会议",Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                            return;
                        }
                        Log.w(TAG,chooseTime);
                        Log.w(TAG,beginTime1.getText().toString()+beginTime2.getText().toString());
                        if (chooseTime.compareTo(beginTime1.getText().toString()+beginTime2.getText().toString())<0){
                            Toast.makeText(AddActivity.this,"结束时间必须在开始时间之后",Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                            return;
                        }
                        overTime1.setText(month_year.getText().toString());
                        if (time_tv.getText().toString().equals("选择具体时间")){
                            overTime2.setText("00:00");
                        }else {
                            overTime2.setText(time_tv.getText().toString());
                        }
                        dialog.dismiss();
                    }
                });
            }
        });
        prepare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OptionsPickerView pvOptions = new OptionsPickerBuilder(AddActivity.this, new OnOptionsSelectListener() {
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
        member.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(AddActivity.this,AttendeeActivity.class);
                startActivityForResult(intent,1);
            }
        });
        meeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(AddActivity.this,ChooseBoardroomActivity.class);
                startActivityForResult(intent,2);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode){
            case 100:
                boardroomId=data.getIntExtra("BoardroomId",0);
                meetingName.setText(data.getStringExtra("BoardroomName"));
                break;
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
