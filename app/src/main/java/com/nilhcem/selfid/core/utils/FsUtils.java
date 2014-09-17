package com.nilhcem.selfid.core.utils;

import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import timber.log.Timber;

public class FsUtils {

    private static final String IMAGES_PATH = "selfid";
    private static final String IMAGE_NAME = "selfie_%s_%s.jpg";
    private static final String IMAGE_NAME_PATTERN = "yyyymmddhhmmss";

    private FsUtils() {
        throw new UnsupportedOperationException();
    }

    public static File getImagesDir() {
        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), IMAGES_PATH);
        if (!dir.exists() && !dir.mkdirs()) {
            Timber.w("Can't create directory to save image");
            return null;
        }
        return dir;
    }

    public static File generateFileName(String prefix) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(IMAGE_NAME_PATTERN, Locale.US);
        String fileName = String.format(Locale.US, IMAGE_NAME, dateFormat.format(new Date()), prefix);
        File imagesDir = getImagesDir();
        if (imagesDir != null) {
            return new File(imagesDir, fileName);
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
