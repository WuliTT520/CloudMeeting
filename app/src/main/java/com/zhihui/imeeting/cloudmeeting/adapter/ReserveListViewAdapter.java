package com.zhihui.imeeting.cloudmeeting.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhihui.imeeting.cloudmeeting.R;

public class ReserveListViewAdapter extends RecyclerView.Adapter<ReserveListViewAdapter.MyViewHolder> {
    private Context context;
    private String[] topic;
    private String[] status;
    private String[] meetDate;
    private String[] begin;
    private String[] over;

    public void setContext(Context context) {
        this.context = context;
    }

    public void setTopic(String[] topic) {
        this.topic = topic;
    }

    public void setStatus(String[] status) {
        this.status = status;
    }

    public void setMeetDate(String[] meetDate) {
        this.meetDate = meetDate;
    }

    public void setBegin(String[] begin) {
        this.begin = begin;
    }

    public void setOver(String[] over) {
        this.over = over;
    }

    public ReserveListViewAdapter(Context context) {
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.reserve_item, parent,false));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
//        TextView textView = holder.textView;
//        textView.setText("-----"+position);
        TextView statusTV=holder.status;
        TextView startTV=holder.start;
        TextView endTV=holder.end;
        TextView timeTV=holder.time;
        TextView topicTV=holder.topic;
        statusTV.setText(status[position]);
        startTV.setText(begin[position]);
        endTV.setText(over[position]);
        timeTV.setText(meetDate[position]);
        topicTV.setText(topic[position]);
        if (status[position].equals("预约失败")||status[position].equals("会议结束")
                ||status[position].equals("取消会议")||status[position].equals("调用失败")){
            holder.root.setBackgroundResource(R.drawable.gradient_grey);
        }
    }

    @Override
    public int getItemCount() {
        return topic.length;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        LinearLayout root;
        TextView start;
        TextView end;
        TextView time;
        TextView topic;
        TextView status;
        public MyViewHolder(View itemView) {
            super(itemView);
//            textView = (TextView) itemView.findViewById(R.id.tv_);
            start=itemView.findViewById(R.id.start);
            end=itemView.findViewById(R.id.end);
            time=itemView.findViewById(R.id.time);
            topic=itemView.findViewById(R.id.topic);
            status=itemView.findViewById(R.id.status);
            root=itemView.findViewById(R.id.root);
        }
    }
}
