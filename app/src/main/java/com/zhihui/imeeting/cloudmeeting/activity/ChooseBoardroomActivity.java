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
import com.zhihui.imeeting.cloudmeeting.helper.FileHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChooseBoardroomActivity extends Activity {
    private static final String TAG="ChooseBoardroomActivity";
    private ImageView back;
    private RecyclerView boardroom_list;
    private int[] id;
    private String[] name;
    private String[] place;
    private int[] contain;
    private String[] tools;
//    private String[][] alltool;
    private int[] toolId;
    private String[] toolName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_boardroom);
        getInfo();
        init();
        setListener();
    }
    public void init(){
        back=findViewById(R.id.back);
        boardroom_list=findViewById(R.id.boardroom_list);
        boardroom_list.setLayoutManager(new LinearLayoutManager(ChooseBoardroomActivity.this));
        final BoardroomListAdapter adapter=new BoardroomListAdapter(ChooseBoardroomActivity.this);
        adapter.setId(id);
        adapter.setName(name);
        adapter.setContain(contain);
        adapter.setPlace(place);
        adapter.setTools(tools);
        adapter.setOnItemClickLitener(new BoardroomListAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
//                Toast.makeText(ChooseBoardroomActivity.this,id[position]+"",Toast.LENGTH_LONG).show();
                Intent intent=getIntent();
                intent.putExtra("BoardroomId",id[position]);
                intent.putExtra("BoardroomName",name[position]);
                setResult(100,intent);
                finish();
            }
        });
        boardroom_list.setAdapter(adapter);

    }
    public void setListener(){
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(500);
                finish();
            }
        });

    }
    public void getInfo(){
        FileHelper helper=new FileHelper();
        String result=helper.read(ChooseBoardroomActivity.this,"meetingInfo.txt");
        Log.w(TAG,result);
        try {
            JSONArray data = new JSONObject(result).getJSONArray("data");
            JSONArray rooms=data.getJSONArray(2);
            id=new int[rooms.length()];
            name=new String[id.length];
            place=new String[id.length];
            contain=new int[id.length];
            for(int i=0;i<rooms.length();i++){
                id[i]=rooms.getJSONObject(i).getInt("id");
                name[i]=rooms.getJSONObject(i).getString("name");
                place[i]=rooms.getJSONObject(i).getString("place");
                contain[i]=rooms.getJSONObject(i).getInt("contain");
            }
            JSONArray tool=data.getJSONArray(1);
//            alltool=new String[tool.length()][2];
            toolId=new int[tool.length()];
            toolName=new String[tool.length()];
            for(int i=0;i<tool.length();i++){
                toolId[i]=tool.getJSONObject(i).getInt("id");
                toolName[i]=tool.getJSONObject(i).getString("name");
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
                    key= Arrays.binarySearch(id,meetroomId);
                    str+=toolName[Arrays.binarySearch(toolId,equipId)]+" ";
                }
                tools[key]=str;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
