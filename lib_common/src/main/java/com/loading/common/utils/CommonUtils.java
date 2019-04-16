package com.loading.common.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Looper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;

/**
 * Created by loading on 3/14/17.
 */

public class CommonUtils {
    /**
     * 从assert下的json文件读取测试数据
     *
     * @param strAssertFileName
     * @return
     */
    public static String readAssertResource(Context context, String strAssertFileName) {
        AssetManager assetManager = context.getAssets();
        String strResponse = "";
        try {
            InputStream ims = assetManager.open(strAssertFileName);
            strResponse = getStringFromInputStream(ims);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return strResponse;
    }

    private static String getStringFromInputStream(InputStream a_is) {
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            br = new BufferedReader(new InputStreamReader(a_is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                }
            }
        }
        return sb.toString();
    }

    private static GradientDrawable getTL2BRGradientDrawable(int[] colors) {
        GradientDrawable gradientDrawable
                = new GradientDrawable(GradientDrawable.Orientation.TL_BR, colors);
        gradientDrawable.setCornerRadius(0.0f);//rectangle
        return gradientDrawable;
    }

    public static LayerDrawable getTL2BRGradientMaskDrawable(int[] colors, int maskResId, Context context) {
        return new LayerDrawable(new Drawable[]{
                getTL2BRGradientDrawable(colors),
                context.getResources().getDrawable(maskResId)
        });
    }

    public static boolean isMainThread() {
        boolean isMainThread = false;
        Thread curThread = Thread.currentThread();
        Looper mainLooper = Looper.getMainLooper();
        if (mainLooper != null) {
            Thread mainThread = mainLooper.getThread();
            isMainThread = (curThread.getId() == mainThread.getId());
        }
        return isMainThread;
    }

    public static <T> int sizeOf(final Collection<T> collection) {
        return collection == null ? 0 : collection.size();
    }

    public static <T> boolean isEmpty(final Collection<T> collection) {
        return sizeOf(collection) <= 0;
    }
}
