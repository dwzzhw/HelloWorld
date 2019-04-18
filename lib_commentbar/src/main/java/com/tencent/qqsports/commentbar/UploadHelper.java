package com.tencent.qqsports.commentbar;

import android.graphics.ImageFormat;
import android.text.TextUtils;

import com.loading.common.utils.AsyncOperationUtil;
import com.loading.common.utils.CommonUtils;
import com.loading.common.utils.FilePathUtil;
import com.loading.common.utils.Loger;
import com.loading.common.widget.TipsToast;
import com.loading.modules.data.MediaEntity;
import com.loading.modules.interfaces.upload.UploadProgressMonitorListener;
import com.loading.modules.interfaces.upload.data.UploadPicPojo;
import com.loading.modules.interfaces.upload.data.UploadVideoPojo;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by wilmaliu on 17/8/31.
 * the helper model for upload media
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class UploadHelper implements MultiDataModel.IMultiDataListener {
    private final static String TAG = UploadHelper.class.getName();
    public final static int UPLOAD_CHANNEL_SHEQU = 1; //"shequ";
    public final static int UPLOAD_CHANNEL_COMMENT = 2; //"comment";

    private static final long COMPRESS_IMAGE_THRESHOLD = CommonUtil.BYTE_SIZE_MB;  //Byte
    private static final int UPLOAD_IMAGE_MAX_WH = 1024;

    private static final int MOCK_COMPRESS_TOTAL_PERCENT = 20;  //假设压缩文件所耗时间在整个过程总的占比
    private static final int MOCK_SINGLE_IMG_COMPRESS_TIME = 500;  //压缩单个文件所耗时间，ms

    private UploadProgressMonitorListener mUploadProgressListener;
    private UploadMultiModel mUploadMultiModel;
    private UploadHelperCallback mUploadHelperCallback;
    private List<String> mSelectedLocalPics;
    private HashMap<String, String> mInitCompressedPicMap = null;

    private HashMap<String, Object> mFinishedPicMemoryCache;
    private HashMap<String, UploadVideoPojo> videoMemoryCache;

    private int mSourceChannel;

    void setUploadProgressListener(UploadProgressMonitorListener uploadProgressListener) {
        this.mUploadProgressListener = uploadProgressListener;
        if (mUploadMultiModel != null) {
            mUploadMultiModel.setUploadProgressMonitorListener(mUploadProgressListener);
        }
    }

    UploadHelper(int sourceChannel) {
        mSourceChannel = sourceChannel;
    }

    public void setUploadMediaListener(UploadHelperCallback listener) {
        mUploadHelperCallback = listener;
    }

    public void startUpload(List<String> localPics, MediaEntity videoEntity, String topicStr) {
        mSelectedLocalPics = localPics;
        notifyCompressBegin(getNeedCompressImgCnt(localPics));
        if (!CommonUtils.isEmpty(localPics)) {
            checkPicValidation(localPics, videoEntity, topicStr);
        } else {
            startUploadInternal(localPics, videoEntity, topicStr);
        }
    }

    private void checkPicValidation(List<String> localPics, MediaEntity videoEntity, String topicStr) {
        AsyncOperationUtil.asyncOperation(() -> {
            //compress too large image
            if (mSelectedLocalPics != null) {
                for (String initUrl : mSelectedLocalPics) {
                    compressTooLargeImage(initUrl);
                }
            }
        }, objData -> {
            //re-organize upload image list
            List<String> finalUploadImgUrl = new ArrayList<>();
            if (mSelectedLocalPics != null) {
                for (String initUrl : mSelectedLocalPics) {
                    String compressedUrl = mInitCompressedPicMap != null ? mInitCompressedPicMap.get(initUrl) : null;
                    if (!TextUtils.isEmpty(compressedUrl)) {
                        finalUploadImgUrl.add(compressedUrl);
                    } else {
                        if (checkImgFileExist(initUrl)) {
                            finalUploadImgUrl.add(initUrl);
                        } else {
                            TipsToast.getInstance().showTipsText(R.string.bbs_send_topic_img_not_exits);
                            return;
                        }
                    }
                }
                startUploadInternal(finalUploadImgUrl, videoEntity, topicStr);
            }
        }, TAG);
    }

    private boolean checkImgFileExist(String path) {
        boolean exist = false;
        if (TextUtils.isEmpty(path)) {
            return exist;
        }

        File file = new File(path);
        if (file != null && file.exists()) {
            exist = true;
        }

        return exist;
    }

    /**
     * 耗时任务，需要在子线程执行
     *
     * @param initPicUrl the image url
     */
    private void compressTooLargeImage(String initPicUrl) {
        Loger.d(TAG, "-->compressTooLargeImage(), initPicUrl=" + initPicUrl);
        if (!TextUtils.isEmpty(initPicUrl)) {
            File initImgFile = new File(initPicUrl);
            if (initImgFile.exists() && initImgFile.isFile() && initImgFile.length() > COMPRESS_IMAGE_THRESHOLD) {
                ImageFormat imageFormat = ImageFormatChecker.getImageFormat(initPicUrl);
                Loger.d(TAG, "compressTooLargeImage() ->image Format : " + imageFormat);
                if (imageFormat == DefaultImageFormats.PNG || imageFormat == DefaultImageFormats.JPEG) {
                    String targetCompressFileName = getCompressedImgUrl(initPicUrl);
                    if (!TextUtils.isEmpty(targetCompressFileName)) {
                        File targetCompressFile = new File(targetCompressFileName);
                        boolean compressResult = true;
                        if (!targetCompressFile.exists()) {
                            compressResult = BitmapUtil.compressImageToJpg(initImgFile, targetCompressFileName, UPLOAD_IMAGE_MAX_WH, COMPRESS_IMAGE_THRESHOLD);
                        }

                        if (compressResult) {
                            if (mInitCompressedPicMap == null) {
                                mInitCompressedPicMap = new HashMap<>();
                            }
                            mInitCompressedPicMap.put(initPicUrl, targetCompressFileName);
                        }
                    }
                }
            }
        }
    }

    private String getCompressedImgUrl(String initPicUrl) {
        String targetCompressedFileName = CommonUtil.md5String(initPicUrl) + ".jpg";
        return CacheManager.getTempFilePath(targetCompressedFileName);
    }

    private int getNeedCompressImgCnt(List<String> localPics) {
        int cnt = 0;
        if (!CommonUtil.isEmpty(localPics)) {
            for (String picPath : localPics) {
                File initImgFile = new File(picPath);
                if (initImgFile.exists() && initImgFile.isFile() && initImgFile.length() > COMPRESS_IMAGE_THRESHOLD) {
                    cnt++;
                }
            }
        }
        return cnt;
    }

    private String getUploadSourceChannelStr() {
        String channel = null;
        switch (mSourceChannel) {
            case UPLOAD_CHANNEL_SHEQU:
                channel = "shequ";
                break;
            case UPLOAD_CHANNEL_COMMENT:
                channel = "comment";
                break;
        }
        return channel;
    }

    /**
     * 经过预处理的最终上传请求
     *
     * @param localPics
     * @param videoEntity
     * @param topicStr
     */
    private void startUploadInternal(List<String> localPics, MediaEntity videoEntity, String topicStr) {
        if (mUploadMultiModel == null) {
            mUploadMultiModel = new UploadMultiModel(this, getUploadSourceChannelStr());
            mUploadMultiModel.setUploadProgressMonitorListener(mUploadProgressListener);
        } else {
            mUploadMultiModel.removeAllModels();
        }

        String videoPath = videoEntity != null ? videoEntity.getPath() : "";

        Loger.i(TAG, "video path: " + videoPath + ", pics: " + mSelectedLocalPics);
        if (mUploadHelperCallback != null) {
            mUploadHelperCallback.uploadBegin();
        }

        boolean isRealRequest = false;
        List<String> needUploadPics = getNeedUploadPicParam(localPics);
        if (!CommonUtil.isEmpty(needUploadPics)) {
            mUploadMultiModel.addPicUploadModel(needUploadPics);
            isRealRequest = true;
        }

        if (videoEntity != null && !TextUtils.isEmpty(videoPath) && !isVideoCache(videoPath)) {
            mUploadMultiModel.addVideoUploadModel(videoEntity, topicStr);
            isRealRequest = true;
        }

        if (isRealRequest) {
            mUploadMultiModel.loadData();
        } else {
            if (mUploadHelperCallback != null) {
                if (mUploadProgressListener != null) {
                    mUploadProgressListener.onUploadProgress(null, 100, 100); //fake data to ensure show 100%
                }
                mUploadHelperCallback.uploadEnd(true, "", getUploadResp(), videoMemoryCache.get(videoPath));
            }
        }
    }

    private boolean isVideoCache(String videoPath) {
        boolean isRet = false;
        if (!TextUtils.isEmpty(videoPath) && videoMemoryCache != null && videoMemoryCache.containsKey(videoPath)) {
            UploadVideoPojo uploadVideoPojo = videoMemoryCache.get(videoPath);
            if (uploadVideoPojo != null && uploadVideoPojo.getVideoResp() != null && uploadVideoPojo.getVideoResp().isRespEffected()) {
                isRet = true;
            }
        }
        return isRet;
    }

    private List<String> getNeedUploadPicParam(List<String> needRequestUrls) {
        List<String> requestUrls;
        if (needRequestUrls != null && mFinishedPicMemoryCache != null) {
            requestUrls = new ArrayList<>();
            for (int i = 0; i < needRequestUrls.size(); i++) {
                if (!mFinishedPicMemoryCache.containsKey(needRequestUrls.get(i))) {
                    requestUrls.add(needRequestUrls.get(i));
                }
            }
        } else {
            requestUrls = needRequestUrls;
        }
        return requestUrls;
    }


    private void setVideoCache(UploadVideoPojo uploadVideoPojo) {
        if (videoMemoryCache == null) {
            videoMemoryCache = new HashMap<>();
        }
        if (uploadVideoPojo != null) {
            videoMemoryCache.put(uploadVideoPojo.getVideoPath(), uploadVideoPojo);
        }
    }

    /**
     * 缓存已上传成功的图片信息
     *
     * @param upPicRespDataHashMap
     */
    private void cacheUploadedPicInfo(HashMap<String, UploadPicPojo> upPicRespDataHashMap) {
        if (mFinishedPicMemoryCache == null) {
            mFinishedPicMemoryCache = new HashMap<>();
        }
        if (upPicRespDataHashMap != null) {
            Iterator iterator = upPicRespDataHashMap.keySet().iterator();
            String key;
            UploadPicPojo value;
            while (iterator.hasNext()) {
                key = (String) iterator.next();
                value = upPicRespDataHashMap.get(key);
                if (value != null && value.isRetSuccess() && value.isEffectPictures()) {
                    mFinishedPicMemoryCache.put(key, value);
                }
            }
        }
    }

    public void clearPicCache() {
        if (mFinishedPicMemoryCache != null) {
            mFinishedPicMemoryCache.clear();
            mFinishedPicMemoryCache = null;
        }
        if (mInitCompressedPicMap != null) {
            for (String tmpCompressFilePath : mInitCompressedPicMap.values()) {
                FilePathUtil.removeFile(tmpCompressFilePath);
            }
            mInitCompressedPicMap.clear();
        }
    }

    public void onDestroy() {
        if (mUploadMultiModel != null) {
            mUploadMultiModel.cancelRequest();
            mUploadMultiModel.onDestroy();
            mUploadMultiModel = null;
        }
        clearPicCache();
        clearVideoMemCache();
    }

    public void clearVideoMemCache() {
        if (videoMemoryCache != null) {
            videoMemoryCache.clear();
            videoMemoryCache = null;
        }
    }

    @Override
    public void onDataComplete(MultiDataModel dataModel, int dataType) {
        if (mUploadMultiModel != null) {
            cacheUploadedPicInfo(mUploadMultiModel.getUploadPicPojo());
            UploadPicPojo.UpPicRespData uploadPicPojo = getUploadResp();

            UploadVideoPojo uploadVideoPojo = mUploadMultiModel.getUploadVideoPojo();
            setVideoCache(uploadVideoPojo);

            Loger.d(TAG, "onDataComplete, on upload media finished ...");
            if (uploadPicPojo != null && !CollectionUtils.isEmpty(mSelectedLocalPics) && !mUploadMultiModel.isUploadSuccess() ||
                    uploadVideoPojo != null && !TextUtils.isEmpty(uploadVideoPojo.getVideoPath()) && !uploadVideoPojo.isRetSuccess()) {
                String errMsg = (uploadPicPojo != null && !mUploadMultiModel.isUploadSuccess()) ? mUploadMultiModel.getPicErroMsg() : (uploadVideoPojo != null ? uploadVideoPojo.getErrorMsg() : null);
                if (mUploadHelperCallback != null) {
                    mUploadHelperCallback.uploadEnd(false, errMsg, null, null);
                }
            } else {
                if (mUploadHelperCallback != null) {
                    mUploadHelperCallback.uploadEnd(true, ConstantValues.STRING_EMPTY, uploadPicPojo, uploadVideoPojo);
                }
            }
        }
    }

    @Override
    public void onDataError(MultiDataModel dataModel, int retCode, String retMsg, int dataType) {
        Loger.e(TAG, "onDataError, retCode: " + retCode + ", retMsg: " + retMsg);
        cacheUploadedPicInfo(mUploadMultiModel.getUploadPicPojo());
        if (mUploadHelperCallback != null) {
            mUploadHelperCallback.uploadEnd(false, retMsg, null, null);
        }
    }

    private UploadPicPojo.UpPicRespData getUploadResp() {
        UploadPicPojo.UpPicRespData uploadPicPojo = null;
        if (mSelectedLocalPics != null && mSelectedLocalPics.size() > 0) {
            uploadPicPojo = new UploadPicPojo.UpPicRespData();
            List<UploadPicPojo.UpPicInfo> pictures = new ArrayList<>();
            String key;
            UploadPicPojo value;
            for (int i = 0; i < mSelectedLocalPics.size(); i++) {
                key = mSelectedLocalPics.get(i);
                if (mFinishedPicMemoryCache != null) {
                    if (!mFinishedPicMemoryCache.containsKey(key) && mInitCompressedPicMap != null && mInitCompressedPicMap.containsKey(key)) {
                        key = mInitCompressedPicMap.get(key);
                    }
                    if (mFinishedPicMemoryCache.containsKey(key)) {
                        value = (UploadPicPojo) mFinishedPicMemoryCache.get(key);
                        if (value != null && value.getPicture() != null) {
                            pictures.addAll(value.getPicture());
                        }
                    }
                }
            }
            uploadPicPojo.setPicture(pictures);
        }
        return uploadPicPojo;
    }

    private void notifyCompressBegin(int compressFileCnt) {

    }

    public interface UploadHelperCallback {
        void onPrepareMediaBegin(int estimatedPrepareTimeInMs);

//        /**
//         * 外显的上传进度，包括前期压缩，中期上传，后期等待上传完毕的回调，其中前后两段通过假写来实现
//         *
//         * @param currentProgress
//         */
//        void onTotalUploadProgressChanged(long currentProgress);

        void uploadBegin();

        void uploadEnd(boolean success, String msg, UploadPicPojo.UpPicRespData upPicRespData, UploadVideoPojo uploadVideoPojo);
    }
}
