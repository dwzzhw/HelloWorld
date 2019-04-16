package com.loading.common.utils;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.loading.common.component.CApplication;

import java.io.BufferedReader;
import java.io.FileReader;

public class SystemUtils {
    public static final String TAG = "SystemUtils";
    private static DisplayMetrics sDisplayMertics;

    public static String getCurrentProcessName() {
        String processName = null;
        BufferedReader reader = null;
        try {
            int pid = android.os.Process.myPid();
            reader = new BufferedReader(new FileReader("/proc/" + pid + "/cmdline"));
            processName = reader.readLine();
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim();
            }
            return processName;
        } catch (Throwable throwable) {
            Loger.e(TAG, "throwable: " + throwable);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (Exception exception) {
                Loger.e(TAG, "exception: " + exception);
            }
        }
        return processName;
    }

    public static boolean isSdcardExist() {
        boolean isSdexist = false;
        try {
            isSdexist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        } catch (Exception e) {
            Loger.e(TAG, "isSdcardExist exception: " + e);
        }
        return isSdexist;
    }


    public static boolean isMainProcess() {
        // 获取当前包名
        String packageName = CApplication.getAppContext().getPackageName();
        // 获取当前进程名
        String processName = SystemUtils.getCurrentProcessName();
        return TextUtils.isEmpty(processName) || TextUtils.equals(packageName, processName);
    }

    public static int dpToPx(int dp) {
        int outResult = dp;
        DisplayMetrics disMetric = getDeviceDisplayMetrics(CApplication.getAppContext());
        if (disMetric != null) {
            outResult = (int) (dp * disMetric.density + 0.5f);
        }
        return outResult;
    }

    private static DisplayMetrics getDeviceDisplayMetrics(Context context) {
        if (sDisplayMertics == null && context != null) {
            android.view.WindowManager windowsManager = (android.view.WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            if (windowsManager != null) {
                android.view.Display display = windowsManager.getDefaultDisplay();
                if (display != null) {
                    sDisplayMertics = new DisplayMetrics();
                    display.getMetrics(sDisplayMertics);
                }
            }
        }
        return sDisplayMertics;
    }
}
