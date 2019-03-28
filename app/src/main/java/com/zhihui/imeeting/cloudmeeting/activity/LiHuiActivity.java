package com.zhihui.imeeting.cloudmeeting.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.zhihui.imeeting.cloudmeeting.R;

public class LiHuiActivity extends Activity {
    ImageView back;
    ImageView add;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_li_hui);
        back=findViewById(R.id.back);
        add=findViewById(R.id.add);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(LiHuiActivity.this,LiHuiAddActivity.class);
                startActivity(intent);
            }
        });
    }

}
