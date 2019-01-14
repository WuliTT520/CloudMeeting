package com.zhihui.imeeting.cloudmeeting.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.hardware.Camera;
import android.media.FaceDetector;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.TextureView;
import android.widget.Button;
import android.widget.ImageView;

import com.arcsoft.face.AgeInfo;
import com.arcsoft.face.ErrorInfo;
import com.arcsoft.face.Face3DAngle;
import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.FaceFeature;
import com.arcsoft.face.FaceInfo;
import com.arcsoft.face.GenderInfo;
import com.arcsoft.face.LivenessInfo;
import com.arcsoft.face.VersionInfo;
import com.zhihui.imeeting.cloudmeeting.R;
import com.zhihui.imeeting.cloudmeeting.common.Constants;
import com.zhihui.imeeting.cloudmeeting.util.camera.CameraHelper;
import com.zhihui.imeeting.cloudmeeting.util.camera.CameraListener;
import com.zhihui.imeeting.cloudmeeting.widget.FaceRectView;

import java.util.ArrayList;
import java.util.List;

public class FaceIDActivity extends AppCompatActivity {

    public Button ok;
    public ImageView fanhui;
    public TextureView face;

    private static final String TAG = "FaceIDActivity";
    FaceEngine faceEngine = null;
    static int errorCode = -1;

    private CameraHelper cameraHelper;
    private Camera.Size previewSize;
    //private int processMask = FaceEngine.ASF_AGE | FaceEngine.ASF_FACE3DANGLE | FaceEngine.ASF_GENDER | FaceEngine.ASF_LIVENESS;
    private int processMask = FaceEngine.ASF_FACE_RECOGNITION | FaceEngine.ASF_FACE_DETECT | FaceEngine.ASF_AGE | FaceEngine.ASF_GENDER | FaceEngine.ASF_FACE3DANGLE | FaceEngine.ASF_LIVENESS;
    private Integer cameraID = Camera.CameraInfo.CAMERA_FACING_FRONT;
    private TextureView previewView ;//相机预览显示控件
    private FaceRectView faceRectView;//人脸侦测帮助框

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

