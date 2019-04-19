package com.loading.common.utils;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import com.loading.common.component.CApplication;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@SuppressWarnings("unused")
public class FilePathUtil {
    //SD上本应用私有的文件路径，/sdcard/Android/data/com.tencent.qqsports/files, 不需要额外的访问权限，但是卸载应用时会被系统自动清理
    //SD卡根目录下的某个路径，访问需要对应的权限
    private static final String TAG = "FilePathUtil";

    private final static int filePathType = 1;
    private final static int cachePathType = 2;
    private final static int sdcardCacheType = 3;
    private final static int sdcardFileType = 4;
    private final static int dataCachePathType = 5;
    private final static int sdcardCommonFileType = 6;
    private final static int sdcardDataCachePathType = 7;

    private final static String DATA_CACHE_ROOT_DIR = "dataCache";
    private final static String SD_DATA_CACHE_DIR = "sdDataCache";

    public static String getFullFilePathName(String moduleDir, String fileName) {
        String path = getFilePathDir(moduleDir);
        if (!TextUtils.isEmpty(path)) {
            path += File.separator + fileName;
        }
        return path;
    }

    public static String getFilePathDir(String moduleDir) {
        return getFullPath(moduleDir, filePathType, true);
    }

    public static String getFullRootFileName(String fileName) {
        return getFullPath(fileName, filePathType, false);
    }

    public static String getDataCacheDir(String moduleDir) {
        return getFullPath(moduleDir, dataCachePathType, true);
    }

    public static String getSdDataCacheDir() {
        return getFullPath(null, sdcardDataCachePathType, true);
    }

    public static File getDataBasePath() {
        File fakeFile = CApplication.getAppContext().getDatabasePath("db");
        File path = null;
        if (fakeFile != null) {
            path = fakeFile.getParentFile();
        }
        return path;
    }

    public static String getNamedCacheFolder(String folderName) {
        return SystemUtil.isSdcardExist() ?
                getFullPath(folderName, sdcardDataCachePathType, true) :
                getFullPath(folderName, dataCachePathType, true);
    }

    public static String getNamedDataCacheFolder(String folderName) {
        return getFullPath(folderName, dataCachePathType, true);
    }

    public static String getNamedFileFolder(String folderName) {
        return getFullPath(folderName, SystemUtil.isSdcardExist() ? sdcardFileType : filePathType, true);
    }

    public static String getNamedDataFileFolder(String folderName) {
        return getFullPath(folderName, filePathType, true);
    }

    public static String getCommonFilePath(String fileName) {
        return getFullPath(fileName, SystemUtil.isSdcardExist() ? sdcardCommonFileType : filePathType, true);
    }

    public static String getSdcardFileFullPath(String fileName, boolean isFolder) {
        return SystemUtil.isSdcardExist() ? getFullPath(fileName, sdcardFileType, isFolder) : null;
    }

