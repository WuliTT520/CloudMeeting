package com.zhihui.imeeting.cloudmeeting.activity;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.zhihui.imeeting.cloudmeeting.R;
import com.zhihui.imeeting.cloudmeeting.adapter.ReserveListViewAdapter;
import com.zhihui.imeeting.cloudmeeting.controller.MyURL;
import com.zhihui.imeeting.cloudmeeting.helper.MyLoader;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private static final String TAG="HomeFragment";
    View view;
    Button myreserve;
    private TextView tip;
    private RecyclerView meetingList;
    private Banner banner;
    private Button room;
    private Message msg;
    private Handler handler;
    private SharedPreferences sp;
    private Calendar cal;
    private String current_time;
    private String next;
    private String afterNext;

    private String current_time_year;
    private String current_time_month;
    private String current_time_day;

    private int sign;

    private List<Integer> id;
    private List<String> Ltopic;
    private List<String> Lstatus;
    private List<String> LmeetDate;
    private List<String> Lbegin;
    private List<String> Lover;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        view = inflater.inflate(R.layout.fragment_home, null);
        init();
        setListener();
        getInfo(current_time);
//        getInfo(next);
//        getInfo(afterNext);
        return view;
    }
    public void init(){
        /*获取当前时间*/
        cal = Calendar.getInstance();
        current_time_year = String.valueOf(cal.get(Calendar.YEAR));
        current_time_month = String.format("%02d",cal.get(Calendar.MONTH)+1);
        current_time_day = String.format("%02d",cal.get(Calendar.DATE));
        current_time=current_time_year+"-"+current_time_month+"-"+current_time_day;
        /*明天时间*/
        cal.setTime(new Date(cal.getTime().getTime()+1000*60*60*24));
        current_time_year = String.valueOf(cal.get(Calendar.YEAR));
        current_time_month = String.format("%02d",cal.get(Calendar.MONTH)+1);
        current_time_day = String.format("%02d",cal.get(Calendar.DATE));
        next=current_time_year+"-"+current_time_month+"-"+current_time_day;
        /*后天时间*/
        cal.setTime(new Date(cal.getTime().getTime()+1000*60*60*24));
        current_time_year = String.valueOf(cal.get(Calendar.YEAR));
        current_time_month = String.format("%02d",cal.get(Calendar.MONTH)+1);
        current_time_day = String.format("%02d",cal.get(Calendar.DATE));
        afterNext=current_time_year+"-"+current_time_month+"-"+current_time_day;

//        Toast.makeText(getActivity(),afterNext,Toast.LENGTH_LONG).show();
        /*初始化*/

        Integer[] image={R.drawable.adv01,R.drawable.adv02,R.drawable.adv03};
        List<Integer> images=Arrays.asList(image);
        String[] titles={"欢迎使用会议室管理助手","预订会议室，就用会议室管理系统","会议室管理助手"};
        banner =view.findViewById(R.id.banner);
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE);
        //设置图片加载器
        banner.setImageLoader(new MyLoader());
        //设置图片集合
        banner.setImages(images);
        //设置banner动画效果
        banner.setBannerAnimation(Transformer.DepthPage);
        //设置标题集合（当banner样式有显示title时）
        banner.setBannerTitles(Arrays.asList(titles));
        //设置自动轮播，默认为true
        banner.isAutoPlay(true);
        //设置轮播时间
        banner.setDelayTime(3*1000);
        //设置指示器位置（当banner模式中有指示器时）
        banner.setIndicatorGravity(BannerConfig.CENTER);
        //banner设置方法全部调用完毕时最后调用
        banner.start();

        id=new ArrayList<>();
        Ltopic=new ArrayList<>();
        Lstatus=new ArrayList<>();
        LmeetDate=new ArrayList<>();
        Lbegin=new ArrayList<>();
        Lover=new ArrayList<>();
        sign=0;
        sp=getActivity().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        tip=view.findViewById(R.id.tip);
        room=view.findViewById(R.id.room_info);
        myreserve=view.findViewById(R.id.myreserve);
        meetingList=view.findViewById(R.id.meetingList);
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 200:
                        if (id.size()==0){
                            tip.setText("近3天没有会议哦");
                        }else {
                            meetingList.setLayoutManager(new LinearLayoutManager(getActivity()));
                            final ReserveListViewAdapter adapter = new ReserveListViewAdapter(getActivity());
                            adapter.setBegin(Lbegin.toArray(new String[Lbegin.size()]));
                            adapter.setMeetDate(LmeetDate.toArray(new String[LmeetDate.size()]));
                            adapter.setOver(Lover.toArray(new String[Lover.size()]));
                            adapter.setStatus(Lstatus.toArray(new String[Lstatus.size()]));
                            adapter.setTopic(Ltopic.toArray(new String[Ltopic.size()]));
                            adapter.setOnItemClickLitener(new ReserveListViewAdapter.OnItemClickLitener() {
                                @Override
                                public void onItemClick(View view, int position) {
//                                    Toast.makeText(getActivity(),id.get(position)+"",Toast.LENGTH_LONG).show();
                                Intent intent=new Intent(getActivity(),MeetingInfo5Activity.class);
                                intent.putExtra("meetingId",id.get(position));
                                startActivity(intent);
                                }
                            });
                            meetingList.setAdapter(adapter);
                        }
