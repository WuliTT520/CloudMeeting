package com.zhihui.imeeting.cloudmeeting.activity;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.arcsoft.face.ErrorInfo;
import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.VersionInfo;
import com.zhihui.imeeting.cloudmeeting.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MineFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MineFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public MineFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MineFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MineFragment newInstance(String param1, String param2) {
        MineFragment fragment = new MineFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initEngine();
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mine, container, false);
    }





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

    /**
     * 初始化引擎
     */
    private void initEngine() {
        if(!checkPermissions(NEEDED_PERMISSIONS)) {
            ActivityCompat.requestPermissions(this.getActivity(), NEEDED_PERMISSIONS, ACTION_REQUEST_PERMISSIONS);
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
        int errorCode = faceEngine.init(this.getContext(), FaceEngine.ASF_DETECT_MODE_VIDEO,
                                        FaceEngine.ASF_OP_0_HIGHER_EXT,
                       16, 1,
                          FaceEngine.ASF_FACE_DETECT | FaceEngine.ASF_AGE | FaceEngine.ASF_FACE3DANGLE | FaceEngine.ASF_GENDER | FaceEngine.ASF_LIVENESS);
        //获取版本信息
        VersionInfo versionInfo = new VersionInfo();
        faceEngine.getVersion(versionInfo);
        Log.i(TAG, "初始化引擎成功!  errorCode: " + errorCode + "  引擎版本号:" + versionInfo);
        if (errorCode != ErrorInfo.MOK) {
            Log.i(TAG, "初始化引擎失败");
        }
    }

    /**
     * 销毁引擎
     */
    private void unInitEngine() {
        if (errorCode == 0) {
            errorCode = faceEngine.unInit();
            Log.i(TAG, "销毁引擎!  errorCode: " + errorCode);
        }
    }

    private boolean checkPermissions(String[] neededPermissions) {
        if (neededPermissions == null || neededPermissions.length == 0) {
            return true;
        }
        boolean allGranted = true;
        for (String neededPermission : neededPermissions) {
            allGranted &= ContextCompat.checkSelfPermission(this.getContext(), neededPermission) == PackageManager.PERMISSION_GRANTED;
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
