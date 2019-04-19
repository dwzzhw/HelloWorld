package com.tencent.qqsports.face;

import android.graphics.Bitmap;

/**
 * 表情面板的其中一页内容
 * Created by loading in 2019.02.19
 */
public class FacePageItems {
    private BaseFacePackage mPackageInfo;
    private int mIndexInPackage;
    private int mFaceCntPerPage;

    public FacePageItems(BaseFacePackage packageInfo, int indexInPackage, int cntPerPage) {
        this.mPackageInfo = packageInfo;
        this.mIndexInPackage = indexInPackage;
        this.mFaceCntPerPage = cntPerPage;
    }

    public Bitmap getFaceBitmapAtGroupPosition(int pos) {
        return mPackageInfo != null ? mPackageInfo.getFaceBitmapAtPosition(mFaceCntPerPage * mIndexInPackage + pos) : null;
    }

    public String getFaceStringAtGroupPosition(int pos) {
        return mPackageInfo != null ? mPackageInfo.getFaceStringAtPosition(mFaceCntPerPage * mIndexInPackage + pos) : null;
    }

    public int getPanelColumnCnt() {
        return mPackageInfo != null ? mPackageInfo.getColumnCnt() : 0;
    }

    public int getGridItemPaddingLR() {
        return mPackageInfo != null ? mPackageInfo.getGridItemPaddingLR() : 0;
    }

    public int getGridItemVerticalSpacing() {
        return mPackageInfo != null ? mPackageInfo.getGridItemVerticalSpacing() : 0;
    }

    public int getPanelRowCnt() {
        return mPackageInfo != null ? mPackageInfo.getRowCnt() : 0;
    }

    public int getFaceCntPerPage() {
        return mFaceCntPerPage;
    }

    public BaseFacePackage getFacePackageInfo() {
        return mPackageInfo;
    }

    public int getPageIndexInFacePackage() {
        return mIndexInPackage;
    }
}
