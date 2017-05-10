package com.lzp.classroomassistant.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.FaceRequest;
import com.iflytek.cloud.RequestListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.lzp.classroomassistant.BaseActivity;
import com.lzp.classroomassistant.R;
import com.lzp.classroomassistant.data.FaceRecogni;
import com.lzp.classroomassistant.data.User;
import com.lzp.classroomassistant.mainpages.MainActivity;
import com.lzp.classroomassistant.util.Constant;
import com.lzp.classroomassistant.util.FaceUtil;
import com.lzp.classroomassistant.util.ToastUtil;
import com.lzp.classroomassistant.view.CameraPreview;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

public class CameraActivity extends BaseActivity implements Camera.AutoFocusCallback {

    @InjectView(R.id.camera_preview)
    FrameLayout preview ;
    @InjectView(R.id.button_capture)
    Button mCapBtn;
    private Camera mCamera;
    private CameraPreview mPreview;
    private int code;
    private static final String TAG = "CameraActivity";
    // FaceRequest对象，集成了人脸识别的各种功能
    private FaceRequest mFaceRequest;
    private Bitmap mImage = null;
    private byte[] mImageData = null;
    //进度对话框
    private ProgressDialog mProDialog;
    private boolean isClick =  false;

    @OnClick({R.id.button_capture})
    void onClick(View view){
        switch (view.getId()){
            case R.id.button_capture:
                if (!isClick){
                    mCamera.takePicture(null, null, mPicture);
                    isClick = true;
                } else {
                    mCamera.startPreview();
                    isClick = false;
                }

                break;
        }
    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_camera;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {

        initCamera();
        initProgressDialog();
        code = getIntent().getIntExtra(Constant.KEY,0);
        if (code == Constant.REQUEST_CAMERA_IMAGE){
            mCapBtn.setText("拍照设置头像");
        }else if (code == Constant.REQUEST_FACE_RECOGINTION ){
            mCapBtn.setText("人脸识别验证");
        }


    }


    private void setUserAvatar(File file){
        final BmobFile bmobFile = new BmobFile(file);
        bmobFile.uploadblock(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                if(e == null){
                    //bmobFile.getFileUrl()--返回的上传文件的完整地址
                    final User mCurrUser = BmobUser.getCurrentUser(User.class);
                    User user = new User();
                    user.setAvatar(bmobFile.getFileUrl());
                    user.update(mCurrUser.getObjectId(), new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null){
                                setMsc(mCurrUser.getObjectId());
                                ToastUtil.showToast("头像设置成功！");
                                Log.d(TAG, "设置成功！！！！");

                            } else {
                                Log.d(TAG,e.getMessage().toString());
                            }
                        }
                    });


                }else{
                    Log.i(TAG,"上传文件失败：" + e.getMessage());
                }
            }
            @Override
            public void onProgress(Integer value) {
                Log.d(TAG," 进度 " + value);
                if (value.intValue() == 100){
                    ToastUtil.showToast("上传文件成功!!" );
                }
            }
        });
    }

    private void initCamera(){
        // Create an instance of Camera
        mCamera = getCameraInstance();

        // Create our Preview view and set it as the content of our activity.
        if (checkCameraHardware(this)){
            mPreview = new CameraPreview(this, mCamera);
        } else {
            ToastUtil.showToast("权限不足！");
            finish();
        }
        preview.addView(mPreview);
    }

    /** A safe way to get an instance of the Camera object. */
    public Camera getCameraInstance(){
        Camera c = null;
        int cameras = Camera.getNumberOfCameras();
        try {
            if (cameras >= 2){
                c = Camera.open(1); // attempt to get a Camera instance
            } else {
                c = Camera.open();
            }
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    /** Check if this device has a camera */
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    @Override
    public void onAutoFocus(boolean success, Camera camera) {
        if (success){
            setCameraParam();
        }
    }
    private void setCameraParam(){
        Camera.Parameters parameters = mCamera.getParameters();
        List<Camera.Size> list = parameters.getSupportedPreviewSizes();
        parameters.setPictureSize(480,360);
        parameters.setPreviewSize(480,360);
        parameters.setJpegQuality(100);
        parameters.setPictureFormat(PixelFormat.JPEG);// 设置照片的格式
        parameters.setFlashMode(Camera.Parameters.FOCUS_MODE_AUTO);
        parameters.setSceneMode(Camera.Parameters.SCENE_MODE_AUTO);

    }
    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

             File pictureFile = new File(Environment.getExternalStorageDirectory(),
                     "picture" + System.currentTimeMillis()/1000 + ".jpg");
            if (pictureFile == null){
                Log.d(TAG, "Error creating media file, check storage permissions: " );
                return;
            }

            try {
                mImage = BitmapFactory.decodeByteArray(data,0,data.length);
                mImage = FaceUtil.rotateImage(90, mImage);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                mImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                mImageData = baos.toByteArray();

                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(mImageData);
                fos.close();
                if (code == Constant.REQUEST_CAMERA_IMAGE){
                    Log.d(TAG,"设置头像 ！！！");
                    setUserAvatar(pictureFile);
                } else if (code == Constant.REQUEST_FACE_RECOGINTION){
                    Log.d(TAG," 人脸识别验证！！！");
                    faceRecogintion(BmobUser.getCurrentUser(User.class).getObjectId());

                }
            } catch (FileNotFoundException e) {
                Log.d(TAG, "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d(TAG, "Error accessing file: " + e.getMessage());
            }
        }
    };

    private void initProgressDialog(){
        mProDialog = new ProgressDialog(this);
        mProDialog.setCancelable(true);
        mProDialog.setTitle("请稍后");

        mProDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                // cancel进度框时,取消正在进行的操作
                if (null != mFaceRequest) {
                    mFaceRequest.cancel();
                }
            }
        });

        mFaceRequest = new FaceRequest(this);
    }
    /**
     *  设置人脸识别
     */
    private void setMsc(String authid){
        mProDialog.setMessage("上传至人脸识别系统...");
        mProDialog.show();
        // 设置用户标识，格式为6-18个字符（由字母、数字、下划线组成，不得以数字开头，不能包含空格）。
        // 当不设置时，云端将使用用户设备的设备ID来标识终端用户。
        mFaceRequest.setParameter(SpeechConstant.AUTH_ID, authid);
        mFaceRequest.setParameter(SpeechConstant.WFR_SST, "reg");
        mFaceRequest.sendRequest(mImageData, mRequestListener);
    }

    private void faceRecogintion(String authid){
        mProDialog.setMessage("人脸识别验证中...");
        mProDialog.show();
        // 设置用户标识，格式为6-18个字符（由字母、数字、下划线组成，不得以数字开头，不能包含空格）。
        // 当不设置时，云端将使用用户设备的设备ID来标识终端用户。
        mFaceRequest.setParameter(SpeechConstant.AUTH_ID, authid);
        mFaceRequest.setParameter(SpeechConstant.WFR_SST, "verify");
        mFaceRequest.sendRequest(mImageData, mRequestListener);
    }

    private RequestListener mRequestListener = new RequestListener() {

        @Override
        public void onEvent(int eventType, Bundle params) {
        }

        @Override
        public void onBufferReceived(byte[] buffer) {
            if (null != mProDialog) {
                mProDialog.dismiss();
            }

            try {
                String result = new String(buffer, "utf-8");
                Log.d("FaceDemo", result);

                JSONObject object = new JSONObject(result);
                String type = object.optString("sst");
                if ("reg".equals(type)) {
                    register(object);
                } else if ("verify".equals(type)) {
                    verify(object);
                } else if ("detect".equals(type)) {
//                    detect(object);
                } else if ("align".equals(type)) {
//                    align(object);
                }
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (JSONException e) {
                // TODO: handle exception
            }
        }

        @Override
        public void onCompleted(SpeechError error) {
            if (null != mProDialog) {
                mProDialog.dismiss();
            }

            if (error != null) {
                switch (error.getErrorCode()) {
                    case ErrorCode.MSP_ERROR_ALREADY_EXIST:
                        ToastUtil.showToast("authid已经被注册，请更换后再试（人脸识别）");
                        break;
                    default:
                        ToastUtil.showToast(error.getPlainDescription(true));
                        break;
                }
            }
        }
    };
    private void register(JSONObject obj) throws JSONException {
        int ret = obj.getInt("ret");
        if (ret != 0) {
            ToastUtil.showToast("人脸识别，注册失败");
            finish();
            return;
        }
        if ("success".equals(obj.get("rst"))) {
            ToastUtil.showToast("人脸识别，注册成功");
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            ToastUtil.showToast("人脸识别，注册失败");
        }
    }

    private void verify(JSONObject obj) throws JSONException {
        int ret = obj.getInt("ret");
        if (ret != 0) {
            ToastUtil.showToast(" 验证失败 !!!");
            return;
        }

        if ("success".equals(obj.get("rst"))) {
            if (obj.getBoolean("verf")) {

                updataFaceRecogni(appData.getFaceRecogni(),Constant.CHECK_FACE_RECOGNITION);
            } else {
                updataFaceRecogni(appData.getFaceRecogni(),Constant.UNCHECK_FACE_RECOGNITION);
            }
        } else {
            ToastUtil.showToast("人脸识别，验证失败");
        }
    }

    private void updataFaceRecogni(FaceRecogni faceRecogni, final int i){
        FaceRecogni face = new FaceRecogni(faceRecogni.getTeacher(),
                faceRecogni.getCourse(),faceRecogni.getStudent(),
                i);
        face.setObjectId(faceRecogni.getObjectId());
        face.update(faceRecogni.getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null){
                    if (i == Constant.CHECK_FACE_RECOGNITION){
                        ToastUtil.showToast("人脸识别，通过验证 ！");
                    } else if (i == Constant.UNCHECK_FACE_RECOGNITION){
                        ToastUtil.showToast("人脸识别，验证不通过");
                    }
                } else {
                    Log.e(TAG,e.getMessage().toString());
                    ToastUtil.showToast(e.getMessage().toString());
                }
            }
        });
    }




}
