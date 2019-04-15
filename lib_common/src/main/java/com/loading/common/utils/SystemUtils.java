package com.loading.common.utils;

import android.app.Application;
import android.text.TextUtils;

import com.loading.common.component.CApplication;

import java.io.BufferedReader;
import java.io.FileReader;

public class SystemUtils {
    public static final String TAG = "SystemUtils";

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

    public static boolean isMainProcess() {
        // 获取当前包名
        String packageName = CApplication.getAppContext().getPackageName();
        // 获取当前进程名
        String processName = SystemUtils.getCurrentProcessName();
        return TextUtils.isEmpty(processName) || TextUtils.equals(packageName, processName);
    }
}
