package com.tencent.qqsports.commentbar.anim;

import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.loading.common.manager.ListenerManager;
import com.loading.common.utils.Loger;
import com.loading.common.utils.ViewUtils;
import com.tencent.qqsports.commentbar.anim.animator.BaseViewAnimator;
import com.tencent.qqsports.commentbar.anim.animator.EnterAndPopAwayAnimator;
import com.tencent.qqsports.commentbar.anim.animator.FadeOutAnimator;
import com.tencent.qqsports.commentbar.anim.animator.MyStandupAnimator;
import com.tencent.qqsports.commentbar.anim.animator.ScaleInFromLB_Shake_ScaleOutAnimator;
import com.tencent.qqsports.commentbar.anim.animator.ScaleInFromLB_Shake_StayAnimator;

/**
 * Created by loading on 09/20/18.
 */
public class YoyoPlayer implements BaseViewAnimator.IInternalAnimationListener {
    private static final String TAG = "YoyoPlayer";

    public static final int ANIM_TYPE_STANDUP = 1;
    public static final int ANIM_TYPE_ENTER_POP_EXIT = 2;
    public static final int ANIM_TYPE_FADE_OUT = 3;
    public static final int ANIM_TYPE_SCALE_IN_SHAKE_SCALE_OUT = 4;
    public static final int ANIM_TYPE_SCALE_IN_SHAKE_STAY = 5;

    public static final float CENTER_PIVOT = Float.MAX_VALUE;
    private float mPivotX = CENTER_PIVOT, mPivotY = CENTER_PIVOT;
    private ListenerManager<IAnimationPlayListener> mListenerManager;

    private int mAnimatItemIndex = -1; //正在执行动画的Item在recyclerView中的位置
    private View mAnimationContentView = null;
    private BaseViewAnimator mCurrentPlayingAnimator = null;
    private boolean mDisableParentClip; //此次动画是否允许超出parent的限制

    public static YoyoPlayer newInstance() {
        return new YoyoPlayer();
    }

    private YoyoPlayer() {
        this.mListenerManager = new ListenerManager<>();
    }

    public void playAnimation(View animationContentView, int animationType) {
        playAnimation(animationContentView, createAnimator(animationType), false);
    }

    public void playAnimation(View animationContentView, int animationType, boolean disableParentClip) {
        playAnimation(animationContentView, createAnimator(animationType), disableParentClip);
    }

    public void playAnimation(View animationContentView, BaseViewAnimator animator, boolean disableParentClip) {
        Loger.d(TAG, "-->playAnimation(), animationContentView=" + animationContentView + ", animator=" + animator + ", disableParentClip=" + disableParentClip);
        if (animator != null) {
            stop();
            mAnimationContentView = animationContentView;
            mDisableParentClip = disableParentClip;
            if (disableParentClip) {
                setAllParentsClip(animationContentView, false);
            }

            mCurrentPlayingAnimator = animator;
            startPlayOnTargetView();
        }
    }

    private void startPlayOnTargetView() {
        if (mCurrentPlayingAnimator != null) {
            mCurrentPlayingAnimator.setTarget(mAnimationContentView);

            if (mPivotX == CENTER_PIVOT) {
                ViewCompat.setPivotX(mAnimationContentView, mAnimationContentView.getMeasuredWidth() / 2.0f);
            } else {
                mAnimationContentView.setPivotX(mPivotX);
            }
            if (mPivotY == CENTER_PIVOT) {
                ViewCompat.setPivotY(mAnimationContentView, mAnimationContentView.getMeasuredHeight() / 2.0f);
            } else {
                mAnimationContentView.setPivotY(mPivotY);
            }

            mCurrentPlayingAnimator.setAnimationListener(this);
            mCurrentPlayingAnimator.animate();
        }
    }

    public void stop() {
        Loger.d(TAG, "-->stop()");
        if (mCurrentPlayingAnimator != null) {
            mCurrentPlayingAnimator.cancel();
        }
    }

    public boolean isRunning() {
        return mCurrentPlayingAnimator != null && mCurrentPlayingAnimator.isRunning();
    }

