package com.zhihui.imeeting.cloudmeeting.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.zhihui.imeeting.cloudmeeting.R;
import com.zhihui.imeeting.cloudmeeting.adapter.GroupListViewAdapter;
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

public class GroupActivity extends Activity {
    private static final String TAG="GroupActivity";
    private SwipeMenuListView groupList;
//    private AppAdapter mAdapter;
    Handler handler;
    Message msg;
    SharedPreferences sp;
    int[] groupid;
    String[] groupname;
//    int[][] childid;
//    int [] groupnum;
    private ImageView set;
    SimpleAdapter adapter;
    List mdata;
    private ImageView back;
//    String[][] childStr;
//    List<String> [] childStr;
//    private GroupListViewAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        init();
        getInfo();
        setListener();
    }
    public void init(){
        groupList=(SwipeMenuListView)findViewById(R.id.groupList);
        back=findViewById(R.id.back);
        set=findViewById(R.id.group_set);
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "open" item
                SwipeMenuItem openItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,
                        0xCE)));
                // set item width
                openItem.setWidth(dp2px(90));
                // set item title
                openItem.setTitle("编辑");
                // set item title fontsize
                openItem.setTitleSize(18);
                // set item title font color
                openItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(openItem);

                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                // set item width
                deleteItem.setWidth(dp2px(90));
                // set a icon
                deleteItem.setTitle("删除");
                // set item title fontsize
                deleteItem.setTitleSize(18);
                // set item title font color
                deleteItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };
        groupList.setMenuCreator(creator);


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
                        mdata=new ArrayList<Map<String,Object>>();
                        for(int i=0;i<groupid.length;i++){
                            Map item=new HashMap<String,Object>();
//                            item.put("id",groupid[i]);
                            item.put("name",groupname[i]);
                            mdata.add(item);
                        }
                        adapter=new SimpleAdapter(GroupActivity.this,mdata,R.layout.group_item,
                                new String[] {"name"},
                                new int[]{R.id.group_name} );
                        groupList.setAdapter(adapter);
//                        adapter = new GroupListViewAdapter();
//                        adapter.setGroupString(groupStr);
//                        adapter.setChildString(childStr);
//                        adapter.setGroupNum(groupnum);
//                        groupList.setAdapter(adapter);
                        break;
                    case 201:
                        mdata.remove((int)msg.obj);
//                        adapter.notifyDataSetChanged();
                        adapter=new SimpleAdapter(GroupActivity.this,mdata,R.layout.group_item,
                                new String[] {"name"},
                                new int[]{R.id.group_name} );
                        groupList.setAdapter(adapter);
                        Toast.makeText(GroupActivity.this,"删除成功",Toast.LENGTH_SHORT).show();
                        break;
                        default:
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
        groupList.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        Intent intent=new Intent(GroupActivity.this,GroupSetActivity.class);
                        intent.putExtra("id",groupid[position]);
                        startActivity(intent);

//                        Toast.makeText(GroupActivity.this,position+"编辑",Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        // delete
//					delete(item);
//                        mAppList.remove(position);
//                        mAdapter.notifyDataSetChanged();
                        MyURL url=new MyURL();
                        final OkHttpClient client = new OkHttpClient();
                        RequestBody form=new FormBody.Builder()
                                .add("id",groupid[position]+"")
                                .build();
                        final Request request = new Request.Builder()
                                .addHeader("cookie", sp.getString("sessionID", ""))
                                .url(url.deleteGroup())
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
//                    Log.w(TAG,result);
                                    JSONObject data =new JSONObject(result);
                                    boolean flag=data.getBoolean("status");
                                    if (flag){
                                        msg=Message.obtain();
                                        msg.what=201;
                                        msg.obj=position;
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
//                        Toast.makeText(GroupActivity.this,position+"删除",Toast.LENGTH_SHORT).show();
                        break;
                }
                return false;
            }
        });
        groupList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Toast.makeText(GroupActivity.this,i+"",Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(GroupActivity.this,GroupInfoActivity.class);
                intent.putExtra("id",groupid[i]);
                startActivity(intent);
            }
        });

        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(GroupActivity.this,GroupAddActivity.class);
                startActivity(intent);
            }
        });
    }

    public void getInfo(){
        MyURL url=new MyURL();
        final OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .addHeader("cookie", sp.getString("sessionID", ""))
                .url(url.showGroup())
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
    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }
}
