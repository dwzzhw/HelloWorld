package com.tencent.qqsports.commentbar.anim.animator;

import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

public class MyStandupAnimator extends BaseViewAnimator {
    private static final String TAG = "MyStandupAnimator";

    public MyStandupAnimator() {
        setDuration(1200);
    }

    @Override
    protected void prepare(View target) {
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

        getAnimatorAgent().playTogether(
                ObjectAnimator.ofFloat(target, "pivotX", x, x),
                ObjectAnimator.ofFloat(target, "pivotY", y, y),
                ObjectAnimator.ofFloat(target, "rotationX", valueList));

        setInterpolator(new AccelerateDecelerateInterpolator());
    }
}
