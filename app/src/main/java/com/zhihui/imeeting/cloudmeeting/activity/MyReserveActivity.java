package com.zhihui.imeeting.cloudmeeting.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.necer.calendar.Miui9Calendar;
import com.necer.entity.NDate;
import com.necer.listener.OnCalendarChangedListener;
import com.zhihui.imeeting.cloudmeeting.R;
import com.zhihui.imeeting.cloudmeeting.adapter.ReserveListViewAdapter;
import com.zhihui.imeeting.cloudmeeting.controller.MyURL;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MyReserveActivity extends BaseActivity {


    private static final String TAG="MyReserveActivity";
    private SwipeRefreshLayout refresh_layout;
    private RecyclerView recyclerView;
    private Miui9Calendar miui9Calendar;
    private TextView showtime;
    private ImageView back;
    private SharedPreferences sp;
    private Handler handler;
    private Message msg;
    private List<String> pointList;
    private int[] id;
    private String[] topic;
    private String[] status;
    private String[] meetDate;
    private String[] begin;
    private String[] over;
    private int year;
    private int month;
    private int day;
    @Override
    protected int getLayoutId() {
        return R.layout.activity_my_reserve;
    }

    @Override
    protected void onCreatee() {
        init();
        setListener();

    }

    public void init(){
        refresh_layout = findViewById(R.id.refresh_layout);
        recyclerView = findViewById(R.id.recyclerView);
        miui9Calendar = findViewById(R.id.miui9Calendar);
        showtime=findViewById(R.id.showtime);
        back=findViewById(R.id.back);
        sp=this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 404:
                        Toast.makeText(MyReserveActivity.this,"网络异常",Toast.LENGTH_SHORT).show();
                        break;
                    case 500:
                        Toast.makeText(MyReserveActivity.this,"数据错误",Toast.LENGTH_SHORT).show();
                        break;
                    case 200:
                        miui9Calendar.setPointList(pointList);
                        break;
                    case 201:
                        recyclerView.setLayoutManager(new LinearLayoutManager(MyReserveActivity.this));
                        final ReserveListViewAdapter adapter = new ReserveListViewAdapter(MyReserveActivity.this);
                        adapter.setBegin(begin);
                        adapter.setMeetDate(meetDate);
                        adapter.setOver(over);
                        adapter.setStatus(status);
                        adapter.setTopic(topic);
                        adapter.setOnItemClickLitener(new ReserveListViewAdapter.OnItemClickLitener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                String str=status[position];
//                                Toast.makeText(MyReserveActivity.this,id[position]+"",Toast.LENGTH_LONG).show();
                                switch (str){
                                    case "预约成功":
                                        Intent intent4=new Intent(MyReserveActivity.this,MeetingInfo4Activity.class);
                                        intent4.putExtra("meetingId",id[position]);
                                        startActivityForResult(intent4,3);
                                        break;
                                    case "调用中":
                                    case "预约中":
                                        Intent intent2=new Intent(MyReserveActivity.this,MeetingInfo2Activity.class);
                                        intent2.putExtra("meetingId",id[position]);
                                        startActivityForResult(intent2,1);
                                        break;
                                    case "会议进行中":
                                        Intent intent3=new Intent(MyReserveActivity.this,MeetingInfo3Activity.class);
                                        intent3.putExtra("meetingId",id[position]);
                                        startActivityForResult(intent3,2);
                                        break;
                                    case "调用失败":
                                    case "预约失败":
                                    case "取消会议":
                                    case "会议结束":
                                        Intent intent=new Intent(MyReserveActivity.this,MeetingInfoActivity.class);
                                        intent.putExtra("meetingId",id[position]);
                                        startActivity(intent);
                                        break;
                                }
                            }
                        });
                        recyclerView.setAdapter(adapter);
                }
            }
        };
    }
    public void setListener(){
        miui9Calendar.setOnCalendarChangedListener(new OnCalendarChangedListener() {
            @Override
            public void onCalendarDateChanged(NDate date, boolean isClick) {

                year=date.localDate.getYear();
                month=date.localDate.getMonthOfYear();
                day=date.localDate.getDayOfMonth();
                getInfo(year+"-"+String.format("%02d", month));
//                Log.w(TAG,year+"-"+String.format("%02d", month));
                getInfo2(year+"-"+String.format("%02d", month)+"-"+String.format("%02d", day));
                showtime.setText(year+"年"+month+"月");
//                Toast.makeText(MyReserveActivity.this,year+"年"+month+"月"+day+"日",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCalendarStateChanged(boolean isMonthSate) {

            }
        });
        refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getInfo2(year+"-"+String.format("%02d", month)+"-"+String.format("%02d", day));
                refresh_layout.setRefreshing(false);

            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void getInfo(String yearMonth){
        MyURL url=new MyURL();
        final OkHttpClient client = new OkHttpClient();
        RequestBody form=new FormBody.Builder()
                .add("yearMonth",yearMonth)
                .build();
        final Request request = new Request.Builder()
                .addHeader("cookie", sp.getString("sessionID", ""))
                .url(url.specifiedMyReserve())
                .post(form)
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
                        pointList=new ArrayList<String>();
                        JSONArray info=data.getJSONArray("data");
                        for (int i=0;i<info.length();i++){
//                            Log.w(TAG,info.toString());
                            pointList.add(i,info.getJSONObject(i).getString("meetDate"));
                        }
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
    public void getInfo2(String reserveDate){
        MyURL url=new MyURL();
        final OkHttpClient client = new OkHttpClient();
        RequestBody form=new FormBody.Builder()
                .add("reserveDate",reserveDate)
                .build();
        final Request request = new Request.Builder()
                .addHeader("cookie", sp.getString("sessionID", ""))
                .url(url.showOneDayReserve())
                .post(form)
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
                        JSONArray info=data.getJSONArray("data");
                        id=new int[info.length()];
                        topic=new String[info.length()];
                        status=new String[info.length()];
                        meetDate=new String[info.length()];
                        begin=new String[info.length()];
                        over=new String[info.length()];
                        for(int i=0;i<info.length();i++){
                            id[i]=info.getJSONObject(i).getInt("id");
                            topic[i]=info.getJSONObject(i).getString("topic");
                            status[i]=info.getJSONObject(i).getString("status");
                            meetDate[i]=info.getJSONObject(i).getString("meetDate");
                            String str=info.getJSONObject(i).getString("begin");
                            Log.w(TAG,str.substring(11,16));
                            begin[i]=str.substring(11,16);
                            str=info.getJSONObject(i).getString("over");
                            Log.w(TAG,str.substring(11,16));
                            over[i]=str.substring(11,16);
                        }
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
    public void monthCalendar(View view) {
        miui9Calendar.toMonth();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode){
            case 1:
                getInfo2(year+"-"+String.format("%02d", month)+"-"+String.format("%02d", day));
                break;
            case 500:
                break;
        }
    }
}