        previewView=findViewById(R.id.face);
        //faceRectView = findViewById(R.id.face_rect_view);
        if(!checkPermissions(NEEDED_PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, NEEDED_PERMISSIONS, ACTION_REQUEST_PERMISSIONS);
        } else {
            initEngine();
            initCamera();
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
                initCamera();
            } else {
                Log.i(TAG, "未授权权限");
            }
        }
    }

    /**
     * 初始化引擎
     */
    private void initEngine() {
        faceEngine = new FaceEngine();
        //激活引擎
        int activeCode = faceEngine.active(this, Constants.ArcFace_APP_ID, Constants.ArcFace_SDK_KEY);

        /**
         * faceEngine.init()初始化引擎
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
                processMask);
        //获取版本信息
        VersionInfo versionInfo = new VersionInfo();
        faceEngine.getVersion(versionInfo);
        if (errorCode != ErrorInfo.MOK) {
            Log.i(TAG, "初始化引擎失败");
        } else {
            //deBug信息
            Log.i(TAG, "初始化引擎成功!  errorCode: " + errorCode + "  引擎版本号:" + versionInfo);
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



    private byte[] faceFeatureData; //存储人脸特征值数据

    private void initCamera() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        CameraListener cameraListener = new CameraListener() {
            @Override
            public void onCameraOpened(Camera camera, int cameraId, int displayOrientation, boolean isMirror) {
                Log.i(TAG, "onCameraOpened: " + cameraId + "  " + displayOrientation + " " + isMirror);
                previewSize = camera.getParameters().getPreviewSize();
                /*drawHelper = new DrawHelper(previewSize.width, previewSize.height, previewView.getWidth(), previewView.getHeight(), displayOrientation
                        , cameraId, isMirror);*/
            }


            @Override
            public void onPreview(byte[] nv21, Camera camera) {
                /*
                if (faceRectView != null) {
                    faceRectView.clearFaceInfo();
                }
                */

                List<FaceInfo> faceInfoList = new ArrayList<>();
                int code = faceEngine.detectFaces(nv21, previewSize.width, previewSize.height, FaceEngine.CP_PAF_NV21, faceInfoList);
                if (code == ErrorInfo.MOK && faceInfoList.size() > 0) {
                    //code = faceEngine.process(nv21, previewSize.width, previewSize.height, FaceEngine.CP_PAF_NV21, faceInfoList, processMask);
                    //Log.i("code", "code: " + code);
                    //mainFeature = new FaceFeature();
                    Log.i("faceInfoList.get(0)", "faceInfoList.get(0): "+faceInfoList.get(0).toString());

                    FaceFeature faceFeatures = new FaceFeature();
                    int extractFaceFeatureCodes;
                    //从图片解析出人脸特征数据
                    long frStartTime = System.currentTimeMillis();
                    extractFaceFeatureCodes = faceEngine.extractFaceFeature(nv21, previewSize.width, previewSize.height, FaceEngine.CP_PAF_NV21, faceInfoList.get(0), faceFeatures);
                    Log.i("特征值提取: ", "error:"+extractFaceFeatureCodes);
                    if(extractFaceFeatureCodes == ErrorInfo.MOK) {
                        faceFeatureData = faceFeatures.getFeatureData();
                        return;
                    }

                    //int res = faceEngine.extractFaceFeature(nv21, previewSize.width, previewSize.height, FaceEngine.CP_PAF_NV21, faceInfoList.get(0), mainFeature);
                    //Log.i("res", "res: " + extractFaceFeatureCodes[0]);
                    //if (code != ErrorInfo.MOK) {
                    //    return;
                    //}
                }else {
                    return;
                }

                /*
                Log.i("人脸侦测", "人脸侦测成功");
                List<AgeInfo> ageInfoList = new ArrayList<>();
                List<GenderInfo> genderInfoList = new ArrayList<>();
                List<Face3DAngle> face3DAngleList = new ArrayList<>();
                List<LivenessInfo> faceLivenessInfoList = new ArrayList<>();


                int ageCode = faceEngine.getAge(ageInfoList);
                int genderCode = faceEngine.getGender(genderInfoList);
                int face3DAngleCode = faceEngine.getFace3DAngle(face3DAngleList);
                int livenessCode = faceEngine.getLiveness(faceLivenessInfoList);
                Log.i("ageCode: ", "ageCode: "+ageCode);
                Log.i("年龄: ", "年龄: "+ageInfoList.get(0).getAge());


                //有其中一个的错误码不为0，return
                if ((ageCode | genderCode | face3DAngleCode | livenessCode) != ErrorInfo.MOK) {
                    return;
                }
                */
                /*if (faceRectView != null && drawHelper != null) {
                    List<DrawInfo> drawInfoList = new ArrayList<>();
                    for (int i = 0; i < faceInfoList.size(); i++) {
                        drawInfoList.add(new DrawInfo(faceInfoList.get(i).getRect(), genderInfoList.get(i).getGender(), ageInfoList.get(i).getAge(), faceLivenessInfoList.get(i).getLiveness(), null));
                    }
                    drawHelper.draw(faceRectView, drawInfoList);
                }*/
            }

            @Override
            public void onCameraClosed() {
                Log.i(TAG, "onCameraClosed: ");
            }

            @Override
            public void onCameraError(Exception e) {
                Log.i(TAG, "onCameraError: " + e.getMessage());
            }

            @Override
            public void onCameraConfigurationChanged(int cameraID, int displayOrientation) {
                /*if (drawHelper != null) {
                    drawHelper.setCameraDisplayOrientation(displayOrientation);
                }*/
                Log.i(TAG, "onCameraConfigurationChanged: " + cameraID + "  " + displayOrientation);
            }
        };
        cameraHelper = new CameraHelper.Builder()
                .metrics(metrics)
                .rotation(getWindowManager().getDefaultDisplay().getRotation())
                .specificCameraId(cameraID != null ? cameraID : Camera.CameraInfo.CAMERA_FACING_FRONT)
                .isMirror(false)
                .previewOn(previewView)
                .cameraListener(cameraListener)
                .build();
        cameraHelper.init();
    }

}
