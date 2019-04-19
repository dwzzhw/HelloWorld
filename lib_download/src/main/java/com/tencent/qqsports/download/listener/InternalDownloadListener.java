
package com.tencent.qqsports.download.listener;

import com.loading.modules.interfaces.download.DownloadRequest;
import com.tencent.qqsports.download.BaseDownloader;
import com.tencent.qqsports.download.data.DownloadDataInfo;

import java.util.List;
import java.util.Map;

/**
 * 下载监听器, 包含的状态较多, 仅供下载器内部使用
 */
public interface InternalDownloadListener {
    void onDownloadPause(BaseDownloader downloader, DownloadRequest downloadRequest, long completeSize, long totalSize, int percentProgress);
    void onDownloadUpdate(BaseDownloader downloader, DownloadRequest downloadRequest, long completeSize, long totalSize, int percentProgress);
    void onDownloadError(BaseDownloader downloader, DownloadRequest downloadRequest, long completeSize, long totalSize, int percentProgress);
    void onDownloadFinish(BaseDownloader downloader, DownloadRequest downloadRequest, long completeSize, long totalSize);

    void executeTask(Runnable runnable);
    void cancelTask(Runnable runnable);

    String getFinalDownloadPath(String taskId);

    boolean syncFileToDiskCache(String taskId, String tempFilePath, Map<String, List<String>> respHeaders);

    void checkAndWaitReady();
    List<DownloadDataInfo> getDownloadInfoFromTaskId(String taskId);
    long insertDownloadInfo(DownloadDataInfo downloadDataInfo);
    void updateDownloadInfos(DownloadDataInfo... downloadDataInfo);
    void removeDownloadInfos(String taskId);
}
