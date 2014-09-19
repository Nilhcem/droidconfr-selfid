package com.nilhcem.selfid.ui;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.nilhcem.selfid.R;
import com.nilhcem.selfid.ui.widgets.FloatingActionButton;
import com.viewpagerindicator.CirclePageIndicator;
import com.viewpagerindicator.PageIndicator;

import butterknife.ButterKnife;
import butterknife.InjectView;
import timber.log.Timber;

import static android.hardware.Camera.CameraInfo;

public class SelfieActivity extends Activity implements View.OnClickListener {

    @InjectView(R.id.selfie_camera_container) ViewGroup mCameraViewContainer;
    @InjectView(R.id.selfie_frames_layer) ViewPager mFramesViewPager;
    @InjectView(R.id.selfie_frames_indicator) CirclePageIndicator mFramesIndicator;

    private int mCameraId;
    private CameraView mCameraView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selfie);
        ButterKnife.inject(this);
        hideStatusBar();
        createFloatingActionButton();

        mCameraId = getFrontCameraId();
        mFramesViewPager.setAdapter(new FramesPagerAdapter(this));
        mFramesIndicator.setViewPager(mFramesViewPager);
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
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        // Immersive mode
        if (hasFocus) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            }
        }
    }

    @Override
    public void onClick(View v) {
        mCameraView.takePicture(((FramesPagerAdapter) mFramesViewPager.getAdapter()).getDrawableIdAt(mFramesViewPager.getCurrentItem()));
    }

    private void hideStatusBar() {
        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(attrs);
    }

    private void createFloatingActionButton() {
        new FloatingActionButton.Builder(this)
                .withDrawable(getResources().getDrawable(R.drawable.ic_camera))
                .withButtonColor(getResources().getColor(R.color.idapps_green))
                .withGravity(Gravity.BOTTOM | Gravity.RIGHT)
                .withMargins(0, 0, 32, 16)
                .create().setOnClickListener(this);
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
