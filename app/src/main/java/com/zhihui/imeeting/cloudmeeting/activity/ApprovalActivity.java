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
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
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
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ApprovalActivity extends Activity {
    private static final String TAG="ApprovalActivity";
    private ImageView back;
    private ListView info_list;
    private TextView tip;
    private Handler handler;
    private Message msg;
    private int meetingId;


    private int[] leaveInfoId;
    private String[] peopleName;
    private String[] peoplePhone;
    private String[] note;
    private int[] status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approval);
        init();
        getInfo();
        setListener();
    }
    public void init(){
        Intent intent=getIntent();
        meetingId=intent.getIntExtra("meetingId",-1);
        back=findViewById(R.id.back);
        info_list=findViewById(R.id.info_list);
        tip=findViewById(R.id.tip);
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 500:
                        Toast.makeText(ApprovalActivity.this,"数据错误",Toast.LENGTH_LONG).show();
                        break;
                    case 404:
                        Toast.makeText(ApprovalActivity.this,"网络错误",Toast.LENGTH_LONG).show();
                        break;
                    case 200:
                        if (leaveInfoId.length==0){
                            tip.setText("没有请假申请");
                        }else {
                            List mdata=new ArrayList<Map<String,Object>>();
                            for(int i=0;i<leaveInfoId.length;i++){
                                Map item=new HashMap<String,Object>();

                                item.put("peopleName",peopleName[i]);
                                item.put("peoplePhone","联系方式:"+peoplePhone[i]);
                                item.put("note",note[i]);
                                if (status[i]==0){
                                    item.put("status","状态:未操作");
                                }
                                if (status[i]==1){
                                    item.put("status","状态:同意请假");
                                }
                                if (status[i]==2){
                                    item.put("status","状态:拒绝请假");
                                }
                                mdata.add(item);
                            }
                            SimpleAdapter adapter=new SimpleAdapter(ApprovalActivity.this,mdata,R.layout.approval_item,
                                    new String[] {"peopleName","peoplePhone","note","status"},
                                    new int[]{R.id.peopleName,R.id.peoplePhone,R.id.note,R.id.state} );
                            info_list.setAdapter(adapter);
                        }

                        break;
                    case 201:
                        Toast.makeText(ApprovalActivity.this,"操作成功",Toast.LENGTH_LONG).show();
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
            public void onItemClick(AdapterView<?> adapterView, View view, final int position, long l) {
//                Toast.makeText(ApprovalActivity.this,""+leaveInfoId[position],Toast.LENGTH_LONG).show();

                if (status[position]==0){
                    final AlertDialog.Builder builder = new AlertDialog.Builder(ApprovalActivity.this);
                    builder.setTitle("同意该申请吗");
                    builder.setPositiveButton("同意", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
//                        Toast.makeText(MeetingInfo2Activity.this,"取消会议",Toast.LENGTH_LONG).show();
                            MyURL url=new MyURL();
                            final OkHttpClient client = new OkHttpClient();
                            RequestBody body=new FormBody.Builder()
                                    .add("leaveInfoId",leaveInfoId[position]+"")
                                    .build();
                            final Request request = new Request.Builder()
                                    .url(url.agreeLeave())
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
                    builder.setNegativeButton("拒绝", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            MyURL url=new MyURL();
                            final OkHttpClient client = new OkHttpClient();
                            RequestBody body=new FormBody.Builder()
                                    .add("leaveInfoId",leaveInfoId[position]+"")
                                    .build();
                            final Request request = new Request.Builder()
                                    .url(url.disagreeLeave())
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
                    builder.show();
                }
            }
        });
    }
    public void getInfo(){
        MyURL url=new MyURL();
        final OkHttpClient client = new OkHttpClient();
        RequestBody form=new FormBody.Builder()
                .add("meetingId",meetingId+"")
                .build();
        final Request request = new Request.Builder()
                .url(url.showOneMeetingLeaveInfo())
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
                    JSONObject data = new JSONObject(result);
                    boolean flag = data.getBoolean("status");
                    if (flag){
                        JSONArray info=data.getJSONArray("data");
                        leaveInfoId=new int[info.length()];
                        peopleName=new String[info.length()];
                        peoplePhone=new String[info.length()];
                        note=new String[info.length()];
                        status=new int[info.length()];

                        for(int i=0;i<info.length();i++){
                            leaveInfoId[i]=info.getJSONObject(i).getInt("leaveInfoId");
                            peopleName[i]=info.getJSONObject(i).getString("peopleName");
                            peoplePhone[i]=info.getJSONObject(i).getString("peoplePhone");
                            note[i]=info.getJSONObject(i).getString("note");
                            status[i]=info.getJSONObject(i).getInt("status");
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