//                        Toast.makeText(getActivity(),"显示列表",Toast.LENGTH_LONG).show();
                        break;
                    case 404:
                        Toast.makeText(getActivity(),"网络错误",Toast.LENGTH_LONG).show();
                        break;
                    case 500:
                        Toast.makeText(getActivity(),"数据错误",Toast.LENGTH_LONG).show();
                        break;
                }
            }
        };
    }
    public void setListener(){
        myreserve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(),MyReserveActivity.class);
                startActivity(intent);
            }
        });
        room.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(),MeetingRoomInfoActivity.class);
                startActivity(intent);
            }
        });
    }
    public void getInfo(String time){
        MyURL url=new MyURL();
        final OkHttpClient client = new OkHttpClient();
        RequestBody body=new FormBody.Builder()
                .add("meetDate",time)
                .build();
        final Request request = new Request.Builder()
                .addHeader("cookie", sp.getString("sessionID", ""))
                .url(url.selectMyJoinMeetingByDate())
                .post(body)
                .build();
        Log.w(TAG,time);
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
                    JSONObject data =new JSONObject(result);
                    boolean flag=data.getBoolean("status");
                    if (flag){
                        JSONArray info=data.getJSONArray("data");
                        for(int i=0;i<info.length();i++){
                            if (info.getJSONObject(i).getString("status").equals("已结束")){
                                continue;
                            }
                            id.add(info.getJSONObject(i).getInt("id"));
                            Ltopic.add(info.getJSONObject(i).getString("topic"));
                            Lstatus.add(info.getJSONObject(i).getString("status"));
                            /*处理数据*/
//                            LmeetDate.add(info.getJSONObject(i).getString("meetDate"));
                            String str=info.getJSONObject(i).getString("begin");
                            LmeetDate.add(str.substring(0,10));
//                            Log.w("时间",str.substring(0,10));
                            Lbegin.add(str.substring(11,16));
//                            Log.w("时间",str.substring(11,16));
                            str=info.getJSONObject(i).getString("over");
                            Lover.add(str.substring(11,16));
//                            Log.w("时间",str.substring(11,16));

                        }
//                        Log.w(TAG,"sign="+sign);
                        sign++;
//                        Log.w(TAG,"sign="+sign);
                        if (sign==1){
                            getInfo(next);
                        }
                        if (sign==2){
                            getInfo(afterNext);
                        }
                        if (sign==3){
                            msg=Message.obtain();
                            msg.what=200;
                            handler.sendMessage(msg);
                        }
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
