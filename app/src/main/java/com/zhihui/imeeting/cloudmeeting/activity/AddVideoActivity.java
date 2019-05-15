package com.zhihui.imeeting.cloudmeeting.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zhihui.imeeting.cloudmeeting.R;
import com.zhihui.imeeting.cloudmeeting.adapter.GroupListViewAdapter;
import com.zhihui.imeeting.cloudmeeting.controller.MyURL;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddVideoActivity extends Activity {
    private final static String TAG="AddVideoActivity";

    private ImageView back;
    private ExpandableListView member_list;
    private TextView ok;

    private int[] groupid;
    private String[] groupStr;
    private List[] childid;
    private List[] childStr;
    private Handler handler;
    private Message msg;
    private GroupListViewAdapter adapter;
    private SharedPreferences sp;
    private HashMap<String, Boolean> state;
    private int[] choose;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private ProgressDialog waitingDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_video);
        init();
        getInfo();
        setListener();
    }
    public void init(){
        sp=this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        back=findViewById(R.id.back);
        member_list=findViewById(R.id.member_list);
        ok=findViewById(R.id.ok);
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 200:
                        adapter = new GroupListViewAdapter();
                        adapter.setGroupString(groupStr);
                        adapter.setChildString(childStr);
                        member_list.setAdapter(adapter);
                        for (int i = 0; i < groupid.length; i++) {
                            member_list.expandGroup(i);
                        }
                        member_list.setGroupIndicator(null);
                        break;
                    case 404:
                        Toast.makeText(AddVideoActivity.this,"请求失败",Toast.LENGTH_SHORT).show();
                        break;
                    case 500:
                        Toast.makeText(AddVideoActivity.this,"数据错误",Toast.LENGTH_SHORT).show();
                        break;
                    case 201:
                        waitingDialog.cancel();
                        Toast.makeText(AddVideoActivity.this,"创建成功",Toast.LENGTH_SHORT).show();
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
        member_list.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
                return true;
            }
        });
        member_list.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {

                return false;
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                state = adapter.getState();
                Iterator iter = state.entrySet().iterator();
                choose = new int[state.size()];
                int i = 0;
                while (iter.hasNext()) {
                    Map.Entry entry = (Map.Entry) iter.next();
                    Object key = entry.getKey();
//                    Object val = entry.getValue();
                    System.out.println((String) key);
                    String[] index = ((String) key).split(",");
                    choose[i] = (int) childid[Integer.parseInt(index[0])].get(Integer.parseInt(index[1]));
                    i++;
                }
//                for(int j=0;j<choose.length;j++){
//                    System.out.println(choose[j]);
//                }

                AlertDialog.Builder builder = new AlertDialog.Builder(AddVideoActivity.this);
                builder.setTitle("输入视频会议名称");
                View dialogview = LayoutInflater.from(AddVideoActivity.this).inflate(R.layout.input_dialog, null);
                builder.setView(dialogview);
                final EditText name = (EditText) dialogview.findViewById(R.id.name);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String str=name.getText().toString();
                        if (name.length()>0){
                            showWaitingDialog();
                            try {
                                JSONObject jsonObject=new JSONObject();
                                jsonObject.put("videoRoomName",str);
                                JSONArray array=new JSONArray();
                                for(int j=0;j<choose.length;j++){
                                    array.put(choose[j]);
                                }
                                jsonObject.put("userId",array);
                                Log.w(TAG,jsonObject.toString());
                                MyURL url=new MyURL();
                                final OkHttpClient client = new OkHttpClient();
                                RequestBody body = RequestBody.create(JSON, jsonObject.toString());
                                final Request request = new Request.Builder()
                                        .addHeader("cookie", sp.getString("sessionID", ""))
                                        .url(url.createMeetRoom())
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
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }else {
                            Toast.makeText(AddVideoActivity.this,"请输入会议名称",Toast.LENGTH_SHORT).show();
                        }


                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {

                    }
                });
                builder.show();
            }
        });
    }

    public void getInfo(){
        MyURL url=new MyURL();
        final OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .addHeader("cookie", sp.getString("sessionID", ""))
                .url(url.showUser())
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
                    JSONObject data = new JSONObject(result);
                    boolean flag = data.getBoolean("status");
                    if (flag){
                        JSONArray info=data.getJSONArray("data");
                        JSONArray groups=info.getJSONArray(0);
                        JSONArray peoples=info.getJSONArray(1);
                        groupid=new int[groups.length()];
                        groupStr=new String[groups.length()];
                        for(int i=0;i<groups.length();i++){
                            groupid[i]=groups.getJSONObject(i).getInt("id");
                            groupStr[i]=groups.getJSONObject(i).getString("name");
                        }

                        childid=new ArrayList[groups.length()];

                        childStr=new ArrayList[groups.length()];

                        for(int i=0;i<groups.length();i++){
                            JSONArray people=peoples.getJSONArray(i);
                            childStr[i]=new ArrayList<String>();
                            childid[i]=new ArrayList<Integer>();
                            for(int j=0;j<people.length();j++){
                                childid[i].add(j,people.getJSONObject(j).getInt("id"));

                                childStr[i].add(j,people.getJSONObject(j).getString("name"));
                            }

                        }

                        msg=Message.obtain();
                        msg.what=200;
                        handler.sendMessage(msg);
                    }else{
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
    private void showWaitingDialog() {
        waitingDialog= new ProgressDialog(AddVideoActivity.this);
        waitingDialog.setMessage("请等待...");
        waitingDialog.setIndeterminate(true);
        waitingDialog.setCancelable(false);
        waitingDialog.show();
    }
}
