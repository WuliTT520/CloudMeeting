package com.zhihui.imeeting.cloudmeeting.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GroupAddActivity extends Activity {
    private static final String TAG="GroupAddActivity";
    private ExpandableListView member_list;
    private ImageView back;
    private TextView creat;
    private Handler handler;
    private Message msg;
    private SharedPreferences sp;
    private int[] groupid;
    private String[] groupStr;
    private List[] childid;
    private List[] childStr;
    private GroupListViewAdapter adapter;
    private HashMap<String, Boolean> state;
    private int[] choose;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_add);
        init();
        getInfo();
        setListener();
    }
    public void init(){
        member_list=findViewById(R.id.member_list);
        back=findViewById(R.id.back);
        creat=findViewById(R.id.creat);
        sp=this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case 404:
                        Toast.makeText(GroupAddActivity.this,"网络异常",Toast.LENGTH_SHORT).show();
                        break;
                    case 500:
                        Toast.makeText(GroupAddActivity.this,"请先登陆",Toast.LENGTH_SHORT).show();
                        break;
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
                    case 201:
                        Toast.makeText(GroupAddActivity.this, "创建成功", Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(GroupAddActivity.this,GroupActivity.class);
                        startActivity(intent);
                        GroupAddActivity.this.finish();
                        break;
                }
                super.handleMessage(msg);
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
        creat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                state=adapter.getState();
                Iterator iter = state.entrySet().iterator();
                choose=new int[state.size()];
                int i=0;
                while (iter.hasNext()) {
                    Map.Entry entry = (Map.Entry) iter.next();
                    Object key = entry.getKey();
//                    Object val = entry.getValue();
                    System.out.println((String) key);
                    String[] index=((String) key).split(",");
                    choose[i]=(int)childid[Integer.parseInt(index[0])].get(Integer.parseInt(index[1]));
                    i++;
                }
//                for(int j=0;j<choose.length;j++){
//                    System.out.println(choose[j]);
//                }

                AlertDialog.Builder builder = new AlertDialog.Builder(GroupAddActivity.this);
                builder.setTitle("输入群组名称");
                View dialogview = LayoutInflater.from(GroupAddActivity.this).inflate(R.layout.group_dialog, null);
                builder.setView(dialogview);
                final EditText name = (EditText)dialogview.findViewById(R.id.group_name);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        String s = name.getText().toString().trim();

                        MyURL url=new MyURL();
                        final OkHttpClient client = new OkHttpClient();
                        String json="{\"name\": \""+s+"\",\"userIds\": [";
                        for(int i=0;i<choose.length;i++){
                            if (i!=choose.length-1){
                                json+=choose[i]+",";
                            }else{
                                json+=choose[i];
                            }
                        }
                        json+="]}";
                        System.out.println(json);
                        RequestBody body = RequestBody.create(JSON, json);
                        final Request request = new Request.Builder()
                                .addHeader("cookie", sp.getString("sessionID", ""))
                                .url(url.saveGroup())
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
        member_list.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
                return true;
            }
        });
        member_list.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {

//                Toast.makeText(GroupAddActivity.this,"i="+i+",i1="+i1,Toast.LENGTH_SHORT).show();
                return false;
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
}
