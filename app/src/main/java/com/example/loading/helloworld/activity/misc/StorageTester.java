package com.example.loading.helloworld.activity.misc;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;

import com.loading.common.utils.Loger;

import java.io.File;
import java.lang.reflect.Method;
import java.util.List;

@TargetApi(Build.VERSION_CODES.N)
public class StorageTester {
    private static final String TAG = "StorageTester";

    public StorageTester() {

    }

    public String getExternalSDCardPath(Context context) {
        try {
            StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
            // 7.0才有的方法
            List<StorageVolume> storageVolumes = storageManager.getStorageVolumes();
            Class<?> volumeClass = Class.forName("android.os.storage.StorageVolume");
            Method getPath = volumeClass.getDeclaredMethod("getPath");
            Method isRemovable = volumeClass.getDeclaredMethod("isRemovable");
            getPath.setAccessible(true);
            isRemovable.setAccessible(true);
            for (int i = 0; i < storageVolumes.size(); i++) {
                StorageVolume storageVolume = storageVolumes.get(i);
                String mPath = (String) getPath.invoke(storageVolume);
                Boolean isRemove = (Boolean) isRemovable.invoke(storageVolume);
                Loger.d(TAG, "mPath is === " + mPath + ", isRemoveble == " + isRemove);

                File tmpFile = new File(mPath + "/tmp/mi_mall.mht");
                boolean isTmpFileExist = tmpFile.exists();
                Loger.d(TAG, "-->getExternalSDCardPath(), tmpFile=" + tmpFile.getAbsolutePath()
                        + ", exist=" + isTmpFileExist + ", size=" + tmpFile.length());
            }
        } catch (Exception e) {
            Loger.d(TAG, "e == " + e.getMessage());
        }
        return "";
    }
}
