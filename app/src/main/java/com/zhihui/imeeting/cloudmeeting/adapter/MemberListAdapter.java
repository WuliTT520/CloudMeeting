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

public class MemberListAdapter extends RecyclerView.Adapter<MemberListAdapter.MyViewHolder> {
    private String[] joinPeopleName;
    private Context context;
    public MemberListAdapter(Context context) {
        this.context = context;
    }

    public String[] getJoinPeopleName() {
        return joinPeopleName;
    }

    public void setJoinPeopleName(String[] joinPeopleName) {
        this.joinPeopleName = joinPeopleName;
    }

    @NonNull
    @Override
    public MemberListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        MemberListAdapter.MyViewHolder viewHolder=new MemberListAdapter.MyViewHolder(LayoutInflater.from(context).inflate(R.layout.member_item, viewGroup,false));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MemberListAdapter.MyViewHolder myViewHolder, int i) {
        TextView name_TV=myViewHolder.name;
        name_TV.setText(joinPeopleName[i]);
    }

    @Override
    public int getItemCount() {
        return joinPeopleName.length;
    }
    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView name;
        public MyViewHolder(View itemView) {
            super(itemView);
//            textView = (TextView) itemView.findViewById(R.id.tv_);
           name=itemView.findViewById(R.id.member_name);

        }

    }
}
