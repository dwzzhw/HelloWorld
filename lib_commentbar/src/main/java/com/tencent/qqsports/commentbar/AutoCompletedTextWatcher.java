package com.tencent.qqsports.commentbar;

import android.text.Editable;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.widget.EditText;

import com.tencent.qqsports.logger.Loger;

/**
 * Created by loading on 3/22/17.
 */

public class AutoCompletedTextWatcher implements TextWatcher {
    private static final String TAG = "AutoCompletedTextWatch";
    private int atSymbolIndex = -1;    //该位置出现了一个@，可能是手动输入或黏贴的最后一个字符，也可能是从后面开始删除到该位置
    private EditText mEditText;
    private char mTriggerChar;
    private String mTargetStr;

    private int mAutoCompletedTxtColor;

    public AutoCompletedTextWatcher(EditText editText,
                                    char triggerChar,
                                    String targetString) {
        mEditText = editText;
        mTriggerChar = triggerChar;
        mTargetStr = targetString;
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
//        Loger.d(TAG, "-->onTextChanged(), s=" + s + ", start=" + start + ", before=" + before + ", count=" + count);
        atSymbolIndex = -1;
        if (mTriggerChar > 0) {
            if (before == 0 && count > 0) {   //手动输入或黏贴的最后一个字符 @
                if (s.charAt(start + count - 1) == mTriggerChar) {
                    Loger.i(TAG, "input " + mTriggerChar);
                    atSymbolIndex = start + count - 1;
                }
            }
//            else if (before > 0 && count == 0) {  //从后面开始删除字符，遇到@
//                if (start > 0 && s.charAt(start - 1) == mTriggerChar) {
//                    Loger.i(TAG, "delete then encount " + mTriggerChar);
//                    atSymbolIndex = start - 1;
//                }
//            }
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//        Loger.d(TAG, "-->beforeTextChanged(), s=" + s + ", start=" + start + ", after=" + after + ", count=" + count);
    }

    @Override
    public void afterTextChanged(Editable s) {
//        Loger.d(TAG, "-->afterTextChanged(), s=" + s);
        int encountedAtIndex = atSymbolIndex;
        if (encountedAtIndex >= 0 && s.charAt(encountedAtIndex) == mTriggerChar) {
            Loger.i(TAG, "Do auto replace now, atSymolIndex=" + encountedAtIndex);
            s.replace(encountedAtIndex, encountedAtIndex + 1, mTargetStr);
            if (!TextUtils.isEmpty(mTargetStr) && mAutoCompletedTxtColor != 0) {
                ForegroundColorSpan colorSpan = new ForegroundColorSpan(mAutoCompletedTxtColor);
                s.setSpan(colorSpan, encountedAtIndex, encountedAtIndex + mTargetStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

//                CustomizedTextDrawable faceDrawable = new CustomizedTextDrawable(mContext, mTargetStr, mEditText.getPaint());
//                if (mAutoCompletedTxtColor != 0) {
//                    faceDrawable.setTextColor(mAutoCompletedTxtColor);
//                }
//                faceDrawable.setBounds(0, 0, faceDrawable.getIntrinsicWidth(), faceDrawable.getIntrinsicHeight());
//                ImageSpan imageSpan = new ImageSpan(faceDrawable, DynamicDrawableSpan.ALIGN_BOTTOM);
//                s.setSpan(imageSpan, encountedAtIndex, encountedAtIndex + mTargetStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    }

    public void setAutoCompletedTxtColor(int txtColor) {
        mAutoCompletedTxtColor = txtColor;
    }
}
