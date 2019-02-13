package com.zhihui.imeeting.cloudmeeting.activity;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.zhihui.imeeting.cloudmeeting.R;
import com.zhihui.imeeting.cloudmeeting.adapter.ColleagueListAdapter;
import com.zhihui.imeeting.cloudmeeting.adapter.GroupListViewAdapter;
import com.zhihui.imeeting.cloudmeeting.controller.MyURL;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ScheduleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScheduleFragment extends Fragment {
    private ExpandableListView member_list;
    private View view;
    Handler handler;
    Message msg;
    SharedPreferences sp;
    private int[] groupid;
    private String[] groupStr;
    private List[] childid;
    private List[] childStr;
    private ColleagueListAdapter adapter;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public ScheduleFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ScheduleFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ScheduleFragment newInstance(String param1, String param2) {
        ScheduleFragment fragment = new ScheduleFragment();
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
        view = inflater.inflate(R.layout.fragment_schedule, null);
        init();
        getInfo();
        setListener();
        return view;
//        return inflater.inflate(R.layout.fragment_schedule, container, false);
    }
    public void init(){
        member_list=view.findViewById(R.id.member_list);
        sp = this.getActivity().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 200:
                        adapter = new ColleagueListAdapter();
                        adapter.setChildid(childid);
                        adapter.setGroupString(groupStr);
                        adapter.setChildString(childStr);
                        adapter.setMcontext(getActivity());
                        member_list.setAdapter(adapter);
                        for (int i = 0; i < groupid.length; i++) {
                            member_list.expandGroup(i);
                        }
                        break;
                    case 500:
                        Toast.makeText(getActivity(),"请先登陆",Toast.LENGTH_LONG).show();
                        break;
                    case 404:
                        Toast.makeText(getActivity(),"网络错误",Toast.LENGTH_LONG).show();
                        break;
                }
            }
        };

    }
    public void setListener(){

    }
    public void getInfo(){
        MyURL url=new MyURL();
        final OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .addHeader("cookie", sp.getString("sessionID", ""))
                .url(url.showUser())
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
//                    Log.w("通讯录数据",result);
                    JSONObject data = new JSONObject(result);
                    boolean flag = data.getBoolean("status");
                    if (flag){
                        JSONArray info=data.getJSONArray("data");
                        JSONArray groups=info.getJSONArray(0);
                        JSONArray peoples=info.getJSONArray(1);
                        groupid=new int[groups.length()];
                        groupStr=new String[groups.length()];
                        for(int i=0;i<groups.length();i++){
                            groupid[i]=groups.getJSONObject(i).getInt("id");
                            groupStr[i]=groups.getJSONObject(i).getString("name");
                        }

                        childid=new ArrayList[groups.length()];

                        childStr=new ArrayList[groups.length()];

                        for(int i=0;i<groups.length();i++){
                            JSONArray people=peoples.getJSONArray(i);
                            childStr[i]=new ArrayList<String>();
                            childid[i]=new ArrayList<Integer>();
                            for(int j=0;j<people.length();j++){
                                childid[i].add(j,people.getJSONObject(j).getInt("id"));
                                childStr[i].add(j,people.getJSONObject(j).getString("name"));
                            }
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
}
