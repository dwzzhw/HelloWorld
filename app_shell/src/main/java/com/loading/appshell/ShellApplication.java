package com.loading.appshell;

import android.app.Application;

import com.loading.common.component.CApplication;
import com.loading.modules.ModuleManager;
import com.loading.modules.interfaces.commentpanel.ICommentPanelService;
import com.loading.modules.interfaces.download.IDownloadService;
import com.loading.modules.interfaces.face.IFaceService;
import com.loading.comp.commentbar.CommentPanelManager;
import com.loading.comp.download.DownloadModuleService;
import com.loading.comp.face.FaceManager;
import com.squareup.leakcanary.LeakCanary;

public class ShellApplication extends Application {
    private static final String TAG = "ShellApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        CApplication.onAppCreate(this);

        initDynamicModules();
        LeakCanary.install(this);
    }

    private void initDynamicModules() {
        ModuleManager.register(IFaceService.class, FaceManager.getInstance());
        DownloadModuleService downloadService = new DownloadModuleService();
        downloadService.asyncInitConfig();
        ModuleManager.register(IDownloadService.class, downloadService);
        ModuleManager.register(ICommentPanelService.class, new CommentPanelManager());
    }
}
