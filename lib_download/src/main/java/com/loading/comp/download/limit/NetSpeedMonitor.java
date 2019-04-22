package com.loading.comp.download.limit;

import com.loading.common.utils.Loger;

/**
 * @author loading  2019.01.18
 */
public class NetSpeedMonitor {
    private static final String TAG = "NetSpeedMonitor";
    public static final int PACKAGE_BUFFER_SIZE = 4096;       //InputStream read buffer length
    public static final long NANO_IN_MS = 1000000l;       //1ms里的ns数，ms的精度不够
    private static final long MEASURE_SPEED_TARGET_DATA_LENGTH = 200 * 1024l;  //测速使用的数据长度，当前固定为200KB
    private static final long DOWNLOAD_PAUSE_INTERVAL_TOTAL_PACKAGE_LENGTH = 50 * PACKAGE_BUFFER_SIZE; //限速等待前下载的目标数据长度，即下载该数量的数据后再尝试等待, 200KB
    private static final long RE_MEASURE_INTERVAL_TOTAL_PACKAGE_LENGTH = 30 * 5 * DOWNLOAD_PAUSE_INTERVAL_TOTAL_PACKAGE_LENGTH; //需要重新测量网络的间隔数据长度， 30MB
    private static final long RE_MEASURE_DURATION_IN_MS = 20 * 1000; //需要重新测量网络的时间间隔 20s
    private static final int DEFAULT_LIMIT_SPEED = 400; // Byte/ms，默认400KB/s
    private static final int MAX_LIMIT_SPEED = 1000; // Byte/ms，限定最高速度1MB/s
    private static final int MIN_LIMIT_SPEED = 100; // Byte/ms，限定最低速度100KB/s
    private static final float TARGET_LIMIT_RATE = 0.2f; // 目标下载速度占全速的比例

    private static final int STATE_WAITING_MEASURE = 0;  //等待测速开始，需要消耗完此前socket buffer中的存量数据
    private static final int STATE_MEASURING = 1;        //测速进行中。全速下载，通过下载固定数量长度的数据所用时间来估算网络
    private static final int STATE_MONITOR = 2;       //测速完毕，限速进行中，在固定的间隔处增加时延

    private int mTotalReceivePackageCnt = 0;
    private int mTotalCostTimeInNano = 0;  //次数使用网络下载所耗费的时间，以排除其他操作引起的误差
    private long mTotalReceiveDataSize = 0;
    private int mCurrentMonitorState = STATE_WAITING_MEASURE;
    private int mCurrentLimitSpeed = DEFAULT_LIMIT_SPEED;

    private long mReceivedDataLengthSinceLastPause = 0;    //上次等待后已下载的数据长度，超过阈值后将检查是否需要等待
    private long mReceivedDataLengthSinceLastMeasure = 0;    //上次测速后已下载的数据长度，超过阈值后将重新测试
    private long mRealTimeOfLastMeasureInMs = 0;    //上次测速的真实时间点，超过一定时间后将重新测试
    private int mMeasureSpeedDataLength = 0;
    private long mStartMeasuringTimeInNano = 0;
    private long mStartMonitorTimeInNano = 0;  //最近一次进入限速状态的时间点，此处使用真实的系统时间，即需要将其他非下载所耗费的时间也计算在内

    public NetSpeedMonitor() {
    }

    public void onPackageReceived(int receivedPackageSize, long costTimeInNano) {
//        Loger.d(TAG, "-->onPackageReceived(), receivedPackageSize=" + receivedPackageSize + ", costTime=" + costTimeInNano);
        mTotalReceivePackageCnt++;
        mTotalCostTimeInNano += costTimeInNano;
        mTotalReceiveDataSize += receivedPackageSize;

        switch (mCurrentMonitorState) {
            case STATE_MONITOR:
                mReceivedDataLengthSinceLastPause += receivedPackageSize;
                mReceivedDataLengthSinceLastMeasure += receivedPackageSize;
                if (mReceivedDataLengthSinceLastPause > DOWNLOAD_PAUSE_INTERVAL_TOTAL_PACKAGE_LENGTH) {
                    checkToSleep();
                }
                break;
            case STATE_WAITING_MEASURE:
                if (receivedPackageSize < PACKAGE_BUFFER_SIZE) {  //假设此处已清空socket buffer，开始进入测速截断
                    switchToMeasuringState();
                }
                break;
            case STATE_MEASURING:
                mMeasureSpeedDataLength += receivedPackageSize;
                if (mMeasureSpeedDataLength >= MEASURE_SPEED_TARGET_DATA_LENGTH) {
                    calculateLimitSpeed();
                }
                break;
        }
    }

