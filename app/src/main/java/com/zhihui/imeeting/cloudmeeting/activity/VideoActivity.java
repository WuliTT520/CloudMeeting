package com.zhihui.imeeting.cloudmeeting.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.zhihui.imeeting.cloudmeeting.R;
import com.zhihui.imeeting.cloudmeeting.TRTC.TRTCGetUserIDAndUserSig;
import com.zhihui.imeeting.cloudmeeting.adapter.VideoRoomListAdapter;
import com.zhihui.imeeting.cloudmeeting.controller.MyURL;
import com.zhihui.imeeting.cloudmeeting.entity.Equip;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class VideoActivity extends Activity {
    private final static String TAG="VideoActivity";

    /**/

    private final static int REQ_PERMISSION_CODE = 0x1000;
    private TRTCGetUserIDAndUserSig mUserInfoLoader;
    /**/

    private ImageView back;
    private ImageView add;
    private RecyclerView list;

    private SharedPreferences sp;
    private Handler handler;
    private Message msg;

    private int id[];
    private String videoRoomName[];
    private String createTime[];
    private String name[];

    private int userId;
    private String userToken;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        Init();
        getInfo();
        getInfo2();
        setListener();

        checkPermission();
    }
    public void Init(){
        sp=this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        back=findViewById(R.id.back);
        add=findViewById(R.id.add);
        list=findViewById(R.id.list);


        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 200:
                        list.setLayoutManager(new LinearLayoutManager(VideoActivity.this));
                        final VideoRoomListAdapter adapter=new VideoRoomListAdapter(VideoActivity.this);
                        adapter.setIds(id);
                        adapter.setVideoRoomNames(videoRoomName);
                        adapter.setCreateTimes(createTime);
                        adapter.setNames(name);
                        adapter.setOnItemClickLitener(new VideoRoomListAdapter.OnItemClickLitener() {
                            @Override
                            public void onItemClick(View view, int position) {

                                onJoinRoom(id[position],String.valueOf(userId));
                            }
                        });
                        list.setAdapter(adapter);
                        break;
                    case 500:
                        Toast.makeText(VideoActivity.this,"数据错误",Toast.LENGTH_LONG).show();
                        break;
                    case 404:
                        Toast.makeText(VideoActivity.this,"连接超时",Toast.LENGTH_LONG).show();
                        break;
                    case 201:
                        mUserInfoLoader=new TRTCGetUserIDAndUserSig(changJson(String.valueOf(userId),userToken));

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
                .url(url.selectMyVideoRoom())
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
                        id=new int[array.length()];
                        videoRoomName=new String[array.length()];
                        createTime=new String[array.length()];
                        name=new String[array.length()];

                        for(int i=0;i<array.length();i++){
                            JSONObject item=array.getJSONObject(i);
                            id[i]=item.getInt("id");
                            videoRoomName[i]=item.getString("videoRoomName");
                            createTime[i]=item.getString("createTime");
                            name[i]=item.getJSONObject("userinfo").getString("name");
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
    public void getInfo2(){
        MyURL url=new MyURL();
        final OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .addHeader("cookie", sp.getString("sessionID", ""))
                .url(url.joinMeeting())
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
                        userId=array.getInt(0);
                        userToken=array.getString(2);
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
    }

    public void setListener(){
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(VideoActivity.this,AddVideoActivity.class);
                startActivity(intent);
            }
        });
    }
    private Context getContext(){
        return this;
    }
    //////////////////////////////////    动态权限申请   ////////////////////////////////////////

    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> permissions = new ArrayList<>();
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)) {
                permissions.add(Manifest.permission.CAMERA);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO)) {
                permissions.add(Manifest.permission.RECORD_AUDIO);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
            if (permissions.size() != 0) {
                ActivityCompat.requestPermissions(VideoActivity.this,
                        (String[]) permissions.toArray(new String[0]),
                        REQ_PERMISSION_CODE);
                return false;
            }
        }

        return true;
    }

