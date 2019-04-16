package com.tencent.qqsports.face;

import android.text.TextUtils;

import com.loading.common.utils.Loger;
import com.loading.common.utils.ZipUtils;
import com.loading.modules.interfaces.download.DownloadListener;
import com.loading.modules.interfaces.download.DownloadModuleMgr;
import com.loading.modules.interfaces.download.DownloadRequest;
import com.tencent.qqsports.face.data.RemoteFacePackageInfo;

import java.io.File;
import java.io.IOException;

public class FaceUtil {
    private static final String TAG = "FaceUtil";
    public static final String UNZIP_FINISH_FLAG_FILE_NAME = "face_unzip_done";

    public static void downloadRemoteFacePackage(final RemoteFacePackageInfo packageInfo, IRemoteFacePackageListener facePackageListener, boolean limitDownload) {
        Loger.d(TAG, "-->downloadRemoteFacePackage(), packageInfo=" + packageInfo);
        if (packageInfo != null) {
            DownloadRequest downloadRequest = DownloadRequest.newInstance(null, packageInfo.getZipFileUrl(), packageInfo.getZipFileMd5(), limitDownload);
            String downloadTaskId = DownloadModuleMgr.startDownload(downloadRequest, new DownloadListener() {
                @Override
                public void onDownloadProgress(String taskId, String downloadUrl, String tempFilePath, long completeSize, long totalSize, int nProgress, DownloadRequest downloadRequest) {

                }

                @Override
                public void onDownloadPaused(String taskId, String downloadUrl, String tempFilePath, long completeSize, long totalSize, int nProgress, DownloadRequest downloadRequest) {

                }

                @Override
                public void onDownloadError(String taskId, String downloadUrl, String tempFilePath, long completeSize, long totalSize, int nProgress, DownloadRequest downloadRequest) {
                    Loger.w(TAG, "-->onDownloadError(), downloadUrl=" + downloadUrl + ", packageInfo=" + packageInfo);
                    if (facePackageListener != null) {
                        facePackageListener.onRemoteFaceDownloadFail(packageInfo);
                    }
                }

                @Override
                public void onDownloadComplete(String taskId, String downloadUrl, String finalFilePath, long completeSize, long totalSize, DownloadRequest downloadRequest) {
                    Loger.d(TAG, "-->onDownloadComplete(), downloadUrl=" + downloadUrl + ", finalFilePath=" + finalFilePath + ", packageInfo=" + packageInfo);
                    unzipRemoteFacePackage(packageInfo, finalFilePath, facePackageListener);
                }
            });
            if (TextUtils.isEmpty(downloadTaskId) && facePackageListener != null) {
                facePackageListener.onRemoteFaceDownloadFail(packageInfo);
            }
        }
    }

    public static void unzipRemoteFacePackage(final RemoteFacePackageInfo packageInfo, String zipFilePath, IRemoteFacePackageListener facePackageListener) {
        Loger.d(TAG, "-->unzipRemoteFacePackage(), zipFilePath=" + zipFilePath + ", target path=" + (packageInfo != null ? packageInfo.getFacePackageFolderFullPath() : "Null"));
        if (packageInfo != null && !TextUtils.isEmpty(zipFilePath)) {
            if (ZipUtils.unZip(zipFilePath, packageInfo.getFacePackageFolderFullPath())) {
                Loger.d(TAG, "-->unzipRemoteFacePackage(), success, target path=" + packageInfo.getFacePackageFolderFullPath());
                createSuccessFlagFileInFolder(packageInfo.getFacePackageFolderFullPath());
                if (facePackageListener != null) {
                    facePackageListener.onRemoteFaceReady(packageInfo);
                }
            } else {
                Loger.w(TAG, "-->unzipRemoteFacePackage(), fail, target path=" + packageInfo.getFacePackageFolderFullPath());
                if (facePackageListener != null) {
                    facePackageListener.onRemoteFaceUnzipFail(packageInfo);
                }
            }
        }
    }

    /**
     * 增加一个解压成功的flag，以兼容解压过程被异常中断的场景
     *
     * @param folderPath
     */
    public static void createSuccessFlagFileInFolder(String folderPath) {
        if (!TextUtils.isEmpty(folderPath)) {
            File faceFolder = new File(folderPath);
            if (faceFolder.isDirectory() && faceFolder.exists()) {
                File successFlagFile = new File(faceFolder, UNZIP_FINISH_FLAG_FILE_NAME);
                if (!successFlagFile.exists()) {
                    try {
                        successFlagFile.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static boolean isValidFacePackageFolder(String folderPath) {
        boolean isValid = false;
        if (!TextUtils.isEmpty(folderPath)) {
            File flagFile = new File(folderPath, UNZIP_FINISH_FLAG_FILE_NAME);
            isValid = flagFile.exists();
        }

        return isValid;
    }


    public interface IRemoteFacePackageListener {
        void onRemoteFaceDownloadFail(RemoteFacePackageInfo packageInfo);

        void onRemoteFaceUnzipFail(RemoteFacePackageInfo packageInfo);

        void onRemoteFaceReady(RemoteFacePackageInfo packageInfo);
    }
}
