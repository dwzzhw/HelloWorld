package com.tencent.qqsports.commentbar.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.loading.common.component.CApplication;
import com.loading.common.utils.Loger;
import com.tencent.qqsports.commentbar.R;

public class FacePackageIndicatorView extends RelativeLayout {
    private static final String TAG = "FacePackageIndicatorView";
    private ImageView mContentImgView;

    private int mPackageStartIndexInViewPager = 0;
    private boolean isSelected = false;

    private Object mSelectedBackgroundRes;

    public FacePackageIndicatorView(Context context) {
        this(context, null);
    }

    public FacePackageIndicatorView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FacePackageIndicatorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.facepanel_package_indicator_layout, this);
        mContentImgView = findViewById(R.id.content_img);
    }

    public void setPackageStartIndexInViewPager(int packageStartIndexInViewPager) {
        this.mPackageStartIndexInViewPager = packageStartIndexInViewPager;
    }

    public int getPackageStartIndexInViewPager() {
        return mPackageStartIndexInViewPager;
    }

    public void setPackageIndicatorRes(Object indicatorRes) {
        Loger.d(TAG, "-->setPackageIndicatorRes(), indicatorRes=" + indicatorRes);
        mSelectedBackgroundRes = indicatorRes;
        updateIndicatorRes();
    }

    private void updateIndicatorRes() {
        if (mSelectedBackgroundRes instanceof Bitmap) {
            mContentImgView.setImageBitmap((Bitmap) mSelectedBackgroundRes);
        } else if (mSelectedBackgroundRes instanceof Integer) {
            mContentImgView.setImageResource((Integer) mSelectedBackgroundRes);
        }

        setBackgroundColor(CApplication.getColorFromRes(isSelected ? R.color.white : R.color.transparent));
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
        updateIndicatorRes();
    }
}
