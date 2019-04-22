package com.loading.common.lifecycle;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;

public interface IActivityLifecycleCallback {
    default void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    }

    default void onActivityStarted(Activity activity) {
    }

    default void onActivityResumed(Activity activity) {
    }

    default void onActivityPaused(Activity activity) {
    }

    default void onActivityStopped(Activity activity) {
    }

    default void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    default void onActivityDestroyed(Activity activity) {
    }

    default void onConfigurationChanged(Configuration newConfig) {
    }
}
