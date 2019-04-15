package com.tencent.qqsports.commentbar.anim.animator;

import android.animation.Animator;
import android.view.View;

import com.tencent.qqsports.logger.Loger;
import com.tencent.qqsports.common.util.UiThreadUtil;

public class ScaleInFromLB_Shake_ScaleOutAnimator extends BaseViewAnimator {
    private static final String TAG = "ScaleInFromLB_Shake_ScaleOutAnimator";

    public static final int ANIMATION_STAGE_ENTER = 1;
    public static final int ANIMATION_STAGE_STAY = 2;
    public static final int ANIMATION_STAGE_EXIT = 3;

    public static final int ANIMATION_STAY_DURATION = 200;

    public ScaleInFromLB_Shake_ScaleOutAnimator() {
        setDuration(-1);
        mShowViewWhenAnimationStart = true;
        mHideViewWhenAnimationEnd = true;
    }

    @Override
    protected void prepare(View target) {
        Animator enterAnimator = AnimatorUtils.getScaleInFromLeftBottomAnimator(target);
        Animator shakeAnimator = AnimatorUtils.getShakeLRAnimator(target, target != null ? 0.2f * target.getHeight() : 0);
        Animator exitAnimator = AnimatorUtils.getScaleOutToMiddleTopAnimator(target, target != null ? 0.2f * target.getHeight() : 0);
        if (enterAnimator != null && shakeAnimator != null && exitAnimator != null) {
            exitAnimator.setStartDelay(ANIMATION_STAY_DURATION);
            getAnimatorAgent().playSequentially(enterAnimator, shakeAnimator, exitAnimator);

            exitAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    Loger.d(TAG, "exitAnimator-->onAnimationStart()");
                    UiThreadUtil.postDelay(mNotifyExitBeginRunnable, ANIMATION_STAY_DURATION);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    Loger.d(TAG, "exitAnimator-->onAnimationEnd()");
                    UiThreadUtil.removeRunnable(mNotifyExitBeginRunnable);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    UiThreadUtil.removeRunnable(mNotifyExitBeginRunnable);
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            });
        }
    }

    @Override
    public boolean isInExitStage(int stage) {
        return ANIMATION_STAGE_EXIT == stage;
    }

    private Runnable mNotifyExitBeginRunnable = () -> {
        notifyAnimationStageChanged(ANIMATION_STAGE_EXIT);
    };
}
