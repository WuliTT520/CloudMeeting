package com.zhihui.imeeting.cloudmeeting.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.arcsoft.face.ErrorInfo;
import com.arcsoft.face.FaceEngine;
import com.zhihui.imeeting.cloudmeeting.R;
import com.zhihui.imeeting.cloudmeeting.common.Constants;

public class WelcomeActivity extends AppCompatActivity {

    private static final String TAG = "WelcomeActivity";
    SharedPreferences sp;
    /**
     * 所需权限
     */
    private static final int ACTION_REQUEST_PERMISSIONS = 0x001;
    private static final String[] NEEDED_PERMISSIONS = new String[]{
            Manifest.permission.READ_PHONE_STATE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        sp=this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        //激活引擎
//        activeEngine(null);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(sp.getBoolean("isLogin",false)){
                    Intent intent=new Intent(WelcomeActivity.this,MainActivity.class);
                    startActivity(intent);
                    WelcomeActivity.this.finish();
                }else{
                    Intent intent=new Intent(WelcomeActivity.this,LoginActivity.class);
                    startActivity(intent);
                    WelcomeActivity.this.finish();
                }
            }
        },1000*1);

    }


    /**
     * 激活引擎
     */
    void activeEngine(View view) {
        if (!checkPermissions(NEEDED_PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, NEEDED_PERMISSIONS, ACTION_REQUEST_PERMISSIONS);
        }
        FaceEngine faceEngine = new FaceEngine();
        int errorCode = faceEngine.active(this, Constants.ArcFace_APP_ID, Constants.ArcFace_SDK_KEY);
        if(errorCode == ErrorInfo.MOK) {
            Log.i(TAG, "SDK激活成功");
        } else if(errorCode == ErrorInfo.MERR_ASF_ALREADY_ACTIVATED) {
            Log.i(TAG, "SDK已激活");
        } else {
            Log.i(TAG, "SDK激活失败, errorCode: "+errorCode);
        }
    }

    /**
     * 检查权限是否获取
     * @param neededPermissions
     * @return
     */
    private boolean checkPermissions(String[] neededPermissions) {
        if (neededPermissions == null || neededPermissions.length == 0) {
            return true;
        }
        boolean allGranted = true;
        for (String neededPermission : neededPermissions) {
            allGranted &= ContextCompat.checkSelfPermission(this, neededPermission) == PackageManager.PERMISSION_GRANTED;
        }
        return allGranted;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ACTION_REQUEST_PERMISSIONS) {
            boolean isAllGranted = true;
            for (int grantResult : grantResults) {
                isAllGranted &= (grantResult == PackageManager.PERMISSION_GRANTED);
            }
            if (isAllGranted) {
                activeEngine(null);
            } else {
                Log.i(TAG, "未授权权限");
            }
        }
    }

}