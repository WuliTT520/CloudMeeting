package com.zhihui.imeeting.cloudmeeting.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.Toast;

import com.zhihui.imeeting.cloudmeeting.R;
import com.zhihui.imeeting.cloudmeeting.adapter.GroupListViewAdapter;
import com.zhihui.imeeting.cloudmeeting.controller.MyURL;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GroupActivity extends Activity {
    private static final String TAG="GroupActivity";
    private ExpandableListView groupList;
    Handler handler;
    Message msg;
    SharedPreferences sp;
    int[] groupid;
    String[] groupStr;
    int[][] childid;
    int [] groupnum;
    private ImageView set;
//    String[][] childStr;
    List<String> [] childStr;
    private GroupListViewAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        init();
        getInfo();
        setListener();
    }
    public void init(){
        groupList=findViewById(R.id.groupList);
        set=findViewById(R.id.group_set);
        sp = this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case 404:
                        Toast.makeText(GroupActivity.this,"网络异常",Toast.LENGTH_SHORT).show();
                        break;
                    case 500:
                        Toast.makeText(GroupActivity.this,"请求错误",Toast.LENGTH_SHORT).show();
                        break;
                    case 200:

                        adapter = new GroupListViewAdapter();
                        adapter.setGroupString(groupStr);
                        adapter.setChildString(childStr);
                        adapter.setGroupNum(groupnum);
                        groupList.setAdapter(adapter);
                        break;
                        default:
                }
                super.handleMessage(msg);
            }
        };
    }
    public void setListener(){
        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(GroupActivity.this,GroupSetActivity.class);
                startActivity(intent);
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
                Log.w(TAG,"连接错误");
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
                        JSONArray groups=info.getJSONArray(0);
                        JSONArray peoples=info.getJSONArray(1);
                        groupid=new int[groups.length()];
                        groupStr=new String[groups.length()];
                        for(int i=0;i<groups.length();i++){
                            groupid[i]=groups.getJSONObject(i).getInt("id");
                            groupStr[i]=groups.getJSONObject(i).getString("name");
                        }
//                        for(int i=0;i<groups.length();i++){
//                            System.out.println("id="+groupid[i]+",name="+groupStr[i]);
//                        }
//                        Log.w(TAG,"代码以执行");
                        childid=new int[groups.length()][100];
//                        childStr=new String[groups.length()][100];
                        childStr=new ArrayList[groups.length()];
                        groupnum=new int[groups.length()];
                        for(int i=0;i<groups.length();i++){
                            JSONArray people=peoples.getJSONArray(i);
                            childStr[i]=new ArrayList<String>();
                            for(int j=0;j<people.length();j++){
                                childid[i][j]=people.getJSONObject(j).getInt("id");
//                                childStr[i][j]=people.getJSONObject(j).getString("name");
//                                String hah=people.getJSONObject(j).getString("name");
                                childStr[i].add(j,people.getJSONObject(j).getString("name"));
                            }
                            groupnum[i]=people.length();
                        }
//                        for(int i=0;i<groups.length();i++){
//                            JSONArray people=peoples.getJSONArray(i);
//                            for(int j=0;j<people.length();j++){
//                                System.out.print("id="+childid[i][j]+",name="+childStr[i][j]+" ");
//                            }
//                            System.out.println();
//                        }
//                        Log.w(TAG,"代码以执行");
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
