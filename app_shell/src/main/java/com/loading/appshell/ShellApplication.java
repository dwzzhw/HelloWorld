package com.loading.appshell;

import android.app.Application;

import com.loading.common.component.CApplication;
import com.loading.modules.ModuleManager;
import com.loading.modules.interfaces.face.IFaceService;
import com.tencent.qqsports.face.FaceManager;

public class ShellApplication extends Application {
    private static final String TAG = "ShellApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        CApplication.onAppCreate(this);

        initDynamicModules();
    }

    private void initDynamicModules() {
        ModuleManager.register(IFaceService.class, FaceManager.getInstance());
    }
}
