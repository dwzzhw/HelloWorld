package com.loading.common.toolbox;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.loading.common.utils.AsyncOperationUtil;
import com.loading.common.utils.FileHandler;
import com.loading.common.utils.Loger;
import com.loading.common.utils.ObjectHelper;
import com.loading.common.utils.SystemUtil;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"UnusedReturnValue", "unused"})
public class FileDiskLruCache {
    private static final String TAG = "DownloadDiskCache";
    private static final int STATE_INIT = 0; //未初始化完成
    private static final int STATE_ALIVE = 1; //初始化完成
    private static final int STATE_DESTROYED = 2; //已销毁

    private static final String HEADER_MAP_POSTFIX = "_hader_map_key";
    private static final long MAX_CACHE_SIZE = 40 * 1024 * 1024L;
    private static final int CACHE_FILE_INDEX = 0;

    private DiskLruCache mDiskLruCache;
    private final Object mDiskCacheLock = new Object();

    private volatile int mInitState = STATE_INIT;
    private long maxCacheSize = MAX_CACHE_SIZE;

    public FileDiskLruCache() {
    }

    public void setMaxCacheSize(long maxCacheSize) {
        this.maxCacheSize = maxCacheSize;
    }

    public void asyncInitCache(final String cacheDir) {
        AsyncOperationUtil.asyncOperation(() -> syncInitCache(cacheDir), null, TAG);
    }

    /**
     *  should be called in non-ui thread
     * @param cacheDir the cache dir
     */
    public void syncInitCache(String cacheDir) {
        ObjectHelper.requireNotNull(cacheDir, "cachedir should not be empty");
        try {
            synchronized (mDiskCacheLock) {
                mInitState = STATE_INIT;
                mDiskLruCache = DiskLruCache.open(new File(cacheDir), SystemUtil.getVersionCode(), 1, maxCacheSize);
                mInitState = STATE_ALIVE;
                mDiskCacheLock.notifyAll();
            }
        } catch (Exception e) {
            Loger.e(TAG, "cache init error ....., exception: " + e);
        }
    }

    private void checkAndWaitInit() {
        try {
            synchronized (mDiskCacheLock) {
                while (mInitState == STATE_INIT) {
                    mDiskCacheLock.wait();
                }
            }
        } catch (Exception e) {
            Loger.e(TAG, "checkAndWaitInit, exception: " + e);
            try {
                Thread.sleep(100);
            } catch (Exception e1) {
                Loger.e(TAG, "excpetion after exception: " + e);
            }
        }
    }

    public void syncWriteCacheObj(String key, Object cacheObj) {
        if (!TextUtils.isEmpty(key) && cacheObj != null) {
            checkAndWaitInit();
            synchronized (mDiskCacheLock) {
                DiskLruCache.Editor tEditor = null;
                OutputStream oStream = null;
                try {
                    tEditor = mDiskLruCache.edit(getCacheKey(key));
                    if (tEditor != null) {
                        oStream = tEditor.newOutputStream(0);
                        FileHandler.writeSerObjectToStream(oStream, cacheObj);
                    }
                } catch (Exception e) {
                    try {
                        if (tEditor != null) {
                            tEditor.abort();
                            tEditor = null;
                        }
                    } catch (Exception te) {
                        Loger.e(TAG, "abort editor exception: " + te);
                    }
                    Loger.e(TAG, "asyncWrite cache error: " + e);
                } finally {
                    DiskLruCache.closeQuietly(oStream);
                    if (tEditor != null) {
                        try {
                            tEditor.commit();
                        } catch (Exception e) {
                            Loger.e(TAG, "editor commit excetion: " + e);
                        }
                    }
                }
            }
        }
    }

    public Object syncReadCache(String key) {
        Object resultObj = null;
        if (!TextUtils.isEmpty(key)) {
            checkAndWaitInit();
            synchronized (mDiskCacheLock) {
                DiskLruCache.Snapshot tSnapShot = null;
                try {
                    tSnapShot = mDiskLruCache.get(getCacheKey(key));
                    if (tSnapShot != null) {
                        resultObj = FileHandler.readSerObjectFromInStream(tSnapShot.getInputStream(0));
                    }
                } catch (Exception e) {
                    Loger.e(TAG, "syncReadCache, exception: " + e);
                } finally {
                    if (tSnapShot != null) {
                        tSnapShot.close();
                    }
                }
            }
        }
        return resultObj;
    }

