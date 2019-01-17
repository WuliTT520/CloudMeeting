package com.zhihui.imeeting.cloudmeeting.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.zhihui.imeeting.cloudmeeting.R;

import java.util.HashMap;
import java.util.List;

public class GroupListViewAdapter extends BaseExpandableListAdapter {
    private Context mcontext;
    public String[] groupString ;
    public List[] childString;
    HashMap<String, Boolean> state = new HashMap<String, Boolean>();

    public HashMap<String, Boolean> getState() {
        return state;
    }

    public void setState(HashMap<String, Boolean> state) {
        this.state = state;
    }
    //    public int[] groupNum;

//    public int[] getGroupNum() {
//        return groupNum;
//    }
//
//    public void setGroupNum(int[] groupNum) {
//        this.groupNum = groupNum;
//    }

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

    @Override
    // 获取分组的个数
    public int getGroupCount() {
        return groupString.length;
    }

    //获取指定分组中的子选项的个数
    @Override
    public int getChildrenCount(int groupPosition) {
        return childString[groupPosition].size();
    }

    //        获取指定的分组数据
    @Override
    public Object getGroup(int groupPosition) {
        return groupString[groupPosition];
    }

    //获取指定分组中的指定子选项数据
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return childString[groupPosition].get(childPosition);
    }

    //获取指定分组的ID, 这个ID必须是唯一的
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    //获取子选项的ID, 这个ID必须是唯一的
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    //分组和子选项是否持有稳定的ID, 就是说底层数据的改变会不会影响到它们
    @Override
    public boolean hasStableIds() {
        return true;
    }


    /**
     *
     * 获取显示指定组的视图对象
     *
     * @param groupPosition 组位置
     * @param isExpanded 该组是展开状态还是伸缩状态
     * @param convertView 重用已有的视图对象
     * @param parent 返回的视图对象始终依附于的视图组
     */
// 获取显示指定分组的视图
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupViewHolder groupViewHolder;
        if (convertView == null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_partent_item,parent,false);
            groupViewHolder = new GroupViewHolder();
            groupViewHolder.tvTitle = (TextView)convertView.findViewById(R.id.group_name);
//            groupViewHolder.tvNum=(TextView)convertView.findViewById(R.id.group_num);
            convertView.setTag(groupViewHolder);
        }else {
            groupViewHolder = (GroupViewHolder)convertView.getTag();
        }
        groupViewHolder.tvTitle.setText(groupString[groupPosition]);
//        groupViewHolder.tvNum.setText(groupNum[groupPosition]+"");
        return convertView;
    }
    /**
     *
     * 获取一个视图对象，显示指定组中的指定子元素数据。
     *
     * @param groupPosition 组位置
     * @param childPosition 子元素位置
     * @param isLastChild 子元素是否处于组中的最后一个
     * @param convertView 重用已有的视图(View)对象
     * @param parent 返回的视图(View)对象始终依附于的视图组
     * @return
     * @see android.widget.ExpandableListAdapter#getChildView(int, int, boolean, android.view.View,
     *      android.view.ViewGroup)
     */

    //取得显示给定分组给定子位置的数据用的视图
    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildViewHolder childViewHolder;
        if (convertView==null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.gruop_child_item,parent,false);
            childViewHolder = new ChildViewHolder();
            childViewHolder.tvTitle = (TextView)convertView.findViewById(R.id.people_name);
            childViewHolder.childBox=(CheckBox)convertView.findViewById(R.id.childbox);
            childViewHolder.childBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b){
                        state.put(groupPosition+","+childPosition,b);
                    }else {
                        state.remove(groupPosition+","+childPosition);
                    }
                }
            });

            convertView.setTag(childViewHolder);

        }else {
            childViewHolder = (ChildViewHolder) convertView.getTag();
        }
        childViewHolder.childBox.setChecked((state.get(groupPosition+","+childPosition) == null ? false : true));
        childViewHolder.tvTitle.setText(childString[groupPosition].get(childPosition).toString());
        return convertView;
    }

    //指定位置上的子元素是否可选中
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    static class GroupViewHolder {
        TextView tvTitle;

    }

    static class ChildViewHolder {
        TextView tvTitle;
        CheckBox childBox;
    }
}
