package com.loading.comp.download;

import androidx.room.Room;
import android.content.IntentFilter;
import androidx.annotation.NonNull;
import android.text.TextUtils;

import com.loading.common.component.CApplication;
import com.loading.common.manager.CacheManager;
import com.loading.common.manager.ListenerManager;
import com.loading.common.toolbox.FileDiskLruCache;
import com.loading.common.utils.AsyncOperationUtil;
import com.loading.common.utils.CommonUtil;
import com.loading.common.utils.HttpUtils;
import com.loading.common.utils.Loger;
import com.loading.common.utils.ObjectHelper;
import com.loading.common.utils.TagRunnable;
import com.loading.common.utils.UiThreadUtil;
import com.loading.comp.download.data.DownloadDataDBHelper;
import com.loading.comp.download.limit.LimitSpeedDownloader;
import com.loading.comp.download.utils.DownloadUtils;
import com.loading.modules.interfaces.download.DownloadCheckListener;
import com.loading.modules.interfaces.download.DownloadListener;
import com.loading.modules.interfaces.download.DownloadRequest;
import com.loading.comp.download.data.DownloadDataInfo;
import com.loading.comp.download.listener.DownloadAppInstallListener;
import com.loading.comp.download.listener.InternalDownloadListener;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 到本类时,假设已经进行了下载必要性检查, 直接依赖此前下载状况继续下载流程即可
 * 如app的版本比较不在本类的设计范围
 */
@SuppressWarnings("WeakerAccess")
public class DownloadManager {
    private static final String TAG = "DownloadManager";
    private static final int STATE_INIT = 0; //未初始化完成
    private static final int STATE_ALIVE = 1; //初始化完成
    private static final int STATE_DESTROYED = 2; //已销毁

    private final static long DOWN_LOAD_CACHE_MAX_SIZE = 60 * 1024 * 1024L;
    private final static long EXPIRE_TIME = 3 * 24 * 60 * 1000;
    /**
     * 当前正在下载的任务
     **/
    private final LinkedHashMap<String, BaseDownloader> mDownloaders = new LinkedHashMap<>();
    private final ConcurrentMap<String, ListenerManager<DownloadListener>> mListenersMap = new ConcurrentHashMap<>();

    private final Object mLockObj = new Object();
    private volatile int mInitState;

    private FileDiskLruCache mDiskLruCache;
    private ThreadPoolExecutor mExecutor;
    private DownloadDataDBHelper mDbHelper;
    private DownloadNotificationManager mNotifyMgr;
    private Queue<DownloadRequest> mPendingBackgroundRequest = null;

    private DownloadAppInstallListener mAppInstallListener = null;

    public static DownloadManager getInstance() {
        return InstanceHolder.instance;
    }

    private static final class InstanceHolder {
        static DownloadManager instance = new DownloadManager();
    }

    public void asyncInitConfig() {
        AsyncOperationUtil.asyncOperation(() -> {
            synchronized (mLockObj) {
                mInitState = STATE_INIT;
                if (mDbHelper == null || !mDbHelper.isOpen()) {
                    mDbHelper = Room.databaseBuilder(CApplication.getAppContext(),
                            DownloadDataDBHelper.class,
                            DownloadDataInfo.DATABASE_NAME)
                            .fallbackToDestructiveMigration()
                            .build();
                }
                mDiskLruCache.syncInitCache(CacheManager.getDownloadDir());
                mInitState = STATE_ALIVE;
                mLockObj.notify();
                checkAndDelExpiredDownloadInfo();
            }
            Loger.d(TAG, "init db cache done ...");
        }, null, TAG);
    }

    private DownloadManager() {
        mDiskLruCache = new FileDiskLruCache();
        mDiskLruCache.setMaxCacheSize(DOWN_LOAD_CACHE_MAX_SIZE);
        mExecutor = new ThreadPoolExecutor(1, 2, 60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>());
        mExecutor.setRejectedExecutionHandler((r, executor) -> {
            DownloadRunnable downloadRunnable = r instanceof DownloadRunnable ? (DownloadRunnable) r : null;
            final Downloader downloader = downloadRunnable != null ? downloadRunnable.getDownloader() : null;
            if (downloader != null) {
                UiThreadUtil.runOnUiThread(() -> {
                    Loger.w(TAG, "the download task is rejected!!....");
                    downloader.notifyDownloadError(null);
                });
            }
        });
        mPendingBackgroundRequest = new LinkedBlockingQueue<>();
    }

