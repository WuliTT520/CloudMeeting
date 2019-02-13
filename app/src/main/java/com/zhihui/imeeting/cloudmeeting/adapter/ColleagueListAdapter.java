package com.zhihui.imeeting.cloudmeeting.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.zhihui.imeeting.cloudmeeting.R;
import com.zhihui.imeeting.cloudmeeting.activity.ColleagueInfoActivity;

import java.util.List;

public class ColleagueListAdapter extends BaseExpandableListAdapter {
    private Context mcontext;
    public String[] groupString ;
    public List[] childString;
    private List[] childid;

    public Context getMcontext() {
        return mcontext;
    }

    public void setMcontext(Context mcontext) {
        this.mcontext = mcontext;
    }

    public String[] getGroupString() {
        return groupString;
    }

    public void setGroupString(String[] groupString) {
        this.groupString = groupString;
    }

    public List[] getChildString() {
        return childString;
    }

    public void setChildString(List[] childString) {
        this.childString = childString;
    }

    public List[] getChildid() {
        return childid;
    }

    public void setChildid(List[] childid) {
        this.childid = childid;
    }

    @Override
    public int getGroupCount() {
        return groupString.length;
    }

    @Override
    public int getChildrenCount(int i) {
        return childString[i].size();
    }

    @Override
    public Object getGroup(int i) {
        return groupString[i];
    }

    @Override
    public Object getChild(int i, int i1) {
        return childString[i].get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        GroupViewHolder groupViewHolder;
        if (view == null){
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.colleague_group_item,viewGroup,false);
            groupViewHolder = new GroupViewHolder();
            groupViewHolder.tvTitle = (TextView)view.findViewById(R.id.group_name);
            groupViewHolder.num = (TextView)view.findViewById(R.id.num);
//            groupViewHolder.tvNum=(TextView)convertView.findViewById(R.id.group_num);
            view.setTag(groupViewHolder);
        }else {
            groupViewHolder = (GroupViewHolder)view.getTag();
        }
//        for(int k=0;k<groupString.length;k++){
//            Log.w("数据",groupString[k]);
//        }
        groupViewHolder.tvTitle.setText(groupString[i]);
        groupViewHolder.num.setText(childid[i].size()+"");
        return view;
    }

    @Override
    public View getChildView(final int i,final int i1, boolean b, View view, ViewGroup viewGroup) {
        ChildViewHolder childViewHolder;
        if (view == null){
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.colleague_child_item,viewGroup,false);
            childViewHolder = new ChildViewHolder();
            childViewHolder.tvTitle = (TextView)view.findViewById(R.id.people_name);

            view.setTag(childViewHolder);
        }else {
            childViewHolder = (ChildViewHolder)view.getTag();
        }

        childViewHolder.tvTitle.setText(childString[i].get(i1).toString());
        childViewHolder.tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Log.w("点击的人员ID：",childid[i].get(i1).toString());
                Intent intent=new Intent(mcontext,ColleagueInfoActivity.class);
                intent.putExtra("userId",Integer.parseInt(childid[i].get(i1).toString()));
                mcontext.startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }
    static class GroupViewHolder {
        TextView tvTitle;
        TextView num;
    }

    static class ChildViewHolder {
        TextView tvTitle;
    }
}
