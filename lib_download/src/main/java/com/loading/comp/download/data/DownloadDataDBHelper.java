package com.loading.comp.download.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.database.sqlite.SQLiteException;
import android.text.TextUtils;

import com.loading.common.component.CApplication;

import java.util.List;

@Database(entities = {DownloadDataInfo.class}, version = 3)
public abstract class DownloadDataDBHelper extends RoomDatabase {

    public static DownloadDataDBHelper getInstance() {
        return InstanceHolder.sInstance;
    }

    private static class InstanceHolder {
        static DownloadDataDBHelper sInstance = Room
                .databaseBuilder(CApplication.getAppContext(),
                        DownloadDataDBHelper.class,
                        DownloadDataInfo.DATABASE_NAME)
                .fallbackToDestructiveMigration()
                .build();
    }

//    //新的需要版本升级吗？ todo by wilma
//    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
//        @Override
//        public void migrate(SupportSQLiteDatabase database) {
//            //这里创建新的表，把老的数据迁移过来
//        }
//    };

    abstract DownloadDataDao getDownloadDataDao();

    public List<DownloadDataInfo> getDownloadInfoForTask(String taskId) {
        try {
            return !TextUtils.isEmpty(taskId) ? getDownloadDataDao().getInfosByTaskId(taskId) : null;
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        return null;
    }

    public long insertDownloadInfo(DownloadDataInfo downloadDataInfo) {
        try {
            return downloadDataInfo != null ? getDownloadDataDao().insert(downloadDataInfo) : -1;
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void updateDownloadInfo(DownloadDataInfo ... downloadDataInfos) {
        if ( downloadDataInfos != null  && downloadDataInfos.length > 0 ) {
            try {
                getDownloadDataDao().updateDownloadInfo(downloadDataInfos);
            } catch (SQLiteException e) {
                e.printStackTrace();
            }
        }
    }

    public void removeDownloadInfos(String taskId) {
        if (!TextUtils.isEmpty(taskId)) {
            try {
                getDownloadDataDao().deleteInfoByTaskId(taskId);
            } catch (SQLiteException e) {
                e.printStackTrace();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *
     * @param expireTime eg：三天 ： 3*24*60*60*1000
     */
    public void removeExpiredDownloadInfos(long expireTime) {
        try {
            getDownloadDataDao().deleteExpireInfo(System.currentTimeMillis() - expireTime);
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
    }


//    public void getInfosByTaskIdWithInvalidation(final String taskId, final String url, final String md5, final IDatabaseAsyncListener callback) {
//
//        AsyncOperationUtil.asyncOperation(new TagRunnable<List<DownloadDataInfo>>() {
//            @Override
//            public void run() {
//                setTagObj(getDownloadDataDao().getInfosByTaskIdWithInvalidation(taskId, url, md5));
//            }
//        }, objData -> {
//            if (objData instanceof TagRunnable) {
//                Object obj = ((TagRunnable) objData).getTagObj();
//                if (obj instanceof List) {
//                    if (callback != null) {
//                        callback.onDBAsyncComplete((List<DownloadDataInfo>) obj);
//                    }
//                }
//            }
//        });
//    }
//
//    public void getInfosByAppPackageName(final String packageName, final IDatabaseAsyncListener callback) {
//        AsyncOperationUtil.asyncOperation(new TagRunnable<List<DownloadDataInfo>>() {
//            @Override
//            public void run() {
//                setTagObj(getDownloadDataDao().getInfosByAppPackageName(packageName));
//            }
//        }, objData -> {
//            if (objData instanceof TagRunnable) {
//                Object obj = ((TagRunnable) objData).getTagObj();
//                if (obj instanceof List) {
//                    if (callback != null) {
//                        callback.onDBAsyncComplete((List<DownloadDataInfo>) obj);
//                    }
//                }
//            }
//        });
//    }
//
//    public void getInfosByTaskId(final String taskId, final IDatabaseAsyncListener callback) {
//        AsyncOperationUtil.asyncOperation(new TagRunnable<List<DownloadDataInfo>>() {
//            @Override
//            public void run() {
//                setTagObj(getDownloadDataDao().getInfosByTaskId(taskId));
//            }
//        }, objData -> {
//            if (objData instanceof TagRunnable) {
//                Object obj = ((TagRunnable) objData).getTagObj();
//                if (obj instanceof List) {
//                    if (callback != null) {
//                        callback.onDBAsyncComplete((List<DownloadDataInfo>) obj);
//                    }
//                }
//            }
//        });
//    }
//
//    public List<DownloadDataInfo> getAllList(final IDatabaseAsyncListener callback) {
//        AsyncOperationUtil.asyncOperation(new TagRunnable<List<DownloadDataInfo>>() {
//            @Override
//            public void run() {
//                setTagObj(getDownloadDataDao().getAllList());
//            }
//        }, objData -> {
//            if (objData instanceof TagRunnable) {
//                Object obj = ((TagRunnable) objData).getTagObj();
//                if (obj instanceof List) {
//                    if (callback != null) {
//                        callback.onDBAsyncComplete((List<DownloadDataInfo>) obj);
//                    }
//                }
//            }
//        });
//        return getDownloadDataDao().getAllList();
//    }
//
//    public void deleteInfoByTaskId(final String taskId) {
//        AsyncOperationUtil.asyncOperation(()-> getDownloadDataDao().deleteInfoByTaskId(taskId));
//    }
//
//    public void deleteList(List<String> taskIds) {
//        AsyncOperationUtil.asyncOperation(()-> getDownloadDataDao().deleteList(taskIds));
//    }
//
//    public void updateDownloadInfo(DownloadDataInfo dataInfo) {
//        AsyncOperationUtil.asyncOperation(()-> getDownloadDataDao().updateDownloadInfo(dataInfo));
//    }
//
//    public void addInfos(List<DownloadDataInfo> dataInfos) {
//        AsyncOperationUtil.asyncOperation(()-> getDownloadDataDao().insertAll(dataInfos));
//    }
//
//    public interface IDatabaseAsyncListener {
//        void onDBAsyncComplete(List<DownloadDataInfo> downloadInfoList);
//    }
}
