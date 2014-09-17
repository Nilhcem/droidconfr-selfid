package com.nilhcem.selfid.core.selfie;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.Uri;
import android.widget.Toast;

import com.nilhcem.selfid.R;
import com.nilhcem.selfid.core.utils.BitmapUtils;
import com.nilhcem.selfid.core.utils.FsUtils;

import java.io.File;

public class PhotoSaver implements Camera.PictureCallback {

    private final Context mContext;

    public PhotoSaver(Context context) {
        mContext = context.getApplicationContext();
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        // Get photo from camera
        Bitmap bitmap = BitmapUtils.fromByteArray(data);

        // Get layer
        Bitmap layer = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.logo_layer);

        // Apply layer above photo
        byte[] result = BitmapUtils.toByteArray(BitmapUtils.overlay(bitmap, layer));

        // Save everything
        File output = FsUtils.generateFileName("generated");
        if (output == null || !FsUtils.saveDataToFile(result, output)) {
            Toast.makeText(mContext, R.string.selfie_error_save, Toast.LENGTH_LONG).show();
            return;
        }

        // Force the MediaScanner to add the file (so it is visible on the gallery)
        mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(output)));
    }
}
