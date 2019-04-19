package com.loading.common.widget;

import android.annotation.SuppressLint;
import android.support.annotation.StringRes;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loading.common.R;
import com.loading.common.component.CApplication;
import com.loading.common.lifecycle.Foreground;
import com.loading.common.utils.CommonUtils;
import com.loading.common.utils.Loger;
import com.loading.common.utils.SystemUtil;
import com.loading.common.utils.UiThreadUtil;
import com.loading.common.utils.VersionUtils;

public class TipsToast {
    private static final String TAG = "TipsToast";

    private static final int TIPS_TIME = Toast.LENGTH_SHORT;

    static private Toast mToast;

    private View tipsView;
    private View mEmptyView;

    private TipsToast() {
    }

    private static class InstanceHolder {
        @SuppressLint("StaticFieldLeak")
        private static TipsToast INSTANCE = new TipsToast();
    }

    public static TipsToast getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public void dismissTips() {
        if (mToast != null) {
            mToast.cancel();
        }
        showTips(createEmptyView(), TIPS_TIME);
    }

    public void showTipsText(@StringRes final int stringId) {
        if (Foreground.getInstance().isForeground()) {
            String msgStr = CApplication.getAppContext().getResources().getString(stringId);
            showTipsText(msgStr);
        }
    }

    public void showTipsText(final CharSequence msg) {
        if (Foreground.getInstance().isForeground()) {
            showTipsCustom(msg, -1);
        }
    }

    public void showTipsTextIgnoreRunning(@StringRes final int stringId) {
        String msgStr = CApplication.getAppContext().getResources().getString(stringId);
        showTipsTextIgnoreRunning(msgStr);
    }

    public void showTipsTextIgnoreRunning(final String msg) {
        showTipsCustom(msg, -1);
    }

    public void showTips(final View v, final int dur) {
        try {
            if (mToast != null) {
                if (!VersionUtils.hasIceCreamSandwich()) {
                    mToast.cancel();
                }
            } else {
                mToast = new Toast(CApplication.getAppContext());
            }
            mToast.setView(v);
            mToast.setGravity(Gravity.CENTER, 0, 0);
            mToast.setDuration(dur);
            mToast.show();
        } catch (Exception e) {
            Loger.e("QQSports", "exception: " + e);
        }
    }

    private void showTipsCustom(final CharSequence msg, final int tipsImgResId) {
        if (CommonUtils.isMainThread()) {
            if (!supportCustomToast()) {
                Toast.makeText(CApplication.getAppContext(), msg, TIPS_TIME).show();
            } else {
                showTips(makeTipsView(msg, tipsImgResId), TIPS_TIME);
            }
        } else {
            UiThreadUtil.runOnUiThread(() -> showTipsCustom(msg, tipsImgResId));
        }
    }

    private View createEmptyView() {
        if (mEmptyView == null) {
            mEmptyView = new View(CApplication.getAppContext());
        }
        return mEmptyView;
    }

    @SuppressLint("InflateParams")
    private synchronized View makeTipsView(CharSequence msg, int tipsImage) {
        LayoutInflater lf = LayoutInflater.from(CApplication.getAppContext());
        if (tipsView == null) {
            tipsView = lf.inflate(R.layout.common_view_tips, null);
        }
        ImageView tips_icon = (ImageView) tipsView.findViewById(R.id.tips_icon);
        TextView tips_msg = (TextView) tipsView.findViewById(R.id.tips_msg);
        if (tips_icon != null && tips_msg != null) {
            if (tipsImage >= 0) {
                tips_icon.setImageResource(tipsImage);
                tips_icon.setVisibility(View.VISIBLE);
            } else {
                tips_icon.setVisibility(View.GONE);
            }
            tips_msg.setText(msg);
        }
        return tipsView;
    }

    private boolean supportCustomToast() {
        boolean support = true;
        String deviceName = SystemUtil.getDeviceName();
        String deviceProducName = SystemUtil.getDeviceProductName();
        if ((deviceName != null && (deviceName.contains("IUIN") || deviceName.contains("iuni")))
                || (deviceProducName != null && (deviceProducName.contains("IUNI") || deviceProducName.contains("iuni")))) {
            Loger.w(TAG, "-->supportCustomToast(),  current device [" + deviceName + "_:_" + deviceProducName + "] not support custom toast");
            support = false;
        }
        return support;
    }


    /*-------------------------App内不建议使用带图标的Toast,暂且保留相关功能-------------------------*/
    private static final int IMG_SUCCESS = R.drawable.common_st_success;
    private static final int IMG_WARNING = R.drawable.common_st_warning;
    private static final int IMG_SOFT_WARNING = R.drawable.common_st_smile;
    private static final int IMG_ERROR = R.drawable.common_st_error;

    @Deprecated
    public void showTipsSuccess(final CharSequence msg) {
        if (Foreground.getInstance().isForeground()) {
            showTipsCustom(msg, IMG_SUCCESS);
        }
    }

    public void showTipsSuccess(@StringRes final int stringID) {
        if (Foreground.getInstance().isForeground()) {
            String msgStr = CApplication.getAppContext().getResources().getString(stringID);
            showTipsSuccess(msgStr);
        }
    }

    @Deprecated
    public void showTipsError(final CharSequence msg) {
        if (Foreground.getInstance().isForeground()) {
            showTipsCustom(msg, IMG_ERROR);
        }
    }

    @Deprecated
    public void showTipsError(@StringRes final int stringId) {
        if (Foreground.getInstance().isForeground()) {
            String msgStr = CApplication.getAppContext().getResources().getString(stringId);
            showTipsError(msgStr);
        }
    }

    /**
     * 强硬警告
     *
     * @param msg
     */
    @Deprecated
    public void showTipsWarning(final CharSequence msg) {
        if (Foreground.getInstance().isForeground()) {
            showTipsCustom(msg, IMG_WARNING);
        }
    }

    /**
     * 柔和警告
     *
     * @param msg
     */
    @Deprecated
    public void showTipsSoftWarning(final CharSequence msg) {
        if (Foreground.getInstance().isForeground()) {
            showTipsCustom(msg, IMG_SOFT_WARNING);
        }
    }

    @Deprecated
    public void showTipsSuccessIgnoreRunning(final String msg) {
        showTipsCustom(msg, IMG_SUCCESS);
    }

    @Deprecated
    public void showTipsErrorIgnoreRunning(final String msg) {
        showTipsCustom(msg, IMG_ERROR);
    }

    @Deprecated
    public void showTipsWarningIgnoreRunning(final String msg) {
        showTipsCustom(msg, IMG_WARNING);
    }

    /**
     * 柔和警告,忽略正在运行
     *
     * @param msg
     */
    @Deprecated
    public void showTipsSoftWarningIgnoreRunning(final String msg) {
        showTipsCustom(msg, IMG_SOFT_WARNING);
    }

}
