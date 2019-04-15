package com.tencent.qqsports.face;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import com.tencent.qqsports.common.CApplication;
import com.tencent.qqsports.common.util.CollectionUtils;
import com.tencent.qqsports.common.util.FileHandler;
import com.tencent.qqsports.emoj.R;
import com.tencent.qqsports.face.data.FaceItem;
import com.tencent.qqsports.face.data.FacePackageInfo;
import com.tencent.qqsports.logger.Loger;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class LocalFacePackage extends BaseFacePackage {
    private static final String TAG = "LocalFacePackage";

    public LocalFacePackage() {
        init();
    }

    @Override
    protected void init() {
        Object localPackageObj = FileHandler.getObjectFileFromAssets(FaceManager.LOCAL_FACE_PACKAGE_NAME);
        Loger.d(TAG, "-->init(), localPackageObj=" + localPackageObj);
        if (localPackageObj instanceof FacePackageInfo) {
            FacePackageInfo facePackageInfo = (FacePackageInfo) localPackageObj;

            List<FaceItem> faceItemList = facePackageInfo.getFaceNameList();
            if (!CollectionUtils.isEmpty(faceItemList)) {
                mFaceName2FaceFileMappingProperties = new Properties();
                mFaceNameList = new ArrayList<>();
                for (FaceItem faceItem : faceItemList) {
                    String faceName = faceItem != null ? faceItem.getWrapperStickerName() : null;
                    String faceResName = faceItem != null ? faceItem.getFileNameWithoutPostFix() : null;
                    if (!TextUtils.isEmpty(faceName) && !TextUtils.isEmpty(faceResName)) {
                        mFaceName2FaceFileMappingProperties.put(faceName, faceResName);
                        mFaceNameList.add(faceName);
                    }
                }
            }
        }
    }

    @Override
    protected Bitmap createFaceBitmapForFaceFile(String faceFileName) {
        Bitmap bitmap = null;
        if (!TextUtils.isEmpty(faceFileName)) {
            try {
                bitmap = BitmapFactory.decodeResource(CApplication.getAppContext().getResources(), R.drawable.class.getDeclaredField(faceFileName).getInt(null));
            } catch (IllegalArgumentException e) {
                Loger.e(TAG, "exception: " + e);
            } catch (IllegalAccessException e) {
                Loger.e(TAG, "exception: " + e);
            } catch (NoSuchFieldException e) {
                Loger.e(TAG, "exception: " + e);
            } catch (Exception e) {
                Loger.e(TAG, "exception: " + e);
            } catch (OutOfMemoryError e) {
                Loger.e(TAG, "exception: " + e);
                System.gc();
                System.runFinalization();
            }
        }
        return bitmap;
    }

    @Override
    public Object getPackageIndicatorRes() {
        return R.drawable.weixiao1;
    }
}
