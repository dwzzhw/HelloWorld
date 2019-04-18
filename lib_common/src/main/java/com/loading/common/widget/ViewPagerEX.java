package com.loading.common.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.TextView;

import com.loading.common.utils.Loger;

/**
 * 类的描述. ViewPager里若包含有singleLine且Gravity非left的TextView，因为该TextView 'canScroll'，
 * 会使ViewPager的滑动手势失效，故增加该类处理这一问题
 *
 * @author loading
 */
@SuppressWarnings("unused")
public class ViewPagerEX extends ViewPager {
    private static final String TAG = "ViewPagerEX";
    public static final int SCROLL_DIRECTION_NONE = -1;
    public static final int SCROLL_DIRECTION_LEFT = 0;
    public static final int SCROLL_DIRECTION_RIGHT = 1;

    protected int viewPagerTouchSlop;
    private int mScrollState = SCROLL_STATE_IDLE;
    private boolean isScrollable = true;
    protected int direction;

    public ViewPagerEX(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ViewPagerEX(Context context) {
        super(context);
        init(context);
    }


    private void init(Context context) {
        ViewConfiguration configuration = ViewConfiguration.get(context);
        viewPagerTouchSlop = configuration.getScaledPagingTouchSlop();
        addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                mScrollState = state;
                Loger.i(TAG, "scroll state changed: " + mScrollState);
            }
        });
//        horizontalScrollViewTouchSlop = configuration.getScaledTouchSlop();
//        Loger.d(TAG, "-->init(), viewPagerTouchSlop=" + viewPagerTouchSlop
//                + ", horizontalScrollViewTouchSlop="
//                + horizontalScrollViewTouchSlop);
    }

    private boolean isNestedViewPagerEX(View v) {
        return v != this && v instanceof ViewPager;
    }

    @Override
    protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
        // Assume all "text view" can not scroll  and "nested viewpager" can not scroll
        return !(v instanceof TextView)
                && !(isNestedViewPagerEX(v) && !((ViewPagerEX) v).isScrollable())
                && super.canScroll(v, checkV, dx, x, y);
    }


    private float mDownPosX = 0;
    private float mDownPosY = 0;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        boolean isIntercept = false;
        if (isScrollable) {
            try {
                isIntercept = super.onInterceptTouchEvent(event);
                final float x = event.getX();
                final float y = event.getY();

                final int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        mDownPosX = x;
                        mDownPosY = y;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        final float deltaX = Math.abs(x - mDownPosX);
                        final float deltaY = Math.abs(y - mDownPosY);
                        if (deltaX > viewPagerTouchSlop && deltaY > deltaX) {
                            Loger.i(TAG, "onInterceptTouchEvent: false");
                            return false;
                        }
                        if(x > mDownPosX) {
                            direction = SCROLL_DIRECTION_LEFT;
                        } else {
                            direction = SCROLL_DIRECTION_RIGHT;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        direction = SCROLL_DIRECTION_NONE;
                        break;
                    default:
                        break;
                }



            } catch (Exception ex) {
                Loger.e(TAG, "");
            }
        }
        return isIntercept;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        boolean isConsumed = false;
        if (isScrollable) {
            try {
                isConsumed = super.onTouchEvent(ev);
            } catch (Exception e) {
                Loger.e(TAG, "exception: " + e);
            }
        }
        return isConsumed;
    }

    public boolean isScrollable() {
        return isScrollable;
    }

    public void setScrollable(boolean isScrollable) {
        this.isScrollable = isScrollable;
    }

    public boolean isScrollStateIdle() {
        return mScrollState == SCROLL_STATE_IDLE;
    }

    public int getScrollDirection() {
        return direction;
    }

    public interface ViewPagerDisableScrollInterface {
        void disableScroll(boolean disable);
    }
}
