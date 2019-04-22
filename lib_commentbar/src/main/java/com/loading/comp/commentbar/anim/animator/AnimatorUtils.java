package com.loading.comp.commentbar.anim.animator;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.ViewParent;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import com.loading.common.utils.SystemUtil;

public class AnimatorUtils {
    /**
     * 多段动画每一段默认的播放时间
     */
    public static final int ANIMATION_STAGE_DURATION_MS = 600;
    public static final int ANIMATION_STAGE_DURATION_MS_SHADE = 800;

    public static Animator getScaleInFromLeftBottomAnimator(View targetView) {
        AnimatorSet animatorSet = null;
        if (targetView != null && targetView.getHeight() > 0) {
            ViewParent viewParent = targetView.getParent();
            if (viewParent instanceof View) {
                View parentView = (View) viewParent;
                animatorSet = new AnimatorSet();
                Animator enterXAnimator = ObjectAnimator.ofFloat(targetView, "x", 0, targetView.getX());
                Animator enterYAnimator = ObjectAnimator.ofFloat(targetView, "y", parentView.getHeight(), targetView.getY());
                Animator enterScaleXAnimator = ObjectAnimator.ofFloat(targetView, "scaleX", 0, 1);
                Animator enterScaleYAnimator = ObjectAnimator.ofFloat(targetView, "scaleY", 0, 1);
                Animator enterAlphaAnimator = ObjectAnimator.ofFloat(targetView, "alpha", 0, 1);
                Animator pivotXAnimator = ObjectAnimator.ofFloat(targetView, "pivotX", 0, 0);
                Animator pivotYAnimator = ObjectAnimator.ofFloat(targetView, "pivotY", 0, 0);

                animatorSet.playTogether(enterXAnimator, enterYAnimator, enterScaleXAnimator, enterScaleYAnimator, pivotXAnimator, pivotYAnimator, enterAlphaAnimator);
                animatorSet.setInterpolator(new DecelerateInterpolator());
                animatorSet.setDuration(ANIMATION_STAGE_DURATION_MS);
            }
        }
        return animatorSet;
    }

    public static Animator getShakeLRAnimator(View targetView) {
        return getShakeLRAnimator(targetView, 0);
    }

    public static Animator getShakeLRAnimator(View targetView, float floatUpDeltaY) {
        AnimatorSet animatorSet = null;
        if (targetView != null && targetView.getHeight() > 0) {
            float pivotX = (float) ((targetView.getWidth() - targetView.getPaddingLeft() - targetView.getPaddingRight()) / 2 + targetView.getPaddingLeft());
            float pivotY = (float) (targetView.getHeight() - targetView.getPaddingBottom());

            animatorSet = new AnimatorSet();

            AnimatorSet.Builder builder = animatorSet.play(ObjectAnimator.ofFloat(targetView, "pivotX", pivotX, pivotX))
                    .with(ObjectAnimator.ofFloat(targetView, "pivotY", pivotY, pivotY))
                    .with(ObjectAnimator.ofFloat(targetView, "rotation", new float[]{0, 3, -3, 3, -3, 3, 0}));
            if (floatUpDeltaY > 0) {
                builder.with(getFloatUpAnimator(targetView, floatUpDeltaY));
            }

            animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
            animatorSet.setDuration(ANIMATION_STAGE_DURATION_MS_SHADE);
        }

        return animatorSet;
    }

    public static Animator getFloatUpAnimator(View targetView, float deltaY) {
        Animator animator = null;
        if (targetView != null && targetView.getHeight() > 0) {
            animator = ObjectAnimator.ofFloat(targetView, "y", targetView.getY(), targetView.getY() - deltaY);
            animator.setInterpolator(new LinearInterpolator());
        }

        return animator;
    }

    public static Animator getScaleOutToMiddleTopAnimator(View targetView, float floatUpDeltaY) {
        AnimatorSet animatorSet = null;
        if (targetView != null && targetView.getHeight() > 0) {
            ViewParent viewParent = targetView.getParent();
            if (viewParent instanceof View) {
                animatorSet = new AnimatorSet();
                View parentView = (View) viewParent;

                int targetViewHeight = targetView.getHeight();
                int parentHeight = parentView.getHeight();

                if (targetViewHeight > 0 && parentHeight > 0) {
                    animatorSet.playTogether(ObjectAnimator.ofFloat(targetView, "x", targetView.getX(), SystemUtil.dpToPx(120)),
                            ObjectAnimator.ofFloat(targetView, "y", targetView.getY() - floatUpDeltaY, parentHeight / 3.0f),
                            ObjectAnimator.ofFloat(targetView, "scaleX", 1, 0),
                            ObjectAnimator.ofFloat(targetView, "scaleY", 1, 0),
                            ObjectAnimator.ofFloat(targetView, "alpha", 1, 0),
                            ObjectAnimator.ofFloat(targetView, "pivotX", 0, 0),
                            ObjectAnimator.ofFloat(targetView, "pivotY", 0, 0));
                    animatorSet.setDuration(ANIMATION_STAGE_DURATION_MS);
                    animatorSet.setInterpolator(new AccelerateInterpolator());
                }
            }
        }

        return animatorSet;
    }

    public static Animator getFadeOutAnimator(View targetView) {
        AnimatorSet animatorSet = null;
        ViewParent viewParent = targetView != null ? targetView.getParent() : null;
        if (viewParent instanceof View) {
            animatorSet = new AnimatorSet();
            View parentView = (View) viewParent;

            int targetViewHeight = targetView.getHeight();
            int parentHeight = parentView.getHeight();
            int parentWidth = parentView.getWidth();

            if (targetViewHeight > 0 && parentHeight > 0) {
                Animator exitXAnimator = ObjectAnimator.ofFloat(targetView, "x", targetView.getX(), parentWidth);
                Animator exitYAnimator = ObjectAnimator.ofFloat(targetView, "y", targetView.getY(), parentHeight);
                Animator exitScaleXAnimator = ObjectAnimator.ofFloat(targetView, "scaleX", 1, 0);
                Animator exitScaleYAnimator = ObjectAnimator.ofFloat(targetView, "scaleY", 1, 0);
                Animator exitAlphaAnimator = ObjectAnimator.ofFloat(targetView, "alpha", 1, 0);
                Animator pivotXAnimator = ObjectAnimator.ofFloat(targetView, "pivotX", 0, 0);
                Animator pivotYAnimator = ObjectAnimator.ofFloat(targetView, "pivotY", 0, 0);

                animatorSet.playTogether(exitXAnimator, exitYAnimator, exitScaleXAnimator, exitScaleYAnimator, pivotXAnimator, pivotYAnimator, exitAlphaAnimator);
                animatorSet.setInterpolator(new AccelerateInterpolator());
            }
        }
        return animatorSet;
    }
}
