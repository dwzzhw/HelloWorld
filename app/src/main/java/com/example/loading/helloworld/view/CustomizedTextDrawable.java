package com.example.loading.helloworld.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

/**
 * Created by loading on 3/22/17.
 */

public class CustomizedTextDrawable extends Drawable {
    private static final String TAG = "CustomizedTextDrawable";
    private Context mContext;
    private String mContentStr = null;
    private Paint mPaint;
    private int mTxtColor;

    public CustomizedTextDrawable(Context context, String contentStr, Paint textPaint) {
        mContentStr = contentStr;
        mContext = context;
        if (textPaint != null) {
            mPaint = textPaint;
        } else {
            mPaint = new Paint();
        }
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        if (!TextUtils.isEmpty(mContentStr)) {
            int oldColor = mPaint.getColor();
            boolean needResetColor = false;
            if (mTxtColor > 0) {
                mPaint.setColor(mTxtColor);
                needResetColor = true;
            }
            canvas.drawText(mContentStr, 0, -mPaint.getFontMetricsInt().ascent, mPaint);
            if (needResetColor) {
                mPaint.setColor(oldColor);
            }
        }
    }

    public void setTextColor(int textColor) {
        mTxtColor = textColor;
    }

    @Override
    public void setAlpha(@IntRange(from = 0, to = 255) int alpha) {
        mPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter cf) {
        mPaint.setColorFilter(cf);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public int getIntrinsicWidth() {
        int width = (int) (mPaint.measureText(mContentStr) + 0.5);
        Log.d(TAG, "-->getIntrinsicWidth(), width=" + width);
        return width;
    }

    @Override
    public int getIntrinsicHeight() {
        int height = mPaint.getFontMetricsInt().descent - mPaint.getFontMetricsInt().ascent;
        Log.d(TAG, "-->getIntrinsicHeight(), height=" + height + ", decent=" + mPaint.getFontMetricsInt().toString());
        return height;
    }
}
