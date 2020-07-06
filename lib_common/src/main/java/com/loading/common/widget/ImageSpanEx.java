package com.loading.common.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import androidx.annotation.DrawableRes;
import android.text.style.ImageSpan;

/**
 * Created by Mr. Orange on 16/3/2.
 * TextView 图文混排时，文字图片居中对齐
 */
public class ImageSpanEx extends ImageSpan {

    public static final int ALIGN_CENTER = 2;

    private Paint mBgPaint;

    public ImageSpanEx(Drawable d) {
        super(d);
    }

    public ImageSpanEx(Context context, @DrawableRes int resourceId, int verticalAlignment) {
        super(context, resourceId, verticalAlignment);
    }

    public ImageSpanEx(Drawable d, int verticalAlignment) {
        super(d, verticalAlignment);
    }

    @Override
    public int getSize(Paint paint, CharSequence text, int start, int end,
                       Paint.FontMetricsInt fm) {
        if (mVerticalAlignment != ALIGN_CENTER) {
            return super.getSize(paint, text, start, end, fm);
        }
        Drawable d = getDrawable();
        if (d == null) {
            return 0;
        }

        Rect rect = d.getBounds();
        if (fm != null) {
            Paint.FontMetricsInt fmPaint = paint.getFontMetricsInt();
            int fontHeight = fmPaint.bottom - fmPaint.top;
            int drHeight = rect.bottom - rect.top;

            int top = drHeight / 2 - fontHeight / 4;
            int bottom = drHeight / 2 + fontHeight / 4;

            fm.ascent = -bottom;
            fm.top = -bottom;
            fm.bottom = top;
            fm.descent = top;
        }
        return rect.right;
    }

    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end,
                     float x, int top, int y, int bottom, Paint paint) {
        Drawable b = getDrawable();
        canvas.save();
        if (b == null) {
            return;
        }

        Paint.FontMetricsInt fm = paint.getFontMetricsInt();
        //默认情况
        int transY = bottom - b.getBounds().bottom;
        if (mVerticalAlignment == ALIGN_BASELINE) {
            transY -= paint.getFontMetricsInt().descent;
        } else if (mVerticalAlignment == ALIGN_CENTER) {
            transY = ((y + fm.descent) + (y + fm.ascent)) / 2 - b.getBounds().bottom / 2;
        }
        canvas.translate(x, transY);
//        if (mBgPaint != null) {
//            canvas.drawRect(b.getBounds(), mBgPaint);
//        }
        b.draw(canvas);
        canvas.restore();
    }

//    public void setBackgroundColor(int backgroundColor) {
//        if (mBgPaint == null) {
//            mBgPaint = new Paint();
//            mBgPaint.setStyle(Paint.Style.FILL);
//        }
//        mBgPaint.tintImageView(backgroundColor);
//    }

}
