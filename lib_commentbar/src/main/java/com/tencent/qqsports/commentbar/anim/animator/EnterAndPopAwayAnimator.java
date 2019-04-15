package com.tencent.qqsports.commentbar.anim.animator;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.ViewParent;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import com.tencent.qqsports.logger.Loger;

public class EnterAndPopAwayAnimator extends BaseViewAnimator {
    private static final String TAG = "EnterAndPopAwayAnimator";

    public EnterAndPopAwayAnimator() {
        setDuration(-1);
    }

    @Override
    protected void prepare(View target) {
        Animator enterAnimator = getEnterAnimator(target);
        Animator standUpAnimator = getStandUpAnimatorSet(target);
        Animator exitAnimator = getExitAnimator(target);
        if (enterAnimator != null && standUpAnimator != null && exitAnimator != null) {
//            getAnimatorAgent().playSequentially(enterAnimator, standUpAnimator, exitAnimator);
            getAnimatorAgent().playSequentially(standUpAnimator, exitAnimator);
        }
    }

    private Animator getEnterAnimator(View target) {
        Animator enterAnimator = null;
        ViewParent viewParent = target.getParent();
        if (viewParent instanceof View) {
            View parentView = (View) viewParent;
            int targetViewHeight = target.getHeight();
            int parentHeight = parentView.getHeight();
            if (targetViewHeight > 0 && parentHeight > 0) {
                enterAnimator = ObjectAnimator.ofFloat(target, "y", parentHeight, parentHeight - targetViewHeight).setDuration(1000);
                enterAnimator.setInterpolator(new DecelerateInterpolator());
            }
        }
        return enterAnimator;
    }

    private Animator getExitAnimator(View target) {
        AnimatorSet exitAnimatorSet = null;
        ViewParent viewParent = target.getParent();
        if (viewParent instanceof View) {
            View parentView = (View) viewParent;

            int targetViewHeight = target.getHeight();
            int parentHeight = parentView.getHeight();
            int parentWidth = parentView.getWidth();
            float startX = target.getX();

            Loger.d(TAG, "-->getExitAnimator(), targetViewHeight=" + targetViewHeight + ", parentHeight=" + parentHeight + ", parentWidth=" + parentWidth);

            if (targetViewHeight > 0 && parentHeight > 0) {
                exitAnimatorSet = new AnimatorSet();
                Animator exitXAnimator = ObjectAnimator.ofFloat(target, "x", target.getX(), parentWidth);
                Animator exitYAnimator = ObjectAnimator.ofFloat(target, "y", target.getY(), 0);
                Animator exitScaleXAnimator = ObjectAnimator.ofFloat(target, "scaleX", 1, 0.5f);
                Animator exitScaleYAnimator = ObjectAnimator.ofFloat(target, "scaleY", 1, 0.5f);
                Animator exitAlphaAnimator = ObjectAnimator.ofFloat(target, "alpha", 1, 0.5f);
                Animator pivotXAnimator = ObjectAnimator.ofFloat(target, "pivotX", 0, 0);
                Animator pivotYAnimator = ObjectAnimator.ofFloat(target, "pivotY", 0, 0);
                exitXAnimator.setInterpolator(new AccelerateInterpolator());
                exitYAnimator.setInterpolator(new LinearInterpolator());
//            exitScaleXAnimator.setInterpolator(new LinearInterpolator());
//            exitScaleYAnimator.setInterpolator(new LinearInterpolator());

                exitAnimatorSet.playTogether(exitXAnimator, exitYAnimator, exitScaleXAnimator, exitScaleYAnimator, pivotXAnimator, pivotYAnimator, exitAlphaAnimator);
//            exitAnimatorSet.playTogether(exitScaleXAnimator, exitScaleYAnimator, pivotX);
                exitAnimatorSet.setDuration(1000);
            }
        }
        return exitAnimatorSet;
    }

    private AnimatorSet getStandUpAnimatorSet(View target) {
        AnimatorSet animatorSet = null;
        if (target != null && target.getHeight() > 0) {
            animatorSet = new AnimatorSet();
            float x = (float) ((target.getWidth() - target.getPaddingLeft() - target.getPaddingRight()) / 2 + target.getPaddingLeft());
            float y = (float) (target.getHeight() - target.getPaddingBottom());

            float startValue = 90;
            int itemCnt = 8;
            float[] valueList = new float[itemCnt];
            int minus = 1;
            for (int i = 0; i < itemCnt - 1; i++) {
                valueList[i] = startValue * minus;
                startValue = (float) Math.sqrt(startValue);
                minus *= -1;
//            Loger.d(TAG, "-->value at " + i + " is " + valueList[i]);
            }
            valueList[itemCnt - 1] = 0;

            animatorSet.playTogether(
                    ObjectAnimator.ofFloat(target, "pivotX", x, x),
                    ObjectAnimator.ofFloat(target, "pivotY", y, y),
                    ObjectAnimator.ofFloat(target, "rotationX", valueList));
            animatorSet.setDuration(1200);
            animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());

        }
        return animatorSet;
    }
}
