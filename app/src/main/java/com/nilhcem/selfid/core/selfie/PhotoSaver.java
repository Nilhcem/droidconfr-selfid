package com.nilhcem.selfid.core.selfie;

import android.content.Context;
import android.hardware.Camera;
import android.widget.Toast;

import com.nilhcem.selfid.R;
import com.nilhcem.selfid.core.utils.FsUtils;

import java.io.File;

public class PhotoSaver implements Camera.PictureCallback {

    private final Context mContext;

    public PhotoSaver(Context context) {
        mContext = context.getApplicationContext();
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        File output = FsUtils.generateFileName("orig");
        if (output == null || !FsUtils.saveDataToFile(data, output)) {
            Toast.makeText(mContext, R.string.selfie_error_save, Toast.LENGTH_LONG).show();
            return;
        }
    }
}
