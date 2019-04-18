package com.loading.modules.interfaces.upload;

import com.loading.modules.interfaces.upload.data.UploadPicPojo;
import com.loading.modules.interfaces.upload.data.UploadVideoPojo;

public interface IUploadListener {
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
