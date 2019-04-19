package com.loading.common.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;

import java.lang.reflect.Method;

/**
 * 刘海屏
 * Created by pcyang on 2018/10/23.
 */

public class NotchPhoneUtil {

    private static boolean hasNotchAtHuawei(Context context) {
        boolean ret = false;
        try {
            ClassLoader classLoader = context.getClassLoader();
            Class HwNotchSizeUtil = classLoader.loadClass("com.huawei.android.util.HwNotchSizeUtil");
            Method get = HwNotchSizeUtil.getMethod("hasNotchInScreen");
            ret = (boolean) get.invoke(HwNotchSizeUtil);
        } catch (ClassNotFoundException e) {
            Log.e("Notch", "hasNotchAtHuawei ClassNotFoundException");
        } catch (NoSuchMethodException e) {
            Log.e("Notch", "hasNotchAtHuawei NoSuchMethodException");
        } catch (Exception e) {
            Log.e("Notch", "hasNotchAtHuawei Exception");
        } finally {
            return ret;
        }
    }

    private static int[] getNotchSizeAtHuawei(Context context) {
        int[] ret = new int[]{0, 0};
        try {
            ClassLoader cl = context.getClassLoader();
            Class HwNotchSizeUtil = cl.loadClass("com.huawei.android.util.HwNotchSizeUtil");
            Method get = HwNotchSizeUtil.getMethod("getNotchSize");
            ret = (int[]) get.invoke(HwNotchSizeUtil);
        } catch (ClassNotFoundException e) {
            Log.e("Notch", "getNotchSizeAtHuawei ClassNotFoundException");
        } catch (NoSuchMethodException e) {
            Log.e("Notch", "getNotchSizeAtHuawei NoSuchMethodException");
        } catch (Exception e) {
            Log.e("Notch", "getNotchSizeAtHuawei Exception");
        } finally {
            return ret;
        }
    }

    private static boolean hasOppoNotchInScreen(Context context) {
        return context.getPackageManager().hasSystemFeature("com.oppo.feature.screen.heteromorphism");
    }


    private static final int VIVO_NOTCH = 0x00000020;//是否有刘海

    private static boolean hasVivoNotchInScreen(Context context) {
        boolean ret = false;
        try {
            ClassLoader classLoader = context.getClassLoader();
            Class FtFeature = classLoader.loadClass("android.util.FtFeature");
            Method method = FtFeature.getMethod("isFeatureSupport", int.class);
            ret = (boolean) method.invoke(FtFeature, VIVO_NOTCH);
        } catch (ClassNotFoundException e) {
            Log.e("Notch", "hasNotchAtVivo ClassNotFoundException");
        } catch (NoSuchMethodException e) {
            Log.e("Notch", "hasNotchAtVivo NoSuchMethodException");
        } catch (Exception e) {
            Log.e("Notch", "hasNotchAtVivo Exception");
        } finally {
            return ret;
        }

    }

    private static boolean hasMIUINotchInScreen() {
        try {
            Class<?> clz = Class.forName("android.os.SystemProperties");
            Method get = clz.getMethod("getInt", String.class, String.class);
            return (int) get.invoke(clz, "ro.miui.notch", "-1") == 1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public static int obtainNotchHeight(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            return 0;
        }
        if (MobileUtil.isVivo() && hasVivoNotchInScreen(context)) {
            return SystemUtil.getStatusBarHeight();
        }
        if (MobileUtil.isHuaWeiDevice() && hasNotchAtHuawei(context)) {
            int[] sizeAtHuawei = getNotchSizeAtHuawei(context);
            return sizeAtHuawei[1];
        }
        if (MobileUtil.isOPPO() && hasOppoNotchInScreen(context)) {
            return 80;
        }
        if (MobileUtil.isXiaomiDevice() && hasMIUINotchInScreen()) {
            return SystemUtil.getStatusBarHeight();
        }
        return 0;
    }

    public static boolean isNotchOnLeft(Context activity) {
        boolean onLeft = false;
        if (activity != null) {
            onLeft = getRotation(activity) == Surface.ROTATION_90;
        }
        return onLeft;
    }

    private static int getRotation(Context context) {
        int rotation = Surface.ROTATION_0;
        if (context instanceof Activity) {
            WindowManager windowManager = ((Activity) context).getWindowManager();
            if (windowManager != null) {
                Display display = windowManager.getDefaultDisplay();
                if (display != null) {
                    rotation = display.getRotation();
                }
            }
        }
        return rotation;
    }


    public static void rectForCutout(Activity activity, View view) {
        if (activity == null || view == null) {
            return;
        }
        final int rotation = getRotation(activity);
        if (rotation != Surface.ROTATION_90 && rotation != Surface.ROTATION_270) {
            return;
        }
        final int cutoutMargin = obtainNotchHeight(activity);
        if (cutoutMargin <= 0) {
            return;
        }
        int left = view.getPaddingLeft();
        int right = view.getPaddingRight();
        if (rotation == Surface.ROTATION_270 && right == 0) {
            right = cutoutMargin;
        } else if (rotation == Surface.ROTATION_90 && left == 0) {
            left = cutoutMargin;
        }
        view.setPadding(left, view.getPaddingTop(), right, view.getPaddingBottom());
    }
}
