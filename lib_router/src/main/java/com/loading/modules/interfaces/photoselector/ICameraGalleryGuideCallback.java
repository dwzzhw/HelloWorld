package com.loading.modules.interfaces.photoselector;

/**
 * Created by anxin on 2018/7/12.
 * <p>
 */
public interface ICameraGalleryGuideCallback {

    // Camera 可支持的类别
    int SHOW_TYPE_VIDEO_AND_PIC = 0; // 支持拍照和拍摄视频
    int SHOW_TYPE_PIC = 1; // 只支持拍照

    void onCameraClick(int showType);

    void onGalleryClick(int showType);

    default void onCameraGalleryDialogCancel() {
    }

    ;
}