    private void setAllParentsClip(View view, boolean enabled) {
        Loger.d(TAG, "-->setAllParentsClip(), view=" + view + ", enabled=" + enabled);
        if (view != null) {
            mAnimatItemIndex = -1;
            while (view.getParent() != null && view.getParent() instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) view.getParent();
                viewGroup.setClipChildren(enabled);
                viewGroup.setClipToPadding(enabled);

                if (viewGroup instanceof RecyclerView) {
                    if (!enabled) {
                        int childCnt = viewGroup.getChildCount();
                        for (int i = 0; i < childCnt; i++) {
                            View item = viewGroup.getChildAt(i);
                            if (item == view) {
                                mAnimatItemIndex = i;
                            }
                        }
                    }

                    ((RecyclerView) viewGroup).setChildDrawingOrderCallback(enabled ? null : mDrawingOrderCallback);
                }
                view = viewGroup;
            }
        }
    }

    private void resetAnimation() {
        Loger.d(TAG, "-->resetAnimation(), mAnimationContentView=" + mAnimationContentView);
        if (mDisableParentClip) {
            setAllParentsClip(mAnimationContentView, true);
            mDisableParentClip = false;
        }
        if (mCurrentPlayingAnimator != null) {
            mCurrentPlayingAnimator.removeAllListener();
            if (mAnimationContentView != null) {
                mCurrentPlayingAnimator.reset(mAnimationContentView);
            }
            mCurrentPlayingAnimator = null;
        }
        mAnimationContentView = null;
        mAnimatItemIndex = -1;
    }

    private BaseViewAnimator createAnimator(int animatorType) {
        BaseViewAnimator animator = null;
        switch (animatorType) {
            case ANIM_TYPE_STANDUP:
                animator = new MyStandupAnimator();
                break;
            case ANIM_TYPE_ENTER_POP_EXIT:
                animator = new EnterAndPopAwayAnimator();
                break;
            case ANIM_TYPE_FADE_OUT:
                animator = new FadeOutAnimator();
                break;
            case ANIM_TYPE_SCALE_IN_SHAKE_STAY:
                animator = new ScaleInFromLB_Shake_StayAnimator();
                break;
            case ANIM_TYPE_SCALE_IN_SHAKE_SCALE_OUT:
                animator = new ScaleInFromLB_Shake_ScaleOutAnimator();
                break;
            default:
                animator = new MyStandupAnimator();
                break;
        }
        return animator;
    }

    private RecyclerView.ChildDrawingOrderCallback mDrawingOrderCallback = (childCount, i) -> {
        int toDrawItemIndex = i;
        if (mAnimatItemIndex >= 0 && mAnimatItemIndex < childCount) {
            if (i >= mAnimatItemIndex) {
                toDrawItemIndex = i + 1;
                if (toDrawItemIndex >= childCount) {
                    toDrawItemIndex = mAnimatItemIndex;
                }
            }
        }
        return toDrawItemIndex;
    };

    @Override
    public void onAnimationStart(BaseViewAnimator animator) {
        Loger.d(TAG, "-->onAnimationStart(), animator=" + animator + ", showViewWhenAnimationStart="
                + (animator != null ? animator.isShowViewWhenAnimationStart() : "Null"));
        if (animator != null && animator.isShowViewWhenAnimationStart() && mAnimationContentView != null) {
            ViewUtils.setVisibility(mAnimationContentView, View.VISIBLE);
        }
        notifyAnimationStart(animator);
    }

    @Override
    public void onAnimationEnd(BaseViewAnimator animator) {
        Loger.d(TAG, "-->onAnimationEnd(), animator=" + animator + ", hideViewWhenAnimationEnd="
                + (animator != null ? animator.isHideViewWhenAnimationEnd() : "Null"));
        if (animator != null && animator.isHideViewWhenAnimationEnd() && mAnimationContentView != null) {
            ViewUtils.setVisibility(mAnimationContentView, View.GONE);
        }
        resetAnimation();
        notifyAnimationEnd(animator);
    }

    @Override
    public void onAnimationCancel(BaseViewAnimator animator) {
        Loger.d(TAG, "-->onAnimationCancel(), animator=" + animator);
        notifyAnimationCanceled(animator);
    }

    @Override
    public void onAnimationRepeat(BaseViewAnimator animator) {
        Loger.d(TAG, "-->onAnimationRepeat(), animator=" + animator);
    }

    @Override
    public void onAnimationStageChanged(BaseViewAnimator animator, int stage) {
        notifyAnimationStageChanged(animator, stage);
    }


    private void notifyAnimationStart(BaseViewAnimator animation) {
        mListenerManager.loopListenerList(listener -> listener.onAnimationEnd(animation));
    }

    private void notifyAnimationStageChanged(BaseViewAnimator animation, int stage) {
        mListenerManager.loopListenerList(listener -> listener.onAnimationStageChanged(animation, stage));
    }

    private void notifyAnimationCanceled(BaseViewAnimator animation) {
        mListenerManager.loopListenerList(listener -> listener.onAnimationCancel(animation));
    }

    private void notifyAnimationEnd(BaseViewAnimator animation) {
        mListenerManager.loopListenerList(listener -> listener.onAnimationEnd(animation));
    }

    public void addAnimationListener(IAnimationPlayListener listener) {
        if (listener != null) {
            mListenerManager.addListener(listener);
        }
    }

    public void removeAnimationListener(IAnimationPlayListener listener) {
        mListenerManager.removeListener(listener);
    }
}
