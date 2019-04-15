package com.tencent.qqsports.commentbar.anim.animator;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.ViewParent;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;

public class FadeOutAnimator extends BaseViewAnimator {
    public FadeOutAnimator() {
        setDuration(500);
        mShowViewWhenAnimationStart = false;
        mHideViewWhenAnimationEnd = true;
    }

    @Override
    public void prepare(View target) {
        ViewParent viewParent = target.getParent();
        if (viewParent instanceof View) {
            View parentView = (View) viewParent;

            int targetViewHeight = target.getHeight();
            int parentHeight = parentView.getHeight();
            int parentWidth = parentView.getWidth();

            if (targetViewHeight > 0 && parentHeight > 0) {
                Animator exitXAnimator = ObjectAnimator.ofFloat(target, "x", target.getX(), parentWidth);
                Animator exitYAnimator = ObjectAnimator.ofFloat(target, "y", target.getY(), parentHeight);
                Animator exitScaleXAnimator = ObjectAnimator.ofFloat(target, "scaleX", 1, 0);
                Animator exitScaleYAnimator = ObjectAnimator.ofFloat(target, "scaleY", 1, 0);
                Animator exitAlphaAnimator = ObjectAnimator.ofFloat(target, "alpha", 1, 0);
                Animator pivotXAnimator = ObjectAnimator.ofFloat(target, "pivotX", 0, 0);
                Animator pivotYAnimator = ObjectAnimator.ofFloat(target, "pivotY", 0, 0);

                getAnimatorAgent().playTogether(exitXAnimator, exitYAnimator, exitScaleXAnimator, exitScaleYAnimator, pivotXAnimator, pivotYAnimator, exitAlphaAnimator);
                setInterpolator(new AccelerateInterpolator());
            }
        }
    }
}