    public String startDownload(String fileUrl, String md5, DownloadListener downloadListener) {
        return startDownload(fileUrl, md5, 0, downloadListener);
    }

    public String startDownload(String fileUrl, String md5, int fileSize, DownloadListener downloadListener) {
        return !TextUtils.isEmpty(fileUrl) ? startDownload(DownloadRequest.newInstance(CommonUtil.md5String(fileUrl), fileUrl, md5, fileSize), downloadListener) : null;
    }

    public synchronized String startDownload(final DownloadRequest downloadRequest, DownloadListener downloadListener) {
        String taskId = null;
        Loger.d(TAG, "startDownload(), downloadRequest=" + downloadRequest + ", downloadListener: " + downloadListener);
        if (downloadRequest != null) {
            taskId = downloadRequest.getTaskId();
            String url = downloadRequest.getUrl();
            ObjectHelper.requireNotNull(downloadRequest.getUrl(), "download url must not be null!");
            if (TextUtils.isEmpty(taskId)) {
                taskId = CommonUtil.md5String(downloadRequest.getUrl());
                downloadRequest.setTaskId(taskId);
            }
            boolean isAddExeTask = false;
            BaseDownloader downloader = getDownloader(taskId);
            //Remove not match former download request
            if (downloader != null && !downloader.canMatchRequest(downloadRequest)) {
                Loger.d(TAG, "-->found not match existing request, cancel it, url=" + downloadRequest.getUrl());
                downloader.cancelDownload();
                removeDownloader(taskId);
                downloader = null;
            }
            if (downloader == null) {
                BaseDownloader backgroundDownloader = getOngoingBackgroundRequest();
                if (backgroundDownloader == null || !downloadRequest.isBackgroundReq()) {
                    if (downloadRequest.getQueriedFileSize() == DownloadRequest.QUERY_FILE_INFO_STATUS_NOT_YET) { //query file size from server
                        downloadRequest.setDownloadListener(downloadListener);
                        queryFileInfoFromServer(downloadRequest);
                        return taskId;
                    }
                    downloader = createDownloaderFromRequest(downloadRequest, mLoaderListener);
                    if (mPendingBackgroundRequest.contains(downloadRequest)) {
                        mPendingBackgroundRequest.remove(downloadRequest);
                    }
                    addDownloader(taskId, downloader);
                    isAddExeTask = true;
                } else {
                    Loger.w(TAG, "-->startDownload(), try start background downloader, but some item already on the way, item=" + backgroundDownloader + ", request=" + downloadRequest);
                    if (downloadListener != null) {
                        downloadRequest.setDownloadListener(downloadListener);
                    }
                    if (!mPendingBackgroundRequest.contains(downloadRequest)) {
                        mPendingBackgroundRequest.offer(downloadRequest);
                    }
                }
            } else {
                if (downloader.isTaskExpired(url, downloadRequest.getMd5String())) {
                    downloader.cancelDownload();
                }
                downloader.setDownloadRequest(downloadRequest);
                isAddExeTask = !downloader.isDownloadGoOn();
            }
            addListener(taskId, downloadListener);
            if (downloadRequest.isDownloadApk()) {
                registerAppInstallReceiver();
            }
            Loger.d(TAG, "taskId: " + taskId + ", isAddExeTask: " + isAddExeTask + ", url: " + url + ", downloader=" + downloader);
            if (isAddExeTask) {
                executeTask((downloader::startDownload));
            }
        }
        return taskId;
    }

    private void queryFileInfoFromServer(DownloadRequest downloadRequest) {
        executeTask(() -> {
            if (downloadRequest != null) {
                final String downloadUrl = downloadRequest.getUrl();

                DownloadUtils.asyncQueryFileInfo(downloadUrl, (url, success, headerFieldsMap) -> {
                    onQueryFileInfoDone(downloadRequest, success, headerFieldsMap);
                });
            }
        });
    }

