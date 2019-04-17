package com.loading.common.lifecycle;

import android.app.Activity;

import com.loading.common.manager.ListenerManager;
import com.loading.common.utils.Loger;

public class Foreground {
    private static final String TAG = Foreground.class.getSimpleName();

    private int mStartedActivityCnt = 0;

    private ListenerManager<ForegroundListener> mListenerMgr;

    private static Foreground instance = null;

    public interface ForegroundListener {
        void onBecameForeground();

        void onBecameBackground();
    }

    private Foreground() {
        mListenerMgr = new ListenerManager<>();
    }

    public static Foreground getInstance() {
        if (instance == null) {
            synchronized (Foreground.class) {
                if (instance == null) {
                    instance = new Foreground();
                }
            }
        }
        return instance;
    }

    public synchronized void addListener(ForegroundListener tListener) {
        Loger.d(TAG, "addListenr, tListener: " + tListener);
        mListenerMgr.addListener(tListener);
    }

    public synchronized void removeListener(ForegroundListener tListener) {
        if (tListener != null) {
            mListenerMgr.removeListener(tListener);
        }
    }

    /**
     * 主线程调用
     */
    public void onActivityStarted(@SuppressWarnings("unused") Activity activity) {
        if (mStartedActivityCnt++ == 0) {
            onForeground();
        }
        Loger.i(TAG, "onActivityStarted, mStartedActivityCnt: " + mStartedActivityCnt + ", activity: " + activity);
    }

    /**
     * 主线程调用
     */
    public void onActivityStopped(@SuppressWarnings("unused") Activity activity) {
        if (--mStartedActivityCnt == 0) {
            onBackground();
        }
        Loger.i(TAG, "onActivityStopped, mStartedActivityCnt: " + mStartedActivityCnt + ", activity: " + activity);
    }

    private void onForeground() {
        Loger.d(TAG, "onForeground ..., mListenerMgr: " + mListenerMgr);
        mListenerMgr.loopListenerList(listener -> listener.onBecameForeground());
    }

    private void onBackground() {
        Loger.d(TAG, "onBackground ..., mListenerMgr: " + mListenerMgr);
        mListenerMgr.loopListenerList(listener -> listener.onBecameBackground());
    }

    public boolean isForeground() {
        return mStartedActivityCnt > 0;
    }
}
