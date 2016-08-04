package com.augmentis.ayp.crimin.model;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.nfc.Tag;
import android.util.Log;

/**
 * Created by Rawin on 04-Aug-16.
 */
public class PictureUtils {
    private static final String TAG = "PictureUtils";

    public static Bitmap getScaledBitmap(String path, int destWidth, int destHeight) {
        // Read the dimension of the image
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        // return null and put meta data (information about the bitmap)
        BitmapFactory.decodeFile(path, options);

        float srcWidth = options.outWidth;
        float srcHeight = options.outHeight;

        Log.d(TAG, "srcWidth=" + srcWidth);
        Log.d(TAG, "srcHeight=" + srcHeight);

        int inSampleSize = 1;

        if(srcHeight > destHeight || srcWidth > destWidth) {
            if(srcWidth > srcHeight) {
                inSampleSize = Math.round(srcHeight/destHeight);
            } else {
                inSampleSize = Math.round(srcWidth/destWidth);
            }
        }

        options = new BitmapFactory.Options();

        Log.d(TAG, "inSampleSize =" + inSampleSize);
        options.inSampleSize = inSampleSize;

        return BitmapFactory.decodeFile(path, options);
    }

    public static Bitmap getScaledBitmap(String path, Activity activity) {
        Point size = new Point();

        activity.getWindowManager().getDefaultDisplay().getSize(size);

        return getScaledBitmap(path, size.x, size.y);
    }
}