    private static synchronized String getFullPath(String fileName, int pathType, boolean isFolder) {
        String path = "";
        try {
            switch (pathType) {
                case filePathType:
                    path = getFilePath() + File.separator + fileName;
                    mkdirForPath(path, isFolder);
                    break;
                case cachePathType:
                    path = getCachePath() + File.separator + fileName;
                    mkdirForPath(path, isFolder);
                    break;
                case dataCachePathType:
                    path = getCachePath() + File.separator + DATA_CACHE_ROOT_DIR + (!TextUtils.isEmpty(fileName) ? (File.separator + fileName) : "");
                    mkdirForPath(path, isFolder);
                    break;
                case sdcardCacheType:
                    if (SystemUtil.isSdcardExist()) {
                        path = getSdCachePath() + File.separator + fileName;
                        mkdirForPath(path, isFolder);
                    }
                    break;
                case sdcardFileType:
                    if (SystemUtil.isSdcardExist()) {
                        path = getSdFilePath() + File.separator + fileName;
                        Loger.d(TAG, "path = " + path);
                        mkdirForPath(path, isFolder);
                    }
                    break;
                case sdcardCommonFileType:
                    if (SystemUtil.isSdcardExist()) {
                        path = getSdRootPath() + File.separator + fileName;
                        mkdirForPath(path, isFolder);
                    }
                    Loger.d(TAG, "sdcardCommonFileType path: " + path);
                    break;
                case sdcardDataCachePathType:
                    if (SystemUtil.isSdcardExist()) {
                        path = getSdCachePath() + File.separator + SD_DATA_CACHE_DIR + (!TextUtils.isEmpty(fileName) ? (File.separator + fileName) : "");
                        mkdirForPath(path, isFolder);
                    }
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            Loger.e(TAG, "FilePathUtil.getFullPath() error: " + e.toString());
        }
        Loger.d(TAG, "getFullPath, path: " + path + ", pathtype: " + pathType + ", isFolder: " + isFolder);
        return path;
    }

    private static void mkdirForPath(String filePath, boolean isFolder) {
        if (!TextUtils.isEmpty(filePath) && isFolder) {
            File file = new File(filePath);
            if (!file.exists()) {
                //noinspection ResultOfMethodCallIgnored
                file.mkdirs();
            }
        }
    }

    /**
     * 获取程序在手机内存中的文件根目录
     *
     * @return the absolute path for files dir ...
     */
    private static String getFilePath() {
        Context context = CApplication.getAppContext();
        return context != null ? context.getApplicationContext().getFilesDir().getAbsolutePath() : null;
    }

    /**
     * 获取程序在手机内存中的缓存根目录
     *
     * @return the cache dir
     */
    private static String getCachePath() {
        File cacheDir = CApplication.getAppContext().getCacheDir();
        return cacheDir != null ? cacheDir.getAbsolutePath() : null;
    }

    private static String getSdCachePath() {
        File extCacheFileDir = CApplication.getAppContext().getExternalCacheDir();
        return extCacheFileDir != null ? extCacheFileDir.getAbsolutePath() : null;
    }

    private static String getSdFilePath() {
        File extFileDir = CApplication.getAppContext().getExternalFilesDir(null);
        return extFileDir != null ? extFileDir.getAbsolutePath() : null;
    }

    private static String getSdRootPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    public static boolean copy(File src, File dst) {
        boolean success;
        InputStream in = null;
        OutputStream out = null;
        try {
            in = new FileInputStream(src);
            out = new FileOutputStream(dst);

            // Transfer bytes from in to out
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            success = true;
        } catch (Exception e) {
            success = false;
            Loger.d(TAG, "copy file exception = " + e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return success;
    }

    public static boolean exist(String path) {
        boolean result = false;
        if (!TextUtils.isEmpty(path)) {
            result = new File(path).exists();
        }
        return result;
    }

    /**
     * Check how much usable space is available at a given path.
     *
     * @param path The path to check
     * @return The space available in bytes
     */
    public static long getUsableSpace(File path) {
        return path.getUsableSpace();
    }

    public static void clearCacheFolders() {
//        removeFile(getNamedCacheFolder(IMAGE_CACHE_ROOT_DIR));
//        removeFile(getNamedCacheFolder(VIDEO_CACHE_ROOT_DIR));
//        removeFile(getNamedCacheFolder(UPLOAD_LOG_CACHE_DIR));
//        removeFile(getNamedCacheFolder(TMP_FILE_CACHE_DIR));
    }

    public static void removeFile(String filePath) {
        removeFile(filePath, false);
    }

    @SuppressWarnings({"ResultOfMethodCallIgnored", "SameParameterValue"})
    private static void removeFile(String filePath, boolean keepRootFolder) {
        Loger.d(TAG, "-->removeFile(), filePath=" + filePath + ", keepRootFolder=" + keepRootFolder);
        if (!TextUtils.isEmpty(filePath)) {
            try {
                File file = new File(filePath);
                if (file.exists()) {
                    if (file.isDirectory()) {
                        for (File subFile : file.listFiles()) {
                            removeFile(subFile.getAbsolutePath(), false);
                        }
                        if (!keepRootFolder) {
                            file.delete();
                        }
                    } else {
                        file.delete();
                    }
                }
            } catch (Exception e) {
                Loger.e(TAG, "-->removeFile fail, filePath=" + filePath, e);
            }
        }
    }
}