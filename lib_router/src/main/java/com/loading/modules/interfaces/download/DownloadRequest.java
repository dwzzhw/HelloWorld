package com.loading.modules.interfaces.download;

import android.text.TextUtils;

import java.util.Map;

public class DownloadRequest {
    private String taskId;
    private String url;
    private String md5String;
    private long mFileSize;
    private String pushTitle;                      //StatusBar的进度提示标题，不需要提示时忽略该字段
    private boolean silent;                        //下载的关键阶段是否允许给出提示
    private boolean cacheResponseHeader;           //是否缓存下载请求的响应头
    private boolean isDownloadApk;
    private Map<String, String> requestHeader;
    private boolean isBackgroundReq;
    private boolean mSupportSliceDownload;         //是否支持分片下载，默认都支持，在检测到不支持时，通过该标记来重置下载方式
    private DownloadListener mDownloadListener;    //该请求所绑定的监听器
    private long mQueriedFileSize;                 //从后台所查询到的文件大小

    public static DownloadRequest newInstance(String taskId, String url) {
        return new DownloadRequest(taskId, url, null, 0, null, true, false, false);
    }

    public static DownloadRequest newInstance(String taskId, String url, boolean isBackgroundReq) {
        return new DownloadRequest(taskId, url, null, 0, null, true, false, isBackgroundReq);
    }

    public static DownloadRequest newInstance(String taskId, String url, String md5String) {
        return new DownloadRequest(taskId, url, md5String, 0, null, true, false, false);
    }

    public static DownloadRequest newInstance(String taskId, String url, String md5String, long fileSize) {
        return new DownloadRequest(taskId, url, md5String, fileSize, null, true, false, false);
    }

    public static DownloadRequest newInstance(String taskId, String url, String md5String, boolean silent) {
        return new DownloadRequest(taskId, url, md5String, 0, null, silent, false, false);
    }

    public static DownloadRequest newInstance(String taskId, String url, String md5String, String pushTitle, boolean isDownloadApk) {
        return new DownloadRequest(taskId, url, md5String, 0, pushTitle, true, isDownloadApk, false);
    }

    public DownloadRequest(String url,
                           String md5String,
                           long fileSize,
                           String pushTitle,
                           boolean silent,
                           boolean isDownloadApk) {
        this(null, url, md5String, fileSize, pushTitle, silent, isDownloadApk, false);
    }

    public DownloadRequest(String taskId,
                           String url,
                           String md5String,
                           long fileSize,
                           String pushTitle,
                           boolean silent,
                           boolean isDownloadApk,
                           boolean isBackgroundReq) {
        this.taskId = taskId;
        this.url = url;
        this.md5String = md5String;
        this.mFileSize = fileSize;
        this.pushTitle = pushTitle;
        this.silent = silent;
        this.isDownloadApk = isDownloadApk;
        this.isBackgroundReq = isBackgroundReq;
        mSupportSliceDownload = true;
    }

    public boolean isFileMd5Match(String fileMd5) {
        return TextUtils.isEmpty(md5String) || !TextUtils.isEmpty(fileMd5) && fileMd5.contains(md5String);
    }

    public void setDownloadApk(boolean downloadApk) {
        isDownloadApk = downloadApk;
    }

    /**
     * 请求参数发生变化时，判断此前的请求对象是否过期
     *
     * @param url the download ur
     * @param md5 md5 string in download request
     * @return true for expired, else false
     */
    public boolean isRequestExpired(String url, String md5) {
        return !TextUtils.isEmpty(url) && !TextUtils.equals(url, this.url) ||
                !TextUtils.isEmpty(md5) && !TextUtils.equals(md5, this.md5String);
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMd5String() {
        return md5String;
    }

    public void setMd5String(String md5String) {
        this.md5String = md5String;
    }

    public String getPushTitle() {
        return pushTitle;
    }

    public void setPushTitle(String pushTitle) {
        this.pushTitle = pushTitle;
    }

    public boolean isSilent() {
        return silent;
    }

    public boolean isDownloadApk() {
        return isDownloadApk;
    }

    public void setSilent(boolean silent) {
        this.silent = silent;
    }

    public boolean isCacheResponseHeader() {
        return cacheResponseHeader;
    }

    public void setCacheResponseHeader(boolean cacheResponseHeader) {
        this.cacheResponseHeader = cacheResponseHeader;
    }

    public void setRequestHeader(Map<String, String> requestHeader) {
        this.requestHeader = requestHeader;
    }

    public Map<String, String> getRequestHeader() {
        return this.requestHeader;
    }

    public long getFileSize() {
        return mFileSize;
    }

    public boolean isBackgroundReq() {
        return isBackgroundReq;
    }

    public void disableSliceDownload() {
        mSupportSliceDownload = false;
    }

    public boolean isSupportSliceDownload() {
        return mSupportSliceDownload;
    }

    public DownloadListener getDownloadListener() {
        return mDownloadListener;
    }

    public void setDownloadListener(DownloadListener downloadListener) {
        this.mDownloadListener = downloadListener;
    }

    public long getQueriedFileSize() {
        return mQueriedFileSize;
    }

    public void setQueriedFileSize(long queriedFileSize) {
        this.mQueriedFileSize = queriedFileSize;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof DownloadRequest && taskId != null && taskId.equals(((DownloadRequest) obj).taskId);
    }

    public boolean hasSameDownloadType(DownloadRequest downloadRequest) {
        return downloadRequest != null && isBackgroundReq == downloadRequest.isBackgroundReq && mSupportSliceDownload == downloadRequest.mSupportSliceDownload;
    }

    @Override
    public String toString() {
        return "DownloadRequest{" +
                "taskId='" + taskId + '\'' +
                ", url='" + url + '\'' +
                ", md5String='" + md5String + '\'' +
                ", pushTitle='" + pushTitle + '\'' +
                ", silent=" + silent +
                ", isBackgroundReq=" + isBackgroundReq +
                '}';
    }
}
