package com.loading.common.widget.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;

import com.loading.common.R;

/**
 * Created by Mr. Orange on 16/10/1.
 * <p>
 */

public class MDAlertDialogFragment extends MDDialogFragment {

    public static MDAlertDialogFragment newInstance(CharSequence title,
                                                    CharSequence message) {
        return newInstance(title, message, null, null);
    }

    public static MDAlertDialogFragment newInstance(CharSequence title,
                                                    CharSequence message,
                                                    CharSequence positiveText) {
        return newInstance(title, message, positiveText, null);
    }

    public static MDAlertDialogFragment newInstance(CharSequence title,
                                                    CharSequence message,
                                                    CharSequence positiveText,
                                                    CharSequence negativeText) {
        MDAlertDialogFragment fragment = new MDAlertDialogFragment();
        fragment.setArguments(getDialogFragBundle(title, message, positiveText, negativeText));
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.SportsAlertDialogStyle);
        if (!TextUtils.isEmpty(mTitle)) {
            builder.setTitle(mTitle);
        }
        if (!TextUtils.isEmpty(mMessage)) {
            builder.setMessage(mMessage);
        }
        if (!TextUtils.isEmpty(mNegativeText)) {
            builder.setNegativeButton(mNegativeText, (dialog, which) -> {
                if (mOnDialogClickListener != null) {
                    mOnDialogClickListener.onDialogClick(MDAlertDialogFragment.this, MDDialogInterface.BUTTON_NEGATIVE, mRequestCode);
                }
                dismissAllowingStateLoss();
            });
        }
        if (!TextUtils.isEmpty(mPositiveText)) {
            builder.setPositiveButton(mPositiveText, (dialog, which) -> {
                if (mOnDialogClickListener != null) {
                    mOnDialogClickListener.onDialogClick(MDAlertDialogFragment.this, MDDialogInterface.BUTTON_POSITIVE, mRequestCode);
                }
                dismissAllowingStateLoss();
            });
        }
        return builder.create();
    }
}
