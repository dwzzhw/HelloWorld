package com.loading.modules.interfaces.upload;

import com.loading.modules.IModuleInterface;
import com.loading.modules.annotation.Repeater;
import com.loading.modules.data.MediaEntity;

import java.util.List;

@Repeater
public interface IUploadService extends IModuleInterface {
    /**
     * @return upload task id
     */
    String startUpload(List<String> localPics, MediaEntity videoEntity, String videoTitle);

    /**
     * @return upload task id
     */
    void startUpload(List<String> localPics, MediaEntity videoEntity, String videoTitle,
                     IUploadListener listener, UploadProgressMonitorListener uploadProgressListener);

    void cancelUpload(String taskId);
}