//    private void startJoinRoom(int roomId,String userId) {
////        final EditText etRoomId = (EditText)findViewById(R.id.et_room_name);
////        final EditText etUserId = (EditText)findViewById(R.id.et_user_name);
////        int roomId = 123;
////        try{
////            roomId = Integer.valueOf(etRoomId.getText().toString());
////        }catch (Exception e){
////            Toast.makeText(getContext(), "请输入有效的房间号", Toast.LENGTH_SHORT).show();
////            return;
////        }
////        final String userId = etUserId.getText().toString();
////        if(TextUtils.isEmpty(userId)) {
////            Toast.makeText(getContext(), "请输入有效的用户名", Toast.LENGTH_SHORT).show();
////            return;
////        }
//
//        onJoinRoom(roomId, userId);
//    }


    /**
     *  Function: 读取用户输入，并创建（或加入）音视频房间
     *
     *  此段示例代码最主要的作用是组装 TRTC SDK 进房所需的 TRTCParams
     *
     *  TRTCParams.sdkAppId => 可以在腾讯云实时音视频控制台（https://console.cloud.tencent.com/rav）获取
     *  TRTCParams.userId   => 此处即用户输入的用户名，它是一个字符串
     *  TRTCParams.roomId   => 此处即用户输入的音视频房间号，比如 125
     *  TRTCParams.userSig  => 此处示例代码展示了两种获取 usersig 的方式，一种是从【控制台】获取，一种是从【服务器】获取
     *
     * （1）控制台获取：可以获得几组已经生成好的 userid 和 usersig，他们会被放在一个 json 格式的配置文件中，仅适合调试使用
     * （2）服务器获取：直接在服务器端用我们提供的源代码，根据 userid 实时计算 usersig，这种方式安全可靠，适合线上使用
     *
     *  参考文档：https://cloud.tencent.com/document/product/647/17275
     */
    private void onJoinRoom(final int roomId, final String userId) {
        final Intent intent = new Intent(getContext(), TRTCMainActivity.class);
        intent.putExtra("roomId", roomId);
        intent.putExtra("userId", userId);

//        boolean isCustomVideoCapture = ((RadioButton)findViewById(R.id.rb_video_file)).isChecked();
//        if (TextUtils.isEmpty(mVideoFile)) isCustomVideoCapture = false;
//        intent.putExtra("customAudioCapture", ((CheckBox)findViewById(R.id.cb_enable_custom_audio_capture)).isChecked());
//        intent.putExtra("customVideoCapture", isCustomVideoCapture);
//        intent.putExtra("videoFile", mVideoFile);

        int sdkAppId = mUserInfoLoader.getSdkAppIdFromConfig();
        if (sdkAppId > 0) {
            //（1） 从控制台获取的 json 文件中，简单获取几组已经提前计算好的 userid 和 usersig
            ArrayList<String> userIdList = mUserInfoLoader.getUserIdFromConfig();
            ArrayList<String> userSigList = mUserInfoLoader.getUserSigFromConfig();
            int position = userIdList.indexOf(userId);
            String userSig = "";
            if (userSigList != null && userSigList.size() > position) {
                userSig = userSigList.get(position);
            }
            intent.putExtra("sdkAppId", sdkAppId);
            intent.putExtra("userSig", userSig);
            startActivity(intent);
        }
//        else {
//            //appId 可以在腾讯云实时音视频控制台（https://console.cloud.tencent.com/rav）获取
//            sdkAppId = -1;
//            if(!TextUtils.isEmpty(mUserId) && mUserId.equalsIgnoreCase(userId) && !TextUtils.isEmpty(mUserSig)) {
//                intent.putExtra("sdkAppId", sdkAppId);
//                intent.putExtra("userSig", mUserSig);
//                saveUserInfo(String.valueOf(roomId), userId, mUserSig);
//                startActivity(intent);
//            }
////            else {
////                //（2） 通过 http 协议向一台服务器获取 userid 对应的 usersig
////                final int finalSdkAppId = sdkAppId;
////                mUserInfoLoader.getUserSigFromServer(sdkAppId, roomId, userId, "12345678", new TRTCGetUserIDAndUserSig.IGetUserSigListener() {
////                    @Override
////                    public void onComplete(String userSig, String errMsg) {
////                        if (!TextUtils.isEmpty(userSig)) {
////                            intent.putExtra("sdkAppId", finalSdkAppId);
////                            intent.putExtra("userSig", userSig);
////                            saveUserInfo(String.valueOf(roomId), userId, userSig);
////                            startActivity(intent);
////                        } else {
////                            runOnUiThread(new Runnable() {
////                                @Override
////                                public void run() {
////                                    Toast.makeText(getContext(), "从服务器获取userSig失败", Toast.LENGTH_SHORT).show();
////                                }
////                            });
////                        }
////                    }
////                });
////            }
//        }
    }

    public static String changJson(String userId,String userToken){
//        InputStream in=null;
        String str="{\"sdkappid\":\"1400208454\",\"users\":[{\"userId\":\"" +
                userId +
                "\",\"userToken\":\"" +
                userToken +
                "\"}],\"roles\":[{\"name\":\"user\",\"value\":\"640x368\"},{\"name\":\"wp640\",\"value\":\"640x480\"},{\"name\":\"wp1280\",\"value\":\"1280x720\"},{\"name\":\"wp320\",\"value\":\"320x240\"},{\"name\":\"test320\",\"value\":\"320x240\"},{\"name\":\"test640\",\"value\":\"640x480\"},{\"name\":\"test1280\",\"value\":\"1280x720\"},{\"name\":\"test480\",\"value\":\"480x360\"},{\"name\":\"test960\",\"value\":\"960x540\"},{\"name\":\"test368\",\"value\":\"640x368\"},{\"name\":\"ed320\",\"value\":\"320x240\"},{\"name\":\"ed640\",\"value\":\"640x480\"},{\"name\":\"ed1280\",\"value\":\"1280x720\"},{\"name\":\"1280\",\"value\":\"1280x720\"},{\"name\":\"wxhspeed\",\"value\":\"640x368\"},{\"name\":\"lsspeed\",\"value\":\"640x368\"},{\"name\":\"lsspeedpc\",\"value\":\"640x368\"},{\"name\":\"wawaji\",\"value\":\"864x480\"},{\"name\":\"test1920\",\"value\":\"1920x1080\"},{\"name\":\"test2560\",\"value\":\"640x368\"},{\"name\":\"miniwhite\",\"value\":\"640x368\"},{\"name\":\"ed960\",\"value\":\"960x540\"},{\"name\":\"ed480\",\"value\":\"640x368\"}]}";
//        try {
//            in = getContext().getResources().openRawResource(R.raw.config);
//
//
//        }catch (Exception e){
//            e.printStackTrace();
//        }
        return str;
    }
}
