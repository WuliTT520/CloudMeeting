package com.zhihui.imeeting.cloudmeeting.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhihui.imeeting.cloudmeeting.R;

public class MeetingRoomInfoAdapter extends RecyclerView.Adapter<MeetingRoomInfoAdapter.MyViewHolder> {
    private Context context;
    private int[] id;
    private String[] name;
    private int[] contain;
    private String[] tools;
    private String[] place;
    private int[] nowStatus;
    private OnItemClickLitener   mOnItemClickLitener;

    public interface OnItemClickLitener{
        void onItemClick(View view, int position);
    }
    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener){
        this.mOnItemClickLitener = mOnItemClickLitener;
    }
    public void setContext(Context context) {
        this.context = context;
    }

    public void setId(int[] id) {
        this.id = id;
    }

    public void setName(String[] name) {
        this.name = name;
    }

    public void setContain(int[] contain) {
        this.contain = contain;
    }

    public void setTools(String[] tools) {
        this.tools = tools;
    }

    public void setPlace(String[] place) {
        this.place = place;
    }
    public void setNowStatus(int[] nowStatus) {
        this.nowStatus = nowStatus;
    }
    public MeetingRoomInfoAdapter(Context context) {
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder viewHolder=new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.meetingroom_info, parent,false));

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
//        TextView textView = holder.textView;
//        textView.setText("-----"+position);
        TextView room_name_TV=holder.room_name;
        TextView number_TV=holder.number;
        TextView tool_TV=holder.tool;
        TextView address_TV=holder.address;
        TextView status_TV=holder.status;
        room_name_TV.setText(name[position]);
        number_TV.setText("可容纳人数："+contain[position]);
        tool_TV.setText("拥有器材："+tools[position]);
        address_TV.setText("地址："+place[position]);
        if (nowStatus[position]==0){
            status_TV.setText("状态：未使用");
        }else {
            status_TV.setText("状态：使用中");
        }
        if (mOnItemClickLitener != null) {
            holder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnItemClickLitener.onItemClick(view, position);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return id.length;
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        LinearLayout root;
        TextView room_name;
        TextView number;
        TextView tool;
        TextView address;
        TextView status;
        public MyViewHolder(View itemView) {
            super(itemView);
//            textView = (TextView) itemView.findViewById(R.id.tv_);
            root=itemView.findViewById(R.id.root);
            room_name=itemView.findViewById(R.id.room_name);
            number=itemView.findViewById(R.id.number);
            tool=itemView.findViewById(R.id.tool);
            address=itemView.findViewById(R.id.address);
            status=itemView.findViewById(R.id.status);
        }

    }
}
