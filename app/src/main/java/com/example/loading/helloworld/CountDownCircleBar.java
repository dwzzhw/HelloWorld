package com.example.loading.helloworld;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;

/**
 * Created by loading on 10/1/15.
 */
public class CountDownCircleBar extends View {
    private static final String TAG = CountDownCircleBar.class.getSimpleName();
    private Context mContext = null;
    private Paint mProgressBarPaint = null;
    private int mBarColor = 0;
    private int mBarWidth = 0;
    private int mBgCircleWidth = 0;
    private RectF mProgressBarRectF = null;
    private RectF mCircleLineRectF = null;
    private int mCircleContainerSize = 100;
    private BarAnimation mAnimation = null;
    private float mProgressRate = 0;

    private ICountingListener mCountingListener = null;
    private boolean isCountingCanceled = false;

    public CountDownCircleBar(Context context) {
        this(context, null);
    }

    public CountDownCircleBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CountDownCircleBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        mBarColor = 0xff0000ff;
        mBarWidth = 20;
        mBgCircleWidth = 2;
        mProgressBarPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mProgressBarPaint.setColor(mBarColor);
        mProgressBarPaint.setStyle(Paint.Style.STROKE);
        mProgressBarPaint.setStrokeWidth(mBarWidth);

        mProgressBarRectF = new RectF();
        mProgressBarRectF.set(0, 0, mCircleContainerSize, mCircleContainerSize);
        mCircleLineRectF = new RectF();
        mCircleLineRectF.set(0, 0, mCircleContainerSize, mCircleContainerSize);

        mAnimation = new BarAnimation();
        mAnimation.setAnimationListener(animationListener);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mProgressBarPaint.setStrokeWidth(mBarWidth);
        canvas.drawArc(mProgressBarRectF, -90, (1 - mProgressRate) * 360, false, mProgressBarPaint);

        mProgressBarPaint.setStrokeWidth(mBgCircleWidth);
        canvas.drawArc(mCircleLineRectF, 0, 360, false, mProgressBarPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        int height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        mCircleContainerSize = Math.min(width, height);
        setMeasuredDimension(mCircleContainerSize, mCircleContainerSize);

        mProgressBarRectF.set(mBarWidth / 2, mBarWidth / 2, mCircleContainerSize - mBarWidth / 2, mCircleContainerSize - mBarWidth / 2);
        mCircleLineRectF.set(mBgCircleWidth / 2, mBgCircleWidth / 2, mCircleContainerSize - mBgCircleWidth / 2, mCircleContainerSize - mBgCircleWidth / 2);
    }

    private void updateProgressBar(float progress) {
        if (progress > 0 && progress < 1) {
            mProgressRate = progress;
            postInvalidate();
        }
    }

    private Animation.AnimationListener animationListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
            Log.d(TAG, "-->onAnimationStart()");
            mProgressRate = 0;
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            Log.d(TAG, "-->onAnimationEnd()");
            mProgressRate = 1;
            if (mCountingListener != null) {
                if(isCountingCanceled){
                    mCountingListener.onCountingCanceled();
                }else{
                    mCountingListener.onCountingFinished();
                }
            }
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
            Log.d(TAG, "-->onAnimationRepeat()");
        }
    };

    public void startCounting(int durationInSec) {
        Log.d(TAG, "-->startCounting(), durationInSec=" + durationInSec);
        isCountingCanceled = false;
        mAnimation.setDuration(durationInSec * 1000l);
        startAnimation(mAnimation);
    }

    public void stopCounting() {
        Log.d(TAG, "-->stopCounting()");
        isCountingCanceled = true;
        clearAnimation();

    }

    public void setcountingListener(ICountingListener countingListener) {
        mCountingListener = countingListener;
    }

    public class BarAnimation extends Animation {
        public BarAnimation() {
            setInterpolator(new LinearInterpolator());
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            updateProgressBar(interpolatedTime);
        }
    }

    public static interface ICountingListener {
        void onCountingFinished();

        void onCountingCanceled();
    }
}
