package com.lzp.classroomassistant.view;

import android.content.Context;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.lzp.classroomassistant.util.Utils;

import java.io.IOException;
import java.util.List;

/**
 * Created by WangChunhe on 2017/5/7.
 */

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private Context mContext;
    private static final String TAG = "CameraPreview";


    public CameraPreview(Context context, Camera camera) {

        super(context);
        mCamera = camera;
        mContext = context;

        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            setCameraParam();
            Utils.followScreenOrientation(mContext,mCamera);
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();

        } catch (IOException e) {
            Log.d(TAG,"Error setting camera preview: " + e.getMessage());
        }

    }
    private void setCameraParam(){
        Camera.Parameters parameters = mCamera.getParameters();
        List<Camera.Size> list = parameters.getSupportedPreviewSizes();
        parameters.setPictureSize(480,360);
        parameters.setPreviewSize(480,360);
        parameters.setJpegQuality(100);
        parameters.setPictureFormat(PixelFormat.JPEG);// 设置照片的格式
        int s = list.size();
        for (int i = 0; i < s; i++){
//            Log.d(TAG," w = " + list.get(i).width + " h = " + list.get(i).height);
        }
        List<Camera.Size> l = parameters.getSupportedPictureSizes();
        for (int i = 0; i < l.size(); i++){
//            Log.i(TAG," width = " + l.get(i).width + " height = " + l.get(i).height);
        }
        parameters.setFlashMode(Camera.Parameters.FOCUS_MODE_AUTO);
        parameters.setSceneMode(Camera.Parameters.SCENE_MODE_AUTO);

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (mHolder.getSurface() == null){
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e){
            // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here

        // start preview with new settings
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();

        } catch (Exception e){
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mCamera != null) {
            // Call stopPreview() to stop updating the preview surface.
            stopPreviewAndFreeCamera();
        }
    }
    /**
     * When this function returns, mCamera will be null.
     */
    private void stopPreviewAndFreeCamera() {

        if (mCamera != null) {
            // Call stopPreview() to stop updating the preview surface.
            mCamera.stopPreview();

            // Important: Call release() to release the camera for use by other
            // applications. Applications should release the camera immediately
            // during onPause() and re-open() it during onResume()).
            mCamera.release();

            mCamera = null;
        }
    }





}
