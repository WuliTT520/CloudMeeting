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
import android.widget.Button;
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
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AttendeeActivity extends Activity {
    private final static String TAG="AttendeeActivity";
    private ImageView back;
    private TextView next;
    private ExpandableListView member_list;
    private Button group;
    private Handler handler;
    private Message msg;
    private SharedPreferences sp;
    private int[] groupid;
    private String[] groupStr;
    private List[] childid;
    private List[] childStr;
    private GroupListViewAdapter adapter;
    private int[] choose;
    HashMap<String, Boolean> state=new HashMap<String, Boolean>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendee);
        init();
        setListener();
        getInfo();

    }
    public void init(){
        back=findViewById(R.id.back);
        next=findViewById(R.id.next);
        member_list=findViewById(R.id.member_list);
        group=findViewById(R.id.group);
        sp=this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
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
                        adapter.setState(state);
                        for (int i = 0; i < groupid.length; i++) {
                            member_list.expandGroup(i);
                        }
                        member_list.setGroupIndicator(null);
                        break;
                    case 404:
                        Toast.makeText(AttendeeActivity.this,"网络异常",Toast.LENGTH_SHORT).show();
                        break;
                    case 500:
                        Toast.makeText(AttendeeActivity.this,"数据异常",Toast.LENGTH_SHORT).show();
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
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //将数据传回预订界面，并刷新预订界面的人数
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
                Intent intent=getIntent();
                intent.putExtra("choose",choose);
                setResult(200,intent);
                finish();
            }
        });
        group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(AttendeeActivity.this,MyGroupActivity.class);
                startActivityForResult(intent,1);
            }
        });
    }
    public void getInfo(){
        MyURL url=new MyURL();
        final OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .addHeader("cookie", sp.getString("sessionID", ""))
                .url(url.selectPeople())
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode){
            case 1:
//                user_resume.setText(data.getStringExtra("resume"));
                Log.w(TAG,"llll;");
                String [] status=data.getStringArrayExtra("status");
                for (int i=0;i<status.length;i++){
                    state.put(status[i],true);
                }
                msg=Message.obtain();
                msg.what=200;
                handler.sendMessage(msg);
                break;
            case 500:
                break;
        }
    }
}
