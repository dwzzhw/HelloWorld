package com.loading.common.utils;

import android.annotation.SuppressLint;
import android.os.Build;
import android.text.TextUtils;

import java.lang.reflect.Method;

/**
 * Created by Mr. Orange on 17/3/1.
 */

public class MobileUtil {
    public static final String HUAWEI = "huawei";
    public static final String HONOR = "Honor";

    //判断是否是vivo手机
    public static boolean isVivo() {
        String brand = Build.BRAND;
        return !TextUtils.isEmpty(brand) && brand.equalsIgnoreCase("vivo");
    }

    //判断是否是魅族手机
    public static boolean isMeizu() {
        String brand = Build.BRAND;
        return !TextUtils.isEmpty(brand) && brand.equalsIgnoreCase("meizu");
    }

    public static boolean isOPPO() {
        String brand = Build.BRAND;
        return !TextUtils.isEmpty(brand) && brand.equals("OPPO");
    }

    /** 检测是否HTC手机 */
    public static boolean isHTC() {
        return SystemUtils.getManufacturer() != null
            && SystemUtils.getManufacturer().toLowerCase().contains("htc");
    }

    //判断是否是小米 V6/V7/V8
    public static boolean isMiuiVersion678() {
        boolean ret = false;
        try {
            @SuppressLint("PrivateApi")
            Class<?> sysClass = Class.forName("android.os.SystemProperties");
            Method methodGetter = sysClass.getDeclaredMethod("get", String.class);
            String miuiVerName = (String) methodGetter.invoke(sysClass, "ro.miui.ui.version.name");
            ret = "V6".equals(miuiVerName) || "V7".equals(miuiVerName) || "V8".equals(miuiVerName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    //判断是否是华为荣耀手机
    public static boolean isHuaWeiH60() {
        String brand = Build.BRAND;
        return !TextUtils.isEmpty(brand) && brand.equalsIgnoreCase(HUAWEI) && TextUtils.equals(Build.MODEL, "H60-L02");
    }

    //判断是否是华为Mate10手机
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isHuaWeiMate10() {
        String brand = Build.BRAND;
        return !TextUtils.isEmpty(brand) && brand.equalsIgnoreCase(HUAWEI) && TextUtils.equals(Build.MODEL, "ALP-AL00");
    }

    public static boolean isHuaWeiDevice() {
        return !TextUtils.isEmpty(Build.BRAND) && (HUAWEI.equalsIgnoreCase(Build.BRAND) || HONOR.equalsIgnoreCase(Build.BRAND)
            && (HUAWEI.equalsIgnoreCase(SystemUtils.getManufacturer()) || HONOR.equalsIgnoreCase(SystemUtils.getManufacturer())));
    }

    public static boolean isXiaomiDevice() {
        boolean ret = false;
        try {
            @SuppressLint("PrivateApi")
            Class<?> sysClass = Class.forName("android.os.SystemProperties");
            Method methodGetter = sysClass.getDeclaredMethod("get", String.class);
            String miuiVerName = (String) methodGetter.invoke(sysClass, "ro.miui.ui.version.name");
            ret = !TextUtils.isEmpty(miuiVerName) && "xiaomi".equalsIgnoreCase(SystemUtils.getManufacturer());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

}
