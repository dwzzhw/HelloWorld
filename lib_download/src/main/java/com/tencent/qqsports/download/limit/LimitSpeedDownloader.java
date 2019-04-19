package com.tencent.qqsports.download.limit;

import com.tencent.qqsports.common.http.HttpUtils;
import com.tencent.qqsports.common.util.FileHandler;
import com.tencent.qqsports.download.BaseDownloader;
import com.tencent.qqsports.download.DownloadManager;
import com.tencent.qqsports.download.data.DownloadDataInfo;
import com.tencent.qqsports.download.listener.InternalDownloadListener;
import com.tencent.qqsports.logger.Loger;
import com.tencent.qqsports.modules.interfaces.download.DownloadRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LimitSpeedDownloader extends BaseDownloader {
    private static final String TAG = "LimitSpeedDownloader";


    private DownloadDataInfo mCurrentDownloadInfo;
    private LimitSpeedDownloadRunnable mCurrentDownloadRunnable = null;

    public LimitSpeedDownloader(DownloadRequest downloadRequest, InternalDownloadListener listener) {
        super(downloadRequest, listener);
    }

    @Override
    protected void startDownload() {
        Loger.d(TAG, "-->startDownload(), url=" + mDownloadRequest.getUrl());
        checkAndWaitReady();
        makeSureFilePath();
        cancelDownload();
        changeStateAndNotify(DOWNLOAD_STATE_BEGIN);
        if (isFileAuthOk(mFinalFilePath)) {
            downloadComplete();
        } else {
            final String taskId = mDownloadRequest.getTaskId();
            Loger.d(TAG, "now prepare to download, taskId: " + taskId);
            //得到数据库中已有的urlstr的下载器的具体信息
            List<DownloadDataInfo> downLoadInfoList = getDownloadInfoList(taskId);
            if (downLoadInfoList != null && downLoadInfoList.size() == 1 && FileHandler.isDirFileExist(localTempFilePath)) {
                Loger.d(TAG, "now go on last time downloading, downloadInofList: " + downLoadInfoList);
                startDownloadTask(downLoadInfoList.get(0), false);
            } else {
                if (downLoadInfoList != null && downLoadInfoList.size() > 0) {
                    removeDownloadInfosFromDb(taskId);
                }
                queryFileInfoFromServer();
            }
        }
    }

    @Override
    protected void onQueryFileInfoDone(boolean success, Map<String, List<String>> respHeader) {
        if (success && respHeader != null) {
            int totalFileSize = 0;
            if (HttpUtils.isSupportRange(respHeader)) {
                totalFileSize = HttpUtils.getContentLength(respHeader);
                startDownloadTask(createDownloadInfo(totalFileSize), true);
            } else {
                tryNormalDownload();
            }
            Loger.i(TAG, "query file size from server: " + totalFileSize + ", respHeaders: " + respHeader);
        } else {
            Loger.w(TAG, "fail to query file info from server");
            tryNormalDownload();
        }
    }

    private DownloadDataInfo createDownloadInfo(long totalFileSize) {
        DownloadDataInfo downloadDataInfo = new DownloadDataInfo(0,
                0,
                totalFileSize,
                totalFileSize,
                0,
                mDownloadRequest.getUrl(),
                mDownloadRequest.getTaskId(),
                null,
                mDownloadRequest.getPushTitle(),
                null,
                null,
                mDownloadRequest.getMd5String(),
                mDownloadRequest.getRequestHeader(),
                System.currentTimeMillis());
        return downloadDataInfo;
    }

    private void startDownloadTask(DownloadDataInfo downloadInfo, boolean needInsertToDb) {
        if (mDownloadInfoList == null) {
            mDownloadInfoList = new ArrayList<>(1);
            mDownloadInfoList.add(downloadInfo);
        }
        mCurrentDownloadInfo = downloadInfo;
        mCurrentDownloadRunnable = new LimitSpeedDownloadRunnable(mCurrentDownloadInfo, this);
        //忽视调用线程，直接开启子线程下载
        executeTask(() -> {
            if (needInsertToDb && downloadInfo != null) {
                downloadInfo.setId(insertDownloadInfoToDb(downloadInfo));
            }
            mCurrentDownloadRunnable.run();
        });
    }

    private DownloadDataInfo getDownloadInfo() {
        return mDownloadInfoList != null && mDownloadInfoList.size() > 0 ? mDownloadInfoList.get(0) : null;
    }

    private void tryNormalDownload() {
        Loger.d(TAG, "-->tryNormalDownload()");
        DownloadManager.getInstance().retryNormalDownload(mDownloadRequest);
    }

    @Override
    protected void cancelDownload() {
        Loger.d(TAG, "-->cancelDownload()");
        if (mCurrentDownloadRunnable != null) {
            mCurrentDownloadRunnable.cancelDownload();
            mCurrentDownloadRunnable = null;
        }
    }

    @Override
    protected boolean isAllDownloadTaskFinished() {
        return mCurrentDownloadRunnable != null ? mCurrentDownloadRunnable.isAllDownloadTaskFinished() : false;
    }

    @Override
    public synchronized void notifyDownloadError(DownloadDataInfo downloadDataInfo) {
        cancelDownload();
        tryNormalDownload();
    }

    @Override
    protected boolean canMatchRequest(DownloadRequest request) {
        return request != null && request.isBackgroundReq() && request.isSupportSliceDownload();
    }

    @Override
    protected boolean isBackgroundTask() {
        return true;
    }
}
