package com.zhihui.imeeting.cloudmeeting.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.zhihui.imeeting.cloudmeeting.R;
import com.zhihui.imeeting.cloudmeeting.adapter.BoardroomListAdapter;
import com.zhihui.imeeting.cloudmeeting.adapter.MeetingRoomInfoAdapter;
import com.zhihui.imeeting.cloudmeeting.helper.FileHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class MeetingRoomInfoActivity extends Activity {
    private static final String TAG="MeetingRoomInfoActivity";
    private ImageView back;
    private RecyclerView meetroom_list;

    private int[] id;
    private String[] name;
    private String[] place;
    private int[] contain;
    private String[] tools;
    private int[] toolId;
    private String[] toolName;
    private int [] nowStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_room_info);
        getInfo();
        init();
        setListener();
    }
    public void init(){
        back=findViewById(R.id.back);
        meetroom_list=findViewById(R.id.meetroom_list);
        meetroom_list.setLayoutManager(new LinearLayoutManager(MeetingRoomInfoActivity.this));
        final MeetingRoomInfoAdapter adapter=new MeetingRoomInfoAdapter(MeetingRoomInfoActivity.this);
        adapter.setId(id);
        adapter.setName(name);
        adapter.setContain(contain);
        adapter.setPlace(place);
        adapter.setTools(tools);
        adapter.setNowStatus(nowStatus);
        adapter.setOnItemClickLitener(new MeetingRoomInfoAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
//                Toast.makeText(MeetingRoomInfoActivity.this,id[position]+"",Toast.LENGTH_LONG).show();
                Intent intent=new Intent(MeetingRoomInfoActivity.this,MeetingReserverActivity.class);
                intent.putExtra("roomid",id[position]);
                startActivity(intent);
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
    public void getInfo(){
        FileHelper helper=new FileHelper();
        String result=helper.read(MeetingRoomInfoActivity.this,"meetingInfo.txt");
        Log.w(TAG,result);
        try {
            JSONArray data = new JSONObject(result).getJSONArray("data");
            JSONArray rooms=data.getJSONArray(2);
            id=new int[rooms.length()];
            name=new String[id.length];
            place=new String[id.length];
            contain=new int[id.length];
            nowStatus=new int[id.length];

            for(int i=0;i<rooms.length();i++){
                id[i]=rooms.getJSONObject(i).getInt("id");
                name[i]=rooms.getJSONObject(i).getString("name");
                place[i]=rooms.getJSONObject(i).getString("place");
                contain[i]=rooms.getJSONObject(i).getInt("contain");
                nowStatus[i]=rooms.getJSONObject(i).getInt("nowStatus");
            }
            JSONArray tool=data.getJSONArray(1);
//            alltool=new String[tool.length()][2];
            toolId=new int[tool.length()];
            toolName=new String[tool.length()];
            for(int i=0;i<tool.length();i++){
                toolId[i]=tool.getJSONObject(i).getInt("id");
//                Log.w("id",toolId[i]+"");
                toolName[i]=tool.getJSONObject(i).getString("name");
//                Log.w("name",toolName[i]+"");
            }
            JSONArray array=data.getJSONArray(4);
            tools=new String[array.length()];
            for(int i=0;i<array.length();i++){
                JSONArray info=array.getJSONArray(i);
                String str="";
                int key=0;
                for(int j=0;j<info.length();j++){
                    int meetroomId=info.getJSONObject(j).getInt("meetroomId");
                    int equipId=info.getJSONObject(j).getInt("equipId");
//                    Log.w("int equipid",equipId+"");
                    key= Arrays.binarySearch(id,meetroomId);
//                    Log.w("Arrays.binarySearch(toolId,equipId)",Arrays.binarySearch(toolId,equipId)+"");
                    str+=toolName[Arrays.binarySearch(toolId,equipId)]+" ";
                }
                tools[key]=str;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
