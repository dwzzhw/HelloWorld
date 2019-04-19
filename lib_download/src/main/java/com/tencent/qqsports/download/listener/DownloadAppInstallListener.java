package com.tencent.qqsports.download.listener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.tencent.qqsports.logger.Loger;

/**
 * Created by loading on 11/18/16.
 */

public class DownloadAppInstallListener extends BroadcastReceiver {
    private static final String TAG = "DownloadAppInstallListener";

    @Override
    public void onReceive(Context context, Intent intent) {
        String packageNameScheme = intent.getDataString();
        Loger.d(TAG, "-->onReceive(), action=" + intent.getAction() + ", packageNameScheme=" + packageNameScheme);
        appInstalled(packageNameScheme);
    }

    public void appInstalled(final String packageNameScheme) {
        if (!TextUtils.isEmpty(packageNameScheme)) {
            String[] p = packageNameScheme.split(":");
//            if (p.length > 1) {
//                final String packageName = p[1];
                //TODO: remove the installed apk file
//            }
        }
    }
}
