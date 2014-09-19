package com.nilhcem.selfid.ui;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.nilhcem.selfid.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import timber.log.Timber;

import static android.hardware.Camera.CameraInfo;

public class SelfieActivity extends Activity {

    @InjectView(R.id.selfie_camera_container) ViewGroup mCameraViewContainer;
    @InjectView(R.id.selfie_frames_layer) ViewPager mFramesViewPager;

    private int mCameraId;
    private CameraView mCameraView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selfie);
        ButterKnife.inject(this);
        hideStatusBar();

        mCameraId = getFrontCameraId();
        mFramesViewPager.setAdapter(new FramesPagerAdapter(this));
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mCameraId < 0) {
            Toast.makeText(this, R.string.selfie_error_no_camera, Toast.LENGTH_LONG).show();
            finish();
        } else {
            bindCameraView();
        }
    }

    @Override
    protected void onPause() {
        if (mCameraView != null) {
            mCameraView.releaseCamera();
        }
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.selfie, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_take_selfie && mCameraView != null) {
            mCameraView.takePicture(((FramesPagerAdapter) mFramesViewPager.getAdapter()).getDrawableIdAt(mFramesViewPager.getCurrentItem()));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void hideStatusBar() {
        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(attrs);
    }

    private void bindCameraView() {
        mCameraViewContainer.removeAllViews();
        Camera frontCamera = Camera.open(mCameraId);
        if (frontCamera != null) {
            mCameraView = new CameraView(this, frontCamera);
            mCameraViewContainer.addView(mCameraView);
        }
    }

    private int getFrontCameraId() {
        int foundId = -1;

        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            int nbCameras = Camera.getNumberOfCameras();
            for (int cameraId = 0; cameraId < nbCameras; cameraId++) {
                CameraInfo info = new CameraInfo();
                Camera.getCameraInfo(cameraId, info);
                if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
                    Timber.d("Camera found: id #%d", cameraId);
                    foundId = cameraId;
                    break;
                }
            }
        }
        return foundId;
    }
}
