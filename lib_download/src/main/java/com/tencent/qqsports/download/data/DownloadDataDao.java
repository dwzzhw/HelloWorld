package com.tencent.qqsports.download.data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;
import android.arch.persistence.room.Update;

import java.util.List;



@Dao
public interface DownloadDataDao {
    @Query("SELECT * FROM " + DownloadDataInfo.TABLE_NAME + " WHERE " + DownloadDataInfo.TASKID + " = :taskId "
            + " and " + DownloadDataInfo.TASKID + " = :url "
            + " ORDER BY " + DownloadDataInfo.SEGMENT_ID )
    List<DownloadDataInfo> getInfosByTaskIdWithInvalidation(String taskId, String url);

    @Query("SELECT * FROM " + DownloadDataInfo.TABLE_NAME + " WHERE " + DownloadDataInfo.TASKID + " = :taskId "
            + " ORDER BY " + DownloadDataInfo.SEGMENT_ID )
    @Transaction
    List<DownloadDataInfo> getInfosByTaskId(String taskId);


    @Query("SELECT * FROM " + DownloadDataInfo.TABLE_NAME + " WHERE " + DownloadDataInfo.PACKAGE + " = :packageName "
            + " ORDER BY " + DownloadDataInfo.SEGMENT_ID)
    List<DownloadDataInfo> getInfosByAppPackageName(String packageName);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(DownloadDataInfo entity);

    @Query("SELECT * FROM " + DownloadDataInfo.TABLE_NAME + " ORDER BY " + DownloadDataInfo.ID + " DESC ")
    List<DownloadDataInfo> getAllList();

    @Query("DELETE FROM " + DownloadDataInfo.TABLE_NAME + " WHERE " + DownloadDataInfo.TASKID + " = :taskId")
    void deleteInfoByTaskId(String taskId);

    @Query("DELETE FROM " + DownloadDataInfo.TABLE_NAME + " WHERE " + DownloadDataInfo.TASKID + " IN (:taskIds)")
    void deleteList(List<String> taskIds);

    @Query("DELETE FROM " + DownloadDataInfo.TABLE_NAME + " WHERE " + DownloadDataInfo.TIMESTAMP + "<= :expireTime")
    void deleteExpireInfo(long expireTime);

    @Update
    @Transaction
    void updateDownloadInfo(DownloadDataInfo ... dataInfos);
}
