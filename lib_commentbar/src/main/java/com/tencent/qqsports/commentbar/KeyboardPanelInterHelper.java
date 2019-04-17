package com.tencent.qqsports.commentbar;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.loading.common.utils.Loger;
import com.loading.common.utils.UiThreadUtil;
import com.loading.common.widget.ime.IBeforeMeasureHeightChangeListener;
import com.loading.common.widget.ime.IMEWindowMonitor;
import com.loading.common.widget.ime.InputMethodChangeListener;

/**
 * 处理软键盘与表情面板交替出现时闪烁的问题
 * Created by loading on 4/4/16.
 */
public class KeyboardPanelInterHelper implements IBeforeMeasureHeightChangeListener {
    private static final String TAG = "KeyboardPanelInter";
    private static final int DEFAULT_DELAY = 1000;  //部分机型上，输入法弹起需要800ms+
    private View mContentView = null;
    private IMEWindowMonitor mIMEMonitor = null;
    private Runnable mPendingRunnable = null;
    private IBeforeIMEChangeListener mBeforeImeChangeListener = null;
    private InputMethodChangeListener mImeChangeListener;
    private Runnable mDefaultRunnableWhenImeHide = null;  //输入法自主收起时进行的回调

    KeyboardPanelInterHelper(View contentView, IBeforeIMEChangeListener imeChangeListener) {
        this(contentView, imeChangeListener, null);
    }

    KeyboardPanelInterHelper(View contentView, IBeforeIMEChangeListener beforeImeChangeListener, InputMethodChangeListener imeChangeListener) {
        mContentView = contentView;
        mBeforeImeChangeListener = beforeImeChangeListener;
        mImeChangeListener = imeChangeListener;
    }

    void onAttachToWindow() {
        mIMEMonitor = null;
        if (mContentView != null) {
            ViewParent parent = mContentView.getParent();
            while (parent != null && parent instanceof ViewGroup) {
                if (parent instanceof IMEWindowMonitor) {
                    mIMEMonitor = (IMEWindowMonitor) parent;
                    break;
                }
                parent = parent.getParent();
            }
            if (mIMEMonitor != null) {
                mIMEMonitor.addMeasureHeightChangeListener(this);
            }
        }
    }

    void onDetachFromWindow() {
        if (mIMEMonitor != null) {
            mIMEMonitor.removeMeasureHeightChangeListener(this);
            mIMEMonitor = null;
        }
    }

    public void doBeforeKeyboardMeasured(Runnable runnable) {
        Loger.d(TAG, "-->doBeforeKeyboardMeasured(), runnable=" + runnable + ", mDefaultRunnableWhenImeHide=" + mDefaultRunnableWhenImeHide);
        mDefaultRunnableWhenImeHide = null;  //有指定行为则忽略默认值
        if (runnable != null) {
            Loger.i(TAG, "doBeforeKeyboardMeasured, mIMEMonitor: " + mIMEMonitor);
            if (mPendingRunnable != null) {
                UiThreadUtil.removeRunnable(mDefaultDelayRunnable);
                mPendingRunnable = null;
            }
            if (mIMEMonitor == null) {
                runnable.run();
            } else {
                mPendingRunnable = runnable;
                UiThreadUtil.postDelay(mDefaultDelayRunnable, DEFAULT_DELAY);
            }
        }
    }

    private Runnable mDefaultDelayRunnable = () -> {
        if (mPendingRunnable != null) {
            mPendingRunnable.run();
            mPendingRunnable = null;
        }
    };

    public void setDefaultRunnableWhenImeHide(Runnable defaultRunnableWhenImeHide) {
        Loger.d(TAG, "-->setDefaultRunnableWhenImeHide(), defaultRunnableWhenImeHide=" + defaultRunnableWhenImeHide);
        this.mDefaultRunnableWhenImeHide = defaultRunnableWhenImeHide;
    }

    @Override
    public void onMeasureHeightChanged(int newHeight, int oldHeight) {
        Loger.d(TAG, "-->onMeasureHeightChanged(), newHeight=" + newHeight + ", oldHeight=" + oldHeight + ", mPendingRunnable=" + mPendingRunnable
                + ", mDefaultRunnableWhenImeHide=" + mDefaultRunnableWhenImeHide + ", mBeforeImeChangeListener=" + mBeforeImeChangeListener);
        if (mPendingRunnable != null) {
            UiThreadUtil.removeRunnable(mDefaultDelayRunnable);
            mPendingRunnable.run();
            mPendingRunnable = null;
        }

        boolean visibleBefore = newHeight > oldHeight;
        boolean visibleAfter = newHeight < oldHeight;
        if (mDefaultRunnableWhenImeHide != null) {
            if (visibleBefore) {
                Loger.d(TAG, "--> call mDefaultRunnableWhenImeHide");
                mDefaultRunnableWhenImeHide.run();
            }
            mDefaultRunnableWhenImeHide = null;   //在beforeIMEChanging之后设定的Runnable才有效，以此避免重复调用
        }
        if (mBeforeImeChangeListener != null) {
            mBeforeImeChangeListener.beforeIMEChanging(visibleBefore, visibleAfter);
        }
    }

    public interface IBeforeIMEChangeListener {
        void beforeIMEChanging(boolean visibleBefore, boolean visibleAfter);
    }
}
