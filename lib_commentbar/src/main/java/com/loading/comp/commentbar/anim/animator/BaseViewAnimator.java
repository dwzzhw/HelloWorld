package com.loading.comp.commentbar.anim.animator;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.Interpolator;

public abstract class BaseViewAnimator {
    public static final long DURATION = 1000;

    private AnimatorSet mAnimatorSet;
    private IInternalAnimationListener mAnimationPlayListener;

    private long mDuration = DURATION;
    private int mRepeatTimes = 0;
    private int mRepeatMode = ValueAnimator.RESTART;
    protected boolean mShowViewWhenAnimationStart = true;
    protected boolean mHideViewWhenAnimationEnd = false;
    private View mTargetView;

    {
        mAnimatorSet = new AnimatorSet();
    }

    protected abstract void prepare(View target);

    public BaseViewAnimator setTarget(View target) {
        mTargetView = target;
        reset(target);
        prepare(target);
        return this;
    }

    public View getTargetView() {
        return mTargetView;
    }

    public void animate() {
        start();
    }

    public void restart() {
        mAnimatorSet = mAnimatorSet.clone();
        start();
    }

    /**
     * reset the view to default status
     *
     * @param target
     */
    public void reset(View target) {
        if (target != null) {
            target.setAlpha(1);
            target.setScaleX(1);
            target.setScaleY(1);
            target.setTranslationX(0);
            target.setTranslationY(0);
            target.setRotation(0);
            target.setRotationY(0);
            target.setRotationX(0);
        }
    }

    /**
     * start to animate
     */
    public void start() {
        for (Animator animator : mAnimatorSet.getChildAnimations()) {
            if (animator instanceof ValueAnimator) {
                ((ValueAnimator) animator).setRepeatCount(mRepeatTimes);
                ((ValueAnimator) animator).setRepeatMode(mRepeatMode);
            }
        }
        if (mDuration >= 0) {
            mAnimatorSet.setDuration(mDuration);
        }
        mAnimatorSet.start();
    }

    public void setDuration(long duration) {
        mDuration = duration;
    }

    public void setAnimationListener(IInternalAnimationListener animationListener) {
        mAnimationPlayListener = animationListener;
        if (animationListener == null) {
            mAnimatorSet.removeListener(mInternalListener);
        } else if (mAnimatorSet.getListeners() == null || !mAnimatorSet.getListeners().contains(mInternalListener)) {
            mAnimatorSet.addListener(mInternalListener);
        }
    }

    private Animator.AnimatorListener mInternalListener = new Animator.AnimatorListener() {

        @Override
        public void onAnimationStart(Animator animation) {
            if (mAnimationPlayListener != null) {
                mAnimationPlayListener.onAnimationStart(BaseViewAnimator.this);
            }
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            if (mAnimationPlayListener != null) {
                mAnimationPlayListener.onAnimationEnd(BaseViewAnimator.this);
            }
        }

        @Override
        public void onAnimationCancel(Animator animation) {
            if (mAnimationPlayListener != null) {
                mAnimationPlayListener.onAnimationCancel(BaseViewAnimator.this);
            }
        }

        @Override
        public void onAnimationRepeat(Animator animation) {
            if (mAnimationPlayListener != null) {
                mAnimationPlayListener.onAnimationRepeat(BaseViewAnimator.this);
            }
        }
    };

    protected void notifyAnimationStageChanged(int stage) {
        if (mAnimationPlayListener != null) {
            mAnimationPlayListener.onAnimationStageChanged(BaseViewAnimator.this, stage);
        }
    }

    public void cancel() {
        mAnimatorSet.cancel();
    }

    public boolean isRunning() {
        return mAnimatorSet.isRunning();
    }

    public boolean isStarted() {
        return mAnimatorSet.isStarted();
    }

    public void removeAllListener() {
        mAnimatorSet.removeAllListeners();
    }

    public void setInterpolator(Interpolator interpolator) {
        mAnimatorSet.setInterpolator(interpolator);
    }

    public long getDuration() {
        return mDuration;
    }

    public AnimatorSet getAnimatorAgent() {
        return mAnimatorSet;
    }

    public void setRepeatTimes(int repeatTimes) {
        mRepeatTimes = repeatTimes;
    }

    public void setRepeatMode(int repeatMode) {
        mRepeatMode = repeatMode;
    }

    public boolean isShowViewWhenAnimationStart() {
        return mShowViewWhenAnimationStart;
    }

    public boolean isHideViewWhenAnimationEnd() {
        return mHideViewWhenAnimationEnd;
    }

    public boolean isInExitStage(int stage) {
        return false;
    }

    public interface IInternalAnimationListener {
        void onAnimationStart(BaseViewAnimator animator);

        void onAnimationEnd(BaseViewAnimator animator);

        void onAnimationCancel(BaseViewAnimator animator);

        void onAnimationRepeat(BaseViewAnimator animator);

        /**
         * 对于多段动画，当前播放到了哪一段
         *
         * @param animator
         * @param stage
         */
        void onAnimationStageChanged(BaseViewAnimator animator, int stage);
    }
}

