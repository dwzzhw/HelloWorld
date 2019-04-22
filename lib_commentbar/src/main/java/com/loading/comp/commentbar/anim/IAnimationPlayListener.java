package com.loading.comp.commentbar.anim;

import com.loading.comp.commentbar.anim.animator.BaseViewAnimator;

public interface IAnimationPlayListener {
    void onAnimationStart(BaseViewAnimator animation);

    void onAnimationStageChanged(BaseViewAnimator animation, int stage);

    void onAnimationEnd(BaseViewAnimator animation);

    void onAnimationCancel(BaseViewAnimator animation);
}
