package com.example.loading.helloworld;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import androidx.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by loading on 11/17/15.
 */
public class MemoryMonitorService extends Service {
    private static final String TAG = MemoryMonitorService.class.getSimpleName();
    private static final int MB = 1024 * 1024;
    private ActivityManager mAM = null;
    private int mTotalMemory = 0;
    private boolean needStop = false;
    private MonitorThread monitorThread = null;

    private List<Bitmap> mBitmapList = new ArrayList<Bitmap>();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "-->onBind()");
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mAM = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (mAM != null) {
            mTotalMemory = mAM.getMemoryClass();
        }
        Log.d(TAG, "-->onCreate(), mTotalMemory=" + mTotalMemory + "M");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "-->onStartCommand(), flags=" + flags + ", startId=" + startId);
        startMemoryMonitorThread();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "-->onDestroy()");
        needStop = true;
        super.onDestroy();
    }


    private void startMemoryMonitorThread() {
        if (monitorThread == null || !monitorThread.isAlive()) {
            monitorThread = new MonitorThread();
            monitorThread.start();
        } else {
            Log.d(TAG, "Monitor thread is running, ignore duplicated request");
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void printMemoryInfo() {
        if (mAM != null) {
            ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
            mAM.getMemoryInfo(memoryInfo);

            Log.d(TAG, "-->printMemoryInfo(), bitmap count=" + mBitmapList.size() + ", available memory=" + memoryInfo.availMem / MB
                    + ", lowMem?" + memoryInfo.lowMemory + ", totalMem=" + memoryInfo.totalMem / MB);

            long maxMem = Runtime.getRuntime().maxMemory();
            long totalMem = Runtime.getRuntime().totalMemory();
            long freeMem = Runtime.getRuntime().freeMemory();
            int usage = (int) ((totalMem - freeMem) * 100 / maxMem);
            Log.i(TAG, "   -->maxMem=" + maxMem / MB + "M, totalMem=" + totalMem / MB + "M, freeMem=" + freeMem / MB + "M, usage=" + (usage) + "%");
            if (usage > 90 && mBitmapList.size() > 0) {
                Log.w(TAG, "  remove some element to reduce menory usage");
                mBitmapList.remove(0);
            }

        } else {
            Log.e(TAG, "-->printMemoryInfo()(), lose AM reference.");
        }
    }

    private Bitmap decordBitmap() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.big_img);
//        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.medium_img);
        Log.d(TAG, "-->decordBitmap(), bitmap size=" + bitmap.getByteCount()+", width="+bitmap.getWidth()+", height="+bitmap.getHeight());
        return bitmap;
    }

    private class MonitorThread extends Thread {
        @Override
        public void run() {
            while (!needStop) {
                printMemoryInfo();
                mBitmapList.add(decordBitmap());

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Log.d(TAG, "MonitorThread will stop, needStop=" + needStop);
        }
    }


}
