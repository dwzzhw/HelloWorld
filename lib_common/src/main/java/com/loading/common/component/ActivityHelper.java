package com.loading.common.component;

import android.content.Context;
import android.content.Intent;

public class ActivityHelper {
    public static void startActivityByIntent(Context fromContext, Intent intent) {
        if (fromContext != null) {
            fromContext.startActivity(intent);
        }
    }
}
