package com.loading.comp.download.limit;

import com.loading.common.utils.Loger;

public class FlowController {
    private static final String TAG = "FlowController";
    public static float FLOW_RATE = 0.2f;                 //限速目标占总带宽的比值，当前默认为20%
    private static int SLICE_SIZE_MAX = 1 * 1024 * 1024;   //最大分片大小  1MB
    private static int SLICE_SIZE_MIN = 200 * 1024;        //最小分片大小  200KB
    private static int SLICE_SIZE_START = 400 * 1024;      //起始分片大小  400KB
    private static int SLICE_SIZE_DELTA = 100 * 1024;      //分片大小调整步幅  100KB

    public static int SLICE_DURATION_MIN = 500;            //处理一个分片的最小周期 500ms，即期望默认1s中最多下载两个分片，避免太频繁的网络请求
    private static int SLICE_DURATION_EXPECT = 1000;       //处理一个分片的期望周期 1s，即期望默认1s中处理一个分片
    public static int SLICE_DURATION_MAX = 2000;           //处理一个分片的最大周期 2s，即期望相邻分片的最大等待时间为 SLICE_DURATION_MAX*(1-FLOW_RATE)

    private long mLastSlicesAverageCostTime = 0;
    private int mNextSliceSize = SLICE_SIZE_START;
    private int mNextSleepTime = (int) (SLICE_DURATION_EXPECT * (1 - FLOW_RATE));
    private long mExpectedSliceCostTime = (int) (SLICE_DURATION_EXPECT * FLOW_RATE);

    public FlowController() {
    }

    public void adjustFlowControlParameters(long lastSliceCostTimeInMs) {
        mLastSlicesAverageCostTime = mLastSlicesAverageCostTime > 0 ? (mLastSlicesAverageCostTime * 2 + lastSliceCostTimeInMs) / 3 : lastSliceCostTimeInMs;   //取过去3次的平均值

        int calNextSliceSize = mNextSliceSize;
        //偏差较大时，暂时逐级增减；震荡时，按比例调整
        if (mLastSlicesAverageCostTime > mExpectedSliceCostTime * 1.2) {
            calNextSliceSize -= SLICE_SIZE_DELTA;
        } else if (mLastSlicesAverageCostTime < mExpectedSliceCostTime * 0.8) {
            calNextSliceSize += SLICE_SIZE_DELTA;
        } else {
            calNextSliceSize = (int) (mExpectedSliceCostTime * mNextSliceSize / mLastSlicesAverageCostTime);  // nextSliceSize/exceptTime == lastSliceSize/lastCostTime
        }

        if (calNextSliceSize > SLICE_SIZE_MAX) {
            Loger.w(TAG, "-->adjustFlowControlParameters(), expected slice size: " + calNextSliceSize + " greater than max allowed size");
        } else if (calNextSliceSize < SLICE_SIZE_MIN) {
            Loger.w(TAG, "-->adjustFlowControlParameters(), expected slice size: " + calNextSliceSize + " smaller than min allowed size");
        }
        mNextSliceSize = Math.min(SLICE_SIZE_MAX, Math.max(calNextSliceSize, SLICE_SIZE_MIN));

        mNextSleepTime = (int) Math.min(Math.max(mLastSlicesAverageCostTime / FLOW_RATE * (1 - FLOW_RATE), SLICE_DURATION_MIN - mLastSlicesAverageCostTime), SLICE_DURATION_MAX * (1 - FLOW_RATE));

        Loger.d(TAG, "-->adjustFlowControlParameters(), lastSliceCostTimeInMs=" + lastSliceCostTimeInMs + ", mLastSlicesAverageCostTime="
                + mLastSlicesAverageCostTime + ", calNextSliceSize=" + calNextSliceSize + ", mNextSleepTime=" + mNextSleepTime + ", mNextSliceSize=" + mNextSliceSize);
    }

    public long getNextSliceSize() {
        return mNextSliceSize;
    }

    public long getNextSleepTime() {
        return mNextSleepTime;
    }
}
