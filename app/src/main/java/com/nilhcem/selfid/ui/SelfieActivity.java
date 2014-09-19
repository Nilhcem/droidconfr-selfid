package com.nilhcem.selfid.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.nilhcem.selfid.R;
import com.nilhcem.selfid.ui.widgets.FloatingActionButton;
import com.viewpagerindicator.CirclePageIndicator;

import butterknife.ButterKnife;
import butterknife.InjectView;
import timber.log.Timber;

public class SelfieActivity extends Activity implements View.OnClickListener {

    @InjectView(R.id.selfie_camera_view) CameraTextureView mCameraView;
    @InjectView(R.id.selfie_frames_layer) ViewPager mFramesViewPager;
    @InjectView(R.id.selfie_frames_indicator) CirclePageIndicator mFramesIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selfie);
        ButterKnife.inject(this);
        hideStatusBar();
        createFloatingActionButton();

        mFramesViewPager.setAdapter(new FramesPagerAdapter(this));
        mFramesIndicator.setViewPager(mFramesViewPager);
    }

    @Override
    protected void onPause() {
        mCameraView.releaseCamera();
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
        try {
            mCameraView.takePicture(((FramesPagerAdapter) mFramesViewPager.getAdapter()).getDrawableIdAt(mFramesViewPager.getCurrentItem()));
        } catch (RuntimeException e) {
            Timber.e(e, "takePicture failed");
            Toast.makeText(this, R.string.selfie_error_take, Toast.LENGTH_LONG).show();
        }
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
}
