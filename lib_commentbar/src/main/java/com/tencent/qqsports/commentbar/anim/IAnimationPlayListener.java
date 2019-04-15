package com.tencent.qqsports.commentbar.anim;

import com.tencent.qqsports.commentbar.anim.animator.BaseViewAnimator;

public interface IAnimationPlayListener {
    void onAnimationStart(BaseViewAnimator animation);

    void onAnimationStageChanged(BaseViewAnimator animation, int stage);

    void onAnimationEnd(BaseViewAnimator animation);

    void onAnimationCancel(BaseViewAnimator animation);
}
