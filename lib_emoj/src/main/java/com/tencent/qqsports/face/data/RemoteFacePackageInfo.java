package com.tencent.qqsports.face.data;

import android.text.TextUtils;

import com.loading.modules.interfaces.face.data.FaceItem;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RemoteFacePackageInfo implements Serializable {
    private static final long serialVersionUID = 8386048639175478955L;

    public FacePackageInfo text;
    public FaceZipFileInfo file;
    public String textMD5;
    public String fileMD5;

    public volatile String mLocalPackageFullPath = null;
    private volatile AtomicInteger mPrepareInfoRetryCnt = new AtomicInteger(0);

    public String getZipFileUrl() {
        return file != null ? file.downURL : null;
    }

    public String getZipFileMd5() {
        return fileMD5;
    }

    public String getPackageName() {
        return text != null ? text.groupName : "";
    }

    public String getPackageIcon() {
        return text != null ? text.icon : null;
    }

    public List<FaceItem> getFaceList() {
        return text != null ? text.encoder : null;
    }

    public FacePackageInfo getFacePackageInfo() {
        return text;
    }

    public int getPrepareInfoRetryCnt() {
        return mPrepareInfoRetryCnt.get();
    }

    public void increasePrepareInfoRetryCnt() {
        this.mPrepareInfoRetryCnt.incrementAndGet();
    }

    public boolean isTheSamePackage(RemoteFacePackageInfo otherPackage) {
        return otherPackage != null && !TextUtils.isEmpty(textMD5) && !TextUtils.isEmpty(fileMD5) && textMD5.equals(otherPackage.textMD5) && fileMD5.equals(otherPackage.fileMD5);
    }

    public static class FaceZipFileInfo implements Serializable {
        private static final long serialVersionUID = -7577930746236211498L;
        public String downURL;
        public String fileName;
    }
}
