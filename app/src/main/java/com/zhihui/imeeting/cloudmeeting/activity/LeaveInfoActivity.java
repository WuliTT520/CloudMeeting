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
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.zhihui.imeeting.cloudmeeting.R;
import com.zhihui.imeeting.cloudmeeting.controller.MyURL;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LeaveInfoActivity extends Activity {
    private static final String TAG="LeaveInfoActivity";
    private ImageView back;
    private ListView info_list;
    private Handler handler;
    private Message msg;
    private SharedPreferences sp;
    private int[] meetingId;
    private String[] topic;
    private int[] allCount;
    private int[] notDealCount;
    private String[] date;
    private String[] begin;
    private String[] over;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_info);
        init();
        getInfo();
        setListener();
    }
    public void init(){
        sp = this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        back=findViewById(R.id.back);
        info_list=findViewById(R.id.info_list);
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 200:
//                        Toast.makeText(LeaveInfoActivity.this,"成功",Toast.LENGTH_LONG).show();
                        List mdata=new ArrayList<Map<String,Object>>();
                        for(int i=0;i<meetingId.length;i++){
                            Map item=new HashMap<String,Object>();

                            item.put("topic",topic[i]);
                            item.put("allCount","请假数:"+allCount[i]);
                            item.put("notDealCount","未处理:"+notDealCount[i]);
                            item.put("date","会议日期:"+date[i]);
                            item.put("begin","开始:"+begin[i]);
                            item.put("over","结束:"+over[i]);
                            mdata.add(item);
                        }
                        SimpleAdapter adapter=new SimpleAdapter(LeaveInfoActivity.this,mdata,R.layout.leave_info_item,
                                new String[] {"topic","allCount","notDealCount","date","begin","over"},
                                new int[]{R.id.topic_tv,R.id.num_tv,R.id.notDeal_tv,R.id.data_tv,R.id.begin_tv,R.id.over_tv} );
                        info_list.setAdapter(adapter);
                        break;
                    case 404:
                        Toast.makeText(LeaveInfoActivity.this,"网络错误",Toast.LENGTH_LONG).show();
                        break;
                    case 500:
                        Toast.makeText(LeaveInfoActivity.this,"数据错误",Toast.LENGTH_LONG).show();
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
        info_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Toast.makeText(LeaveInfoActivity.this,meetingId[i]+"",Toast.LENGTH_LONG).show();
                Intent intent=new Intent(LeaveInfoActivity.this,ApprovalActivity.class);
                intent.putExtra("meetingId",meetingId[i]);
                startActivity(intent);
            }
        });
    }
    public void getInfo(){
        MyURL url=new MyURL();
        final OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .addHeader("cookie", sp.getString("sessionID", ""))
                .url(url.CountLeaveInformation())
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
                        JSONArray info=data.getJSONArray("data");
                        meetingId=new int[info.length()];
                        topic=new String[info.length()];
                        allCount=new int[info.length()];
                        notDealCount=new int[info.length()];
                        date=new String[info.length()];
                        begin=new String[info.length()];
                        over=new String[info.length()];
                        for(int i=0;i<info.length();i++){
                            meetingId[i]=info.getJSONObject(i).getInt("meetingId");
                            topic[i]=info.getJSONObject(i).getString("topic");
                            allCount[i]=info.getJSONObject(i).getInt("allCount");
                            notDealCount[i]=info.getJSONObject(i).getInt("notDealCount");
                            String str=info.getJSONObject(i).getString("meetTime");
                            Log.w(TAG,"运行到这了"+str);
//                            Log.w("............",str.substring(0,10));
                            date[i]=str.substring(0,10);
//                            Log.w("............",str.substring(11,16));
                            begin[i]=str.substring(11,16);
//                            Log.w("............",str.substring(28,33));
                            over[i]=str.substring(28,33);
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
}