    private void onQueryFileInfoDone(DownloadRequest downloadRequest, boolean success, Map<String, List<String>> respHeaders) {
        if (downloadRequest != null) {
            int totalFileSize = DownloadRequest.QUERY_FILE_INFO_STATUS_FAIL;
            if (HttpUtils.isSupportRange(respHeaders)) {
                totalFileSize = HttpUtils.getContentLength(respHeaders);
            }
            downloadRequest.setQueriedFileSize(totalFileSize);
            if (totalFileSize <= 0 || !success) {
                downloadRequest.disableSliceDownload();
            }
            startDownload(downloadRequest, downloadRequest.getDownloadListener());
        }
    }

    private BaseDownloader createDownloaderFromRequest(DownloadRequest downloadRequest, InternalDownloadListener downloadListener) {
        BaseDownloader downloader = null;
        if (downloadRequest != null) {
            if (downloadRequest.isBackgroundReq() && downloadRequest.isSupportSliceDownload()
                    && downloadRequest.getQueriedFileSize() >= 2 * 1024 * 1024  //小文件不可采用分片下载
            ) {
                downloader = new LimitSpeedDownloader(downloadRequest, downloadListener);
            } else {
                downloader = new Downloader(downloadRequest, downloadListener);
            }
        }
        return downloader;
    }

    //NOTE: should be called in non-ui thread
    public String syncGetDownloadFilePath(String taskId, String downloadUrl, String md5) {
        checkAndWaitReady();
        if (TextUtils.isEmpty(taskId) && !TextUtils.isEmpty(downloadUrl)) {
            taskId = getTaskIdFromUrl(downloadUrl);
        }
        String resultFilePath = mDiskLruCache != null ? mDiskLruCache.getCacheFilePath(taskId) : null;
        if (!TextUtils.isEmpty(md5) && !TextUtils.isEmpty(resultFilePath)) {
            String fileMd5 = CommonUtil.md5File(new File(resultFilePath));
            if (TextUtils.isEmpty(fileMd5) || !fileMd5.contains(md5)) {
                resultFilePath = null;
            }
        }
        return resultFilePath;
    }

    private String getTaskIdFromUrl(String downloadUrl) {
        return CommonUtil.md5String(downloadUrl);
    }

    private void tryPlayingPendingBackgroundRequest() {
        Loger.d(TAG, "-->tryPlayingPendingBackgroundRequest()");
        BaseDownloader backgroundDownloader = getOngoingBackgroundRequest();
        if (backgroundDownloader != null) {
            Loger.d(TAG, "-->tryPlayingPendingBackgroundRequest(), background downloader already on the way, item=" + backgroundDownloader);
            return;
        }
        if (mPendingBackgroundRequest != null && mPendingBackgroundRequest.size() > 0) {
            DownloadRequest downloadRequest = mPendingBackgroundRequest.poll();
            if (downloadRequest != null) {
                DownloadListener listener = downloadRequest.getDownloadListener();
                startDownload(downloadRequest, listener);
            } else {
                tryPlayingPendingBackgroundRequest();
            }
        }
    }

    private synchronized BaseDownloader getOngoingBackgroundRequest() {
        BaseDownloader backgroundDownloader = null;
        for (BaseDownloader downloader : mDownloaders.values()) {
            if (downloader != null && (downloader.isBackgroundTask() || (downloader.getDownloadRequest() != null && downloader.getDownloadRequest().isBackgroundReq()))) {
                backgroundDownloader = downloader;
                break;
            }
        }
        return backgroundDownloader;
    }

    public void asyncGetDownloadFilePath(final String taskId, final String downloadUrl, final String md5, final DownloadCheckListener checkListener) {
        AsyncOperationUtil.asyncOperation(new TagRunnable<String>() {
            @Override
            public void run() {
                setTagObj(syncGetDownloadFilePath(taskId, downloadUrl, md5));
            }
        }, objData -> {
            if (checkListener != null && objData instanceof TagRunnable) {
                @SuppressWarnings("unchecked")
                String filePath = ((TagRunnable<String>) objData).getTagObj();
                checkListener.onGetFilePath(filePath);
            }
        }, TAG);
    }

    public void cancelDownload(String taskId) {
        removeListeners(taskId);
        BaseDownloader downloader = getDownloader(taskId);
        if (downloader != null) {
            removeDownloader(taskId);
            downloader.cancelDownload();
        }
    }

