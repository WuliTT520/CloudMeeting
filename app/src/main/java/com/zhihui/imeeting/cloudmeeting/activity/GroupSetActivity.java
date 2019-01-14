package com.zhihui.imeeting.cloudmeeting.activity;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.zhihui.imeeting.cloudmeeting.R;

public class GroupSetActivity extends Activity {
    private LinearLayout addGroup;
    private ListView list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_set);
        init();
        setListener();
    }
    public void init(){
        addGroup=findViewById(R.id.addGroup);
        list=findViewById(R.id.group_list);
    }
    public void setListener(){

    }

}
