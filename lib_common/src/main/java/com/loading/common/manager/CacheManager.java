package com.loading.common.manager;

import android.text.TextUtils;

import com.loading.common.toolbox.FileDiskLruCache;
import com.loading.common.utils.AsyncOperationUtil;
import com.loading.common.utils.CommonUtil;
import com.loading.common.utils.FileHandler;
import com.loading.common.utils.FilePathUtil;
import com.loading.common.utils.Loger;
import com.loading.common.utils.TagRunnable;

import java.io.File;

/**
 * Created by huazhang on 10/3/15.
 * the cache manager manager the app caches
 */
@SuppressWarnings({"UnusedReturnValue", "WeakerAccess", "unused"})
public class CacheManager {
    private static final String TAG = CacheManager.class.getSimpleName();
    public static final String FILE_PROVIDER_AUTHORITY = "com.tencent.qqsports.fileProvider";
    private static final int MAX_CACHE = 15 * 1024 * 1024;

    private static final String CRASH_LOG_DIR = "crashLog";
    private static final String COMMON_CACHE_DIR = "commonCache";

    //NOTE: the APK_CACHE_DIR is related to file_paths.xml resource.
    private static final String APK_CACHE_DIR = "apkCache";

    private static final String PHOTO_DOWNLOAD_PATH = "Tencent/腾讯体育";
    private final static String VIDEO_CACHE_ROOT_DIR = "videoCache";
    private final static String IMAGE_CACHE_ROOT_DIR = "imageCache";
    private final static String APOLLO_VOICE_CACHE_DIR = "voiceCache";
    private final static String DOWN_LOAD_CACHE_DIR = "downLoad";
    private final static String DOWN_LOAD_TEMP_CACHE_DIR = "downLoadTemp";
    private final static String UPLOAD_LOG_CACHE_DIR = "uploadLog";
    private final static String TMP_FILE_CACHE_DIR = "TEMP";
    private final static String XLOG_FILE_CACHE_DIR = "xlog";

    private static FileDiskLruCache mFileDiskLruCache = new FileDiskLruCache();

    //NOTE: this function is mainly for network downloaded and only store to sdcard content
    public static String getPhotoDownloadFile(String imgUrl) {
        String resultFileName = null;
        if (!TextUtils.isEmpty(imgUrl)) {
            resultFileName = FilePathUtil.getCommonFilePath(PHOTO_DOWNLOAD_PATH);
            if (!TextUtils.isEmpty(resultFileName)) {
                resultFileName = resultFileName + File.separator + CommonUtil.md5String(imgUrl);
            }
            Loger.d(TAG, "result filename = " + resultFileName);
        }
        return resultFileName;
    }

    public static String getXlogFileCacheDir() {
        return FilePathUtil.getNamedCacheFolder(XLOG_FILE_CACHE_DIR);
    }

    /**
     * return temp file path
     *
     * @param baseFileName the base file name without /
     * @return the full file path name
     */
    public static String getTempFilePath(String baseFileName) {
        String tempFileFullPath = FilePathUtil.getNamedCacheFolder(TMP_FILE_CACHE_DIR);
        if (!TextUtils.isEmpty(baseFileName)) {
            tempFileFullPath += File.separator + baseFileName;
        }
        Loger.d(TAG, "temp file full path: " + tempFileFullPath);
        return tempFileFullPath;
    }

    private static void clearTempDir() {
        FileHandler.removeFileAtPath(FilePathUtil.getNamedCacheFolder(TMP_FILE_CACHE_DIR));
    }

    public static String getVideoCacheFileName(String videoFileName) {
        String videoCacheDir = FilePathUtil.getNamedCacheFolder(VIDEO_CACHE_ROOT_DIR);
        if (!TextUtils.isEmpty(videoFileName)) {
            videoFileName = videoCacheDir + File.separator + videoFileName;
        }
        return videoFileName;
    }

    public static String getUploadLogFile(String logFileName) {
        String uploadLogDir = FilePathUtil.getNamedCacheFolder(UPLOAD_LOG_CACHE_DIR);
        return !TextUtils.isEmpty(logFileName) && !logFileName.startsWith(File.pathSeparator) ?
                (uploadLogDir + File.separator + logFileName) : logFileName;
    }

    public static String getApkCacheDir() {
        return FilePathUtil.getNamedFileFolder(APK_CACHE_DIR);
    }

