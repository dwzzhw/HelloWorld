package com.loading.comp.download.data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import android.text.TextUtils;

import com.loading.common.utils.CommonUtil;

import java.util.Map;

import io.reactivex.annotations.NonNull;

@Entity(tableName = DownloadDataInfo.TABLE_NAME)
public class DownloadDataInfo {
    public static final String DATABASE_NAME = "sport_download_db";
    public static final String TABLE_NAME    = "download_info";

    public static final String ID = "_id";
    public static final String SEGMENT_ID = "segment_id";// 分片索引
    public static final String START_POS = "start_pos";// 线程开始位置
    public static final String END_POS = "end_pos";// 线程结束位置
    public static final String DOWNLOAD_SIZE = "download_size";
    public static final String COMPELETE_SIZE = "compelete_size";// 该线程块下载完成大小
    public static final String DOWNLOAD_URL = "download_url";// 下载url
    public static final String TASKID = "task_id";// 下载任务Id
    public static final String PACKAGE = "package_name";// 应用的包名
    public static final String TITLE = "push_title";
    public static final String CONTENT = "push_content";
    public static final String ICON = "push_icon";
    public static final String MD5 = "md5";
    public static final String TIMESTAMP = "timestamp"; //加入时间
    public static final String REQUEST_HEADER = "requestHeader";

    @NonNull
    @PrimaryKey(autoGenerate = true) // 设置主键 自增
    @ColumnInfo(name = ID)
    private long id;

    @ColumnInfo(name=SEGMENT_ID)
    private int segmentId;// 下载线程id

    @ColumnInfo(name=START_POS)
    private long startPos;// 该下载线程的起始位置,类似数组下标,从0开始

    @ColumnInfo(name=END_POS)
    private long endPos;// 该下载线程的结束位置

    @ColumnInfo(name=DOWNLOAD_SIZE)
    private long downloadSize; //预先计算的要下载的大小，用于计算当前下载的百分比

    @ColumnInfo(name=COMPELETE_SIZE)
    private long completeSize;// 该下载线程的下载完成大小 ,类似数组长度,从1开始

    @ColumnInfo(name=DOWNLOAD_URL)
    private String urlStr;//下载URL地址

    @ColumnInfo(name=TASKID)
    private String taskId;

    @ColumnInfo(name=PACKAGE)
    private String packageName;//下载包包名

    @ColumnInfo(name=TITLE)
    private String pushTitle = "";

    @ColumnInfo(name=CONTENT)
    private String pushContent = "";

    @ColumnInfo(name=ICON)
    private String pushIcon = "";

    @ColumnInfo(name=MD5)
    private String md5Str;

    @ColumnInfo(name=TIMESTAMP)
    private long timestamp;

    @ColumnInfo(name=REQUEST_HEADER)
    private String requestHeader;

    DownloadDataInfo() {
    }

    public DownloadDataInfo(int segmentId,
                            long startPos,
                            long endPos,
                            long downloadSize,
                            long completeSize,
                            String urlStr,
                            String taskId,
                            String packageName,
                            String pushTitle,
                            String pushContent,
                            String pushIcon,
                            String md5,
                            Map<String, String> requestHeader,
                            long timestamp) {
        this.segmentId = segmentId;
        this.startPos = startPos;
        this.endPos = endPos;
        this.downloadSize = downloadSize;
        this.completeSize = completeSize;
        this.urlStr = urlStr;
        this.taskId = taskId;
        this.packageName = packageName;
        this.pushTitle = pushTitle;
        this.pushContent = pushContent;
        this.pushIcon = pushIcon;
        this.md5Str = md5;
        this.requestHeader = CommonUtil.toJson(requestHeader);
        this.timestamp = timestamp;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getUrlStr() {
        return urlStr;
    }

    public void setUrlStr(String urlStr) {
        this.urlStr = urlStr;
    }

    public int getSegmentId() {
        return segmentId;
    }

    public void setSegmentId(int threadId) {
        this.segmentId = threadId;
    }

    public long getStartPos() {
        return startPos;
    }

    public void setStartPos(long startPos) {
        this.startPos = startPos;
    }

    public long getEndPos() {
        return endPos;
    }

    public void setEndPos(long endPos) {
        this.endPos = endPos;
    }

    public void flatComplete() {
        if ( endPos < 0 && startPos > 0 && completeSize > 0 ) {
            endPos = startPos + completeSize;
        }
    }

    public long getDownloadSize() {
        return endPos > 0 ? (endPos - startPos) : (completeSize > downloadSize ? completeSize : downloadSize);
    }

    public void setDownloadSize(long downloadSize) {
        this.downloadSize = downloadSize;
    }

    public long getCompleteSize() {
        return completeSize;
    }

    public boolean isDownloadCompleted() {
        return completeSize > 0 && getDownloadSize() == completeSize;
    }

    public void setCompleteSize(long completeSize) {
        this.completeSize = completeSize;
    }

    public String getPushTitle() {
        return pushTitle;
    }

    public void setPushTitle(String pushTitle) {
        this.pushTitle = pushTitle;
    }

    public String getPushContent() {
        return pushContent;
    }

    public void setPushContent(String pushContent) {
        this.pushContent = pushContent;
    }

    public String getPushIcon() {
        return pushIcon;
    }

    public void setPushIcon(String pushIcon) {
        this.pushIcon = pushIcon;
    }

    public String getMd5Str() {
        return md5Str;
    }

    public void setMd5Str(String md5Str) {
        this.md5Str = md5Str;
    }

    public void setRequestHeader(String requestHeader) {
        this.requestHeader = requestHeader;
    }

    public String getRequestHeader() {
        return requestHeader;
    }

    public Map<String, String> getRequestHeaderForRequest() {
        String header = getRequestHeader();
        if(!TextUtils.isEmpty(header)) {
            return CommonUtil.fromJson(header, Map.class);
        } else {
            return null;
        }
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isStillValid(String url, String md5) {
        boolean valid = true;
        if (!TextUtils.isEmpty(url) && !url.equals(urlStr) || !TextUtils.isEmpty(md5) && !md5.equals(md5Str)) {
            valid = false;
        }
        return valid;
    }

    @Override
    public String toString() {
        return super.toString() + ", id: " + id + ", threadId: " + segmentId
                 + ", startPos: " + startPos
                 + ", endPos: " + endPos
                 + ", completeSize: " + completeSize
                 + ", md5: " + md5Str
                 + ", timestamp: " + timestamp;
    }
}