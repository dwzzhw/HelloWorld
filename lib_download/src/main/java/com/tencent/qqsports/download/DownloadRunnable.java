package com.tencent.qqsports.download;

import android.text.format.DateUtils;

import com.tencent.qqsports.common.http.HttpHeadersDef;
import com.tencent.qqsports.common.threadpool.NameRunnable;
import com.tencent.qqsports.common.util.CollectionUtils;
import com.tencent.qqsports.common.util.SystemUtil;
import com.tencent.qqsports.download.data.DownloadDataInfo;
import com.tencent.qqsports.download.limit.NetSpeedMonitor;
import com.tencent.qqsports.download.limit.RandomAccessFileCacheHelper;
import com.tencent.qqsports.download.utils.DownloadUtils;
import com.tencent.qqsports.logger.Loger;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.util.Collections;
import java.util.Map;

public class DownloadRunnable extends NameRunnable {
    private final static String TAG = "DownloadRunnable";
    private final static String RUNNABLE_NAME = "DownloadRunnable";
    private final static long CONNNECT_TIME_OUT = 15 * DateUtils.SECOND_IN_MILLIS;
    private final static long READ_TIME_OUT = 20 * DateUtils.SECOND_IN_MILLIS;

    private DownloadDataInfo downloadDataInfo;

    private HttpURLConnection connection = null;
    private RandomAccessFile randomAccessFile = null;
    private RandomAccessFileCacheHelper mRandomFileCacheHelper = null;
    private InputStream is = null;
    private Downloader mDownloader;
    private boolean isCancel;

    public synchronized void cancelDownload() {
        isCancel = true;
    }

    public synchronized boolean isCancel() {
        return isCancel;
    }

    public Downloader getDownloader() {
        return mDownloader;
    }

    DownloadRunnable(DownloadDataInfo dataInfo, Downloader downloader) {
        super(RUNNABLE_NAME + "-" + System.currentTimeMillis());
        downloadDataInfo = dataInfo;
        mDownloader = downloader;
    }

    @Override
    public void run() {
        try {
            long startPos = downloadDataInfo.getStartPos();
            long endPos = downloadDataInfo.getEndPos();
            long downloadSize = downloadDataInfo.getDownloadSize();
            long completeSize = downloadDataInfo.getCompleteSize();
            Loger.d(TAG, "downloadTask starts, downloadSize: " + downloadSize + ", completeSize: " + completeSize +
                    ", taskId: " + mDownloader.getTaskId() + ", url: " + mDownloader.getDownloadUrl());
            if (downloadSize > 0 && completeSize >= downloadSize & endPos > 0) {
                notifyDownloadComplete();
                return;
            }
            final String bytesRange = "bytes=" + (startPos + completeSize) + (endPos > 0 ? ("-" + (endPos - 1)) : "-");
            Map<String, String> requestHeader = downloadDataInfo.getRequestHeaderForRequest();
            if (CollectionUtils.isEmpty(requestHeader)) {
                requestHeader = Collections.singletonMap(HttpHeadersDef.RANGE, bytesRange);
            } else {
                requestHeader.remove(HttpHeadersDef.RANGE);
                requestHeader.remove(HttpHeadersDef.RANGE.toLowerCase());
                requestHeader.put(HttpHeadersDef.RANGE, bytesRange);
            }
            Loger.d(TAG, "requestHeader = " + requestHeader);
            Loger.d(TAG, "download bytes range: " + bytesRange);
            connection = DownloadUtils.getHttpConnection(downloadDataInfo.getUrlStr(),
                    true,
                    true,
                    (int) CONNNECT_TIME_OUT,
                    (int) READ_TIME_OUT,
                    requestHeader,
                    true);
            randomAccessFile = new RandomAccessFile(mDownloader.getTempFilePath(), "rwd");
            mRandomFileCacheHelper = new RandomAccessFileCacheHelper(randomAccessFile);
            mRandomFileCacheHelper.init(startPos + completeSize);
            Loger.d(TAG, "RandomAccessFile seek startPos = " + startPos + " completeSize = " + completeSize + ", connection: " + connection);
            if (connection != null) {
                is = connection.getInputStream();
                byte[] buffer = new byte[4096];
                int length = 0;
                //避免通知更新太频繁
                long lastNotifyTime = 0;

                long packageCostTime = System.nanoTime();
                boolean needLimitSpeed = mDownloader.needLimitDownloadSpeed();
                Loger.d(TAG, "download runnable, task id=" + mDownloader.getTaskId() + ", thread=" + Thread.currentThread().getName() + ", needLimitSpeed=" + needLimitSpeed);

                NetSpeedMonitor netSpeedMonitor = new NetSpeedMonitor();

                while (!isCancel() && (length = is.read(buffer)) != -1) {
                    packageCostTime = System.nanoTime() - packageCostTime;
                    if (needLimitSpeed) {
                        netSpeedMonitor.onPackageReceived(length, packageCostTime);
                    }
                    mRandomFileCacheHelper.cacheData(buffer, length);
                    completeSize += length;
                    downloadDataInfo.setCompleteSize(completeSize);
                    // 用消息将下载信息传给进度条，对进度条进行更新
                    long curTime = System.currentTimeMillis();
                    if (curTime - lastNotifyTime > 200) {  //200ms刷新一次
                        mRandomFileCacheHelper.flushCacheData();
                        notifyDownloadUpdate();
                        lastNotifyTime = curTime;
                    }
                    packageCostTime = System.nanoTime();
                }
                mRandomFileCacheHelper.flushCacheData();
                if (length < 0 && !isCancel()) { //means donwload complete
                    notifyDownloadComplete();
                }

                if (SystemUtil.getDebugMode()) {
                    synchronized (DownloadRunnable.this) {
                        Loger.d(TAG, "download complete, completeSize: " + completeSize + ", length: " + length + ", isCancel: " + isCancel);
                    }
                }
            } else {
                notifyDownloadError();
            }
        } catch (Exception e) {
            Loger.d(TAG, "exception when download:  " + e);
//            mDownloader.notifyDownloadUpdate(downloadDataInfo);
            notifyDownloadError();
        } finally {
            try {
                disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void notifyDownloadComplete() {
        synchronized (this) {
            if (!isCancel && mDownloader != null) {
                mDownloader.notifyDownloadComplete(downloadDataInfo);
            }
        }
    }


    private void notifyDownloadError() {
        synchronized (this) {
            if (!isCancel && mDownloader != null) {
                mDownloader.notifyDownloadError(downloadDataInfo);
            }
        }
    }


    private void notifyDownloadUpdate() {
        synchronized (this) {
            if (!isCancel && mDownloader != null) {
                mDownloader.notifyDownloadUpdate(downloadDataInfo);
            }
        }
    }


    private void disconnect() {
        try {
            if (is != null) {
                is.close();
                is = null;
            }

            if (randomAccessFile != null) {
                randomAccessFile.close();
                randomAccessFile = null;
            }

            if (connection != null) {
                connection.disconnect();
                connection = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
