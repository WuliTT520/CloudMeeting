package com.zhihui.imeeting.cloudmeeting.activity;

import android.Manifest;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.arcsoft.face.ErrorInfo;
import com.arcsoft.face.FaceEngine;
import com.zhihui.imeeting.cloudmeeting.R;
import com.zhihui.imeeting.cloudmeeting.common.Constants;

public class MainActivity extends AppCompatActivity {
    FrameLayout show;
    LinearLayout home,schedule,add,news,mine;
    ImageView home_pic,schedule_pic,add_pic,news_pic,mine_pic;
    TextView home_text,schedule_text,add_text,news_text,mine_text;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        setListener();
    }
    public void init(){
        show=findViewById(R.id.show);
        home=findViewById(R.id.home);
        schedule=findViewById(R.id.schedule);
        add=findViewById(R.id.add);
        news=findViewById(R.id.news);
        mine=findViewById(R.id.mine);

        home_pic=findViewById(R.id.home_pic);
        schedule_pic=findViewById(R.id.schedule_pic);
        add_pic=findViewById(R.id.add_pic);
        news_pic=findViewById(R.id.news_pic);
        mine_pic=findViewById(R.id.mine_pic);

        home_text=findViewById(R.id.home_text);
        schedule_text=findViewById(R.id.schedule_text);
        add_text=findViewById(R.id.add_text);
        news_text=findViewById(R.id.news_text);
        mine_text=findViewById(R.id.mine_text);

        home_pic.setImageResource(R.drawable.main_home_ac);
        home_text.setTextColor(getColor(R.color.colorPrimary));
        FragmentManager manager=getFragmentManager();
        final FragmentTransaction transaction=manager.beginTransaction();
        HomeFragment homeFragment=new HomeFragment();
        transaction.replace(R.id.show,homeFragment);
        transaction.commit();

    }
    public void setListener(){

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeColor(1);
                FragmentManager manager=getFragmentManager();
                final FragmentTransaction transaction=manager.beginTransaction();
                HomeFragment homeFragment=new HomeFragment();
                transaction.replace(R.id.show,homeFragment);
                transaction.commit();
            }
        });

        schedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeColor(2);
                FragmentManager manager=getFragmentManager();
                final FragmentTransaction transaction=manager.beginTransaction();
                ScheduleFragment scheduleFragment=new ScheduleFragment();
                transaction.replace(R.id.show,scheduleFragment);
                transaction.commit();
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeColor(3);
                FragmentManager manager=getFragmentManager();
                final FragmentTransaction transaction=manager.beginTransaction();
                AddFragment addFragment=new AddFragment();
                transaction.replace(R.id.show,addFragment);
                transaction.commit();
            }
        });

        news.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeColor(4);
                FragmentManager manager=getFragmentManager();
                final FragmentTransaction transaction=manager.beginTransaction();
                NewsFragment newsFragment=new NewsFragment();
                transaction.replace(R.id.show,newsFragment);
                transaction.commit();
            }
        });

        mine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeColor(5);
                FragmentManager manager=getFragmentManager();
                final FragmentTransaction transaction=manager.beginTransaction();
                MineFragment mineFragment=new MineFragment();
                transaction.replace(R.id.show,mineFragment);
                transaction.commit();
            }
        });
    }

    public void changeColor(int i){
        if (i==1){
            home_pic.setImageResource(R.drawable.main_home_ac);
            home_text.setTextColor(getColor(R.color.colorPrimary));
        }else{
            home_pic.setImageResource(R.drawable.main_home_pt);
            home_text.setTextColor(getColor(R.color.text));
        }
        if (i==2){
            schedule_pic.setImageResource(R.drawable.main_schedule_ac);
            schedule_text.setTextColor(getColor(R.color.colorPrimary));
        }else{
            schedule_pic.setImageResource(R.drawable.main_schedule_pt);
            schedule_text.setTextColor(getColor(R.color.text));
        }

        if (i==4){
            news_pic.setImageResource(R.drawable.main_news_ac);
            news_text.setTextColor(getColor(R.color.colorPrimary));
        }else{
            news_pic.setImageResource(R.drawable.main_news_pt);
            news_text.setTextColor(getColor(R.color.text));
        }
        if (i==5){
            mine_pic.setImageResource(R.drawable.main_mine_ac);
            mine_text.setTextColor(getColor(R.color.colorPrimary));
        }else{
            mine_pic.setImageResource(R.drawable.main_mine_pt);
            mine_text.setTextColor(getColor(R.color.text));
        }
    }

}