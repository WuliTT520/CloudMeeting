package com.zhihui.imeeting.cloudmeeting.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.arcsoft.face.ErrorInfo;
import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.VersionInfo;
import com.zhihui.imeeting.cloudmeeting.R;

public class FaceIDActivity extends AppCompatActivity {

    private static final String TAG = "MineFragment";
    FaceEngine faceEngine = null;
    static int errorCode = -1;

    /**
     * 所需的所有权限信息
     */
    private static final int ACTION_REQUEST_PERMISSIONS = 0x001;
    private static final String[] NEEDED_PERMISSIONS = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.READ_PHONE_STATE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_id);

    }

    /**
     * 初始化引擎
     */
    private void initEngine() {
        if(!checkPermissions(NEEDED_PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, NEEDED_PERMISSIONS, ACTION_REQUEST_PERMISSIONS);
        }
        faceEngine = new FaceEngine();
        /**
         * 初始化引擎
         * @param context上下文对象
         * @param 视频模式检测
         * @param 人脸检测方向为多方向检测
         * @param 人脸相对于所在图片的长边的占比 [2, 16]
         * @param 引擎最多能检测出的人脸数 [1, 50]
         * @param 引擎功能:人脸检测、人脸识别、年龄检测、人脸三维角度检测、性别检测、活体检测
         */
        int errorCode = faceEngine.init(this.getApplicationContext(), FaceEngine.ASF_DETECT_MODE_VIDEO,
                FaceEngine.ASF_OP_0_HIGHER_EXT,
                16, 1,
                FaceEngine.ASF_FACE_DETECT | FaceEngine.ASF_AGE | FaceEngine.ASF_FACE3DANGLE | FaceEngine.ASF_GENDER | FaceEngine.ASF_LIVENESS);
        //获取版本信息
        VersionInfo versionInfo = new VersionInfo();
        faceEngine.getVersion(versionInfo);

        //deBug信息
        Log.i(TAG, "初始化引擎成功!  errorCode: " + errorCode + "  引擎版本号:" + versionInfo);
        if (errorCode != ErrorInfo.MOK) {
            Log.i(TAG, "初始化引擎失败");
        }
    }

    /**
     * 销毁引擎
     */
    private void unInitEngine() {
        if (errorCode == ErrorInfo.MOK) {
            errorCode = faceEngine.unInit();
            Log.i(TAG, "销毁引擎!  errorCode: " + errorCode);
        }
    }

    /**
     * 检查是否授权
     * @param neededPermissions
     * @return
     */
    private boolean checkPermissions(String[] neededPermissions) {
        if (neededPermissions == null || neededPermissions.length == 0) {
            return true;
        }
        boolean allGranted = true;
        for (String neededPermission : neededPermissions) {
            allGranted &= ContextCompat.checkSelfPermission(this.getApplicationContext(), neededPermission) == PackageManager.PERMISSION_GRANTED;
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
                initEngine();
            } else {
                Log.i(TAG, "未授权权限");
            }
        }
    }
    
}
