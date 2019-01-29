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

public class MyGroupActivity extends Activity {
    private final static String TAG="MyGroupActivity";
    private ListView group_list;
    private Handler handler;
    private Message msg;
    private SharedPreferences sp;
    private ImageView back;
    private int[] groupid;
    private String[] groupname;
    private SimpleAdapter adapter;
    private List mdata;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_group);
        init();
        setListener();
        getInfo();
    }
    public void init(){
        group_list=findViewById(R.id.group_list);
        back=findViewById(R.id.back);
        sp = this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 404:
                        Toast.makeText(MyGroupActivity.this,"网络异常",Toast.LENGTH_SHORT).show();
                        break;
                    case 500:
                        Toast.makeText(MyGroupActivity.this,"数据异常",Toast.LENGTH_SHORT).show();
                        break;
                    case 200:
                        mdata=new ArrayList<Map<String,Object>>();
                        for(int i=0;i<groupid.length;i++){
                            Map item=new HashMap<String,Object>();
//                            item.put("id",groupid[i]);
                            item.put("name",groupname[i]);
                            mdata.add(item);
                        }
                        adapter=new SimpleAdapter(MyGroupActivity.this,mdata,R.layout.group_item,
                                new String[] {"name"},
                                new int[]{R.id.group_name} );
                        group_list.setAdapter(adapter);
                        break;
                }
            }
        };
    }
    public void setListener(){
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(500);
                finish();
            }
        });
        group_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent=new Intent(MyGroupActivity.this,ShowGroupInfoActivity.class);
                intent.putExtra("id",groupid[i]);
                startActivityForResult(intent,200);
            }
        });
    }
    public void getInfo(){
        MyURL url=new MyURL();
        final OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .addHeader("cookie", sp.getString("sessionID", ""))
                .url(url.getGroupList())
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
                        groupid=new int[info.length()];
                        groupname=new String[info.length()];
                        for(int i=0;i<info.length();i++){
                            groupid[i]=info.getJSONObject(i).getInt("id");
                            groupname[i]=info.getJSONObject(i).getString("name");
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
            case 100:
//                user_resume.setText(data.getStringExtra("resume"));
                String [] status=data.getStringArrayExtra("status");
                Intent intent=getIntent();
                intent.putExtra("status",status);
                setResult(1,intent);
                finish();
                break;
            case 500:
                break;
        }
    }
}
