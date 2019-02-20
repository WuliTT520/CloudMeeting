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
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SingInActivity extends Activity {
    private static final String TAG="SingInActivity";
    private ImageView back;
    private ListView list;
    private Message msg;
    private Handler handler;
    private SharedPreferences sp;

    private int[] id;
    private int[] order;
    private String[] topic;
    private String[] begin;
    private String[] over;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sing_in);
        init();
        getInfo();
        setListener();
    }
    public void init(){
        sp=this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        back=findViewById(R.id.back);
        list=findViewById(R.id.list);
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 404:
                        Toast.makeText(SingInActivity.this,"网络错误",Toast.LENGTH_LONG).show();
                        break;
                    case 200:
                        List mdata=new ArrayList<Map<String,Object>>();
                        for(int i=0;i<id.length;i++){
                            Map item=new HashMap<String,Object>();

                            item.put("order",order[i]);
                            item.put("topic",topic[i]);
                            item.put("begin","开始："+begin[i]);
                            item.put("over","结束："+over[i]);
                            mdata.add(item);
                        }
                        SimpleAdapter adapter=new SimpleAdapter(SingInActivity.this,mdata,R.layout.sing_in_list_item,
                                new String[] {"order","topic","begin","over"},
                                new int[]{R.id.order,R.id.topic,R.id.begin,R.id.over} );
                        list.setAdapter(adapter);
//                        Toast.makeText(SingInActivity.this,"？？",Toast.LENGTH_LONG).show();

                        break;
                    case 500:
                        Toast.makeText(SingInActivity.this,"数据错误",Toast.LENGTH_LONG).show();
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
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent=new Intent(SingInActivity.this,SingInfoActivity.class);
                intent.putExtra("meetingId",id[i]);
                startActivity(intent);
            }
        });
    }
    public void getInfo(){
        MyURL url=new MyURL();
        final OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .addHeader("cookie", sp.getString("sessionID", ""))
                .url(url.toJoinPersonIndex())
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
                        id=new int[info.length()];
                        order=new int[info.length()];
                        topic=new String[info.length()];
                        begin=new String [info.length()];
                        over=new String[info.length()];
                        for(int i=0;i<info.length();i++){
                            id[i]=info.getJSONObject(i).getInt("id");
                            order[i]=i+1;
                            topic[i]=info.getJSONObject(i).getString("topic");
                            begin[i]=info.getJSONObject(i).getString("begin");
                            over[i]=info.getJSONObject(i).getString("over");
                            Log.w(TAG,info.getJSONObject(i).getString("topic"));
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
