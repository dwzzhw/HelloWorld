package com.tencent.qqsports.download.limit;

import java.io.IOException;
import java.io.RandomAccessFile;

public class RandomAccessFileCacheHelper {
    private static final String TAG = "RandomAccessFileCacheHelper";
    private final static int LOCAL_CACHE_SIZE = 1 * 1024 * 1024;  //1MB
    public final static int READ_STREAM_BUFFER_SIZE = 4 * 1024;  //4KB

    private RandomAccessFile mRandomAccessFile = null;
    private byte[] mLocalCacheBuffer;
    private int mLocalCacheDataLength;
    private long mFileStartPos;
    private long mCompleteSize;

    public RandomAccessFileCacheHelper(RandomAccessFile randomAccessFile) {
        mRandomAccessFile = randomAccessFile;

        mLocalCacheBuffer = new byte[LOCAL_CACHE_SIZE];
    }

    public void init(long fileStartPos) {
        mFileStartPos = fileStartPos;
        mCompleteSize = 0;
        mLocalCacheDataLength = 0;
        if (mRandomAccessFile != null) {
            try {
                mRandomAccessFile.seek(fileStartPos);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public long getCompleteSize() {
        return mCompleteSize;
    }

    public void cacheData(byte[] data, int dataLength) {
        if (data != null && mRandomAccessFile != null) {
            System.arraycopy(data, 0, mLocalCacheBuffer, mLocalCacheDataLength, dataLength);
            mLocalCacheDataLength += dataLength;
            if (mLocalCacheDataLength >= LOCAL_CACHE_SIZE - READ_STREAM_BUFFER_SIZE) {
                try {
//                    long startWriteTime = System.currentTimeMillis();
                    mRandomAccessFile.write(mLocalCacheBuffer, 0, mLocalCacheDataLength);
                    mCompleteSize += mLocalCacheDataLength;
//                    Loger.d(TAG, "-->cacheData(), cost time=" + (System.currentTimeMillis() - startWriteTime)
//                            + "ms, byte cnt =" + mLocalCacheDataLength + ", complete size=" + mCompleteSize);
                    mLocalCacheDataLength = 0;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void flushCacheData() {
        if (mRandomAccessFile != null && mLocalCacheDataLength > 0) {
            try {
//                long startWriteTime = System.currentTimeMillis();
                mRandomAccessFile.write(mLocalCacheBuffer, 0, mLocalCacheDataLength);
                mCompleteSize += mLocalCacheDataLength;
//                Loger.d(TAG, "-->flushCacheData(), cost time=" + (System.currentTimeMillis() - startWriteTime)
//                        + "ms, byte cnt =" + mLocalCacheDataLength + ", complete size=" + mCompleteSize);
                mLocalCacheDataLength = 0;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
