package com.loading.common.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;

import com.loading.common.component.CApplication;
import com.loading.common.lifecycle.CActivityManager;
import com.loading.common.manager.CacheManager;
import com.loading.common.widget.dialog.MDAlertDialogFragment;
import com.loading.common.widget.dialog.MDDialogInterface;

import java.io.File;
import java.util.List;

/**
 * Created by huazhang on 12/20/16.
 */

@SuppressWarnings({"UnusedReturnValue", "WeakerAccess", "unused"})
public class PackageManagerUtil {
    private static final String TAG = "PackageManagerUtil";
    public static final int REQUEST_CODE_MANAGE_UNKNOWN_SOURCES = 9977;

    /**
     * determine is a application installed.
     *
     * @param context     context
     * @param packageName app package name
     * @param versionName [option] the version name that we want the application's.
     * @return true for intalled, false else
     */
    public static boolean isAppInstalled(Context context, String packageName, String versionName) {
        // 获取到一个PackageManager的instance
        final PackageManager packageManager = context.getPackageManager();
        // PERMISSION_GRANTED = 0
        List<PackageInfo> mPackageInfo = packageManager.getInstalledPackages(0);
        boolean flag = false;
        if (mPackageInfo != null) {
            String tempName = null;
            String versionCode = "";
            for (int i = 0; i < mPackageInfo.size(); i++) {
                // 获取到APK包名
                tempName = mPackageInfo.get(i).packageName;
                versionCode = mPackageInfo.get(i).versionName;
                Loger.i(TAG, "----->Package[" + tempName + "]");
                if (tempName != null && tempName.equals(packageName)) {
                    Loger.i(TAG, "Package[" + packageName + "]:is installed.");
                    if (versionName != null && !versionName.equals("")) {
                        if (!versionName.equalsIgnoreCase(versionCode)) {
                            Loger.i(TAG, "Package versionName [" + versionCode + "]");
                            flag = false;
                            break;
                        }
                    }
                    flag = true;
                    break;
                }
            }
        }
        return flag;
    }

    private static void chmodApk(String localAPKPath) {
        try {
            String cachePath = CacheManager.getApkCacheDir();
            String packageName = CApplication.getAppContext().getPackageName();
            if (!TextUtils.isEmpty(packageName) && cachePath != null && cachePath.contains(packageName)) { //back up one level
                cachePath = cachePath.substring(0, cachePath.indexOf(packageName) + packageName.length() + 1);
            }
            Loger.v(TAG, "-->chmodApk, cachePath=" + cachePath + ", localAPKPath=" + localAPKPath);
            if (localAPKPath != null && !TextUtils.isEmpty(cachePath) && localAPKPath.startsWith(cachePath)) {
                String str = localAPKPath.substring(cachePath.length());
                String[] arrStrings = str.split(File.separator);
                String COM = " chmod 755 ";
                String command;
                Runtime runtime = Runtime.getRuntime();
                if (arrStrings.length > 0) {
                    for (String arrString : arrStrings) {
                        //noinspection StringConcatenationInLoop
                        cachePath += arrString;
                        command = COM + cachePath;
                        runtime.exec(command);
                        cachePath += File.separator;
                    }
                }
            }
        } catch (Exception e) {
            Loger.e(TAG, "chmodApk: " + e);
        }
    }

    /**
     * install a application by given apk file path.
     *
     * @param context      context
     * @param localAPKPath path of apk file
     */
    private static boolean installApp(Context context, String localAPKPath) {
        boolean isSuccess = false;
        try {
            File apkFile = !TextUtils.isEmpty(localAPKPath) ? new File(localAPKPath) : null;
            if (apkFile != null && apkFile.exists()) {
                String apkPackageName = getPackageNameFromApk(CApplication.getAppContext(), localAPKPath);
                if (!TextUtils.isEmpty(apkPackageName)) {
                    Uri apkUri;
                    if (VersionUtils.hasNougat()) {
                        apkUri = FileProvider.getUriForFile(context, CacheManager.FILE_PROVIDER_AUTHORITY, apkFile);
                    } else {
                        chmodApk(localAPKPath);
                        apkUri = Uri.fromFile(new File(localAPKPath));
                    }
                    Loger.i(TAG, "apkUri: " + apkUri + ", localApkPath: " + localAPKPath);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    context.startActivity(intent);
                    isSuccess = true;
                }
            }
        } catch (Exception e) {
            Loger.e(TAG, "installApp exception: " + e);
        }
        return isSuccess;
    }

