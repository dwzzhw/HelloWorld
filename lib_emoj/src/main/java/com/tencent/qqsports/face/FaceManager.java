package com.tencent.qqsports.face;

import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.widget.TextView;

import com.loading.common.utils.CommonUtil;
import com.loading.common.utils.FileHandler;
import com.loading.common.utils.FilePathUtil;
import com.loading.common.utils.Loger;
import com.loading.common.utils.UiThreadUtil;
import com.loading.modules.interfaces.face.IFaceService;
import com.loading.modules.interfaces.face.data.BaseFacePackage;
import com.tencent.qqsports.face.data.FacePackageInfo;
import com.tencent.qqsports.face.data.RemoteFacePackageInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FaceManager implements FaceUtil.IRemoteFacePackageListener, IFaceService {
    private static final String TAG = "FaceManager";
    public static final String LOCAL_FACE_PACKAGE_NAME = "local_face_info";
    private static final String FACE_STORAGE_PATH = "face";
    private static final int PREPARE_PACKAGE_INFO_MAX_RETRY_CNT = 5;
    private static final long PREPARE_PACKAGE_INFO_RETRY_DURATION = 30 * DateUtils.SECOND_IN_MILLIS;

    private List<BaseFacePackage> mLocalPackageList;
    private List<BaseFacePackage> mRemotePackageList;  //在表情面板中展示的远端表情包列表，即展示在面板底部的分组数量
    private String mLocalFacePackageFolderPath;     //网络表情包本地存储路径
    private List<RemoteFacePackageInfo> mRemoteFacePackageInfoList;   //远端获取到的sticker列表

    private static class InstanceHolder {
        static FaceManager sInstance = new FaceManager();
    }

    public static FaceManager getInstance() {
        return InstanceHolder.sInstance;
    }

    private FaceManager() {
        mLocalPackageList = new ArrayList<>(2);
        LocalFacePackage localFacePackage = new LocalFacePackage();
        mLocalPackageList.add(localFacePackage);

        mLocalFacePackageFolderPath = FilePathUtil.getFilePathDir(FACE_STORAGE_PATH);
        Loger.d(TAG, "-->init<>, cached remote FacePackageFolderPath=" + mLocalFacePackageFolderPath);

//        mRemoteDataModel = new RemoteFacePackageModel(new IDataListener() {
//            @Override
//            public void onDataComplete(BaseDataModel data, int dataType) {
//                Loger.d(TAG, "-->onDataComplete(), model=" + data);
//                UiThreadUtil.removeRunnable(mRetryFetchPackageInfoRunnable);
//                if (data == mRemoteDataModel) {
//                    if (mRemoteFaceRespPO == null || !mRemoteFaceRespPO.isTheSameResp(mRemoteDataModel.getResponseData())) {
//                        mRemoteFaceRespPO = mRemoteDataModel.getResponseData();
//                        updateRemoteFacePackageInfo(mRemoteDataModel.getRemoteFacePackageList(), mRemoteDataModel.getDataVersion());
//                    } else {
//                        Loger.d(TAG, "-->Ignore duplicated remote data");
//                    }
//                }
//            }
//
//            @Override
//            public void onDataError(BaseDataModel data, int retCode, String retMsg, int dataType) {
//                Loger.d(TAG, "-->onDataError(), retCode=" + retCode + ", retMsg=" + retMsg + ", dataType=" + dataType);
//                if (mFetchPackageInfoRetryCnt++ < PREPARE_PACKAGE_INFO_MAX_RETRY_CNT) {
//                    UiThreadUtil.removeRunnable(mRetryFetchPackageInfoRunnable);
//                    UiThreadUtil.postDelay(mRetryFetchPackageInfoRunnable, PREPARE_PACKAGE_INFO_RETRY_DURATION);
//                }
//            }
//        });
    }

    private void queryRemoteFaceInfo() {
        Loger.d(TAG, "-->queryRemoteFaceInfo(), not implement yet");
//        UiThreadUtil.removeRunnable(mRetryFetchPackageInfoRunnable);
//        mRemoteDataModel.loadData();
    }

    @Override
    public List<BaseFacePackage> getAvailablePackageList() {
        List<BaseFacePackage> resultList = mLocalPackageList;
        if (!CommonUtil.isEmpty(mRemotePackageList)) {
            boolean isRemotePackageValid = false;
            for (BaseFacePackage remotePackage : mRemotePackageList) {
                if (remotePackage != null && remotePackage.isPackageValid()) {
                    isRemotePackageValid = true;
                    break;
                }
            }
            if (isRemotePackageValid) {
                resultList = mRemotePackageList;
            } else {
                //远端表情包已出错，重新下载
                Loger.w(TAG, "-->getAvailablePackageList(), remote face package damaged, re-get one");
//                queryRemoteFaceInfo();
            }
        }
        return resultList;
    }

    private void checkRemoteFacePackageReadyState(boolean triggerDownload) {
        if (mRemoteFacePackageInfoList != null) {
            boolean isAllInfoReady = true;
            for (RemoteFacePackageInfo packageInfo : mRemoteFacePackageInfoList) {
                if (!FaceUtil.checkFaceInfoReadyState(packageInfo) && !isRemotePackageDownloadFail(packageInfo)) {
                    isAllInfoReady = false;
                    if (triggerDownload) {
                        FaceUtil.downloadRemoteFacePackage(packageInfo, this, true);
                    }
                }
            }
            Loger.d(TAG, "-->checkRemoteFacePackageReadyState(), isAllInfoReady=" + isAllInfoReady + ", triggerDownload=" + triggerDownload);
            if (isAllInfoReady) {
                initRemotePackageList();
            }
        }
    }

    private boolean isRemotePackageDownloadFail(RemoteFacePackageInfo packageInfo) {
        return packageInfo != null && packageInfo.getPrepareInfoRetryCnt() >= PREPARE_PACKAGE_INFO_MAX_RETRY_CNT;
    }

    //根据下载好的信息初始化远端表情包
    private void initRemotePackageList() {
        if (mRemotePackageList == null) {
            mRemotePackageList = new ArrayList<>(2);
        } else {
            mRemotePackageList.clear();
        }

        if (mRemoteFacePackageInfoList != null) {
            for (int i = 0; i < mRemoteFacePackageInfoList.size(); i++) {
                RemoteFacePackageInfo packageInfo = mRemoteFacePackageInfoList.get(i);
                if (FaceUtil.checkFaceInfoReadyState(packageInfo)) {
                    RemoteFacePackage remoteFacePackage = new RemoteFacePackage(packageInfo);

                    mRemotePackageList.add(remoteFacePackage);
                }
            }
            clearOutOfDateFacePackage();
        }
    }

    private void clearOutOfDateFacePackage() {
        if (!CommonUtil.isEmpty(mRemoteFacePackageInfoList)) {
            Set<String> validPackageSet = new HashSet<>(mRemoteFacePackageInfoList.size());
            for (int i = 0; i < mRemoteFacePackageInfoList.size(); i++) {
                validPackageSet.add(FaceUtil.getFacePackageFolderFullPath(mRemoteFacePackageInfoList.get(i)));
            }

            File allFacePackageFolder = new File(mLocalFacePackageFolderPath);
            if (allFacePackageFolder.exists() && allFacePackageFolder.isDirectory()) {
                File[] packageArray = allFacePackageFolder.listFiles();
                if (packageArray != null && packageArray.length > 0) {
                    for (File packageFolder : packageArray) {
                        String packageFolderPath = packageFolder.getAbsolutePath();
                        if (!validPackageSet.contains(packageFolderPath)) {
                            Loger.w(TAG, "-->clearOutOfDateFacePackage(), package[" + packageFolderPath + "] is out of date, delete it");
                            FilePathUtil.removeFile(packageFolderPath);
                        }
                    }
                }
            }
        }
    }

    private void updateRemoteFacePackageInfo(List<RemoteFacePackageInfo> remoteFacePackageInfoList, String version) {
        Loger.d(TAG, "-->updateRemoteFacePackageInfo(), version=" + version + ", remoteFacePackageInfoList=" + remoteFacePackageInfoList);
        mRemoteFacePackageInfoList = remoteFacePackageInfoList;
        checkRemoteFacePackageReadyState(true);
    }

    @Override
    public void onRemoteFaceDownloadFail(RemoteFacePackageInfo packageInfo) {
        Loger.w(TAG, "-->onRemoteFaceDownloadFail()");
        retryDownloadPackageInfo(packageInfo);
    }

    @Override
    public void onRemoteFaceUnzipFail(RemoteFacePackageInfo packageInfo) {
        Loger.w(TAG, "-->onRemoteFaceUnzipFail()");
        retryDownloadPackageInfo(packageInfo);
    }

    @Override
    public void onRemoteFaceReady(RemoteFacePackageInfo packageInfo) {
        Loger.d(TAG, "-->onRemoteFaceReady()");
        checkRemoteFacePackageReadyState(false);
    }

    private void retryDownloadPackageInfo(RemoteFacePackageInfo packageInfo) {
        if (packageInfo != null && !isRemotePackageDownloadFail(packageInfo)) {
            UiThreadUtil.postDelay(new Runnable() {
                @Override
                public void run() {
                    packageInfo.increasePrepareInfoRetryCnt();
                    FaceUtil.downloadRemoteFacePackage(packageInfo, FaceManager.this, true);
                }
            }, PREPARE_PACKAGE_INFO_RETRY_DURATION);
        }
    }

    public String getLocalFacePackageFolderPath() {
        return mLocalFacePackageFolderPath;
    }

    public SpannableStringBuilder fillTextWithFace(String iText, TextView txtView) {
        SpannableStringBuilder resultCharSeq = null;
        if (txtView != null) {
            resultCharSeq = convertToSpannableStr(iText, txtView);
            if (resultCharSeq != null) {
                txtView.setText(resultCharSeq);
            } else {
                txtView.setText(iText);
            }
        }
        return resultCharSeq;
    }

    @Override
    public SpannableStringBuilder convertToSpannableStr(String iText, float textSize) {
        return convertToSpannableStr(iText, textSize, null);
    }

    @Override
    public SpannableStringBuilder convertToSpannableStr(String iText, TextView txtView) {
        return convertToSpannableStr(iText, 0, txtView);
    }

    public SpannableStringBuilder convertToSpannableStr(String iText, float textSize, TextView txtView) {
        SpannableStringBuilder resultBuilder = new SpannableStringBuilder(iText == null ? "" : iText);

        if (iText != null && iText.length() > 0 && (txtView != null || textSize > 0.0001f)) {
            if (!CommonUtil.isEmpty(mLocalPackageList)) {
                for (int i = 0; i < mLocalPackageList.size(); i++) {
                    resultBuilder = mLocalPackageList.get(i).convertToSpannableStr(resultBuilder, textSize, txtView);
                }
            }
            //给远端包一个覆盖本地表情的机会，so, order is important
            if (!CommonUtil.isEmpty(mRemotePackageList)) {
                for (int i = 0; i < mRemotePackageList.size(); i++) {
                    resultBuilder = mRemotePackageList.get(i).convertToSpannableStr(resultBuilder, textSize, txtView);
                }
            }
        }
        return resultBuilder;
    }

//    private Runnable mRetryFetchPackageInfoRunnable = () -> queryRemoteFaceInfo();
//
//    private void checkToUpdateData() {
//        boolean needUpdate = mRemoteDataModel != null && mRemoteDataModel.needUpdate();
//        Loger.d(TAG, "-->checkToUpdateData(), needUpdate=" + needUpdate);
//        if (needUpdate) {
//            queryRemoteFaceInfo();
//        }
//    }

    //生成本地表情包需要的代码，勿删
    public void saveCurrentFacePackageToLocal() {
        Loger.d(TAG, "-->saveCurrentFacePackageToLocal(), remote page info list = " + mRemoteFacePackageInfoList);
        if (mRemoteFacePackageInfoList != null && mRemoteFacePackageInfoList.size() > 0) {
            FacePackageInfo packageInfo = mRemoteFacePackageInfoList.get(0).getFacePackageInfo();
            String packageInfoPath = FilePathUtil.getSdcardFileFullPath(LOCAL_FACE_PACKAGE_NAME, false);
            if (packageInfo != null && !TextUtils.isEmpty(packageInfoPath)) {
                FileHandler.writeObjectToFilePath(packageInfo, packageInfoPath);
                Loger.d(TAG, "-->saveCurrentFacePackageToLocal(), packageInfoPath=" + packageInfoPath);
            }
        }
    }
}
