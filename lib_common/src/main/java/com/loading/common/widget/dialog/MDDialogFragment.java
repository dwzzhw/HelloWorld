package com.loading.common.widget.dialog;

import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import android.text.TextUtils;

import com.loading.common.utils.Loger;

/**
 * Created by Mr. Orange on 16/10/1.
 * <p>
 */

public abstract class MDDialogFragment extends DialogFragment {
    private String TAG = getClass().getSimpleName();

    private static final String EXTRA_DIALOG_TITLE_KEY = "EXTRA_DIALOG_TITLE_KEY";
    private static final String EXTRA_DIALOG_MESSAGE_KEY = "EXTRA_DIALOG_MESSAGE_KEY";
    private static final String EXTRA_DIALOG_POSITIVE_TEXT_KEY = "EXTRA_DIALOG_POSITIVE_TEXT_KEY";
    private static final String EXTRA_DIALOG_NEGATIVE_TEXT_KEY = "EXTRA_DIALOG_NEGATIVE_TEXT_KEY";

    protected CharSequence mTitle;
    protected CharSequence mMessage;
    protected CharSequence mPositiveText;
    protected CharSequence mNegativeText;

    protected boolean mDismissOnConfigChange;
    protected int mRequestCode;

    protected MDDialogInterface.OnDialogClickListener mOnDialogClickListener;
    private DialogInterface.OnDismissListener onDismissListener;

    public static boolean isPositiveBtn(int btnCode) {
        return btnCode == DialogInterface.BUTTON_POSITIVE;
    }

    @SuppressWarnings("unused")
    public static boolean isNegativeBtn(int btnCode) {
        return btnCode == DialogInterface.BUTTON_NEGATIVE;
    }

    protected static Bundle getDialogFragBundle(CharSequence title,
                                                CharSequence message,
                                                CharSequence positiveText,
                                                CharSequence negativeText) {
        Bundle resultBundle = new Bundle();
        if (!TextUtils.isEmpty(title)) {
            resultBundle.putCharSequence(EXTRA_DIALOG_TITLE_KEY, title);
        }

        if (!TextUtils.isEmpty(message)) {
            resultBundle.putCharSequence(EXTRA_DIALOG_MESSAGE_KEY, message);
        }

        if (!TextUtils.isEmpty(positiveText)) {
            resultBundle.putCharSequence(EXTRA_DIALOG_POSITIVE_TEXT_KEY, positiveText);
        }

        if (!TextUtils.isEmpty(negativeText)) {
            resultBundle.putCharSequence(EXTRA_DIALOG_NEGATIVE_TEXT_KEY, negativeText);
        }
        return resultBundle;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parseArgs(getArguments());
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (onDismissListener != null) {
            onDismissListener.onDismiss(dialog);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mDismissOnConfigChange) {
            dismissAllowingStateLoss();
        }
    }

    public void show(FragmentManager manager) {
        show(manager, TAG);
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        try {
            super.show(manager, tag);
        } catch (Exception e) {
            Loger.e(TAG, "exception: " + e);
            manager.beginTransaction().add(this, tag).commitAllowingStateLoss();
        }
    }

    @Override
    public void dismiss() {
        dismissAllowingStateLoss();
    }

    @Override
    public void dismissAllowingStateLoss() {
        try {
            super.dismissAllowingStateLoss();
        } catch (Exception e) {
            Loger.e(TAG, "exception when dismiss: " + e);
        }
    }

    public void setDismissOnConfigChange(boolean dismissOnConfigChange) {
        this.mDismissOnConfigChange = dismissOnConfigChange;
    }

    public void setOnDialogClickListener(MDDialogInterface.OnDialogClickListener listener) {
        mOnDialogClickListener = listener;
    }

    public void setRequestCode(int requestCode) {
        mRequestCode = requestCode;
    }

    protected void parseArgs(Bundle args) {
        if (args != null) {
            mTitle = args.getCharSequence(EXTRA_DIALOG_TITLE_KEY);
            mMessage = args.getCharSequence(EXTRA_DIALOG_MESSAGE_KEY);
            mPositiveText = args.getCharSequence(EXTRA_DIALOG_POSITIVE_TEXT_KEY);
            mNegativeText = args.getCharSequence(EXTRA_DIALOG_NEGATIVE_TEXT_KEY);
//            isLotteAnimEnable = args.getBoolean(EXTRA_DIALOG_SPECIAL_ENABLE, false);
        }
    }

    public boolean isDialogVisible() {
        return getDialog() != null && getDialog().isShowing();
    }

    public void setOnDismissLister(DialogInterface.OnDismissListener listener) {
        onDismissListener = listener;
    }
}