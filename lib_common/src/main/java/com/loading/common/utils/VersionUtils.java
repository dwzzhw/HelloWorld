/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.loading.common.utils;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.text.TextUtils;

import java.util.Arrays;

/**
 * Class containing some static utility methods.
 */
@SuppressWarnings("unused")
public class VersionUtils {

    private static final String TAG = "VersionUtils";

    private VersionUtils() {
    }

    @SuppressLint("ObsoleteSdkInt")
    public static boolean hasGingerbread() {
        return Build.VERSION.SDK_INT >= VERSION_CODES.GINGERBREAD;
    }

//    public static boolean hasHoneycomb() {
//        return Build.VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB;
//    }

//    public static boolean hasHoneycombMR1() {
//        return Build.VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB_MR1;
//    }

//    public static boolean hasHoneycombMR2() {
//        return Build.VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB_MR2;
//    }

    @SuppressLint("ObsoleteSdkInt")
    public static boolean hasIceCreamSandwich() {
        return Build.VERSION.SDK_INT >= VERSION_CODES.ICE_CREAM_SANDWICH;
    }

    @SuppressLint("ObsoleteSdkInt")
    public static boolean hasIceCreamSandwichMR1() {
        return Build.VERSION.SDK_INT >= VERSION_CODES.ICE_CREAM_SANDWICH_MR1;
    }

    @SuppressLint("ObsoleteSdkInt")
    public static boolean hasJellyBean() {
        return Build.VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN;
    }

    @SuppressLint("ObsoleteSdkInt")
    public static boolean hasJellyBeanMR1() {
        return Build.VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN_MR1;
    }

    @SuppressLint("ObsoleteSdkInt")
    public static boolean hasJellyBeanMR2() {
        return Build.VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN_MR2;
    }

    public static boolean hasKitKat() {
        return Build.VERSION.SDK_INT >= VERSION_CODES.KITKAT;
    }

    public static boolean hasKitKatWatch() {
        return Build.VERSION.SDK_INT >= VERSION_CODES.KITKAT_WATCH;
    }

    public static boolean hasLOLLIPOP() {
        return Build.VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP;
    }

    public static boolean hasLOLLIPOP_MR1() {
        return Build.VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP_MR1;
    }

    /**
     * API level is higher than 23
     */
    public static boolean hasMarshmallow() {
        return Build.VERSION.SDK_INT >= VERSION_CODES.M;
    }

    /**
     * API level is higher than 24
     */
    public static boolean hasNougat() {
        return Build.VERSION.SDK_INT >= VERSION_CODES.N;
    }


    /**
     * API level is higher than 25
     */
    public static boolean hasN_MR1() {
        return Build.VERSION.SDK_INT >= VERSION_CODES.N_MR1;
    }

    public static boolean hasOreo() {
        return Build.VERSION.SDK_INT >= 26;
        // return Build.VERSION.SDK_INT >= VERSION_CODES.O;
    }

    public static boolean hasP() {
        return Build.VERSION.SDK_INT >= 28;
    }


    //传入3位版本号，计算为long型的版本号，如5.9.9 = 5 * 10^6 + 9 * 10^3 + 9。 每位版本号最大支持3位。
    public static long getLongVerByString(String appVer) {
        long result = -1L;
        if (!TextUtils.isEmpty(appVer)) {
            String[] digits = appVer.split("\\.");
            Loger.d(TAG, "splits = " + Arrays.toString(digits));
            if (digits.length == 3) {
                result = 0L;
                for (int i = 0; i < digits.length; i++) {
                    int multiplier = i == 0 ? 1000000 : (i == 1 ? 1000 : 1);
                    int currentDigit = CommonUtils.optInt(digits[i], -1);
                    if (currentDigit >= 0) {
                        result = result + currentDigit * multiplier;
                    } else {
                        result = -1;
                        break;
                    }
                }
            }
        }
        Loger.d(TAG, "getLongVerByString(), input = " + appVer + ", output = " + result);
        return result;
    }

    public static long getCurrentLongVer() {
        String currentVersion = SystemUtil.getAppVersion();
        if (!TextUtils.isEmpty(currentVersion)) {
            int lastDotIndex = currentVersion.lastIndexOf(".");
            if (lastDotIndex > 0) {
                return getLongVerByString(currentVersion.substring(0, lastDotIndex));
            }
        }
        return -1;
    }
}
