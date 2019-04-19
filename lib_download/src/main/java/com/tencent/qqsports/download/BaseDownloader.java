package com.tencent.qqsports.download;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.loading.common.manager.CacheManager;
import com.loading.common.utils.CommonUtil;
import com.loading.common.utils.FileHandler;
import com.loading.common.utils.HttpHeadersDef;
import com.loading.common.utils.HttpUtils;
import com.loading.common.utils.Loger;
import com.loading.common.utils.ObjectHelper;
import com.loading.modules.interfaces.download.DownloadRequest;
import com.tencent.qqsports.download.data.DownloadDataInfo;
import com.tencent.qqsports.download.listener.InternalDownloadListener;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 下载器基类，封装下载相关的公共方法，具体策略由子类决定
 *
 * @author loading 2018.12.20
 */
public abstract class BaseDownloader {
    private static final String TAG = "BaseDownloader";

    protected abstract void startDownload();

    protected abstract void cancelDownload();

    protected abstract boolean isAllDownloadTaskFinished();

    protected abstract boolean canMatchRequest(DownloadRequest request);

    protected abstract void onQueryFileInfoDone(boolean success, Map<String, List<String>> respHeader);

    protected abstract boolean isBackgroundTask();

    protected static final int DOWNLOAD_STATE_INIT = 1;// 初始化完成,尚未开始下载
    protected static final int DOWNLOAD_STATE_BEGIN = 2;// 开始下载
    protected static final int DOWNLOAD_STATE_UPDATE = 3;// 下载进度更新
    protected static final int DOWNLOAD_STATE_PAUSE = 4;// 下载暂停
    protected static final int DOWNLOAD_STATE_COMPLETED = 5;// 下载完成
    protected static final int DOWNLOAD_STATE_ERROR = 6;// 下载错误

    protected DownloadRequest mDownloadRequest;
    /**
     * 存放下载信息类的集合, 对应正在下载当前任务的线程数。可能一个或多个
     **/
    protected List<DownloadDataInfo> mDownloadInfoList;
    protected int mDownloadState = DOWNLOAD_STATE_INIT;
    /**
     * 后台文件总大小
     **/
    private long mTotalFileSize;
    /**
     * 现在总完成大小
     **/
    private long mCompleteSize;

    /**
     * 临时文件保存路径
     **/
    protected String localTempFilePath;
    /**
     * 下载后最终的文件路径
     */
    protected String mFinalFilePath;

    protected InternalDownloadListener mListenerMgr;

    protected Map<String, List<String>> mRespHeaders;

    public BaseDownloader(DownloadRequest downloadRequest, InternalDownloadListener listener) {
        mDownloadRequest = downloadRequest;
        mListenerMgr = listener;
        ObjectHelper.requireNotNull(downloadRequest, "download req null!");
        ObjectHelper.requireNotNull(listener, "the lister should not be null..");
    }

