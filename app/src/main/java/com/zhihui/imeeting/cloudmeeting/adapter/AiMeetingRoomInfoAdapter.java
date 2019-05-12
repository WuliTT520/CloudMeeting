package com.zhihui.imeeting.cloudmeeting.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhihui.imeeting.cloudmeeting.R;

public class AiMeetingRoomInfoAdapter extends RecyclerView.Adapter<AiMeetingRoomInfoAdapter.MyViewHolder> {
    private Context context;
    private int meetRoomId[];
    private String meetRoomName[];
    private String similar[];
    private int contain[];
    private String num[];
    private String equips[];
    private MeetingRoomInfoAdapter.OnItemClickLitener mOnItemClickLitener;

    public AiMeetingRoomInfoAdapter(Context context) {
        this.context = context;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        AiMeetingRoomInfoAdapter.MyViewHolder viewHolder=new AiMeetingRoomInfoAdapter.MyViewHolder(LayoutInflater.from(context).inflate(R.layout.meetingroom_info, viewGroup,false));
        Log.w("AiMeeting","4564564564654564654");
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder,final int i) {
        TextView room_name_TV=myViewHolder.room_name;
        TextView number_TV=myViewHolder.number;
        TextView tool_TV=myViewHolder.tool;
        TextView address_TV=myViewHolder.address;
//        TextView status_TV=myViewHolder.status;
        room_name_TV.setText(meetRoomName[i]);
        number_TV.setText("可容纳人数："+contain[i]);
        tool_TV.setText("拥有器材："+equips[i]);
        address_TV.setText("地址："+num[i]);
        if (mOnItemClickLitener != null) {
            myViewHolder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnItemClickLitener.onItemClick(view, i);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return meetRoomId.length;
    }
    public interface OnItemClickLitener{
        void onItemClick(View view, int position);
    }
    public void setOnItemClickLitener(MeetingRoomInfoAdapter.OnItemClickLitener mOnItemClickLitener){
        this.mOnItemClickLitener = mOnItemClickLitener;
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

    public int[] getMeetRoomId() {
        return meetRoomId;
    }

    public void setMeetRoomId(int[] meetRoomId) {
        this.meetRoomId = meetRoomId;
    }

    public String[] getMeetRoomName() {
        return meetRoomName;
    }

    public void setMeetRoomName(String[] meetRoomName) {
        this.meetRoomName = meetRoomName;
    }

    public String[] getSimilar() {
        return similar;
    }

    public void setSimilar(String[] similar) {
        this.similar = similar;
    }

    public int[] getContain() {
        return contain;
    }

    public void setContain(int[] contain) {
        this.contain = contain;
    }

    public String[] getNum() {
        return num;
    }

    public void setNum(String[] num) {
        this.num = num;
    }

    public String[] getEquips() {
        return equips;
    }

    public void setEquips(String[] equips) {
        this.equips = equips;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public MeetingRoomInfoAdapter.OnItemClickLitener getmOnItemClickLitener() {
        return mOnItemClickLitener;
    }

    public void setmOnItemClickLitener(MeetingRoomInfoAdapter.OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }
}
