package com.tencent.qqsports.download.limit;

import android.text.TextUtils;
import android.text.format.DateUtils;

import com.loading.common.utils.HttpHeadersDef;
import com.loading.common.utils.Loger;
import com.tencent.qqsports.download.utils.DownloadUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;


/**
 * 分片下载器，负责下载指定url里的一段内容
 *
 * @author loading 2018.12.21
 */
public class SliceDownloader {
    private static final String TAG = "SliceDownloader";
    private final static long CONNECT_TIME_OUT = 15 * DateUtils.SECOND_IN_MILLIS;
    private final static long READ_TIME_OUT = 20 * DateUtils.SECOND_IN_MILLIS;

    private HttpURLConnection connection = null;
    private InputStream is = null;

    private boolean isCancel;
    private long mEndPos;
    private long mStartPos;
    private RandomAccessFile mRandomAccessFile;
    private ISliceListener mSliceListener;
    private String mDownloadUrl;
    private RandomAccessFileCacheHelper mRandomFileCacheHelper;

    public SliceDownloader(String downloadUrl, long startPos, long endPos, RandomAccessFile destFile, ISliceListener listener) {
        mStartPos = startPos;
        mEndPos = endPos;
        mRandomAccessFile = destFile;
        mSliceListener = listener;
        mDownloadUrl = downloadUrl;
        mRandomFileCacheHelper = new RandomAccessFileCacheHelper(mRandomAccessFile);
    }

    public void startDownload() {
        try {
            Loger.d(TAG, "-->startDownload(), mStartPos= " + mStartPos + ", mEndPos=" + mEndPos +
                    ", completeSize=" + getSliceDownloaderCompleteSize() + ", url=" + mDownloadUrl);
            if (mEndPos > 0 && getSliceDownloaderCompleteSize() >= mEndPos - mStartPos) {
                notifyDownloadComplete();
                return;
            }

            final String bytesRange = "bytes=" + mStartPos + (mEndPos > 0 ? ("-" + (mEndPos - 1)) : "-");
            Map<String, String> requestHeader = new HashMap<>(2);
//                    Collections.singletonMap(HttpHeadersDef.RANGE, bytesRange);
            requestHeader.put(HttpHeadersDef.RANGE, bytesRange);
            requestHeader.put(HttpHeadersDef.CONNECTION, "Keep-Alive");
//            requestHeader.put(HttpHeadersDef.CONNECTION, "close");
            Loger.d(TAG, "download bytes range: " + bytesRange);
            connection = DownloadUtils.getHttpConnection(mDownloadUrl,
                    true,
                    true,
                    (int) CONNECT_TIME_OUT,
                    (int) READ_TIME_OUT,
                    requestHeader,
                    true);
            mRandomFileCacheHelper.init(mStartPos);
            Loger.d(TAG, "RandomAccessFile seek startPos = " + mStartPos + ", connection: " + connection);
            if (connection != null) {
                is = connection.getInputStream();
                byte[] buffer = new byte[RandomAccessFileCacheHelper.READ_STREAM_BUFFER_SIZE];
                int length = 0;
                //避免通知更新太频繁
                long lastNotifyTime = 0;
                while (!isCancel() && (length = is.read(buffer)) != -1) {
                    mRandomFileCacheHelper.cacheData(buffer, length);
                    // 用消息将下载信息传给进度条，对进度条进行更新
                    long curTime = System.currentTimeMillis();
                    if (curTime - lastNotifyTime > 200) {  //200ms刷新一次
                        mRandomFileCacheHelper.flushCacheData();
                        notifyDownloadProgress();
                        lastNotifyTime = curTime;
                    }
                }
                mRandomFileCacheHelper.flushCacheData();
                if (length < 0 && !isCancel()) { //means download complete
                    notifyDownloadComplete();
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

    private void disconnect() {
        try {
            if (is != null) {
                is.close();
                is = null;
            }

            if (connection != null) {
                connection.disconnect();
                connection = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized boolean isCancel() {
        return isCancel;
    }

    public synchronized void cancelDownload() {
        isCancel = true;
    }

    /**
     * 串行任务整体完成的已下载文件大小
     *
     * @return
     */
    public long getTotalCompleteSize() {
        return mStartPos + getSliceDownloaderCompleteSize();
    }

    private long getSliceDownloaderCompleteSize() {
        return mRandomFileCacheHelper.getCompleteSize();
    }

    private void notifyDownloadComplete() {
        if (!isCancel && mSliceListener != null) {
            mSliceListener.onSliceDownloadComplete();
        }
    }

    private void notifyDownloadError() {
        if (!isCancel && mSliceListener != null) {
            mSliceListener.onSliceDownloadError();
        }
    }

    private void notifyDownloadProgress() {
        if (!isCancel && mSliceListener != null) {
            mSliceListener.onSliceDownloadProgressChanged(getSliceDownloaderCompleteSize());
        }
    }

    public interface ISliceListener {
        void onSliceDownloadProgressChanged(long downloadedSize);

        void onSliceDownloadError();

        void onSliceDownloadComplete();
    }
}
