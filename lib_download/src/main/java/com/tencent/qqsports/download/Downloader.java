package com.tencent.qqsports.download;

import android.util.SparseBooleanArray;

import com.loading.common.utils.AsyncOperationUtil;
import com.loading.common.utils.FileHandler;
import com.loading.common.utils.HttpUtils;
import com.loading.common.utils.Loger;
import com.loading.modules.interfaces.download.DownloadRequest;
import com.tencent.qqsports.download.data.DownloadDataInfo;
import com.tencent.qqsports.download.listener.InternalDownloadListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 全速下载器
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class Downloader extends BaseDownloader {
    private static final String TAG = "Downloader";
    private static final int MAX_THREAD_CNT = 2;// 下载线程数
    private static final long MULTI_DOWNLOAD_THRESHOLD = 10 * 1024 * 1024L;

    private static final boolean ALLOW_MULTI_DOWNLOAD = true;


    private List<DownloadRunnable> downLoadRunnables = new ArrayList<>(MAX_THREAD_CNT);
    private SparseBooleanArray mDownloadCompleteInfo;

    Downloader(DownloadRequest downloadRequest, InternalDownloadListener listener) {
        super(downloadRequest, listener);
    }

    /**
     * executed in non-ui-thread
     **/
    @Override
    protected void startDownload() {
        Loger.d(TAG, "-->startDownload()");
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
            if (downLoadInfoList != null && downLoadInfoList.size() > 0 && FileHandler.isDirFileExist(localTempFilePath)) {
                Loger.d(TAG, "now go on last time downloading, downloadInofList: " + downLoadInfoList);
                asyncDoDownloadTask(downLoadInfoList);
            } else {
                if (downLoadInfoList != null && downLoadInfoList.size() > 0) {
                    removeDownloadInfosFromDb(taskId);
                }
                final String downloadUrl = mDownloadRequest.getUrl();
                final long totalDownloadFileSize = mDownloadRequest.getFileSize();
                Loger.d(TAG, "now start download from scratch ..., url: " + downloadUrl + ", totalSize: " + totalDownloadFileSize);
                if (totalDownloadFileSize > 0) {
                    asyncDoDownloadTask(separateAndInsertDownLoadInfo(totalDownloadFileSize));
                } else {
                    queryFileInfoFromServer();
                }
            }
        }
    }

    @Override
    protected void onQueryFileInfoDone(boolean success, Map<String, List<String>> respHeader) {
        if (success && respHeader != null) {
            final int totalFileSize = HttpUtils.isSupportRange(respHeader) ? HttpUtils.getContentLength(respHeader) : 0;
            Loger.i(TAG, "query file size from server: " + totalFileSize + ", respHeaders: " + respHeader);
            AsyncOperationUtil.asyncOperation(() -> asyncDoDownloadTask(separateAndInsertDownLoadInfo(totalFileSize)), null, TAG);
        } else {
            AsyncOperationUtil.asyncOperation(() -> asyncDoDownloadTask(separateAndInsertDownLoadInfo(0)), null, TAG);
        }
    }

    @Override
    protected boolean isBackgroundTask() {
        return false;
    }

    private synchronized void asyncDoDownloadTask(final List<DownloadDataInfo> downloadDataInfoList) {
        if (downloadDataInfoList != null && downloadDataInfoList.size() > 0) {
            mDownloadInfoList = downloadDataInfoList;
            if (mDownloadCompleteInfo == null) {
                mDownloadCompleteInfo = new SparseBooleanArray();
            }
            long curTimeStamp = System.currentTimeMillis();
            for (DownloadDataInfo downloadDataInfo : downloadDataInfoList) {
                mDownloadCompleteInfo.put(downloadDataInfo.getSegmentId(), false);
                downloadDataInfo.setTimestamp(curTimeStamp);
                addDownloadTask(downloadDataInfo);
            }
            collectDownLoadInfo();
            changeStateAndNotify(DOWNLOAD_STATE_UPDATE);
        }
    }

    private void addDownloadTask(DownloadDataInfo downloadDataInfo) {
        if (downloadDataInfo != null) {
            DownloadRunnable runnable = new DownloadRunnable(downloadDataInfo, this);
            downLoadRunnables.add(runnable);
            executeTask(runnable);
        }
    }

    private List<DownloadDataInfo> separateAndInsertDownLoadInfo(long totalFileSize) {
        int downloadCnt = ALLOW_MULTI_DOWNLOAD && totalFileSize > MULTI_DOWNLOAD_THRESHOLD && !mDownloadRequest.isBackgroundReq()? MAX_THREAD_CNT : 1;
        List<DownloadDataInfo> downloadDataInfoList = new ArrayList<>(downloadCnt);
        long rangeSize = totalFileSize / downloadCnt;
        long startPos = 0;
        long endPos = 0;
        long downLoadSize = 0;
        prepareTempFile(totalFileSize);
        for (int i = 0; i < downloadCnt; i++) {
            if (rangeSize > 0) {
                startPos = i * rangeSize;
                endPos = (i == downloadCnt - 1) ? -1 : ((i + 1) * rangeSize);
                downLoadSize = endPos > 0 ? (endPos - startPos) : (totalFileSize - startPos);
            }
            DownloadDataInfo downloadDataInfo = new DownloadDataInfo(i,
                    startPos,
                    endPos,
                    downLoadSize,
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
            downloadDataInfo.setId(insertDownloadInfoToDb(downloadDataInfo));
            downloadDataInfoList.add(downloadDataInfo);
        }
        Loger.d(TAG, "separateAndInsertDownLoadInfo, totalSize: " + totalFileSize + ", downloadCnt: " + downloadCnt + ", rangeSize: " + rangeSize);
        return downloadDataInfoList;
    }

    @Override
    public synchronized void cancelDownload() {
        if (downLoadRunnables != null && downLoadRunnables.size() > 0) {
            for (DownloadRunnable runnable : downLoadRunnables) {
                if (runnable != null) {
                    runnable.cancelDownload();
                }
                if (mListenerMgr != null) {
                    mListenerMgr.cancelTask(runnable);
                }
            }
            downLoadRunnables.clear();
        }
        mDownloadState = DOWNLOAD_STATE_INIT;
    }

    protected boolean isAllDownloadTaskFinished() {
        boolean isDownloadComplete = true;
        for (int i = 0; i < mDownloadCompleteInfo.size(); i++) {
            isDownloadComplete = mDownloadCompleteInfo.get(mDownloadCompleteInfo.keyAt(i));
            if (!isDownloadComplete) {
                break;
            }
        }
        return isDownloadComplete;
    }

    @Override
    protected boolean canMatchRequest(DownloadRequest request) {
        return mDownloadRequest != null && mDownloadRequest.hasSameDownloadType(request);
    }

    @Override
    public synchronized void notifyDownloadComplete(DownloadDataInfo downloadDataInfo) {
        if (downloadDataInfo != null && getDownloadState() != DOWNLOAD_STATE_COMPLETED) {
            mDownloadCompleteInfo.put(downloadDataInfo.getSegmentId(), true);
        }
        super.notifyDownloadComplete(downloadDataInfo);
    }

    @Override
    public String toString() {
        return "Downloader{" +
                "request='" + mDownloadRequest + '\'' +
                ", mDownloadState=" + mDownloadState +
                '}';
    }
}