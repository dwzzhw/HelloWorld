
package com.loading.comp.download;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;
import android.text.TextUtils;

import com.loading.common.component.CApplication;
import com.loading.common.lifecycle.CActivityManager;
import com.loading.common.manager.CacheManager;
import com.loading.common.utils.Loger;
import com.loading.common.utils.PackageManagerUtil;
import com.loading.modules.interfaces.download.DownloadListener;
import com.loading.modules.interfaces.download.DownloadRequest;

import java.io.File;
import java.util.HashMap;

/**
 * 通知管理
 */
public class DownloadNotificationManager implements DownloadListener {
    private static final String TAG = "DownloadNotificationManager";
    private static final String NOTIFY_ID_PREFIX = "download_notify_";

    private NotificationManager mNotifyMgr;
    private HashMap<String, NotificationCompat.Builder> mBuilderMap = new HashMap<>();

    void startNotify(final String taskId, String notifyTitle) {
        if (!TextUtils.isEmpty(taskId)) {
            NotificationCompat.Builder builder = mBuilderMap.get(taskId);
            if (builder == null) {
                builder = new NotificationCompat.Builder(CApplication.getAppContext(), null).
                        setSmallIcon(R.drawable.push_icon).
                        setContentTitle(notifyTitle);
                mBuilderMap.put(taskId, builder);
            }
        }
    }

    void stopNotify(final String taskId) {
        if (!TextUtils.isEmpty(taskId)) {
            removeBuilder(taskId);
            getNotifyMgr().cancel(getNotifyId(taskId));
        }
    }

    private void removeBuilder(final String taskId) {
        if (!TextUtils.isEmpty(taskId)) {
            mBuilderMap.remove(taskId);
        }
    }

    private NotificationCompat.Builder getNotifyBuilder(final String taskId) {
        return mBuilderMap.get(taskId);
    }

    private NotificationManager getNotifyMgr() {
        if (mNotifyMgr == null) {
            mNotifyMgr = (NotificationManager) CApplication.getSysService(android.content.Context.NOTIFICATION_SERVICE);
        }
        return mNotifyMgr;
    }


    private int getNotifyId(String taskId) {
        return (NOTIFY_ID_PREFIX + taskId).hashCode();
    }

    @Override
    public void onDownloadProgress(String taskId, String downloadUrl, String tempFilePath, long completeSize, long totalSize, int nProgress, DownloadRequest downloadRequest) {
        NotificationCompat.Builder builder = getNotifyBuilder(taskId);
        if (builder != null) {
            builder.setContentText("正在下载:" + nProgress + "%").
                    setProgress(100, nProgress, false).
                    setAutoCancel(false).
                    setOngoing(true);
            PendingIntent pendingintent = PendingIntent.getActivity(CApplication.getAppContext(),
                    0,
                    new Intent(CApplication.getAppContext(), CActivityManager.getHomeActivityCls()),
                    PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(pendingintent);
            getNotifyMgr().notify(getNotifyId(taskId), builder.build());
        }
    }

    @Override
    public void onDownloadPaused(String taskId, String downloadUrl, String tempFilePath, long completeSize, long totalSize, int nProgress, DownloadRequest downloadRequest) {
        NotificationCompat.Builder builder = getNotifyBuilder(taskId);
        if (builder != null) {
            getNotifyMgr().notify(getNotifyId(taskId),
                    builder.setOngoing(false).
                            setAutoCancel(true).
                            setContentText("下载已停止").
                            setProgress(0, 0, false).
                            build());
        }
    }

    @Override
    public void onDownloadComplete(String taskId, String downloadUrl, String finalFilePath, long completeSize, long totalSize, DownloadRequest downloadRequest) {
        NotificationCompat.Builder builder = getNotifyBuilder(taskId);
        if (builder != null) {
            String apkPackageName = downloadRequest != null && downloadRequest.isDownloadApk() ?
                    PackageManagerUtil.getPackageNameFromApk(CApplication.getAppContext(), finalFilePath) : null;
            boolean isApk = !TextUtils.isEmpty(apkPackageName);
            builder.setContentText("下载完成" + (isApk ? ", 点击安装" : "")).
                    setAutoCancel(true);
            if (isApk) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Uri contentUri = FileProvider.getUriForFile(CApplication.getAppContext(), CacheManager.FILE_PROVIDER_AUTHORITY, new File(finalFilePath));
                Loger.i(TAG, "download complete, contentUri: " + contentUri + ", localApkPath: " + finalFilePath + ", apkPackageName: " + apkPackageName);
                intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
                PendingIntent pendingintent = PendingIntent.getActivity(CApplication.getAppContext(),
                        0,
                        intent,
                        PendingIntent.FLAG_CANCEL_CURRENT);
                builder.setContentIntent(pendingintent);
            }
            getNotifyMgr().notify(getNotifyId(taskId), builder.build());
            removeBuilder(taskId);
        }
    }

