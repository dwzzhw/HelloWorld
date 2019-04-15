package com.tencent.qqsports.face.data;

import android.text.TextUtils;

import java.io.Serializable;

public class FaceItem implements Serializable {
    private static final String TAG = "FaceItem";
    private static final long serialVersionUID = -528033118219370033L;
    private String stickerId;
    private String stickerName;
    private String fileName;

    public FaceItem(String stickerId, String stickerName, String fileName) {
        this.stickerId = stickerId;
        this.stickerName = stickerName;
        this.fileName = fileName;
    }

    public String getStickerId() {
        return stickerId;
    }

    public String getStickerName() {
        return stickerName;
    }

    public String getWrapperStickerName() {
        String wrapperName = stickerName;
        if (!TextUtils.isEmpty(stickerName)) {
            if (!stickerName.startsWith("[")) {
                wrapperName = "[" + wrapperName;
            }
            if (!stickerName.endsWith("]")) {
                wrapperName = wrapperName + "]";
            }
        }
        return wrapperName;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileNameWithoutPostFix() {
        String name = fileName;
        if (name != null && name.contains(".")) {
            String[] tmpArray = name.split("\\.");
            if (tmpArray != null && tmpArray.length > 0) {
                name = tmpArray[0];
            }
        }
//        Loger.d(TAG, "-->getFileNameWithoutPostFix(), fileName=" + fileName + ", pure name=" + name);
        return name;
    }
}