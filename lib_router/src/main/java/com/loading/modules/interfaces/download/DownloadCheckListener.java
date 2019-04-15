package com.loading.modules.interfaces.download;

/**
 * Created by kuiweiwang on 2018/1/17.
 * listener for file Download
 */

public interface DownloadCheckListener {
    /**
     * 查询文件下载状态的回调
     *
     * @param resultFilePath 非空表示文件已存在
     */
    void onGetFilePath(String resultFilePath);
}
