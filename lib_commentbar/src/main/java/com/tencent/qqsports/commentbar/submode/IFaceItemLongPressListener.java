package com.tencent.qqsports.commentbar.submode;

import android.graphics.Bitmap;

public interface IFaceItemLongPressListener {
    void onFaceLongPressed(Bitmap faceBitmap, String faceName, float faceViewCenterX, float faceViewCenterY);
    void enterLongPressState();
    void exitLongPressState();
}
