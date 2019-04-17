package com.tencent.qqsports.face;

import android.text.TextUtils;

import com.loading.common.utils.Loger;
import com.loading.common.utils.ZipUtils;
import com.loading.modules.interfaces.download.DownloadListener;
import com.loading.modules.interfaces.download.DownloadModuleMgr;
import com.loading.modules.interfaces.download.DownloadRequest;
import com.loading.modules.interfaces.face.data.FaceItem;
import com.loading.modules.interfaces.face.data.FacePackageInfo;
import com.loading.modules.interfaces.face.data.RemoteFacePackageInfo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
        String localFullPathForRemotePackage = getFacePackageFolderFullPath(packageInfo);
        Loger.d(TAG, "-->unzipRemoteFacePackage(), zipFilePath=" + zipFilePath + ", target path=" + localFullPathForRemotePackage);
        if (packageInfo != null && !TextUtils.isEmpty(zipFilePath) && !TextUtils.isEmpty(localFullPathForRemotePackage)) {
            if (ZipUtils.unZip(zipFilePath, localFullPathForRemotePackage)) {
                Loger.d(TAG, "-->unzipRemoteFacePackage(), success, target path=" + localFullPathForRemotePackage);
                createSuccessFlagFileInFolder(localFullPathForRemotePackage);
                if (facePackageListener != null) {
                    facePackageListener.onRemoteFaceReady(packageInfo);
                }
            } else {
                Loger.w(TAG, "-->unzipRemoteFacePackage(), fail, target path=" + localFullPathForRemotePackage);
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

    public static String getFacePackageFolderFullPath(RemoteFacePackageInfo remoteFacePackage) {
        String localPackageFullPath = null;
        if (remoteFacePackage != null) {
            localPackageFullPath = remoteFacePackage.mLocalPackageFullPath;

            if (TextUtils.isEmpty(localPackageFullPath)) {
                StringBuilder fullPathBuilder = new StringBuilder(FaceManager.getInstance().getLocalFacePackageFolderPath());
                if (!FaceManager.getInstance().getLocalFacePackageFolderPath().endsWith(File.separator)) {
                    fullPathBuilder.append(File.separator);
                }
                fullPathBuilder.append(remoteFacePackage.getPackageName());
                fullPathBuilder.append("___");
                if (!TextUtils.isEmpty(remoteFacePackage.getZipFileMd5())) {
                    fullPathBuilder.append(remoteFacePackage.getZipFileMd5());
                }
                localPackageFullPath = fullPathBuilder.toString();

                remoteFacePackage.mLocalPackageFullPath = localPackageFullPath;
            }
        }
        return localPackageFullPath;
    }

    public static boolean checkFaceInfoReadyState(RemoteFacePackageInfo remoteFacePackage) {
        String facePackageFolderPath = getFacePackageFolderFullPath(remoteFacePackage);
        return FaceUtil.isValidFacePackageFolder(facePackageFolderPath);
    }

    public static List<RemoteFacePackageInfo> mockRemoteFacePackageInfoList() {
        List<RemoteFacePackageInfo> infoList = new ArrayList<>(2);

        RemoteFacePackageInfo packageInfo1 = new RemoteFacePackageInfo();
        FacePackageInfo info1 = new FacePackageInfo();
        info1.groupId = "net_face_01";
        info1.groupName = "net_face_01";
        List<FaceItem> faceList1 = new ArrayList<>();
        FaceItem faceItem11 = new FaceItem("11", "[冷]", "leng.png");
        FaceItem faceItem12 = new FaceItem("12", "[冷汗]", "lenghan.png");
        FaceItem faceItem13 = new FaceItem("13", "[冷酷]", "lengku.png");
        FaceItem faceItem14 = new FaceItem("14", "[力量]", "liliang.png");
        faceList1.add(faceItem11);
        faceList1.add(faceItem12);
        faceList1.add(faceItem13);
        faceList1.add(faceItem14);
        info1.encoder = faceList1;
        RemoteFacePackageInfo.FaceZipFileInfo zipFileInfo1 = new RemoteFacePackageInfo.FaceZipFileInfo();
        zipFileInfo1.downURL = "http://mat1.gtimg.com/sports/sportapp/vip/PrivilegeProps/heishao.mp4";
        packageInfo1.file = zipFileInfo1;
        packageInfo1.text = info1;

        RemoteFacePackageInfo packageInfo2 = new RemoteFacePackageInfo();
        FacePackageInfo info2 = new FacePackageInfo();
        info2.groupId = "net_face_02";
        info2.groupName = "net_face_02";
        info2.icon = "http://mat1.gtimg.com/sports/sportapp/vip/teamlogo/basic_logo.png";
        List<FaceItem> faceList2 = new ArrayList<>();
        FaceItem faceItem21 = new FaceItem("21", "[林书豪]", "linshuhao.png");
        FaceItem faceItem22 = new FaceItem("22", "[卖萌]", "maimeng.png");
        FaceItem faceItem23 = new FaceItem("23", "[挪威司机]", "nuoweisiji.png");
        faceList2.add(faceItem21);
        faceList2.add(faceItem22);
        faceList2.add(faceItem23);
        info2.encoder = faceList2;
        RemoteFacePackageInfo.FaceZipFileInfo zipFileInfo2 = new RemoteFacePackageInfo.FaceZipFileInfo();
        zipFileInfo2.downURL = "http://mat1.gtimg.com/sports/sportapp/vip/PrivilegeProps/juesha.mp4";
        packageInfo2.file = zipFileInfo2;
        packageInfo2.text = info2;

        infoList.add(packageInfo1);
        infoList.add(packageInfo2);

        return infoList;
    }

    public interface IRemoteFacePackageListener {
        void onRemoteFaceDownloadFail(RemoteFacePackageInfo packageInfo);

        void onRemoteFaceUnzipFail(RemoteFacePackageInfo packageInfo);

        void onRemoteFaceReady(RemoteFacePackageInfo packageInfo);
    }
}
