package com.nilhcem.selfid.core.selfie;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.Uri;
import android.widget.Toast;

import com.nilhcem.selfid.R;
import com.nilhcem.selfid.core.utils.BitmapUtils;
import com.nilhcem.selfid.core.utils.FsUtils;

import java.io.File;
import java.util.List;

public class PhotoSaver implements Camera.PictureCallback {

    private final Context mContext;

    public PhotoSaver(Context context) {
        mContext = context;
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

        // Twitter share intent
        startTwitterShareIntent(output);
    }

    private void startTwitterShareIntent(File file) {
        try {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_TEXT, mContext.getString(R.string.twitter_share_msg));
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
            intent.setType("image/jpeg");

            PackageManager pm = mContext.getPackageManager();
            List<ResolveInfo> infos = pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            boolean resolved = false;
            for (ResolveInfo ri : infos) {
                if (ri.activityInfo.name.contains("twitter")) {
                    intent.setClassName(ri.activityInfo.packageName, ri.activityInfo.name);
                    resolved = true;
                    break;
                }
            }
            mContext.startActivity(resolved ? intent : Intent.createChooser(intent, mContext.getString(R.string.twitter_share_intent_chooser_title)));
        } catch (final ActivityNotFoundException e) {
            Toast.makeText(mContext, R.string.twitter_not_found, Toast.LENGTH_SHORT).show();
        }
    }
}
