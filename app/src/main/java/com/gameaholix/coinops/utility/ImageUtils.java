package com.gameaholix.coinops.utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ImageUtils {
    public static final String TAG = ImageUtils.class.getSimpleName();

    /**
     * Creates a temporary file to store an image taken by the camera
     * @param context the application context
     * @return the temp file created
     * @throws IOException exception thrown if file was not created
     */
    public static File createImageFile(Context context) throws IOException {
        // Code used from https://developer.android.com/training/camera/photobasics

        // Create an image name from current timestamp
        String filename = new SimpleDateFormat("yyyyMMdd_hhmmss_", Locale.US).format(new Date());

        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(
                filename,
                ".jpg",
                storageDir
        );
    }

    /**
     * Delete temporary file from local storage
     * @param filePath the full path of the temporary file to delete
     */
    public static void deleteTemporaryImageFromDisk(String filePath) {
        // delete file from external storage
        File fdelete = new File(filePath);
        if (fdelete.exists()) {
            if (fdelete.delete()) {
                Log.d(TAG,"file Deleted :" + filePath);
            } else {
                Log.e(TAG, "file not Deleted :" + filePath);
            }
        }
    }

    /**
     * Scales a bitmap at a given path
     * @param filePath the full path of the source Bitmap
     * @param targetW the target width
     * @param targetH the target height
     * @return the scaled Bitmap
     */
    public static Bitmap scaleBitmap(String filePath, int targetW, int targetH) {
        // Get the dimensions of the full bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the target
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        // return the scaled bitmap
        return BitmapFactory.decodeFile(filePath, bmOptions);
    }

    /**
     * Converts a Bitmap to JPEG
     * @param bitmap the source Bitmap to convert
     * @param quality the quality of the JPEG compression
     * @return a byte array with the output JPEG data
     */
    public static byte[] bitmapToJpeg(Bitmap bitmap, int quality) {
        // Compress Bitmap to JPEG compression
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        return baos.toByteArray();
    }
}
