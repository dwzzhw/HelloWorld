package com.loading.modules.interfaces.upload;

/**
 * Created by wilmaliu on 17/8/7.
 * the interface to moniotr post request progress to server
 */
public interface UploadProgressMonitorListener {
    void onUploadProgress(String key, long uploadedSize, long totalSize);
}