    public static void installAppWithPermission(final Activity activity, final String localAPKPath) {
        if (VersionUtils.hasOreo()) {
            boolean canInstall = canRequestPackageInstalls(activity);
            if (canInstall) {
                installApp(activity, localAPKPath);
            } else {
                MDAlertDialogFragment fragment = MDAlertDialogFragment.newInstance("已为您下载最新版安装包",
                        "请到设置中打开开关“未知来源应用安装”，开启后安装最新版",
                        "去设置",
                        "取消");
                fragment.setCancelable(true);
                fragment.setOnDialogClickListener((dialog, which, requestCode) -> {
                    if (which == MDDialogInterface.BUTTON_POSITIVE) {
                        apkInstallRunnable = () -> installAppWithPermission(activity, localAPKPath);
                        Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
                        activity.startActivityForResult(intent, REQUEST_CODE_MANAGE_UNKNOWN_SOURCES);
                    }
                });
                fragment.show(((FragmentActivity) activity).getSupportFragmentManager());
            }
        } else {
            installApp(activity, localAPKPath);
        }
    }

    public static String getPackageNameFromApk(final Context context, final String localAPKPath) {
        String packageName = null;
        try {
//            if (localAPKPath != null && localAPKPath.endsWith(".apk")) {
            if (!TextUtils.isEmpty(localAPKPath)) {
                PackageManager pm = context.getPackageManager();
                PackageInfo info = pm.getPackageArchiveInfo(localAPKPath, PackageManager.GET_ACTIVITIES);
                if (info != null) {
                    ApplicationInfo appInfo = info.applicationInfo;
                    packageName = appInfo.packageName;
                    Loger.d(TAG, "getPackageNameFromApk " + packageName);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return packageName;
    }

    /**
     * launch a default activity of given package name.
     *
     * @param ctx         context.
     * @param packageName the package name that you want to launch.
     */
    public static void startApkActivity(Context ctx, String packageName) {
        startApkActivity(ctx, packageName, false);
    }

    /**
     * launch a default activity of given package name.
     *
     * @param ctx         context.
     * @param packageName the package name that you want to launch.
     * @param isNewTask   start in New Task Flag.
     */
    public static void startApkActivity(final Context ctx, String packageName, boolean isNewTask) {
        PackageManager pm = ctx.getPackageManager();
        PackageInfo pi;
        try {
            if (pm != null) {
                pi = pm.getPackageInfo(packageName, 0);
                if (pi != null) {
                    Intent intent = new Intent(Intent.ACTION_MAIN, null);
                    intent.addCategory(Intent.CATEGORY_LAUNCHER);
                    if (isNewTask) {
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    } else {
                        intent.setFlags(Intent.FLAG_RECEIVER_FOREGROUND | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                    }
                    intent.setPackage(pi.packageName);
                    List<ResolveInfo> apps = pm.queryIntentActivities(intent, 0);
                    if (apps != null && apps.size() > 0) {
                        ResolveInfo ri = apps.iterator().next();
                        if (ri != null && ri.activityInfo != null) {
                            String className = ri.activityInfo.name;
                            intent.setComponent(new ComponentName(packageName, className));
                            ctx.startActivity(intent);
                        }
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            Loger.e(TAG, "" + e);
        }
    }

    @SuppressLint("PackageManagerGetSignatures")
    public static boolean isAppInstalled(String packageName) {
        boolean isInstalled = false;
        if (!TextUtils.isEmpty(packageName)) {
            try {
                isInstalled = CApplication.getAppContext()
                        .getPackageManager()
                        .getPackageInfo(packageName, PackageManager.GET_SIGNATURES) != null;

            } catch (PackageManager.NameNotFoundException e) {
                Loger.d(TAG, "--->isAppInstalled()--NameNotFoundException:" + e.toString());
            }
        }
        Loger.d(TAG, "--->isInstalled:" + isInstalled);
        return isInstalled;
    }

    public static void openApp(Context context, String openUrl, String packageName) {
        Loger.i(TAG, "-->openApp(), openUrl=" + openUrl + ", packageName=" + packageName);
        if (context != null && !TextUtils.isEmpty(openUrl)) {
            Uri uri = Uri.parse(openUrl);
            Intent it = new Intent(Intent.ACTION_VIEW, uri);
            it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            try {
                context.startActivity(it);
            } catch (Exception e) {
                Loger.e(TAG, "-->openApp()--Exception:" + e.toString());
            }
        }
    }

    private static Runnable apkInstallRunnable = null;

    public static void onActivityResult() {
        Activity topActivity = CActivityManager.getInstance().getTopActivity();
        if (topActivity != null && canRequestPackageInstalls(topActivity) && apkInstallRunnable != null) {
            apkInstallRunnable.run();
        }
        apkInstallRunnable = null;
    }

    private static boolean canRequestPackageInstalls(@NonNull final Activity activity) {
        return SystemUtil.getTargetSdkVersion() < Build.VERSION_CODES.O
                || (VersionUtils.hasOreo() && activity.getPackageManager().canRequestPackageInstalls());
    }
}
