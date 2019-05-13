package com.zhihui.imeeting.cloudmeeting.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.zhihui.imeeting.cloudmeeting.entity.RoomInfo;
import com.zhihui.imeeting.cloudmeeting.widget.DragGridView;
import com.zhihui.imeeting.cloudmeeting.R;
import com.zhihui.imeeting.cloudmeeting.controller.MyURL;
import com.zhihui.imeeting.cloudmeeting.entity.Equip;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AIActivity extends Activity {
    private static final String TAG="AIActivity";
    private Handler handler;
    private Message msg;
    private DragGridView sortView;
    private DragGridView choseView;
    private ImageView back;
    private EditText peoplenum;
    private SharedPreferences sp;
    private Button ok;
    private ArrayList<Equip> allEquips=new ArrayList<>();
    private ArrayList<Equip> choseEquips=new ArrayList<>();
    private List<String> finalEquips=new ArrayList();
    private int equip_id[];
    private double weight[];
    private int num;
    private ProgressDialog waitingDialog;

    private ArrayList<RoomInfo> roomInfos=new ArrayList<>();

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai);
        init();
        getInfo();
        setListener();

    }
    public void init(){
        sp=this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        sortView=findViewById(R.id.sortView);
        choseView=findViewById(R.id.choseView);
        ok=findViewById(R.id.ok);
        back=findViewById(R.id.back);
        peoplenum=findViewById(R.id.peoplenum);

        sortView.setHasDrag(true);
        sortView.setTextSize(5);
        sortView.setTextPadding(10);
        sortView.setTextNormalBackground(R.drawable.choose_item);
        sortView.setTextMargin(8);
        sortView.setColumnCount(4);
        sortView.setTextSelectedBackground(R.drawable.select_item);

        choseView.setTextSize(5);
        choseView.setTextPadding(10);
        choseView.setTextNormalBackground(R.drawable.choose_item);
        choseView.setTextMargin(8);
        choseView.setColumnCount(4);

        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 200:
                        for(int i=0;i<allEquips.size();i++){
                            choseView.addItemView(allEquips.get(i).getName());
                        }
                        break;
                    case 404:
                        Toast.makeText(AIActivity.this,"连接超时",Toast.LENGTH_LONG).show();
                        break;
                    case 500:
                        Toast.makeText(AIActivity.this,"数据错误",Toast.LENGTH_LONG).show();
                        break;
                    case 201:
                        /*获取推荐会议室成功，跳转并显示推荐的会议室信息*/
                        waitingDialog.cancel();
                        Intent intent=new Intent(AIActivity.this,AiChoseRoomActivity.class);
                        intent.putExtra("roominfos",roomInfos);
                        startActivityForResult(intent,1);
                        break;
                }
            }
        };

    }
    public void getInfo(){
        MyURL url=new MyURL();
        final OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .addHeader("cookie", sp.getString("sessionID", ""))
                .url(url.selectAll())
                .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.w(TAG,"连接超时");
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
                        /*将获取到的设备存入待选链表中*/
                        JSONArray array=data.getJSONArray("data");
                        for(int i=0;i<array.length();i++){
                            Equip equip=new Equip(array.getJSONObject(i).getInt("id"),array.getJSONObject(i).getString("name"));
                            allEquips.add(equip);
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
    public void setListener(){
        choseView.setOnItemClickListener(new DragGridView.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, ViewGroup parent, String text, int position) {
                choseEquips.add(allEquips.get(position));
                allEquips.remove(position);
                sortView.addItemView(choseEquips.get(choseEquips.size()-1).getName());
                choseView.setItemViews(new String[0]);
                for(int i=0;i<allEquips.size();i++){
                    choseView.addItemView(allEquips.get(i).getName());
                }
            }
        });
        sortView.setOnItemClickListener(new DragGridView.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, ViewGroup parent, String text, int position) {
                allEquips.add(choseEquips.get(search(text)));
                choseEquips.remove(choseEquips.get(search(text)));
                choseView.addItemView(allEquips.get(allEquips.size()-1).getName());
                sortView.setItemViews(new String[0]);
                for(int i=0;i<choseEquips.size();i++){
                    sortView.addItemView(choseEquips.get(i).getName());
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (peoplenum.getText().toString().equals("")){
                    Toast.makeText(AIActivity.this,"请输入开会人数",Toast.LENGTH_LONG).show();
                }else if ((num=Integer.parseInt(peoplenum.getText().toString()))<1){
                    Toast.makeText(AIActivity.this,"开会人数不能小于1",Toast.LENGTH_LONG).show();
                }else {
//                    Toast.makeText(AIActivity.this,"ok!",Toast.LENGTH_LONG).show();
                    showWaitingDialog();
                    finalEquips=sortView.getDefaultItems();
                    try {
                        JSONObject jsonObject=new JSONObject();
                        jsonObject.put("contain",num);
                        equip_id=new int[finalEquips.size()];
//                        weight=new double[finalEquips.size()];
                        /*有权值后删除*/
                        weight=new double[finalEquips.size()+1];
                        for(int j=0;j<equip_id.length;j++){
                            equip_id[j]=choseEquips.get(search(finalEquips.get(j))).getId();
//                            weight[j]=equip_id.length-j;
                            /*有权值后删除*/
                            weight[j]=1;
                            Log.w(TAG,"id:"+equip_id[j]+"weight:"+weight[j]);
                        }
                        /*有权值后删除*/
                        weight[finalEquips.size()]=1;

                        JSONArray eqid=new JSONArray();
                        for(int k=0;k<equip_id.length;k++){
                            eqid.put(equip_id[k]);
                        }
                        JSONArray quanzhi=new JSONArray();
                        for(int k=0;k<weight.length;k++){
                            quanzhi.put(weight[k]);
                        }
                        jsonObject.put("equips",eqid);
                        jsonObject.put("weight",quanzhi);
                        Log.w(TAG,jsonObject.toString());

                        MyURL url=new MyURL();
                        final OkHttpClient client = new OkHttpClient();
                        RequestBody body = RequestBody.create(JSON, jsonObject.toString());
                        final Request request = new Request.Builder()
                                .addHeader("cookie", sp.getString("sessionID", ""))
                                .url(url.recommandMeetRoom())
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
                                        JSONArray array=data.getJSONArray("data");

                                        for(int i=0;i<array.length();i++){
                                            JSONObject item=array.getJSONObject(i);
                                            JSONArray eq=item.getJSONArray("meetroomEquips");
                                            ArrayList<String> list=new ArrayList<>();
                                            for(int j=0;j<eq.length();j++){
//                                                JSONObject equip_item=eq.getJSONObject(j);
                                                list.add(eq.getJSONObject(j).getJSONObject("equip").getString("name"));

                                            }
                                            RoomInfo info=new RoomInfo();
                                            info.setMeetRoomId(item.getInt("meetRoomId"));
                                            info.setMeetRoomName(item.getString("meetRoomName"));
                                            info.setSimilar(item.getString("similar"));
                                            info.setContain(item.getInt("contain"));
                                            info.setNum(item.getString("num"));
                                            info.setEquips(list);
//                                            Log.w(TAG,info.toString());
                                            roomInfos.add(info);

                                        }
                                        msg=Message.obtain();
                                        msg.what=201;
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
                    }catch (Exception e){
                        e.printStackTrace();
                    }


                }
            }
        });
    }

    public int search(String name){
        for(int i=0;i<choseEquips.size();i++){
            if (choseEquips.get(i).getName().equals(name)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        Log.w(TAG,"返回值:"+resultCode);
        switch (resultCode){
            case 100:
                finish();
                break;
            case 500:
                break;
        }
    }

    private void showWaitingDialog() {
        waitingDialog= new ProgressDialog(AIActivity.this);
        waitingDialog.setMessage("请等待...");
        waitingDialog.setIndeterminate(true);
        waitingDialog.setCancelable(false);
        waitingDialog.show();
    }
}
