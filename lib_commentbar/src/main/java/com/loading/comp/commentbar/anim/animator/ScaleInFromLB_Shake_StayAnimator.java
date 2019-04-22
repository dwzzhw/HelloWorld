package com.loading.comp.commentbar.anim.animator;

import android.animation.Animator;
import android.view.View;

public class ScaleInFromLB_Shake_StayAnimator extends BaseViewAnimator {
    public ScaleInFromLB_Shake_StayAnimator() {
        setDuration(-1);
    }

    @Override
    protected void prepare(View target) {
        Animator enterAnimator = AnimatorUtils.getScaleInFromLeftBottomAnimator(target);
        Animator shakeAnimator = AnimatorUtils.getShakeLRAnimator(target);

        if (enterAnimator != null && shakeAnimator != null) {
            getAnimatorAgent().playSequentially(enterAnimator, shakeAnimator);
        }
    }
}