    public void pauseDownload(String taskId) {
        BaseDownloader downloader = getDownloader(taskId);
        if (downloader != null) {
            downloader.pauseDownload();
        }
    }

    /**
     * 通知下载任务在 stauts bar 上显示下载进度
     *
     * @param taskId      download taskid
     * @param notifyTitle 显示下载进度时的 title
     */
    public void startNotifyStatusBar(String taskId, String notifyTitle) {
        if (mNotifyMgr == null) {
            mNotifyMgr = new DownloadNotificationManager();
        }
        mNotifyMgr.startNotify(taskId, notifyTitle);
        addListener(taskId, mNotifyMgr);
    }

    /**
     * 停止在 status bar 上显示进度
     *
     * @param taskId the download task id
     */
    public void stopNotifyStatusBar(String taskId) {
        if (mNotifyMgr != null) {
            mNotifyMgr.stopNotify(taskId);
        }
    }

    @SuppressWarnings("unused")
    public Map<String, List<String>> syncGetRespHeaders(String taskId, String downloadUrl) {
        checkAndWaitReady();
        return mDiskLruCache != null ? mDiskLruCache.syncReadHeaderMap(taskId) : null;
    }

    //NOTE: should not be called in non-ui thread
    public void removeDownloadFile(String taskId) {
        if (mDiskLruCache != null) {
            mDiskLruCache.syncRemoveCacheHeaderMap(taskId);
        }
    }

    //NOTE: should be called in non-ui thread
    public void clearDownloadTaskInfo(String taskId) {
        AsyncOperationUtil.asyncOperation(() -> {
            if (!TextUtils.isEmpty(taskId)) {
                cancelDownload(taskId);
                removeDownloadInfos(taskId);
                removeDownloadFile(taskId);
            }
        });
    }

    public void asyncClose() {
        AsyncOperationUtil.asyncOperation(this::close, null, TAG);
    }

    /**
     * 背景流量下载不生效时，改用普通下载方式
     *
     * @param downloadRequest
     */
    public void retryNormalDownload(DownloadRequest downloadRequest) {
        if (downloadRequest != null) {
            downloadRequest.disableSliceDownload();

            removeDownloader(downloadRequest.getTaskId());
            startDownload(downloadRequest, null);
        }
    }

    private void clearTaskInfo(final String taskId) {
        removeDownloader(taskId);
        removeListeners(taskId);
    }

    private void checkAndDelExpiredDownloadInfo() {
        if (mDbHelper != null) {
            mDbHelper.removeExpiredDownloadInfos(EXPIRE_TIME);
        }
    }

    private void close() {
        checkAndWaitReady();
        mInitState = STATE_DESTROYED;
        if (mDiskLruCache != null) {
            mDiskLruCache.closeCache();
        }
        if (mDbHelper != null) {
            mDbHelper.close();
        }
    }

    public synchronized void onDestroy() {
        checkAndWaitReady();
        mInitState = STATE_DESTROYED;
        if (mDiskLruCache != null) {
            mDiskLruCache.destroy();
        }
        if (!UiThreadUtil.isMainThread()) {
            if (mDbHelper != null) {
                mDbHelper.clearAllTables();
            }
        } else {
            AsyncOperationUtil.asyncOperation(() -> {
                if (mDbHelper != null) {
                    mDbHelper.clearAllTables();
                }
            });
        }
    }

    private synchronized void addDownloader(final String taskId, BaseDownloader downloader) {
        if (!TextUtils.isEmpty(taskId) && downloader != null) {
            mDownloaders.put(taskId, downloader);
        }
    }

    private synchronized void removeDownloader(final String taskId) {
        if (!TextUtils.isEmpty(taskId)) {
            mDownloaders.remove(taskId);
        }
    }

    private synchronized BaseDownloader getDownloader(String taskId) {
        return !TextUtils.isEmpty(taskId) ? mDownloaders.get(taskId) : null;
    }

    private void registerAppInstallReceiver() {
        Loger.d(TAG, "-->registerAppInstallReceiver()");
        if (mAppInstallListener == null) {
            mAppInstallListener = new DownloadAppInstallListener();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addDataScheme("package");
            intentFilter.addAction("android.intent.action.PACKAGE_REMOVED");
            intentFilter.addAction("android.intent.action.PACKAGE_ADDED");
            CApplication.getAppContext().registerReceiver(mAppInstallListener, intentFilter);
        }
    }

