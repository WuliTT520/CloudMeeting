package com.zhihui.imeeting.cloudmeeting.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.zhihui.imeeting.cloudmeeting.R;
import com.zhihui.imeeting.cloudmeeting.adapter.AiMeetingRoomInfoAdapter;
import com.zhihui.imeeting.cloudmeeting.adapter.MeetingRoomInfoAdapter;
import com.zhihui.imeeting.cloudmeeting.controller.MyURL;
import com.zhihui.imeeting.cloudmeeting.entity.RoomInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AiChoseRoomActivity extends Activity {
    private final static String TAG="AiChoseRoomActivity";

    private ImageView back;
    private RecyclerView meetroom_list;


    private SharedPreferences sp;

    private ArrayList<RoomInfo> roomInfos;
    private int id[];
    private String name[];
    private int contain[];
    private String place[];
    private String tools[];
    private String sim[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_chose_room);
        init();
        setListener();
    }
    public void init(){
        sp=this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        final Intent intent=getIntent();
        roomInfos=intent.getParcelableArrayListExtra("roominfos");
        back=findViewById(R.id.back);
        meetroom_list=findViewById(R.id.meetroom_list);

        id=new int[roomInfos.size()];
        name=new String[roomInfos.size()];
        contain=new int[roomInfos.size()];
        place=new String[roomInfos.size()];
        tools=new String[roomInfos.size()];
        sim=new String[roomInfos.size()];

        for(int i=0;i<id.length;i++){
            RoomInfo roomInfo=roomInfos.get(i);
            id[i]=roomInfo.getMeetRoomId();
            name[i]=roomInfo.getMeetRoomName();
            contain[i]=roomInfo.getContain();
            place[i]=roomInfo.getNum();
            sim[i]=roomInfo.getSimilar();
            ArrayList<String> list=roomInfo.getEquips();
            String str="";
            for(int j=0;j<list.size();j++){
                str+=list.get(j);
                str+=" ";
            }
            tools[i]=str;
        }
        meetroom_list.setLayoutManager(new LinearLayoutManager(AiChoseRoomActivity.this));
        final AiMeetingRoomInfoAdapter adapter=new AiMeetingRoomInfoAdapter(AiChoseRoomActivity.this);
        adapter.setMeetRoomId(id);
        adapter.setMeetRoomName(name);
        adapter.setContain(contain);
        adapter.setNum(place);
        adapter.setEquips(tools);
        adapter.setSimilar(sim);
        adapter.setOnItemClickLitener(new MeetingRoomInfoAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent2=new Intent(AiChoseRoomActivity.this,AddActivity.class);
                intent2.putExtra("roomid",id[position]);
                intent2.putExtra("roomname",name[position]);
                startActivityForResult(intent2,1);
//                startActivity(intent2);
            }
        });
        meetroom_list.setAdapter(adapter);
    }
    public void setListener(){
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.w(TAG,"返回值:"+resultCode);
        switch (resultCode){
            case 100:
                setResult(100);
                finish();
                break;
            case 500:
                break;
        }
    }

}
