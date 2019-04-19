package com.tencent.qqsports.download;

import com.loading.modules.interfaces.download.DownloadCheckListener;
import com.loading.modules.interfaces.download.DownloadListener;
import com.loading.modules.interfaces.download.DownloadRequest;
import com.loading.modules.interfaces.download.IDownloadService;
import com.loading.modules.interfaces.download.IQueryFileInfoListener;
import com.tencent.qqsports.download.utils.DownloadUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by loading on 2018/8/28.
 */

@SuppressWarnings("unused")
public class DownloadModuleService implements IDownloadService {
    @Override
    public void asyncInitConfig() {
        DownloadManager.getInstance().asyncInitConfig();
    }

    @Override
    public void asyncClose() {
        DownloadManager.getInstance().asyncClose();
    }

    @Override
    public void syncDestroy() {
        DownloadManager.getInstance().onDestroy();
    }

    @Override
    public String startDownload(String url, String md5, DownloadListener downloadListener) {
        return DownloadManager.getInstance().startDownload(url, md5, downloadListener);
    }

    @Override
    public String startDownload(String url, String md5, int size, DownloadListener downloadListener) {
        return DownloadManager.getInstance().startDownload(url, md5, size, downloadListener);
    }

    @Override
    public String startDownload(DownloadRequest downloadRequest, DownloadListener downloadListener) {
        return DownloadManager.getInstance().startDownload(downloadRequest, downloadListener);
    }

    @Override
    public String syncGetDownloadFilePath(String taskId, String downloadUrl, String fileMd5) {
        return DownloadManager.getInstance().syncGetDownloadFilePath(taskId, downloadUrl, fileMd5);
    }

    @Override
    public void asyncGetDownloadFilePath(String taskId, String downloadUrl, String fileMd5, DownloadCheckListener checkListener) {
        DownloadManager.getInstance().asyncGetDownloadFilePath(taskId, downloadUrl, fileMd5, checkListener);
    }

    @Override
    public void clearDownloadTaskInfo(String taskId) {
        DownloadManager.getInstance().clearDownloadTaskInfo(taskId);
    }

    @Override
    public void cancelDownload(String taskId) {
        DownloadManager.getInstance().cancelDownload(taskId);
    }

    @Override
    public void startNotifyStatusBar(String taskId, String notifyTitle) {
        DownloadManager.getInstance().startNotifyStatusBar(taskId, notifyTitle);
    }

    @Override
    public void stopNotifyStatusBar(String taskId) {
        DownloadManager.getInstance().stopNotifyStatusBar(taskId);
    }

    @Override
    public Map<String, List<String>> syncGetRespHeaders(String taskId, String downloadUrl) {
        return DownloadManager.getInstance().syncGetRespHeaders(taskId, downloadUrl);
    }

    @Override
    public boolean removeListener(DownloadListener downloadListener) {
        return DownloadManager.getInstance().removeListener(downloadListener);
    }

    @Override
    public boolean removeListener(String taskId, DownloadListener downloadListener) {
        return DownloadManager.getInstance().removeListener(taskId, downloadListener);
    }

    @Override
    public void removeListeners(String taskId) {
        DownloadManager.getInstance().removeListeners(taskId);
    }

    @Override
    public void asyncQueryFileInfo(String url, Map<String, String> requestHeader, IQueryFileInfoListener queryFileInfoListener) {
        DownloadUtils.asyncQueryFileInfo(url, requestHeader, queryFileInfoListener);
    }
}