    private synchronized void addListener(String taskId, DownloadListener downloadListener) {
        Loger.d(TAG, "-->addListener(), taskId=" + taskId + ", downloadListener=" + downloadListener);
        if (!TextUtils.isEmpty(taskId) && downloadListener != null) {
            ListenerManager<DownloadListener> listenerManager = mListenersMap.get(taskId);
            if (listenerManager == null) {
                listenerManager = new ListenerManager<>();
                mListenersMap.put(taskId, listenerManager);
            }
            listenerManager.addListener(downloadListener);
        }
    }

    public synchronized void removeListeners(String taskId) {
        Loger.d(TAG, "-->removeListeners(), taskId=" + taskId);
        if (!TextUtils.isEmpty(taskId)) {
            mListenersMap.remove(taskId);
        }
    }

    @SuppressWarnings("UnusedReturnValue")
    public boolean removeListener(DownloadListener downloadListener) {
        return removeListener(null, downloadListener);
    }

    public synchronized boolean removeListener(String taskId, DownloadListener downloadListener) {
        boolean isRemoved = false;
        if (downloadListener != null) {
            if (TextUtils.isEmpty(taskId)) {
                for (Map.Entry<String, ListenerManager<DownloadListener>> entry : mListenersMap.entrySet()) {
                    ListenerManager<DownloadListener> listenerManager = entry.getValue();
                    if (listenerManager != null && listenerManager.removeListener(downloadListener)) {
                        isRemoved = true;
                    }
                }
            } else {
                ListenerManager<DownloadListener> listenerManager = mListenersMap.get(taskId);
                isRemoved = listenerManager != null && listenerManager.removeListener(downloadListener);
            }
        }
        return isRemoved;
    }

    private void executeTask(Runnable taskRunnale) {
        if (taskRunnale != null) {
            mExecutor.execute(taskRunnale);
        }
    }

    private void cancelTask(Runnable runnable) {
        if (runnable != null) {
            mExecutor.remove(runnable);
        }
    }

    private void checkAndWaitReady() {
        try {
            synchronized (mLockObj) {
                Loger.d(TAG, "checkAndWaitReady, state = " + mInitState);
                while (mInitState == STATE_INIT) {
                    mLockObj.wait();
                }
            }
        } catch (Exception e) {
            Loger.e(TAG, "wait exception ...");
        }
    }

    private List<DownloadDataInfo> getDownloadInfoFromTaskId(String taskId) {
        return mDbHelper != null ? mDbHelper.getDownloadInfoForTask(taskId) : null;
    }

    private long insertDownloadInfo(DownloadDataInfo downloadDataInfo) {
        return mDbHelper != null ? mDbHelper.insertDownloadInfo(downloadDataInfo) : -1;
    }

    private void updateDownloadInfos(DownloadDataInfo... downloadDataInfos) {
        if (mDbHelper != null) {
            Loger.d(TAG, "updateDownloadInfos: " + downloadDataInfos[0]);
            mDbHelper.updateDownloadInfo(downloadDataInfos);
        }
    }

    private void removeDownloadInfos(String taskId) {
        if (mDbHelper != null && mInitState == STATE_ALIVE) {
            mDbHelper.removeDownloadInfos(taskId);
        }
    }

