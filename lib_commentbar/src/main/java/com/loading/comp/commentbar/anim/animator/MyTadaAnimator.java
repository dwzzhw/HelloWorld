package com.loading.comp.commentbar.anim.animator;

import android.animation.ObjectAnimator;
import android.view.View;

public class MyTadaAnimator extends BaseViewAnimator {
    public MyTadaAnimator() {
        setDuration(1200);
    }

    @Override
    public void prepare(View target) {
        getAnimatorAgent().playTogether(
//                    ObjectAnimator.ofFloat(target, "scaleX", 1, 0.9f, 0.9f, 1.1f, 1.1f, 1.1f, 1.1f, 1.1f, 1.1f, 1),
//                    ObjectAnimator.ofFloat(target, "scaleY", 1, 0.9f, 0.9f, 1.1f, 1.1f, 1.1f, 1.1f, 1.1f, 1.1f, 1),
//                    ObjectAnimator.ofFloat(target, "rotation", 0, -3, -3, 3, -3, 3, -3, 3, -3, 0)
                ObjectAnimator.ofFloat(target, "scaleX", 1, 0.9f, 0.9f, 3.1f, 3.1f, 3.1f, 3.1f, 3.1f, 3.1f, 1),
                ObjectAnimator.ofFloat(target, "scaleY", 1, 0.9f, 0.9f, 3.1f, 3.1f, 3.1f, 3.1f, 3.1f, 3.1f, 1),
                ObjectAnimator.ofFloat(target, "rotation", 0, -30, -30, 30, -30, 30, -30, 30, -30, 0)
        );
    }
}