    /**
     * 接受完一个包后，判断是否需要sleep一些时间
     * 该方法可能会引起阻塞
     */
    public void checkToSleep() {
        long realCostTimeInMs = (System.nanoTime() - mStartMonitorTimeInNano) / NANO_IN_MS;
        long targetCostTime = mReceivedDataLengthSinceLastPause / mCurrentLimitSpeed;
        Loger.d(TAG, "-->checkToSleep(), realCostTime=" + realCostTimeInMs + ", targetCostTime=" + targetCostTime
                + ", mReceivedDataLengthSinceLastMeasure=" + mReceivedDataLengthSinceLastMeasure + ", mTotalReceiveDataSize=" + mTotalReceiveDataSize
                + ", time since last measure=" + (System.currentTimeMillis() - mRealTimeOfLastMeasureInMs) + "ms");
        sleepInMs(targetCostTime - realCostTimeInMs);
        if (mReceivedDataLengthSinceLastMeasure >= RE_MEASURE_INTERVAL_TOTAL_PACKAGE_LENGTH || System.currentTimeMillis() - mRealTimeOfLastMeasureInMs > RE_MEASURE_DURATION_IN_MS) {
            switchToWaitingMeasureState();
        } else {
            switchToMonitorState();
        }
    }

    public void reset() {

    }

    private void switchToMeasuringState() {
        Loger.i(TAG, "-->switchToMeasuringState()");
        mCurrentMonitorState = STATE_MEASURING;
        mMeasureSpeedDataLength = 0;
        mStartMeasuringTimeInNano = mTotalCostTimeInNano;
    }

    private void switchToMonitorState() {
        Loger.i(TAG, "-->switchToMonitorState(), mCurrentLimitSpeed=" + mCurrentLimitSpeed);
        mCurrentMonitorState = STATE_MONITOR;
        mReceivedDataLengthSinceLastPause = 0;
        mStartMonitorTimeInNano = System.nanoTime();
    }

    private void switchToWaitingMeasureState() {
        Loger.i(TAG, "-->switchToWaitingMeasureState()");
        mReceivedDataLengthSinceLastMeasure = 0;
        mCurrentMonitorState = STATE_WAITING_MEASURE;
    }

    private void calculateLimitSpeed() {
        long measureDurationInMs = (mTotalCostTimeInNano - mStartMeasuringTimeInNano) / NANO_IN_MS;
        int targetSpeed = 0;
        if (measureDurationInMs > 0) {
            Loger.w(TAG, "-->calculateLimitSpeed(), mMeasureSpeedDataLength=" + mMeasureSpeedDataLength + ", measureDuration=" + measureDurationInMs
                    + "ms, full speed=" + (mMeasureSpeedDataLength / measureDurationInMs) + "KB/s, limit rate=" + TARGET_LIMIT_RATE
                    + ", complete size since last measure=" + mReceivedDataLengthSinceLastMeasure + ", total time=" + (mTotalCostTimeInNano / NANO_IN_MS));
            targetSpeed = (int) (TARGET_LIMIT_RATE * mMeasureSpeedDataLength / measureDurationInMs);
        }
        mCurrentLimitSpeed = targetSpeed > 0 ? (Math.min(Math.max(targetSpeed, MIN_LIMIT_SPEED), MAX_LIMIT_SPEED)) : DEFAULT_LIMIT_SPEED;
        Loger.w(TAG, "<--calculateLimitSpeed(), mCurrentLimitSpeed=" + mCurrentLimitSpeed);

        sleepInMs((long) (measureDurationInMs / TARGET_LIMIT_RATE));
        mRealTimeOfLastMeasureInMs = System.currentTimeMillis();
        switchToMonitorState();
    }

    private void sleepInMs(long toSleepMs) {
        Loger.i(TAG, "-->sleepInMs(), toSleepMs=" + toSleepMs);
        if (toSleepMs > 0) {
            try {
                Thread.sleep(toSleepMs);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isMonitoring() {
        return mCurrentMonitorState == STATE_MONITOR;
    }

    private boolean isMeasuring() {
        return mCurrentMonitorState == STATE_MEASURING;
    }

    private boolean isWaitingMeasure() {
        return mCurrentMonitorState == STATE_WAITING_MEASURE;
    }
}
