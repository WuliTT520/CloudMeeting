package com.zhihui.imeeting.cloudmeeting.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.zhihui.imeeting.cloudmeeting.R;
import com.zhihui.imeeting.cloudmeeting.controller.MyURL;
import com.zhihui.imeeting.cloudmeeting.service.MsgService;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MineFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MineFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private TextView Tname;
    private TextView Tworknum;
    private TextView Tresume;
    private LinearLayout myGroup;
    private LinearLayout info;
    private LinearLayout face_manager;
    private LinearLayout change_pwd;
    private LinearLayout myLeave;
    private LinearLayout sing_info;
    private LinearLayout yichang;
    private LinearLayout kaimeng;
    View view;
    private Button logout;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    private static final String TAG="MineFragment";
    Handler handler;
    Message msg;
    String name;
    String worknum;
    String resume;
    public MineFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MineFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MineFragment newInstance(String param1, String param2) {
        MineFragment fragment = new MineFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_mine, null);
//        return inflater.inflate(R.layout.fragment_mine, container, false);
        init();
        setListener();
        getInfo();
        return view;
    }

    public void init(){
        yichang=view.findViewById(R.id.yichang);
        logout=view.findViewById(R.id.logout);
        Tname=view.findViewById(R.id.name);
        Tworknum=view.findViewById(R.id.workNum);
        Tresume=view.findViewById(R.id.resume);
        myGroup=view.findViewById(R.id.myGroup);
        info=view.findViewById(R.id.info);
        face_manager=view.findViewById(R.id.face_manager);
        change_pwd=view.findViewById(R.id.change_pwd);
        myLeave=view.findViewById(R.id.myLeave);
        sing_info=view.findViewById(R.id.sing_info);
        kaimeng=view.findViewById(R.id.kaimeng);
        sp = this.getActivity().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch(msg.what){
                    case 404:
                        Toast.makeText(getActivity(),"网络异常",Toast.LENGTH_SHORT).show();
                        break;
                    case 500:
                        Toast.makeText(getActivity(),"请求错误",Toast.LENGTH_SHORT).show();
                        break;
                    case 100:
//                        Toast.makeText(getActivity(),name+" "+worknum+" "+resume,Toast.LENGTH_SHORT).show();
                        Tname.setText(name);
                        Tworknum.setText("工号："+worknum);
                        Tresume.setText("简介："+resume);
                        break;
                        default:
                }
                super.handleMessage(msg);
            }
        };
    }
    public void setListener(){
        yichang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(),YiChangActivity.class);
                startActivity(intent);
            }
        });
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(),UserInfoActivity.class);
                startActivity(intent);
            }
        });
        face_manager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(),FaceManagerActivity.class);
                startActivity(intent);
            }
        });
        kaimeng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(),OpenDoorActivity.class);
                startActivity(intent);
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyURL myURL = new MyURL();
                final OkHttpClient client = new OkHttpClient();
                final Request request = new Request.Builder()
                        .addHeader("cookie", sp.getString("sessionID", ""))
                        .url(myURL.logout())
                        .build();
                Call call = client.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {

                    }
                });

                editor = sp.edit();
                editor.putString("sessionID", "");
                editor.putString("userCode","");
                editor.putBoolean("isLogin", false);
                editor.putInt("userId",-1);
                editor.commit();
                Intent stopService=new Intent(getActivity(),MsgService.class);
                getActivity().stopService(stopService);
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        myGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), GroupActivity.class);
                startActivity(intent);
            }
        });
        change_pwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ChangePwdActivity.class);
                startActivity(intent);
            }
        });
        myLeave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(),LeaveInfoActivity.class);
                startActivity(intent);
            }
        });
        sing_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(),SingInActivity.class);
                startActivity(intent);
            }
        });
    }
    public void getInfo(){
        MyURL url=new MyURL();
        final OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .addHeader("cookie", sp.getString("sessionID", ""))
                .url(url.showUserinfo())
                .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.w(TAG,"请求失败");
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
                        JSONObject info=data.getJSONObject("data");
                        name=info.getString("name");
                        worknum=info.getString("worknum");
                        resume=info.getString("resume");
                        msg=Message.obtain();
                        msg.what=100;
                        handler.sendMessage(msg);
//                        Log.w(TAG,name+" "+worknum+" "+resume);
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
