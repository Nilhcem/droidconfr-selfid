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

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.nilhcem.selfid.core.utils.FsUtils.IMG_FILENAME_PATTERN;
import static com.nilhcem.selfid.core.utils.FsUtils.IMG_MODIFIED_SUFFIX;
import static com.nilhcem.selfid.core.utils.FsUtils.IMG_ORIG_SUFFIX;
import static com.nilhcem.selfid.core.utils.FsUtils.saveBitmap;

public class PhotoSaver implements Camera.PictureCallback {

    private final Context mContext;
    private final SimpleDateFormat mFileNameFormat = new SimpleDateFormat(IMG_FILENAME_PATTERN, Locale.US);

    private int mCurrentDrawableId;

    public PhotoSaver(Context context) {
        mContext = context;
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        String pictureName = mFileNameFormat.format(new Date());

        // Get photo from camera and save original picture
        Bitmap bitmap = BitmapUtils.fromByteArray(data);
        File savedFile = saveBitmap(bitmap, pictureName, IMG_ORIG_SUFFIX, mContext);
        if (savedFile == null) {
            showToastError();
            return;
        }

        // Get selected layer and apply it above the original photo
        if (mCurrentDrawableId != 0) {
            Bitmap layer = BitmapFactory.decodeResource(mContext.getResources(), mCurrentDrawableId);
            bitmap = BitmapUtils.overlay(bitmap, layer);
        }

        // Save modified picture
        savedFile = saveBitmap(bitmap, pictureName, IMG_MODIFIED_SUFFIX, mContext);
        if (savedFile == null) {
            showToastError();
            return;
        }

        // Start Twitter share intent
        startTwitterShareIntent(savedFile);
    }

    public void setCurrentDrawableId(int drawableId) {
        mCurrentDrawableId = drawableId;
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

    private void showToastError() {
        Toast.makeText(mContext, R.string.selfie_error_save, Toast.LENGTH_LONG).show();
    }
}
