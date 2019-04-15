package com.tencent.qqsports.commentbar.videorecord;

import com.tencent.qqsports.common.pojo.MediaEntity;

/**
 * Created by anxin on 2018/7/12.
 * <p>
 */
public interface IPCCameraCallBack {
    /**
     * 录像返回
     *
     * @param psMediaEntity
     */
    void onCameraRecordRet(MediaEntity psMediaEntity);

    /**
     * 拍照返回
     *
     * @param psMediaEntity
     */
    void onCameraPhotoRet(MediaEntity psMediaEntity);
}
