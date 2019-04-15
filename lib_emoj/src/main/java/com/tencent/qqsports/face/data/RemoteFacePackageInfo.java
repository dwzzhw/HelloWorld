package com.tencent.qqsports.face.data;

import android.text.TextUtils;

import com.tencent.qqsports.face.FaceManager;
import com.tencent.qqsports.face.FaceUtil;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RemoteFacePackageInfo implements Serializable {
    private static final long serialVersionUID = 8386048639175478955L;

    private FacePackageInfo text;
    private FaceZipFileInfo file;
    private String textMD5;
    private String fileMD5;

    private volatile String mLocalPackageFullPath = null;
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

    public String getPackageHintIcon() {
        return text != null ? text.iconHit : null;
    }

    public String getFacePackageFolderFullPath() {
        if (TextUtils.isEmpty(mLocalPackageFullPath)) {
            StringBuilder fullPathBuilder = new StringBuilder(FaceManager.getInstance().getLocalFacePackageFolderPath());
            if (!FaceManager.getInstance().getLocalFacePackageFolderPath().endsWith(File.separator)) {
                fullPathBuilder.append(File.separator);
            }
            fullPathBuilder.append(getPackageName());
            fullPathBuilder.append("___");
            if (!TextUtils.isEmpty(getZipFileMd5())) {
                fullPathBuilder.append(getZipFileMd5());
            }
            mLocalPackageFullPath = fullPathBuilder.toString();
        }
        return mLocalPackageFullPath;
    }

    public List<FaceItem> getFaceList() {
        return text != null ? text.encoder : null;
    }

    public FacePackageInfo getFacePackageInfo() {
        return text;
    }

    public boolean checkFaceInfoReadyState() {
        String facePackageFolderPath = getFacePackageFolderFullPath();
        return FaceUtil.isValidFacePackageFolder(facePackageFolderPath);
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

    public static List<RemoteFacePackageInfo> mockRemoteFacePackageInfoList() {
        List<RemoteFacePackageInfo> infoList = new ArrayList<>(2);

        RemoteFacePackageInfo packageInfo1 = new RemoteFacePackageInfo();
        FacePackageInfo info1 = new FacePackageInfo();
        info1.groupId = "net_face_01";
        info1.groupName = "net_face_01";
        List<FaceItem> faceList1 = new ArrayList<>();
        FaceItem faceItem11 = new FaceItem("11", "[冷]", "leng.png");
        FaceItem faceItem12 = new FaceItem("12", "[冷汗]", "lenghan.png");
        FaceItem faceItem13 = new FaceItem("13", "[冷酷]", "lengku.png");
        FaceItem faceItem14 = new FaceItem("14", "[力量]", "liliang.png");
        faceList1.add(faceItem11);
        faceList1.add(faceItem12);
        faceList1.add(faceItem13);
        faceList1.add(faceItem14);
        info1.encoder = faceList1;
        FaceZipFileInfo zipFileInfo1 = new FaceZipFileInfo();
        zipFileInfo1.downURL = "http://mat1.gtimg.com/sports/sportapp/vip/PrivilegeProps/heishao.mp4";
        packageInfo1.file = zipFileInfo1;
        packageInfo1.text = info1;

        RemoteFacePackageInfo packageInfo2 = new RemoteFacePackageInfo();
        FacePackageInfo info2 = new FacePackageInfo();
        info2.groupId = "net_face_02";
        info2.groupName = "net_face_02";
        info2.icon = "http://mat1.gtimg.com/sports/sportapp/vip/teamlogo/basic_logo.png";
        List<FaceItem> faceList2 = new ArrayList<>();
        FaceItem faceItem21 = new FaceItem("21", "[林书豪]", "linshuhao.png");
        FaceItem faceItem22 = new FaceItem("22", "[卖萌]", "maimeng.png");
        FaceItem faceItem23 = new FaceItem("23", "[挪威司机]", "nuoweisiji.png");
        faceList2.add(faceItem21);
        faceList2.add(faceItem22);
        faceList2.add(faceItem23);
        info2.encoder = faceList2;
        FaceZipFileInfo zipFileInfo2 = new FaceZipFileInfo();
        zipFileInfo2.downURL = "http://mat1.gtimg.com/sports/sportapp/vip/PrivilegeProps/juesha.mp4";
        packageInfo2.file = zipFileInfo2;
        packageInfo2.text = info2;

        infoList.add(packageInfo1);
        infoList.add(packageInfo2);

        return infoList;
    }

    private static class FaceZipFileInfo implements Serializable {
        private static final long serialVersionUID = -7577930746236211498L;
        private String downURL;
        private String fileName;
    }
}
