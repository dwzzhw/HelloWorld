package com.example.loading.helloworld.view;

import android.support.v4.view.ViewCompat;
import android.text.Layout;
import android.text.NoCopySpan;
import android.text.Spannable;
import android.text.method.ScrollingMovementMethod;
import android.text.method.Touch;
import android.view.MotionEvent;
import android.widget.TextView;

public class AutoExpandMovementMethod extends ScrollingMovementMethod {
    private static final String TAG = "AutoExpandMovementMethod";
    private int initMaxHeight;
    private int expandMaxHeight;

    public AutoExpandMovementMethod(int initMaxHeight, int expandMaxHeight) {
        this.initMaxHeight = initMaxHeight;
        this.expandMaxHeight = expandMaxHeight;
    }

    @Override
    public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
//        Loger.d(TAG, "onTouchEvent() -> event : " + event + ", buffer: " + buffer);
        DragState[] ds;
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                ds = buffer.getSpans(0, buffer.length(), DragState.class);
                if (ds != null && ds.length > 0) {
                    for (DragState d : ds) {
                        buffer.removeSpan(d);
                    }
                }
                final int padding = widget.getTotalPaddingTop() + widget.getTotalPaddingBottom();
                final Layout tLayout = widget.getLayout();
                buffer.setSpan(
                        new DragState(padding,
                                tLayout.getHeight(),
                                event.getRawY(),
                                widget.getScrollY()),
                        0,
                        0,
                        Spannable.SPAN_MARK_MARK);
                return true;
            case MotionEvent.ACTION_MOVE:
                ds = buffer.getSpans(0, buffer.length(), DragState.class);
                if (ds != null && ds.length > 0) {
                    float dy = ds[0].mY - event.getRawY();
                    ds[0].mY = event.getRawY();

                    int tHeight = widget.getHeight();
                    int allShowHeight = ds[0].mLayoutHeight + ds[0].mPadding;
                    boolean isTop = widget.getScrollY() == 0 && !ViewCompat.canScrollVertically(widget, -1);
                    int maxHeight = expandMaxHeight < allShowHeight ? expandMaxHeight : allShowHeight;
//                    Loger.d(TAG, "onTouchEvent() -> is top : " + isTop
//                            + " , weight get scroll y : " + widget.getScrollY()
//                            + " , weight height : " + tHeight
//                            + " , text all show height : " + allShowHeight
//                            + " , init max height : " + initMaxHeight
//                            + " , expanded max height " + expandMaxHeight
//                    );
                    float unconsumedY = 0.f;
                    if (isTop && tHeight >= initMaxHeight && tHeight <= maxHeight) {
                        tHeight += dy;
                        if (tHeight < initMaxHeight) {
                            tHeight = initMaxHeight;
                            if (widget.getHeight() == initMaxHeight) {
                                unconsumedY = dy;
                            }
                        } else if (tHeight >= maxHeight) {
                            unconsumedY = dy < 0 ? (maxHeight - tHeight) : (tHeight - maxHeight);
                            tHeight = maxHeight;
                        }
                        if (widget.getHeight() != tHeight) {
                            ds[0].mUsed = true;
//                            Loger.d(TAG, "triggerTouch() -> tHeight : " + tHeight + " , consumed Y : " + unconsumedY);
                            widget.setMaxHeight(tHeight);
                        }
                    } else {
                        unconsumedY = dy;
                    }

//                    Loger.i(TAG, "uncosumedY: " + unconsumedY);
                    if (unconsumedY != 0) {
                        ds[0].mUsed = true;
                        Layout layout = widget.getLayout();
                        int ny = widget.getScrollY() + (int) unconsumedY;
                        ny = Math.min(ny, layout.getHeight() - (widget.getHeight() - ds[0].mPadding));
                        ny = Math.max(ny, 0);
                        int oldX = widget.getScrollX();
                        int oldY = widget.getScrollY();

//                        Loger.d(TAG, "onTouchEvent() -> scroll to y : " + ny);
                        Touch.scrollTo(widget, layout, widget.getScrollX(), ny);

                        // If we actually scrolled, then cancel the up action.
                        if (oldX != widget.getScrollX() || oldY != widget.getScrollY()) {
                            widget.cancelLongPress();
                        }
                    }
                    return ds[0].mUsed;
                }
            case MotionEvent.ACTION_UP:
                ds = buffer.getSpans(0, buffer.length(), DragState.class);
                if (ds != null) {
                    for (DragState d : ds) {
                        buffer.removeSpan(d);
                    }
                    return ds.length > 0 && ds[0].mUsed;
                }
        }
        return false;
    }

    public void resetMaxHeight(TextView weight) {
        weight.setMaxHeight(initMaxHeight);
    }

    private static class DragState implements NoCopySpan {
        float mY;
        int mScrollY;
        int mPadding;
        boolean mUsed;
        int mLayoutHeight;

        DragState(int padding, int layoutHeight, float y, int scrollY) {
            mPadding = padding;
            mLayoutHeight = layoutHeight;
            mY = y;
            mScrollY = scrollY;
        }
    }
}
