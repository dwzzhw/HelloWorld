package com.loading.appshell;

import android.app.Application;

import com.loading.common.component.CApplication;

public class ShellApplication extends Application {
    private static final String TAG = "ShellApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        CApplication.onAppCreate(this);
    }
}