    private Handler mDownloaderMsgHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            Loger.d(TAG, "handleMessage, msg state: " + msg.what);
            if (mListenerMgr != null) {
                switch (msg.what) {
                    case DOWNLOAD_STATE_INIT:
                        break;
                    case DOWNLOAD_STATE_BEGIN:
                        mListenerMgr.onDownloadUpdate(BaseDownloader.this, mDownloadRequest, mCompleteSize, mTotalFileSize, getPercentProgress());
                        break;
                    case DOWNLOAD_STATE_PAUSE:
                        mListenerMgr.onDownloadPause(BaseDownloader.this, mDownloadRequest, mCompleteSize, mTotalFileSize, getPercentProgress());
                        break;
                    case DOWNLOAD_STATE_UPDATE:
                        mListenerMgr.onDownloadUpdate(BaseDownloader.this, mDownloadRequest, mCompleteSize, mTotalFileSize, getPercentProgress());
                        break;
                    case DOWNLOAD_STATE_COMPLETED:
                        mListenerMgr.onDownloadFinish(BaseDownloader.this, mDownloadRequest, mCompleteSize, mTotalFileSize);
                        break;
                    case DOWNLOAD_STATE_ERROR:
                        mListenerMgr.onDownloadError(BaseDownloader.this, mDownloadRequest, mCompleteSize, mTotalFileSize, getPercentProgress());
                        break;
                    default:
                        break;
                }
            }
        }
    };

    //----------------  Utils method begin  ----------------------------
    protected void checkAndWaitReady() {
        if (mListenerMgr != null) {
            mListenerMgr.checkAndWaitReady();
        }
    }

    protected void makeSureFilePath() {
        if (TextUtils.isEmpty(localTempFilePath)) {
            localTempFilePath = CacheManager.getDownloadTempDir() + File.separator + mDownloadRequest.getTaskId();
            Loger.d(TAG, "init the local temp file path, tempFilePath: " + localTempFilePath);
        }
        if (TextUtils.isEmpty(mFinalFilePath)) {
            mFinalFilePath = mListenerMgr != null ? mListenerMgr.getFinalDownloadPath(mDownloadRequest.getTaskId()) : null;
            Loger.d(TAG, "download final file path: " + mFinalFilePath);
        }
    }

    /**
     * 看是否需要下载，只判断是否有文件存在是不完全的，应该还有文件是否有效，也就是下载是否完成。最好的办法是md5校验
     *
     * @return true for need, else false
     */
    protected boolean isFileAuthOk(final String filePath) {
        boolean isFileComplete = false;
        File destFile = !TextUtils.isEmpty(filePath) ? new File(filePath) : null;
        if (destFile != null && destFile.exists()) {
            String md5 = mDownloadRequest.getMd5String();
            if (TextUtils.isEmpty(md5)) {
                isFileComplete = true;
            } else {
                String md5File = CommonUtil.md5File(destFile);
                isFileComplete = !TextUtils.isEmpty(md5File) && md5File.contains(md5);
            }
        }
        return isFileComplete;
    }

    protected void prepareTempFile(long targetFileSize) {
        RandomAccessFile accessFile = null;
        try {
            File file = FileHandler.makeDIRAndCreateFile(localTempFilePath);
            if (targetFileSize > 0) { //开启多线程下载的时候才会需要设置文件大小。。
                accessFile = new RandomAccessFile(file, "rwd");
                accessFile.setLength(targetFileSize);
            }
        } catch (Exception e) {
            Loger.e(TAG, e);
        } finally {
            try {
                if (accessFile != null) {
                    accessFile.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

//    private void postAdjustTempFileLength() {
//        RandomAccessFile accessFile = null;
//        try {
//            Loger.d(TAG, "postAdjustTempFileLength, mTotalFileSize: " + mTotalFileSize + ", mCompleteSize: " + mCompleteSize + ", tempFilePath: " + localTempFilePath);
//            if (mCompleteSize > 0 && !TextUtils.isEmpty(localTempFilePath)) {
//                accessFile = new RandomAccessFile(localTempFilePath, "rwd");
//                accessFile.setLength(mCompleteSize);
//            }
//        } catch (Exception ignore) {
//            Loger.e(TAG, "postAdjustTempFileLength: " + ignore);
//        } finally {
//            if (accessFile != null) {
//                try {
//                    accessFile.close();
//                } catch (IOException ignore) {
//                }
//            }
//        }
//    }

    protected boolean checkLocalTempFile() {
        boolean isOk = false;
        if (FileHandler.isDirFileExist(localTempFilePath)) {
            isOk = true;
            String md5 = mDownloadRequest.getMd5String();
            if (!TextUtils.isEmpty(md5)) {
                String fileMd5 = CommonUtil.md5File(new File(localTempFilePath));
                isOk = !TextUtils.isEmpty(fileMd5) && fileMd5.contains(md5);
                Loger.d(TAG, "isOK: " + isOk + ", fileMd5: " + fileMd5 + ", md5: " + md5 + ", tempFilePath: " + localTempFilePath);
            }
        }
        return isOk;
    }

    protected boolean syncFileToDiskCache() {
        return mListenerMgr != null &&
                mListenerMgr.syncFileToDiskCache(mDownloadRequest.getTaskId(),
                        localTempFilePath,
                        mDownloadRequest.isCacheResponseHeader() ? mRespHeaders : null);
    }

    protected synchronized List<DownloadDataInfo> getDownloadInfoList(String taskId) {
        return (isPause() && mDownloadInfoList != null && mDownloadInfoList.size() > 0) ? mDownloadInfoList : readDownloadInfosFromDb(taskId);
    }

    protected List<DownloadDataInfo> readDownloadInfosFromDb(String taskId) {
        return mListenerMgr != null ? mListenerMgr.getDownloadInfoFromTaskId(taskId) : null;
    }

    protected long insertDownloadInfoToDb(final DownloadDataInfo downloadDataInfo) {
        return mListenerMgr != null ? mListenerMgr.insertDownloadInfo(downloadDataInfo) : -1;
    }

    protected void removeDownloadInfosFromDb(String taskId) {
        if (mListenerMgr != null && !TextUtils.isEmpty(taskId)) {
            mListenerMgr.removeDownloadInfos(taskId);
        }
    }

    private void updateDownloadInfoToDb(final DownloadDataInfo... downloadDataInfo) {
        if (mListenerMgr != null) {
            mListenerMgr.updateDownloadInfos(downloadDataInfo);
        }
    }

    /**
     * summarize collection info
     */
    protected synchronized void collectDownLoadInfo() {
        if (mDownloadInfoList != null && mDownloadInfoList.size() > 0) {
            long totalFileSize = 0;
            long completeSize = 0;
            for (DownloadDataInfo downloadDataInfo : mDownloadInfoList) {
                totalFileSize += downloadDataInfo.getDownloadSize();
                completeSize += downloadDataInfo.getCompleteSize();
            }
            mTotalFileSize = totalFileSize;
            mCompleteSize = completeSize;
        }
        Loger.d(TAG, "collectDownLoadInfo, totalFileSize: " + mTotalFileSize + ", completeSize: " + mCompleteSize);
    }

    //----------------  Utils method end  ----------------------------


    //----------------  Query method begin  ----------------------------
    public long getCompleteSize() {
        return mCompleteSize;
    }

    public long getTotalSize() {
        return mTotalFileSize;
    }

    private int getPercentProgress() {
        return mTotalFileSize > 0 ? Math.min(100, (int) (mCompleteSize * 100 / mTotalFileSize)) : 0;
    }

    synchronized int getDownloadState() {
        return mDownloadState;
    }

    public synchronized void setDownloadState(int mDownloadState) {
        this.mDownloadState = mDownloadState;
    }

    public synchronized boolean isDownloadGoOn() {
        return mDownloadState == DOWNLOAD_STATE_INIT ||
                mDownloadState == DOWNLOAD_STATE_BEGIN ||
                mDownloadState == DOWNLOAD_STATE_UPDATE;
    }

    /**
     * 判断是否正在下载
     */
    public synchronized boolean isDownloading() {
        return mDownloadState == DOWNLOAD_STATE_BEGIN || mDownloadState == DOWNLOAD_STATE_UPDATE;
    }

    /**
     * 判断是否暂停
     */
    public synchronized boolean isPause() {
        return mDownloadState == DOWNLOAD_STATE_PAUSE || mDownloadState == DOWNLOAD_STATE_ERROR;
    }

    /**
     * 判断是否完成
     */
    public synchronized boolean isComplete() {
        return mDownloadState == DOWNLOAD_STATE_COMPLETED;
    }

    public String getTaskId() {
        return mDownloadRequest != null ? mDownloadRequest.getTaskId() : null;
    }

    public String getDownloadUrl() {
        return mDownloadRequest != null ? mDownloadRequest.getUrl() : null;
    }

    public String getPushTitle() {
        return mDownloadRequest != null ? mDownloadRequest.getPushTitle() : null;
    }

    public String getFinalFilePath() {
        return mFinalFilePath;
    }

    public String getTempFilePath() {
        return localTempFilePath;
    }

    public void setDownloadRequest(DownloadRequest mDownloadRequest) {
        this.mDownloadRequest = mDownloadRequest;
    }

    public boolean isTaskExpired(String url, String md5) {
        return mDownloadRequest.isRequestExpired(url, md5);
    }

    public boolean isSilentTask() {
        return mDownloadRequest.isSilent();
    }

    public DownloadRequest getDownloadRequest() {
        return mDownloadRequest;
    }

    public boolean needLimitDownloadSpeed() {
        return mDownloadRequest != null && mDownloadRequest.isBackgroundReq();
    }
    //----------------  Query method end  ----------------------------


    //----------------  Function method begin  ----------------------------
    protected void queryFileInfoFromServer() {
        final String downloadUrl = mDownloadRequest.getUrl();
        final long totalDownloadFileSize = mDownloadRequest.getFileSize();
        Loger.d(TAG, "now start download from scratch ..., url: " + downloadUrl + ", totalSize: " + totalDownloadFileSize);

        //dwz test
//        HttpHeadReq httpHeadReq = new HttpHeadReq(downloadUrl, new HttpReqListener() {
//            @Override
//            public void onReqComplete(NetRequest netReq, Object data) {
//                final Map<String, List<String>> respHeaders = netReq.getRespHeaders();
//                mRespHeaders = respHeaders;
//                onQueryFileInfoDone(true, respHeaders);
//            }
//
//            @Override
//            public void onReqError(NetRequest netReq, int retCode, String retMsg) {
//                Loger.w(TAG, "query download file size error, retcode: " + retCode + ", retMsg: " + retMsg);
//                onQueryFileInfoDone(false, null);
//            }
//        });
//
//        Map<String, String> header = mDownloadRequest.getRequestHeader();
//        if (CommonUtil.isEmpty(header)) {
//            header = Collections.singletonMap(HttpHeadersDef.ACCEPT_RANGE, HttpUtils.HTTP_ACCEPT_RANGE_OPTION);
//        } else {
//            header.remove(HttpHeadersDef.ACCEPT_RANGE);
//            header.remove(HttpHeadersDef.ACCEPT_RANGE.toLowerCase());
//            header.put(HttpHeadersDef.ACCEPT_RANGE, HttpUtils.HTTP_ACCEPT_RANGE_OPTION);
//        }
//        httpHeadReq.setHeader(header);
//        httpHeadReq.start();
    }

    public synchronized void pauseDownload() {
        cancelDownload();
        changeStateAndNotify(DOWNLOAD_STATE_PAUSE);
    }

    public void notifyDownloadUpdate(DownloadDataInfo downloadDataInfo) {
        collectDownLoadInfo();
        updateDownloadInfoToDb(downloadDataInfo);
        if (!isPause()) {
            changeStateAndNotify(DOWNLOAD_STATE_UPDATE);
        }
    }

    public synchronized void notifyDownloadComplete(DownloadDataInfo downloadDataInfo) {
        Loger.d(TAG, "-->notifyDownloadComplete()");
        if (downloadDataInfo != null && getDownloadState() != DOWNLOAD_STATE_COMPLETED) {
            collectDownLoadInfo();
            if (isAllDownloadTaskFinished()) {
//                postAdjustTempFileLength();
                if (checkLocalTempFile() && syncFileToDiskCache()) {
                    downloadComplete();
                } else {
                    downloadError();
                }
                removeDownloadInfosFromDb(mDownloadRequest.getTaskId());
            } else {
                downloadDataInfo.flatComplete();
                updateDownloadInfoToDb(downloadDataInfo);
            }
        }
    }

    /**
     * @param downloadDataInfo the download info, NOTE: it can be null when rejected execution
     */
    public synchronized void notifyDownloadError(DownloadDataInfo downloadDataInfo) {
        Loger.d(TAG, "-->notifyDownloadError()");
        cancelDownload();
        if (mListenerMgr != null) {
            mListenerMgr.removeDownloadInfos(getTaskId());
        }
        downloadError();
    }

    protected void executeTask(Runnable taskRunnalbe) {
        if (mListenerMgr != null) {
            mListenerMgr.executeTask(taskRunnalbe);
        }
    }
    //----------------  Function method end  ----------------------------


    //----------------  Notify method begin  ----------------------------
    private synchronized void notifyListener() {
        mDownloaderMsgHandler.sendEmptyMessage(mDownloadState);
    }

    protected synchronized void changeStateAndNotify(int downloadState) {
        this.mDownloadState = downloadState;
        notifyListener();
    }

    // 下载完成
    protected void downloadComplete() {
        Loger.d(TAG, "-->downloadComplete()");
        changeStateAndNotify(DOWNLOAD_STATE_COMPLETED);
    }

    // 下载出错
    public void downloadError() {
        changeStateAndNotify(DOWNLOAD_STATE_ERROR);
    }
    //----------------  Notify method end  ----------------------------
}
