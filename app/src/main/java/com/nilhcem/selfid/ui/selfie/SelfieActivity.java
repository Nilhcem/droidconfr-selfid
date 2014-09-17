package com.nilhcem.selfid.ui.selfie;

import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Toast;

import com.nilhcem.selfid.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import timber.log.Timber;

import static android.hardware.Camera.CameraInfo;

public class SelfieActivity extends ActionBarActivity {

    @InjectView(R.id.selfie_camera_container) ViewGroup mCameraViewContainer;

    private int mCameraId;
    private CameraView mCameraView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selfie);
        ButterKnife.inject(this);
        mCameraId = getFrontCameraId();
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
            mCameraView.takePicture();
            return true;
        }
        return super.onOptionsItemSelected(item);
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
