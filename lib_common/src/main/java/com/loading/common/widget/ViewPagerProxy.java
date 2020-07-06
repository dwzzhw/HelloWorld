package com.loading.common.widget;

import androidx.viewpager.widget.ViewPager;

/**
 * 代理Viewpager，目前仅用于页码 {@link CirclePageIndicator}
 * Created by madong on 2018/11/6.
 */
public class ViewPagerProxy implements ViewPager.OnPageChangeListener {

    private ViewPager.OnPageChangeListener mListener;

    private ViewPagerCallback mCallback;
    // 当前组内页数
    private int mCount;
    // 记录当前所在组页码，用于判断是否需要刷新
    private int currentGroupIndex = -1;
    // 当前组页码允许滑动的范围
    private int rangeLeft;
    private int rangeRight;

    public ViewPagerProxy() {
    }

    public void setViewPagerCallback(ViewPagerCallback callback) {
        mCallback = callback;
    }

    public void addOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
        this.mListener = listener;
    }

    public void removeOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
        this.mListener = null;
    }

    public int getCount() {
        return mCount;
    }

    // 用于onPageScrolled方法内判断是否跨组切换，注意向右滑动时position为当前，向左时为目标
    public boolean isCrossGroup(int position) {
        return position < rangeLeft || position >= rangeRight;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (isCrossGroup(position)) {
            // 跨组切换时通知SlideNavBar
            return;
        }
        // 同组内切换时通知circleIndicator
        if (mListener != null) {
            mListener.onPageScrolled(mCallback == null ? position : mCallback.getChildPosition(position), positionOffset, positionOffsetPixels);
        }
    }

    @Override
    public void onPageSelected(int position) {
        int newPosition = position;
        if (mCallback != null) {
            newPosition = mCallback.getChildPosition(position);
            int groupIndex = mCallback.getGroupIndex(position);
            mCount = mCallback.getChildCount(groupIndex);
            // 更新当前组的前后边界，用于区分切换是否跨组，例如当前组为第5至第7页，则rangLeft=4，rangRight=6
            rangeLeft = position - newPosition;
            rangeRight = rangeLeft + mCount - 1;
            if (groupIndex != currentGroupIndex) {
                currentGroupIndex = groupIndex;
                // 当前所属组切换过程中不调用onPageScrolled，切换后此处补充调用
                if (mListener != null) {
                    mListener.onPageScrolled(newPosition, 0, 0);
                }
            }
        }

        if (mListener != null) {
            mListener.onPageSelected(newPosition);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (mListener != null) {
            mListener.onPageScrollStateChanged(state);
        }
    }

    public static interface ViewPagerCallback {

        // 当前位置所在组页码
        int getGroupIndex(int position);

        // 当前所在组内页码总数
        int getChildCount(int index);

        // 当前处于页码中的相对位置
        int getChildPosition(int position);
    }
}
