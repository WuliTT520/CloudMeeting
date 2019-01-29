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

public class ShowGroupInfoActivity extends Activity {
    private static final String TAG="ShowGroupInfoActivity";
    private ImageView back;
    private TextView group_name;
    private ListView member_list;
    private int group_id;
    private SharedPreferences sp;
    private Handler handler;
    private Message msg;
    private String name;
    private String[] member_name;
    private int[] member_id;
    private TextView next;
    private String[] status;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_group_info);
        init();
        setListener();
        getInfo();
    }
    public void init(){
        back=findViewById(R.id.back);
        group_name=findViewById(R.id.group_name);
        member_list=findViewById(R.id.member_list);
        group_id=getIntent().getIntExtra("id",-1);
        next=findViewById(R.id.next);
        sp=this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case 404:
                        Toast.makeText(ShowGroupInfoActivity.this,"网络异常",Toast.LENGTH_SHORT).show();
                        break;
                    case 500:
                        Toast.makeText(ShowGroupInfoActivity.this,"数据错误",Toast.LENGTH_SHORT).show();
                        break;
                    case 200:
//                        Toast.makeText(GroupInfoActivity.this,"成功",Toast.LENGTH_SHORT).show();
                        List mdata=new ArrayList<Map<String,Object>>();
                        for(int i=0;i<member_name.length;i++){
                            Map item=new HashMap<String,Object>();
                            item.put("name",member_name[i]);
                            mdata.add(item);
                        }
                        SimpleAdapter adapter=new SimpleAdapter(ShowGroupInfoActivity.this,mdata,R.layout.member_item,
                                new String[] {"name"},
                                new int[]{R.id.member_name} );
                        member_list.setAdapter(adapter);
                        group_name.setText(name);
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
                setResult(500);
                finish();
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=getIntent();
                intent.putExtra("status",status);
                setResult(100,intent);
                finish();
            }
        });
    }
    public void getInfo(){
        MyURL url=new MyURL();
        final OkHttpClient client = new OkHttpClient();
        RequestBody form=new FormBody.Builder()
                .add("id",group_id+"")
                .build();
        final Request request = new Request.Builder()
                .addHeader("cookie", sp.getString("sessionID", ""))
                .url(url.showOneGroup())
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
                        name=info.getJSONObject(0).getString("name");
                        member_name=new String[info.getJSONArray(1).length()];
                        member_id=new int[member_name.length];
                        status=new String[member_name.length];
                        int sign=0;
                        int groupsign=info.getJSONArray(1).getJSONObject(0).getInt("groupId")-1;
                        for(int i=0;i<member_name.length;i++){
                            member_id[i]=info.getJSONArray(1).getJSONObject(i).getInt("userId");
                            member_name[i]=info.getJSONObject(2).getString(""+member_id[i]);
                            if (info.getJSONArray(1).getJSONObject(i).getInt("groupId")-1!=groupsign){
                                sign=0;
                                groupsign=info.getJSONArray(1).getJSONObject(i).getInt("groupId")-1;
                            }
                            status[i]=info.getJSONArray(1).getJSONObject(i).getInt("groupId")-1+","+sign;
                            sign++;
                        }
                        for(int i=0;i<status.length;i++){
                            Log.w(TAG,status[i]);
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
