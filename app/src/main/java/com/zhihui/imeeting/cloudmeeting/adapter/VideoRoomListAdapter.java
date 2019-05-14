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

public class VideoRoomListAdapter extends RecyclerView.Adapter<VideoRoomListAdapter.MyViewHolder>{
    private Context context;

    private int ids[];
    private String videoRoomNames[];
    private String createTimes[];
    private String names[];

    private VideoRoomListAdapter.OnItemClickLitener mOnItemClickLitener;
    public VideoRoomListAdapter(Context context) {
        this.context = context;
    }

    public OnItemClickLitener getmOnItemClickLitener() {
        return mOnItemClickLitener;
    }

    public void setmOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public int[] getIds() {
        return ids;
    }

    public void setIds(int[] ids) {
        this.ids = ids;
    }

    public String[] getVideoRoomNames() {
        return videoRoomNames;
    }

    public void setVideoRoomNames(String[] videoRoomNames) {
        this.videoRoomNames = videoRoomNames;
    }

    public String[] getCreateTimes() {
        return createTimes;
    }

    public void setCreateTimes(String[] createTimes) {
        this.createTimes = createTimes;
    }

    public String[] getNames() {
        return names;
    }

    public void setNames(String[] names) {
        this.names = names;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        VideoRoomListAdapter.MyViewHolder viewHolder=new VideoRoomListAdapter.MyViewHolder(LayoutInflater.from(context).inflate(R.layout.video_room_item, viewGroup,false));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int i) {

        LinearLayout root=myViewHolder.root;
        TextView meetingname=myViewHolder.meetingname;
        TextView time=myViewHolder.time;
        TextView peoplename=myViewHolder.peoplename;

        meetingname.setText(videoRoomNames[i]);
        time.setText(createTimes[i]);
        peoplename.setText(names[i]);

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
        return ids.length;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        LinearLayout root;
        TextView meetingname;
        TextView time;
        TextView peoplename;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            root=itemView.findViewById(R.id.root);
            meetingname=itemView.findViewById(R.id.meetingname);
            time=itemView.findViewById(R.id.time);
            peoplename=itemView.findViewById(R.id.peoplename);
        }
    }

    public interface OnItemClickLitener{
        void onItemClick(View view, int position);
    }
    public void setOnItemClickLitener(VideoRoomListAdapter.OnItemClickLitener mOnItemClickLitener){
        this.mOnItemClickLitener = mOnItemClickLitener;
    }
}
