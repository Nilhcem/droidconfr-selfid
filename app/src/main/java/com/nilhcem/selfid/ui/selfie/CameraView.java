package com.nilhcem.selfid.ui.selfie;

import android.content.Context;
import android.hardware.Camera;
import android.os.Build;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.nilhcem.selfid.core.selfie.PhotoSaver;

import java.io.IOException;

import timber.log.Timber;

public class CameraView extends SurfaceView implements SurfaceHolder.Callback {

    private Camera mCamera;
    private SurfaceHolder mHolder;
    private PhotoSaver mPhotoSaver;

    public CameraView(Context context, Camera camera) {
        super(context);
        mCamera = camera;
        mHolder = getHolder();
        mHolder.addCallback(this);
        mPhotoSaver = new PhotoSaver(context);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (mCamera != null) {
            startPreview(holder);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (mHolder.getSurface() == null) {
            return;
        }
        if (mCamera != null) {
            mCamera.stopPreview();
            startPreview(mHolder);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // Do nothing
    }

    public void releaseCamera() {
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    public void takePicture() {
        if (mCamera != null) {
            mCamera.takePicture(null, null, mPhotoSaver);
        }
    }

    private void startPreview(SurfaceHolder holder) {
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException e) {
            Timber.e(e, "Error setting preview display (surface may be unavailable)");
        }
    }
}
