package com.tencent.qqsports.face;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import com.loading.common.utils.CommonUtils;
import com.loading.common.utils.Loger;
import com.tencent.qqsports.emoj.R;
import com.tencent.qqsports.face.data.FaceItem;
import com.tencent.qqsports.face.data.RemoteFacePackageInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class RemoteFacePackage extends BaseFacePackage {
    private static final String TAG = "RemoteFacePackage";
    private RemoteFacePackageInfo mRemoteFacePackageInfo;
    private String mFacePackageFolderPath; //表情包所在的文件夹完整路径
    private Object mPackageSelectedIndicatorObj;

    RemoteFacePackage(RemoteFacePackageInfo remoteFacePackageInfo) {
        Loger.d(TAG, "-->RemoteFacePackage<init>, remoteFacePackageInfo=" + remoteFacePackageInfo);
        mRemoteFacePackageInfo = remoteFacePackageInfo;
        init();
    }

    @Override
    protected void init() {
        if (mRemoteFacePackageInfo != null) {
            mFacePackageFolderPath = mRemoteFacePackageInfo.getFacePackageFolderFullPath();
            if (mFacePackageFolderPath != null && !mFacePackageFolderPath.endsWith(File.separator)) {
                mFacePackageFolderPath += File.separator;
            }

            List<FaceItem> faceItemList = mRemoteFacePackageInfo.getFaceList();
            if (!CommonUtils.isEmpty(faceItemList)) {
                mFaceName2FaceFileMappingProperties = new Properties();
                mFaceNameList = new ArrayList<>();
                File folderFile = new File(mFacePackageFolderPath);
                for (FaceItem faceItem : faceItemList) {
                    String faceName = faceItem != null ? faceItem.getWrapperStickerName() : null;
                    String facePath = faceItem != null ? faceItem.getFileName() : null;
                    if (!TextUtils.isEmpty(faceName) && !TextUtils.isEmpty(facePath)) {
                        mFaceName2FaceFileMappingProperties.put(faceName, facePath);
                        if (new File(folderFile, facePath).exists()) {
                            mFaceNameList.add(faceName);
                        }
                    }
                }
            }
        }
    }

    @Override
    protected Bitmap createFaceBitmapForFaceFile(String faceFileName) {
        Bitmap bitmap = null;
        if (!TextUtils.isEmpty(mFacePackageFolderPath) && !TextUtils.isEmpty(faceFileName)) {
            try {
                bitmap = BitmapFactory.decodeFile(mFacePackageFolderPath + faceFileName);
            } catch (Exception e) {
                Loger.w(TAG, "Fail to load face image, faceFileName=" + faceFileName, e);
            }
        }
        return bitmap;
    }

    @Override
    public Object getPackageIndicatorRes() {
        if (mPackageSelectedIndicatorObj == null) {
            String bitmapName = mRemoteFacePackageInfo != null ? mRemoteFacePackageInfo.getPackageIcon() : null;
            mPackageSelectedIndicatorObj = createFaceBitmapForFaceFile(bitmapName);

            Loger.d(TAG, "-->getPackageIndicatorRes(), bitmapName=" + bitmapName + ", obj=" + mPackageSelectedIndicatorObj);
            if (mPackageSelectedIndicatorObj == null) {
                mPackageSelectedIndicatorObj = R.drawable.weixiao1;
            }
        }
        return mPackageSelectedIndicatorObj;
    }
}
