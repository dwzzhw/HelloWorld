package com.loading.common.widget.dialog;

/**
 * Created by Mr. Orange on 16/10/1.
 */
public interface MDDialogInterface {
    int BUTTON_POSITIVE = -1;
    int BUTTON_NEGATIVE = -2;

    interface OnDialogClickListener {
        void onDialogClick(MDDialogFragment dialog, int which, int requestCode);
    }

    interface OnDialogItemSelectedListener {
        void onListItemSelected(MDDialogFragment dialog, CharSequence value, int number, int requestCode);
    }
}
