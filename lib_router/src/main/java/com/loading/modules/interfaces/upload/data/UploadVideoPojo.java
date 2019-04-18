package com.loading.modules.interfaces.upload.data;

import android.text.TextUtils;

import java.io.Serializable;

/**
 * Created by wilmaliu on 17/6/6.
 */

public class UploadVideoPojo implements Serializable {
    private static final long serialVersionUID = 3074181575024949320L;
    private int code = -1;
    private String msg;
    private UploadVideoRespData data;
    private transient UploadVideoLocalData videoLocalData;

    public UploadVideoRespData getVideoResp() {
        return data;
    }

    public UploadVideoRespData getData() {
        return data;
    }

    public void setRespData(UploadVideoRespData data) {
        this.data = data;
    }


    public int getCode() {
        return code;
    }

    public boolean isRetSuccess() {
        return code == 0;
    }

    public String getErrorMsg() {
        return msg;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getVideoPath() {
        return videoLocalData != null ? videoLocalData.videoPath : null;
    }

    public UploadVideoLocalData getVideoLocalData() {
        return videoLocalData;
    }

    public void setVideoLocalData(UploadVideoLocalData localData) {
        this.videoLocalData = localData;
    }

    public static class UploadVideoRespData implements Serializable {
        private static final long serialVersionUID = -290568977900915359L;
        public String vid;
        public String videoUrl;

        public boolean isRespEffected() {
            return !TextUtils.isEmpty(vid) && !TextUtils.isEmpty(videoUrl);
        }

        public void setVid(String vid) {
            this.vid = vid;
        }
    }

    public static class UploadVideoLocalData implements Serializable {
        private static final long serialVersionUID = 8444526812857743170L;
        public String videoPath;
        public float aspect;
        public String picUrl;
        public boolean isFromInnerShare;
        public long durationL;
    }
}
