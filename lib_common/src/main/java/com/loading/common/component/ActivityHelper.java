package com.loading.common.component;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.loading.common.utils.VersionUtils;

public class ActivityHelper {
    public static boolean isActivityFinished(Activity tActivity) {
        return tActivity != null && (tActivity.isFinishing() || (VersionUtils.hasJellyBeanMR1() && tActivity.isDestroyed()));
    }

    public static void startActivity(Context fromContext, Class<?> toActivityClass) {
        if (fromContext != null) {
            Intent tIntent = new Intent(fromContext, toActivityClass);
            fromContext.startActivity(tIntent);
        }
    }
    
    public static void startActivityByIntent(Context fromContext, Intent intent) {
        if (fromContext != null) {
            fromContext.startActivity(intent);
        }
    }
}
