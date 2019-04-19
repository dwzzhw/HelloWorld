package com.tencent.qqsports.download.limit;

import com.loading.common.utils.Loger;
import com.loading.common.utils.NameRunnable;
import com.tencent.qqsports.download.data.DownloadDataInfo;

import java.io.FileNotFoundException;
import java.io.RandomAccessFile;

public class LimitSpeedDownloadRunnable extends NameRunnable implements SliceDownloader.ISliceListener {
    private static final String TAG = "LimitSpeedDownloadRunnable";
    private final static String RUNNABLE_NAME = "LimitSpeedDownloadRunnable";

    private FlowController mFlowController;
    private SliceDownloader mCurrentSliceDownloader = null;
    private LimitSpeedDownloader mLimitSpeedDownloader = null;
    private RandomAccessFile mRandomAccessFile = null;
    private DownloadDataInfo mDownloadDataInfo = null;
    private boolean isDownloadCompleted = false;
    private boolean isDownloadCanceled = false;

    public LimitSpeedDownloadRunnable(DownloadDataInfo downloadInfo, LimitSpeedDownloader downloader) {
        super(RUNNABLE_NAME + "-" + System.currentTimeMillis());
        mDownloadDataInfo = downloadInfo;
        mLimitSpeedDownloader = downloader;
        mFlowController = new FlowController();
    }

    private void doDownloadTaskInSubThread(DownloadDataInfo downloadInfo) {
        Loger.d(TAG, "-->doDownloadTaskInSubThread(), isDownloadCompleted=" + isDownloadCompleted + ", isDownloadCanceled=" + isDownloadCanceled + ", downloadInfo=" + downloadInfo);
        while (!isDownloadCompleted && !isDownloadCanceled && downloadInfo != null && mLimitSpeedDownloader != null) {
            if (mRandomAccessFile == null) {
                try {
                    mRandomAccessFile = new RandomAccessFile(mLimitSpeedDownloader.getTempFilePath(), "rwd");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
            mCurrentSliceDownloader = new SliceDownloader(downloadInfo.getUrlStr(), downloadInfo.getCompleteSize(),
                    downloadInfo.getCompleteSize() + mFlowController.getNextSliceSize(), mRandomAccessFile, LimitSpeedDownloadRunnable.this);
            long sliceStartTime = System.currentTimeMillis();
            mCurrentSliceDownloader.startDownload();
            mFlowController.adjustFlowControlParameters(System.currentTimeMillis() - sliceStartTime);

            try {
                Thread.sleep(mFlowController.getNextSleepTime());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        doDownloadTaskInSubThread(mDownloadDataInfo);
    }

    public void cancelDownload() {
        isDownloadCanceled = true;
        if (mCurrentSliceDownloader != null) {
            mCurrentSliceDownloader.cancelDownload();
        }
    }

    public boolean isAllDownloadTaskFinished() {
        return isDownloadCompleted;
    }

    private void updateTotalCompleteSize() {
        if (mCurrentSliceDownloader != null && mDownloadDataInfo != null) {
            mDownloadDataInfo.setCompleteSize(mCurrentSliceDownloader.getTotalCompleteSize());
        }
    }

    private boolean isDownloadCompleted() {
        return mDownloadDataInfo != null && mDownloadDataInfo.isDownloadCompleted();
    }

    @Override
    public void onSliceDownloadProgressChanged(long downloadedSize) {
//        Loger.d(TAG, "-->onSliceDownloadProgressChanged(), downloadedSize=" + downloadedSize);
        updateTotalCompleteSize();
        notifyTotalDownloadStateChanged();
    }

    @Override
    public void onSliceDownloadError() {
        Loger.d(TAG, "-->onSliceDownloadError()");
        updateTotalCompleteSize();
        notifyTotalDownloadError();
    }

    @Override
    public void onSliceDownloadComplete() {
        Loger.d(TAG, "-->onSliceDownloadComplete()");
        updateTotalCompleteSize();
        if (isDownloadCompleted()) {
            isDownloadCompleted = true;
            notifyTotalDownloadComplete();
        } else {
            notifyTotalDownloadStateChanged();
        }
    }

    private void notifyTotalDownloadComplete() {
        if (mLimitSpeedDownloader != null) {
            mLimitSpeedDownloader.notifyDownloadComplete(mDownloadDataInfo);
        }
    }

    private void notifyTotalDownloadError() {
        if (mLimitSpeedDownloader != null) {
            mLimitSpeedDownloader.notifyDownloadError(mDownloadDataInfo);
        }
    }

    private void notifyTotalDownloadStateChanged() {
        if (mLimitSpeedDownloader != null) {
            mLimitSpeedDownloader.notifyDownloadUpdate(mDownloadDataInfo);
        }
    }

}
