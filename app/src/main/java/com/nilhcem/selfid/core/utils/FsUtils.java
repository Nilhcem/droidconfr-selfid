package com.nilhcem.selfid.core.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;

import timber.log.Timber;

public class FsUtils {

    public static final String IMG_ORIG_SUFFIX = "orig";
    public static final String IMG_MODIFIED_SUFFIX = "modified";
    public static final String IMG_FILENAME_PATTERN = "'selfie_'yyyymmddhhmmss'_%s.jpg'";

    private static final String IMAGES_PATH = "selfid";

    private FsUtils() {
        throw new UnsupportedOperationException();
    }

    public static File getImagesDir() {
        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), IMAGES_PATH);
        if (!dir.exists() && !dir.mkdirs()) {
            Timber.e("Can't create directory to save image");
            return null;
        }
        return dir;
    }

    public static File saveBitmap(Bitmap bitmap, String imageName, String imageSuffix, Context context) {
        // Get images root directory
        File imagesDir = FsUtils.getImagesDir();

        if (imagesDir != null) {
            // Convert bitmap to byte[] and save it to a new file
            byte[] bitmapData = BitmapUtils.toByteArray(bitmap);
            String outputFilename = String.format(Locale.US, imageName, imageSuffix);
            File outputFile = new File(imagesDir, outputFilename);

            if (FsUtils.saveDataToFile(bitmapData, outputFile)) {
                // Force the MediaScanner to add the file (so it is visible on the gallery)
                context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(outputFile)));
                return outputFile;
            }
        }
        return null;
    }

    public static boolean saveDataToFile(byte[] data, File output) {
        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream(output);
            fos.write(data);
            Timber.i("Image saved to: %s", output.getAbsolutePath());
            return true;
        } catch (IOException e) {
            Timber.e(e, "Error saving image to %s", output.getAbsolutePath());
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    // Do nothing
                }
            }
        }
        return false;
    }
}
