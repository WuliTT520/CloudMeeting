package com.zhihui.imeeting.cloudmeeting.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.zhihui.imeeting.cloudmeeting.R;
import com.zhihui.imeeting.cloudmeeting.adapter.MemberListAdapter;
import com.zhihui.imeeting.cloudmeeting.adapter.OutsideListAdapter;
import com.zhihui.imeeting.cloudmeeting.helper.FullyLinearLayoutManager;
import com.zhihui.imeeting.cloudmeeting.helper.Userinfo;

public class JoinMemberListActivity extends Activity {
    private static final String TAG="JoinMemberListActivity";

    private int[] joinPeopleId;
    private int[] outsideJoinPersonsId;
    private String[] outsideJoinPersonsName;
    private String[] outsideJoinPersonsPhone;
    private String[] joinPeopleName;
    private ImageView back;
    private RecyclerView join;
    private RecyclerView outside;
    private FullyLinearLayoutManager mLayoutManager = new FullyLinearLayoutManager(this);
    private FullyLinearLayoutManager mLayoutManager2 = new FullyLinearLayoutManager(this);
    private Userinfo userinfo=new Userinfo(JoinMemberListActivity.this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_member_list);
        init();
        setListener();
    }
    public void init(){
        back=findViewById(R.id.back);
        join=findViewById(R.id.join);
        outside=findViewById(R.id.outside);
        Intent intent=getIntent();
        joinPeopleId=intent.getIntArrayExtra("joinPeopleId");
        outsideJoinPersonsId=intent.getIntArrayExtra("outsideJoinPersonsId");
        outsideJoinPersonsName=intent.getStringArrayExtra("outsideJoinPersonsName");
        outsideJoinPersonsPhone=intent.getStringArrayExtra("outsideJoinPersonsPhone");
        joinPeopleName=new String[joinPeopleId.length];
        for (int i=0;i<joinPeopleId.length;i++){
            joinPeopleName[i]=userinfo.searchById(joinPeopleId[i]);
        }

        join.setLayoutManager(mLayoutManager);
        final MemberListAdapter memberListAdapter=new MemberListAdapter(JoinMemberListActivity.this);
        memberListAdapter.setJoinPeopleName(joinPeopleName);
        join.setAdapter(memberListAdapter);
        outside.setLayoutManager(mLayoutManager2);
        final OutsideListAdapter outsideListAdapter=new OutsideListAdapter(JoinMemberListActivity.this);
        outsideListAdapter.setIds(outsideJoinPersonsId);
        outsideListAdapter.setNames(outsideJoinPersonsName);
        outsideListAdapter.setPhones(outsideJoinPersonsPhone);
        outside.setAdapter(outsideListAdapter);

    }
    public void setListener(){

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
