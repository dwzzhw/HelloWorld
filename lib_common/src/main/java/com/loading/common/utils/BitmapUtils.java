package com.loading.common.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

public class BitmapUtils {
    public static final Bitmap loadBitmapFromFile(String filePath) {
        Bitmap result = null;
        if (!TextUtils.isEmpty(filePath)) {
            try {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                options.inJustDecodeBounds = false;
                result = BitmapFactory.decodeFile(filePath, options);
            } catch (OutOfMemoryError e) {
                return null;
            } catch (IllegalArgumentException e) {
                return null;
            }

        }
        return result;
    }
}