    public boolean syncWriteCacheFile(String key, String filePath) {
        boolean success = false;
        if (!TextUtils.isEmpty(key) && !TextUtils.isEmpty(filePath)) {
            File targetFile = new File(filePath);
            if (targetFile.exists()) {
                checkAndWaitInit();
                synchronized (mDiskCacheLock ) {
                    DiskLruCache.Editor editor = null;
                    try {
                        editor = mDiskLruCache.edit(getCacheKey(key));
                        if (editor != null) {
                            success = editor.putFileForKey(filePath, CACHE_FILE_INDEX);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (editor != null) {
                            try {
                                editor.commit();
                            } catch (IOException e) {
                                Loger.e(TAG, "syncWriteCacheFile exception: " + e);
                            }
                        }
                    }
                }
            }
        }
        return success;
    }

    public void syncRemoveCache(String key) {
        Loger.d(TAG, "-->syncRemoveCacheHeaderMap(), taskId=" + key);
        if (!TextUtils.isEmpty(key)) {
            checkAndWaitInit();
            synchronized (mDiskCacheLock) {
                try {
                    mDiskLruCache.remove(getCacheKey(key));
                } catch (IOException e) {
                    Loger.e(TAG, "syncRemoveCacheHeaderMap: " + e);
                }
            }
        }
    }

    public void syncRemoveCacheHeaderMap(String key) {
        Loger.d(TAG, "-->syncRemoveCacheHeaderMap(), taskId=" + key);
        if (!TextUtils.isEmpty(key)) {
            checkAndWaitInit();
            synchronized (mDiskCacheLock) {
                try {
                    mDiskLruCache.remove(getCacheKey(key));
                    mDiskLruCache.remove(getCacheHeaderMapKey(key));
                } catch (IOException e) {
                    Loger.e(TAG, "syncRemoveCacheHeaderMap: " + e);
                }
            }
        }
    }

    /**
     * @param key 缓存文件的 key
     * @return 返回其对应的缓存文件，并确保其在文件系统中存在。
     */
    public String getCacheFilePath(String key) {
        String targetPath = null;
        if (!TextUtils.isEmpty(key)) {
            checkAndWaitInit();
            String tPath;
            synchronized (mDiskCacheLock) {
                tPath = mDiskLruCache.getFilePath(getCacheKey(key), CACHE_FILE_INDEX);
            }
            if (FileHandler.isDirFileExist(tPath) ) {
                targetPath = tPath;
            }
        }
        return  targetPath;
    }

    /**
     * @param key the cache key
     * @return 如果为 key 写入文件的话， 返回其最终的文件名
     */
    public String getFutureFilePath(String key) {
        String targetPath = null;
        if (!TextUtils.isEmpty(key)) {
            checkAndWaitInit();
            synchronized (mDiskCacheLock) {
                targetPath = mDiskLruCache.getFutureFilePath(getCacheKey(key), CACHE_FILE_INDEX);
            }
        }
        return targetPath;
    }

    /**
     * 主要用来为 vasonic 的 webview 预加载请求 h5 资源
     * @param key key
     * @param headerMap 缓存的请求头
     * @return 成功 true, 失败 false
     */
    public boolean syncWriteHeaderMap(String key, Map<String, List<String>> headerMap) {
        boolean success = false;
        if (!TextUtils.isEmpty(key) && headerMap != null && headerMap.size() > 0) {
            checkAndWaitInit();
            synchronized (mDiskCacheLock) {
                DiskLruCache.Editor editor = null;
                try {
                    editor = mDiskLruCache.edit(getCacheHeaderMapKey(key));
                    if (editor != null) {
                        editor.set(CACHE_FILE_INDEX, new Gson().toJson(headerMap));
                        success = true;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (editor != null) {
                        try {
                            editor.commit();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
//            Loger.d(TAG, "<--syncWriteHeaderMap(), key=" + key + ", key=" + key + ", cache size=" + mDiskLruCache.size());
            }
        }
        return success;
    }

    /**
     * 主要用来为 vasonic 的 webview 预加载请求 h5 资源
     * @param key key
     * @return 成功 返回缓存的请求头
     */
    public Map<String, List<String>> syncReadHeaderMap(String key) {
        Map<String, List<String>> cachedRespMap = null;
        if (!TextUtils.isEmpty(key)) {
            checkAndWaitInit();
            DiskLruCache.Snapshot tSnapShot = null;
            synchronized (mDiskCacheLock) {
                try {
                    tSnapShot = mDiskLruCache.get(getCacheHeaderMapKey(key));
                    if (tSnapShot != null) {
                        String respStr = tSnapShot.getString(CACHE_FILE_INDEX);
                        if (!TextUtils.isEmpty(respStr)) {
                            //noinspection unchecked
                            cachedRespMap = new Gson().fromJson(respStr, Map.class);
                        }
                    }
                } catch (Exception e) {
                    Loger.e(TAG, "syncReadCache, exception: " + e);
                } finally {
                    if (tSnapShot != null) {
                        tSnapShot.close();
                    }
                }
                Loger.d(TAG, "<--syncWriteHeaderMap(), taskId=" + key + ", key=" + key
                        + ", cachedRespMap=" + cachedRespMap + ", cache size=" + mDiskLruCache.size());
            }
        }
        return cachedRespMap;
    }

    public void destroy() {
        checkAndWaitInit();
        synchronized (mDiskCacheLock) {
            try {
                mDiskLruCache.delete();
            } catch (Exception e) {
                Loger.e(TAG, "exception when destroy: " + e);
            }
        }
    }

    public void closeCache() {
        Loger.d(TAG, "-->closeCache()");
        checkAndWaitInit();
        synchronized (mDiskCacheLock) {
            try {
                mDiskLruCache.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                mInitState = STATE_DESTROYED;
            }
        }
    }

    private String getCacheKey(String taskId) {
        return taskId;
    }

    private String getCacheHeaderMapKey(String taskId) {
        return taskId + HEADER_MAP_POSTFIX;
    }
}