    private InternalDownloadListener mLoaderListener = new InternalDownloadListener() {
        @Override
        public void onDownloadPause(@NonNull BaseDownloader downloader, DownloadRequest downloadRequest, long completeSize, long totalSize, int percentProgress) {
            final String taskId = downloader.getTaskId();
            final String url = downloader.getDownloadUrl();
            final String tempFilePath = downloader.getTempFilePath();
            Loger.d(TAG, "-->onDownloadPause(), taskId: " + taskId + ", completeSize: " + completeSize + ", totalSize: " + totalSize);
            ListenerManager<DownloadListener> listenerManager = mListenersMap.get(taskId);
            if (listenerManager != null) {
                listenerManager.loopListenerList(listener -> listener.onDownloadPaused(taskId, url, tempFilePath, completeSize, totalSize, percentProgress, downloadRequest));
            }
        }

        @Override
        public void onDownloadUpdate(@NonNull BaseDownloader downloader, DownloadRequest downloadRequest, long completeSize, long totalSize, int percentProgress) {
            String taskId = downloader.getTaskId();
            final String url = downloader.getDownloadUrl();
            final String tempFilePath = downloader.getTempFilePath();
            final int downloadProgress = totalSize > 0 ? Math.min(100, (int) (completeSize * 100 / totalSize)) : 0;
            ListenerManager<DownloadListener> listenerManager = mListenersMap.get(taskId);
            if (listenerManager != null) {
                listenerManager.loopListenerList(listener -> listener.onDownloadProgress(taskId, url, tempFilePath, completeSize, totalSize, downloadProgress, downloadRequest));
            }
        }

        @Override
        public void onDownloadError(@NonNull BaseDownloader downloader, DownloadRequest downloadRequest, long completeSize, long totalSize, int percentProgress) {
            final String taskId = downloader.getTaskId();
            final String url = downloader.getDownloadUrl();
            final String tempFilePath = downloader.getTempFilePath();
            Loger.d(TAG, "-->onDownloadError(), taskId: " + taskId + ", url: " + url);
            ListenerManager<DownloadListener> listenerManager = mListenersMap.get(taskId);
            if (listenerManager != null) {
                listenerManager.loopListenerList(listener -> listener.onDownloadError(taskId, url, tempFilePath, completeSize, totalSize, percentProgress, downloadRequest));
            }
            clearTaskInfo(taskId);
            tryPlayingPendingBackgroundRequest();
        }

        @Override
        public void onDownloadFinish(@NonNull BaseDownloader downloader, DownloadRequest downloadRequest, long completeSize, long totalSize) {
            final String taskId = downloader.getTaskId();
            final String url = downloader.getDownloadUrl();
            final String finalFilePath = downloader.getFinalFilePath();
            Loger.d(TAG, "-->onDownloadFinish(), taskId: " + taskId + ", finalFilePath: " + finalFilePath + ", url: " + url);
            ListenerManager<DownloadListener> listenerManager = mListenersMap.get(taskId);
            if (listenerManager != null) {
                listenerManager.loopListenerList(listener -> listener.onDownloadComplete(taskId, url, finalFilePath, completeSize, totalSize, downloadRequest));
            }
            clearTaskInfo(taskId);
            tryPlayingPendingBackgroundRequest();
        }

        @Override
        public void executeTask(Runnable taskRunnale) {
            DownloadManager.this.executeTask(taskRunnale);
        }

        @Override
        public void cancelTask(Runnable runnable) {
            DownloadManager.this.cancelTask(runnable);
        }

        @Override
        public void checkAndWaitReady() {
            DownloadManager.this.checkAndWaitReady();
        }

        @Override
        public String getFinalDownloadPath(String taskId) {
            return mDiskLruCache != null ? mDiskLruCache.getFutureFilePath(taskId) : null;
        }

        @Override
        public boolean syncFileToDiskCache(String taskId, String tempFilePath, Map<String, List<String>> respHeaders) {
            boolean isSuccess = false;
            if (mDiskLruCache != null) {
                isSuccess = mDiskLruCache.syncWriteCacheFile(taskId, tempFilePath);
                if (respHeaders != null && respHeaders.size() > 0) {
                    mDiskLruCache.syncWriteHeaderMap(taskId, respHeaders);
                }
            }
            return isSuccess;
        }

        @Override
        public List<DownloadDataInfo> getDownloadInfoFromTaskId(String taskId) {
            return DownloadManager.this.getDownloadInfoFromTaskId(taskId);
        }

        @Override
        public long insertDownloadInfo(DownloadDataInfo downloadDataInfo) {
            return DownloadManager.this.insertDownloadInfo(downloadDataInfo);
        }

        @Override
        public void updateDownloadInfos(DownloadDataInfo... downloadDataInfos) {
            DownloadManager.this.updateDownloadInfos(downloadDataInfos);
        }

        @Override
        public void removeDownloadInfos(String taskId) {
            DownloadManager.this.removeDownloadInfos(taskId);
        }
    };
}