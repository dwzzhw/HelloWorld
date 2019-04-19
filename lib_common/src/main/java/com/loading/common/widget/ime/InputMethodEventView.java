package com.loading.common.widget.ime;

import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.loading.common.manager.ListenerManager;
import com.loading.common.utils.Loger;
import com.loading.common.utils.SystemUtil;
import com.loading.common.utils.UiThreadUtil;

public class InputMethodEventView extends RelativeLayout implements IMEWindowMonitor {
    private static final String TAG = InputMethodEventView.class.getSimpleName();
    private static final int SOFTKEY_MIN_HEIGHT = SystemUtil.dpToPx(60);

    private InputMethodChangeListener mInputMethodChangeListener;
    private ListenerManager<IBeforeMeasureHeightChangeListener> mNotifyManager = new ListenerManager<>();
    private int mOldMeasuredHeight = -1;
    private int mNewMeasuredHeight = -1;
    private SizeChangeRunnable mDetectRunnable = null;

    public void setInputMethodChangeListener(InputMethodChangeListener mInputMethodChangeListener) {
        this.mInputMethodChangeListener = mInputMethodChangeListener;
    }

    public InputMethodEventView(Context context) {
        super(context);
    }

    public InputMethodEventView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InputMethodEventView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onSizeChanged(int w, final int h, int oldw, final int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Loger.d(TAG, "w: " + w + ", h: " + h + ", oldW: " + oldw + ", oldh: " + oldh + ", MIN_HEIGHT_THRESHOLD: " + SOFTKEY_MIN_HEIGHT);
        if (mDetectRunnable == null) {
            mDetectRunnable = new SizeChangeRunnable();
        }
        mDetectRunnable.nowH = h;
        mDetectRunnable.oldH = oldh;
        UiThreadUtil.removeRunnable(mDetectRunnable);
        UiThreadUtil.postDelay(mDetectRunnable, 100);
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        Loger.d(TAG, "onConfigurationChaned ....");
        super.onConfigurationChanged(newConfig);
        if (mDetectRunnable != null) {
            UiThreadUtil.postDelay(() -> UiThreadUtil.removeRunnable(mDetectRunnable), 40);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mNewMeasuredHeight = MeasureSpec.getSize(heightMeasureSpec);
        Loger.d(TAG, "-->onMeasure(), old height=" + mOldMeasuredHeight + ", new height=" + mNewMeasuredHeight
                + ", width=" + MeasureSpec.getSize(widthMeasureSpec));

        if (mOldMeasuredHeight > 0 && mNewMeasuredHeight > 0 && Math.abs(mOldMeasuredHeight - mNewMeasuredHeight) > SOFTKEY_MIN_HEIGHT) {
            Loger.i(TAG, "-->notify view height changed.");
            mNotifyManager.loopListenerList(listener -> listener.onMeasureHeightChanged(mNewMeasuredHeight, mOldMeasuredHeight));
        }
        mOldMeasuredHeight = mNewMeasuredHeight;
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private class SizeChangeRunnable implements Runnable {
        int nowH;
        int oldH;

        @Override
        public void run() {
            if (mInputMethodChangeListener != null && nowH > 0 && oldH > 0) {
                final int heightDiff = oldH - nowH;
                Loger.i(TAG, "oldh=" + oldH + ", h=" + nowH + ", heightChange=" + heightDiff);
                if (Math.abs(heightDiff) > SOFTKEY_MIN_HEIGHT) {
                    if (heightDiff > 0) {
                        Loger.d(TAG, "onInputMethod open .....");
                        mInputMethodChangeListener.onInputMethodOpen(heightDiff);
                    } else {
                        Loger.d(TAG, "onInputMethod close .....");
                        mInputMethodChangeListener.onInputMethodClose(-heightDiff);
                    }
                }
            }
        }
    }

    @Override
    public void addMeasureHeightChangeListener(IBeforeMeasureHeightChangeListener listener) {
        mNotifyManager.addListener(listener);
    }

    @Override
    public void removeMeasureHeightChangeListener(IBeforeMeasureHeightChangeListener listener) {
        mNotifyManager.removeListener(listener);
    }
}