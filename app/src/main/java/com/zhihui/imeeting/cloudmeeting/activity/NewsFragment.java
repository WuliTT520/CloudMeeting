package com.zhihui.imeeting.cloudmeeting.activity;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.zhihui.imeeting.cloudmeeting.R;
import com.zhihui.imeeting.cloudmeeting.helper.Msg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NewsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private static final String TAG="NewsFragment";
    private SwipeMenuListView msgList;
    private View view;
    private List<String[]> data;
    SharedPreferences sp;

    private String[] id;
    private String[] message;
    private String[] time;
    private String[] meeting_id;

    public NewsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NewsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NewsFragment newInstance(String param1, String param2) {
        NewsFragment fragment = new NewsFragment();
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
        view = inflater.inflate(R.layout.fragment_news, null);
        getInfo();
        init();
        setData();
        setListener();
        return view;
    }
    public void init(){
        msgList=view.findViewById(R.id.msgList);
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {

                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getActivity());
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
        msgList.setMenuCreator(creator);


    }
    public void setData(){
        /*如果有数据*/
        if (id.length!=0){
            List mdata=new ArrayList<Map<String,Object>>();
            for(int i=0;i<id.length;i++){
                Map item=new HashMap<String,Object>();

                item.put("message",message[i]);
                item.put("time",time[i]);
                mdata.add(item);
            }
            SimpleAdapter adapter=new SimpleAdapter(getActivity(),mdata,R.layout.msg_item,
                    new String[] {"message","time"},
                    new int[]{R.id.message_tv,R.id.time_tv} );
            msgList.setAdapter(adapter);
        }else {
            List emptyData=new ArrayList<Map<String,Object>>();
            SimpleAdapter empty=new SimpleAdapter(getActivity(),emptyData,R.layout.msg_item,
                    new String[] {"message","time"},
                    new int[]{R.id.message_tv,R.id.time_tv} );
            msgList.setAdapter(empty);
        }
    }
    public void setListener(){
        msgList.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index){
                    case 0:
                        Msg msg=new Msg(getActivity());
                        msg.delete(Integer.parseInt(id[position]));
                        Toast.makeText(getActivity(),"删除成功",Toast.LENGTH_LONG).show();
                        getInfo();
                        setData();
                        break;
                }
                return false;
            }
        });
        msgList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Toast.makeText(getActivity(),meeting_id[i],Toast.LENGTH_LONG).show();
                Intent intent=new Intent(getActivity(),MeetingInfoActivity.class);
                intent.putExtra("meetingId",Integer.parseInt(meeting_id[i]));
                startActivity(intent);
            }
        });
    }
    public void getInfo(){
        sp = this.getActivity().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        Msg msg=new Msg(getActivity());
        Log.w(TAG,sp.getInt("userId",-1)+"");
        data=msg.search(sp.getInt("userId",-1));
        id=new String [data.size()];
        message=new String [data.size()];
        meeting_id=new String [data.size()];
        time=new String [data.size()];
        int j=0;
        for(int i=data.size()-1;i>=0;i--){
            id[j]=data.get(i)[0];
            message[j]=data.get(i)[1];
            time[j]=data.get(i)[2];
            meeting_id[j]=data.get(i)[3];
            j++;
        }
//        Toast.makeText(getActivity(),id[0]+" "+message[0]+" "+time[0]+" "+meeting_id[0],Toast.LENGTH_LONG).show();
    }
    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }
}