    public static String getDownloadDir() {
        return FilePathUtil.getNamedCacheFolder(DOWN_LOAD_CACHE_DIR);
    }

    public static String getDownloadTempDir() {
        return FilePathUtil.getNamedCacheFolder(DOWN_LOAD_TEMP_CACHE_DIR);
    }

    /**
     * 下载更新的APK的路径
     *
     * @param name the apk file name
     * @return the full path file name
     */
    public static String getApkCachePath(String name) {
        String resultFileName = getApkCacheDir();
        if (!TextUtils.isEmpty(name)) {
            resultFileName += File.separator + name;
        }
        return resultFileName;
    }

    public static String getCrashLogDir() {
        return FilePathUtil.getSdcardFileFullPath(CRASH_LOG_DIR, true);
    }

    public static String getImageCacheDir() {
        return FilePathUtil.getNamedCacheFolder(IMAGE_CACHE_ROOT_DIR);
    }

    public static String getVoiceCacheDir() {
        return FilePathUtil.getNamedCacheFolder(APOLLO_VOICE_CACHE_DIR);
    }

    public static void initCache() {
        AsyncOperationUtil.asyncOperation(() -> {
            String cacheDir = FilePathUtil.getDataCacheDir(COMMON_CACHE_DIR);
            mFileDiskLruCache.setMaxCacheSize(MAX_CACHE);
            mFileDiskLruCache.syncInitCache(cacheDir);
        }, null, TAG);
    }

    public static void closeCache() {
        AsyncOperationUtil.asyncOperation(() -> mFileDiskLruCache.closeCache(), null, TAG);
    }

    public static void syncWriteCache(final String key, final Object toCacheObj) {
        mFileDiskLruCache.syncWriteCacheObj(key, toCacheObj);
    }

    public static void asyncWriteCache(final String key, final Object toCacheObj) {
        AsyncOperationUtil.asyncOperation(() -> syncWriteCache(key, toCacheObj), null, TAG);
    }

    public static void asyncWriteCache(final String key,
                                       final Object toCacheObj,
                                       AsyncOperationUtil.AsyncOperationListener listener) {
        AsyncOperationUtil.asyncOperation(() -> syncWriteCache(key, toCacheObj), listener, TAG);
    }

    public static Object syncReadCache(final String key) {
        return mFileDiskLruCache.syncReadCache(key);
    }

    public static void asyncReadCache(final String key, final IAsyncReadListener asyncReadListener) {
        if (!TextUtils.isEmpty(key)) {
            AsyncOperationUtil.asyncOperation(new TagRunnable<Object>() {
                @Override
                public void run() {
                    setTagObj(syncReadCache(key));
                }
            }, objData -> {
                if (objData instanceof TagRunnable) {
                    if (asyncReadListener != null) {
                        asyncReadListener.onAsyncReadDone(((TagRunnable) objData).getTagObj());
                    }
                }
            }, TAG);
        }
    }

    public static void syncRemoveCache(String key) {
        mFileDiskLruCache.syncRemoveCache(key);
    }

    public static void asyncRemoveCache(final String key) {
        AsyncOperationUtil.asyncOperation(() -> syncRemoveCache(key));
    }

    private static String getDataCacheDir() {
        return FilePathUtil.getDataCacheDir(null);
    }

    private static String getSdCacheDir() {
        return FilePathUtil.getSdDataCacheDir();
    }

    public static long getCacheTotalSize() {
        final String dataCacheDir = getDataCacheDir();
        final String sdCacheDir = getSdCacheDir();
        long dataCacheSize = FileHandler.getFolderSize(dataCacheDir);
        long sdCacheSize = FileHandler.getFolderSize(sdCacheDir);
        Loger.d(TAG, "dataCacheSize: " + dataCacheSize + ", sdCacheSize: " + sdCacheSize);
        return dataCacheSize + sdCacheSize;
    }

    public static void clearCache() {
        mFileDiskLruCache.destroy();
        String dataCacheDir = getDataCacheDir();
        String sdCacheDir = getSdCacheDir();
        Loger.d(TAG, "dataCacheDir: " + dataCacheDir + ", sdCacheDir: " + sdCacheDir);
        FileHandler.removeFileAtPath(dataCacheDir);
        FileHandler.removeFileAtPath(sdCacheDir);
    }
}