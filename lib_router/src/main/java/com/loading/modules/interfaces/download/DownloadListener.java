
package com.loading.modules.interfaces.download;

/**
 * APP下载状态监听
 **/
public interface DownloadListener {
    void onDownloadProgress(final String taskId, final String downloadUrl, final String tempFilePath, long completeSize, long totalSize, int nProgress, DownloadRequest downloadRequest);

    void onDownloadPaused(final String taskId, final String downloadUrl, final String tempFilePath, long completeSize, long totalSize, int nProgress, DownloadRequest downloadRequest);

    void onDownloadError(final String taskId, final String downloadUrl, final String tempFilePath, long completeSize, long totalSize, int nProgress, DownloadRequest downloadRequest);

    void onDownloadComplete(String taskId, final String downloadUrl, final String finalFilePath, long completeSize, long totalSize, DownloadRequest downloadRequest);
}
