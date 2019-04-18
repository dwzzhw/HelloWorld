package com.loading.modules.interfaces.photoselector;

import android.app.Activity;
import android.content.Context;

import com.loading.modules.IModuleInterface;
import com.loading.modules.annotation.Repeater;
import com.loading.modules.data.MediaEntity;

import java.util.ArrayList;

/**
 * Created by loading on 2018/7/25.
 */

@Repeater
public interface IPhotoSelectorService extends IModuleInterface {
    void startPhotoSelectPage(Context context, int maxCount, ArrayList<MediaEntity> psPhotoEntities, boolean includeVideo);

    void startPhotoPreviewPage(Context context, boolean isPreView, boolean isShowDelete, String selectedPath, ArrayList<MediaEntity> hasSelectedList);

    void addPSListener(IPSListener listener);

    void removePSListener(IPSListener listener);

    void notifyPSChanged(ArrayList<MediaEntity> psPhotoEntities);

    void startVideoEditorPage(Activity activity, MediaEntity mediaEntity, int requestCode);
}
