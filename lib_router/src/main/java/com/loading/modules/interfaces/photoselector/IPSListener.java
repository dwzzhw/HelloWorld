package com.loading.modules.interfaces.photoselector;

import com.loading.modules.data.MediaEntity;

import java.util.ArrayList;

/**
 * Created by loading on 2018/7/31.
 */

public interface IPSListener {
    void onSelectedPhotoChanged(ArrayList<MediaEntity> data);
}
