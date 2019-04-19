package com.loading.modules.interfaces.download;

import com.loading.modules.IModuleInterface;
import com.loading.modules.annotation.Repeater;

import java.util.List;
import java.util.Map;

/**
 * Created by loading on 2018/8/28.
 */

@Repeater
public interface IDownloadService extends IModuleInterface {
    //NOTE: should be called in non-ui thread
//    void syncInitDbCache();
    void asyncInitConfig();
    void asyncClose();
    void syncDestroy();

    String startDownload(String url, String md5, DownloadListener downloadListener);
    String startDownload(String url, String md5, int size, DownloadListener downloadListener);
    String startDownload(DownloadRequest downloadRequest, DownloadListener downloadListener);

    /**
     * 这个是一个同步方法，含有IO操作，可能会阻塞UI，异步的场景请使用asyncCheckDownloadState
     * downloadUrl和fileMd5 用于校验此前下载文件的合法性，可忽略
     */
    String syncGetDownloadFilePath(String taskId, String downloadUrl, String fileMd5);
    void asyncGetDownloadFilePath(String taskId, String downloadUrl, String fileMd5, DownloadCheckListener checkListener);

    void cancelDownload(String taskId);


    /**
     * 通知下载任务在 stauts bar 上显示下载进度
     * @param taskId download taskid
     * @param notifyTitle 显示下载进度时的 title
     */
    void startNotifyStatusBar(String taskId, String notifyTitle);

    /**
     * 停止在 status bar 上显示进度
     * @param taskId the download task id
     */
    public void stopNotifyStatusBar(String taskId);

    void clearDownloadTaskInfo(String taskId);

    Map<String, List<String>> syncGetRespHeaders(String taskId, String downloadUrl);

    boolean removeListener(DownloadListener downloadListener);
    boolean removeListener(String taskId, DownloadListener downloadListener);
    void removeListeners(String taskId);
    
    void asyncQueryFileInfo(String url, Map<String, String> requestHeader, IQueryFileInfoListener queryFileInfoListener);
}
