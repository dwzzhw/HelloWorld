package com.loading.common.widget.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;

import com.loading.common.R;

/**
 * Created by Mr. Orange on 16/10/1.
 */

public class MDListDialogFragment extends MDDialogFragment {
    private String[] mItems;
    private static final String EXTRA_DIALOG_LIST_KEY = "EXTRA_DIALOG_LIST_KEY";

    private MDDialogInterface.OnDialogItemSelectedListener mItemSelectedListener;

    public static MDListDialogFragment newInstance(String title, String[] items) {
        Bundle bundle = getDialogFragBundle(title, null, null, null);
        if ( bundle != null && items != null && items.length > 0 ) {
            bundle.putStringArray(EXTRA_DIALOG_LIST_KEY, items);
        }
        MDListDialogFragment fragment = new MDListDialogFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.SportsAlertDialogStyle);
        builder.setTitle(mTitle);
        if (mItems != null && mItems.length > 0) {
            builder.setItems(mItems, (dialog, which) -> {
                if (mItemSelectedListener != null && mItems != null && which < mItems.length) {
                    mItemSelectedListener.onListItemSelected(MDListDialogFragment.this, mItems[which], which, mRequestCode);
                }
            });
        }
        return builder.create();
    }

    public void setOnDialogItemSelectedListener(MDDialogInterface.OnDialogItemSelectedListener listener) {
        mItemSelectedListener = listener;
    }

    @Override
    protected void parseArgs(Bundle args) {
        super.parseArgs(args);
        mItems = args.getStringArray(EXTRA_DIALOG_LIST_KEY);
    }
}
