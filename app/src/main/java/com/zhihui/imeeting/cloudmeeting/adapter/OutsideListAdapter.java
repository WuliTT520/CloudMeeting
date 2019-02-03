package com.zhihui.imeeting.cloudmeeting.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zhihui.imeeting.cloudmeeting.R;

public class OutsideListAdapter extends RecyclerView.Adapter<OutsideListAdapter.MyViewHolder>{
    private Context context;
    private int[] ids;
    private String[] names;
    private String[] phones;

    public int[] getIds() {
        return ids;
    }

    public void setIds(int[] ids) {
        this.ids = ids;
    }

    public String[] getNames() {
        return names;
    }

    public void setNames(String[] names) {
        this.names = names;
    }

    public String[] getPhones() {
        return phones;
    }

    public void setPhones(String[] phones) {
        this.phones = phones;
    }
    public OutsideListAdapter(Context context) {
        this.context = context;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        OutsideListAdapter.MyViewHolder viewHolder=new OutsideListAdapter.MyViewHolder(LayoutInflater.from(context).inflate(R.layout.outside_item, viewGroup,false));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        TextView name_TV=myViewHolder.name;
        TextView phone_TV=myViewHolder.phone;
        name_TV.setText(names[i]);
        phone_TV.setText(phones[i]);
    }

    @Override
    public int getItemCount() {
        return ids.length;
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView name;
        TextView phone;
        public MyViewHolder(View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.name);
            phone=itemView.findViewById(R.id.phone);
        }

    }
}