    @Override
    public void onDownloadError(String taskId, String downloadUrl, String tempFilePath, long completeSize, long totalSize, int nProgress, DownloadRequest downloadRequest) {
        NotificationCompat.Builder builder = getNotifyBuilder(taskId);
        if (builder != null) {
            getNotifyMgr().notify(getNotifyId(taskId),
                    builder.setOngoing(false).
                            setAutoCancel(true).
                            setContentText("下载失败").
                            setProgress(0, 0, false).
                            build());
            removeBuilder(taskId);
        }
    }

    //    void removeNotification(String taskId) {
//        nm.cancel(getNotifyId(taskId));
//    }

//    @Override
//    public void onDownloadStateChanged(String taskId, int state, int n_progress,
//                                       String t_progress) {
//        Builder builder = mBuilderMap.get(taskId);
//        if (builder != null && state == DownloadListener.STATE_ONGOING) {
//            builder.setContentText("正在下载:" + n_progress + "%").setProgress(100,
//                    n_progress, false).setAutoCancel(false).setOngoing(true);
//            // setContentInent如果不设置在4.0+上没有问题，在4.0以下会报异常
//            PendingIntent pendingintent = PendingIntent.getActivity(CApplication.getAppContext(), 0,
//                    new Intent(CApplication.getAppContext(), ActivityManager.getHomeActivityCls()), PendingIntent.FLAG_UPDATE_CURRENT);
//            builder.setContentIntent(pendingintent);
//            nm.notify(getNotifyId(taskId), builder.build());
//        }
//    }

//    static DownloadNotificationManager getInstance() {
//        if (instance == null) {
//            instance = new DownloadNotificationManager();
//        }
//        return instance;
//    }

//    private DownloadNotificationManager() {
//        nm = (NotificationManager) CApplication.getSysService(android.content.Context.NOTIFICATION_SERVICE);
//    }
//
//    void showNotification(String taskId, int code, String pushTitle) {
//        Loger.d(TAG, "-->showNotification(), taskId=" + taskId + ", code=" + code);
//        if (!TextUtils.isEmpty(pushTitle)) {
//            Builder builder = mBuilderMap.get(taskId);
//            if (builder == null) {
//                builder = new NotificationCompat.Builder(CApplication.getAppContext());
//                mBuilderMap.put(taskId, builder);
//            }
//            builder.setContentTitle(pushTitle);
//            if (VersionUtils.hasLOLLIPOP()) {
//                builder.setSmallIcon(R.drawable.push_icon);
//            } else {
//                builder.setSmallIcon(R.mipmap.ic_launcher_qqsports);
//            }
//            builder.setOngoing(false).setAutoCancel(true).setProgress(0, 0, false);
//            switch (code) {
//                case DownloadListener.STATE_COMPLETE:
//                    String localAPKPath = DownloadUtils.getDownloadRealFilePath(taskId);
//                    String apkPackageName = CommonUtil.getPackageNameFromApk(CApplication.getAppContext(), localAPKPath);
//                    boolean isAPK = !TextUtils.isEmpty(apkPackageName);
//                    builder.setContentText("下载完成" + (isAPK ? ", 点击安装" : ""));
//                    if (isAPK) {
//                        Intent intent = new Intent(Intent.ACTION_VIEW);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                        Uri contentUri = FileProvider.getUriForFile(CApplication.getAppContext(), CacheManager.FILE_PROVIDER_AUTHORITY, new File(localAPKPath));
//                        Loger.i(TAG, "download complete, contentUri: " + contentUri + ", localApkPath: " + localAPKPath + ", apkPackageName: " + apkPackageName);
//                        intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
//                        PendingIntent pendingintent = PendingIntent.getActivity(CApplication.getAppContext(),
//                                0,
//                                intent,
//                                PendingIntent.FLAG_CANCEL_CURRENT);
//                        builder.setContentIntent(pendingintent);
//                    }
//                    break;
//                case DownloadListener.STATE_ERROR:
//                    builder.setContentText("下载失败");
//                    break;
//                case DownloadListener.STATE_PAUSED:
//                    builder.setContentText("下载已停止");
//                    break;
//                case DownloadListener.STATE_ONGOING:
//                    builder.setContentText("正在下载");
//                    break;
//            }
//            nm.notify(getNotifyId(taskId), builder.build());
//        } else {
//            removeNotification(taskId);
//        }
//    }
}