package com.nilhcem.selfid.ui;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.TextureView;
import android.widget.Toast;

import com.nilhcem.selfid.R;
import com.nilhcem.selfid.core.selfie.PhotoSaver;

import java.io.IOException;

import timber.log.Timber;

public class CameraTextureView extends TextureView implements TextureView.SurfaceTextureListener {

    private Camera mCamera;
    private PhotoSaver mPhotoSaver;

    public CameraTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPhotoSaver = new PhotoSaver(context);
        setSurfaceTextureListener(this);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        if (!bindFrontCameraId()) {
            Toast.makeText(getContext(), R.string.selfie_error_no_camera, Toast.LENGTH_LONG).show();
            ((Activity) getContext()).finish();
            return ;
        }

        // Create a matrix to invert the x-plane (moving it back otherwise it'll be off to the left)
        Matrix matrix = new Matrix();
        matrix.setScale(-1, 1);
        matrix.postTranslate(width, 0);
        setTransform(matrix);

        try {
            // Tell the camera to write onto our textureView mTextureView
            mCamera.setPreviewTexture(surface);
            mCamera.startPreview();
        } catch (IOException e) {
            Timber.e(e, "Error starting preview");
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        // Do nothing
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
        }
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        // Do nothing
    }

    public void releaseCamera() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }

    public void takePicture(int drawableId) throws RuntimeException {
        if (mCamera != null) {
            mPhotoSaver.setCurrentDrawableId(drawableId);
            mCamera.takePicture(null, null, mPhotoSaver);
        }
    }

    private boolean bindFrontCameraId() {
        if (getContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            int nbCameras = Camera.getNumberOfCameras();
            for (int cameraId = 0; cameraId < nbCameras; cameraId++) {
                Camera.CameraInfo info = new Camera.CameraInfo();
                Camera.getCameraInfo(cameraId, info);
                if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    Timber.d("Camera found: id #%d", cameraId);
                    mCamera = Camera.open(cameraId);
                    return true;
                }
            }
        }
        return false;
    }
}
