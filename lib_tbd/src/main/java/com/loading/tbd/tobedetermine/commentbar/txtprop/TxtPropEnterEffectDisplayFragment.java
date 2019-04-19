package com.loading.tobedetermine.commentbar.txtprop;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.loading.common.utils.CommonUtil;
import com.loading.common.utils.Loger;
import com.tencent.qqsports.commentbar.R;
import com.tencent.qqsports.commentbar.anim.IAnimationPlayListener;
import com.tencent.qqsports.commentbar.anim.YoyoPlayer;
import com.tencent.qqsports.commentbar.anim.animator.BaseViewAnimator;
import com.tencent.qqsports.commentbar.anim.animator.ScaleInFromLB_Shake_ScaleOutAnimator;
import com.loading.tobedetermine.commentbar.txtprop.view.TxtPropPreviewView;

import java.util.concurrent.LinkedBlockingQueue;


public class TxtPropEnterEffectDisplayFragment extends BaseFragment implements IAnimationPlayListener {
    private static final String TAG = "TxtPropEnterEffectDisplayFragment";
    private static final int MSG_ADD_ITEM = 2;
    private int mItemDisplayIntervalInMs;      //连续两个文字特效的出现间隔，根据系统版本动态调整

    protected ViewGroup mRootView = null;
//    private TxtPropPreviewView mPreviewArea;

    private Queue<CommentInfo> mPendingPreviewPropQueue;
    //此处采用队列来保证播放顺序，且可以避免大量图片请求的同时发出；若不care这些点，可简单采用map标记，图片请求简单交由ImageFetcher模块处理
    private Queue<CommentInfo> mPendingFetchBgResQueue;
    //当前正在等待获取背景图片资源的道具项
    private CommentInfo mWaitingBgResCommentInfo = null;

    private IEnterEffectListener mEEListener;
    private CommentInfo mCurrentPlayCommentInfo;
    private ObjectReuseCache<TxtPropPreviewView> mPreviewReuseCache;

    private int mTxtPropItemPreviewHeight = 0;
    private long mLastItemDisplayTime = 0;
    private int mPreviewAreaMarginBottom;

    public static TxtPropEnterEffectDisplayFragment newInstance() {
        TxtPropEnterEffectDisplayFragment fragment = new TxtPropEnterEffectDisplayFragment();
        return fragment;
    }

    public TxtPropEnterEffectDisplayFragment() {
        this.mPendingPreviewPropQueue = new LinkedBlockingQueue<>();
        this.mPendingFetchBgResQueue = new LinkedBlockingQueue<>();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTxtPropItemPreviewHeight = CApplication.getDimensionPixelSize(R.dimen.comment_txt_prop_preview_area_height);
        mPreviewAreaMarginBottom = CApplication.getDimensionPixelSize(R.dimen.comment_txt_prop_preview_area_margin_bottom);
        initIntentData();
    }

    private void initIntentData() {
        Loger.d(TAG, "-->initIntentData()");
        Bundle arguments = getArguments();
        if (arguments != null) {
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_ADD_ITEM:
                    tryPlayPendingProp();
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = (ViewGroup) inflater.inflate(R.layout.txt_prop_enter_effect_display_layout, container, false);
//        mPreviewArea = (TxtPropPreviewView) mRootView.findViewById(R.id.txt_prop_preview_area);
//        mPreviewArea.addAnimationListener(this);
        return mRootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Loger.d(TAG, "-->onViewCreated()");
        tryPlayPendingProp();
    }

    @Override
    protected void onUiResume(boolean isContentEmpty) {
        super.onUiResume(isContentEmpty);
        Loger.d(TAG, "-->onUiResume(), isContentEmpty=" + isContentEmpty);
        tryFetchPropBgRes();
        tryPlayPendingProp();
    }

    @Override
    protected void onUiPause(boolean isContentEmpty) {
        super.onUiPause(isContentEmpty);
        stopAnimation();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        boolean isFullScreenFragment = newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE;
        Loger.d(TAG, "-->onConfigurationChanged(), isFullScreen=" + isFullScreenFragment);
        if (!isFullScreenFragment) {
            tryPlayPendingProp();
        } else {
            stopAnimation();
        }
    }

    private void stopAnimation() {
        for (int i = 0; i < mRootView.getChildCount(); i++) {
            View childView = mRootView.getChildAt(i);
            if (childView instanceof TxtPropPreviewView) {
                ((TxtPropPreviewView) childView).stopAnimation();
            }
        }
    }

    private boolean isContainerActivityFullScreen() {
        return getActivity() instanceof AbsActivity && ((AbsActivity) getActivity()).isFullScreen();
    }

    public void addTxtPropCommentItem(CommentInfo commentInfo) {
        Loger.d(TAG, "-->addTxtPropCommentItem(), commentInfo=" + commentInfo + ", this=" + this);
        if (commentInfo != null && commentInfo.getTxtPropInfo() != null) {
            mPendingPreviewPropQueue.offer(commentInfo);
            tryPlayPendingProp();
        }
    }

    public void addTxtPropCommentItem(List<CommentInfo> commentInfoList) {
        Loger.d(TAG, "-->addTxtPropCommentItem(), commentInfoList=" + commentInfoList + ", this=" + this);
        if (!CommonUtil.isEmpty(commentInfoList)) {
            clearTooMuchPendingData();
            for (CommentInfo commentInfo : commentInfoList) {
                if (commentInfo != null && isValidEnterEffectProp(commentInfo) && !TextUtils.isEmpty(commentInfo.getTxtPropItemBgResUrl())) {
                    mPendingFetchBgResQueue.offer(commentInfo);
                }
            }
            tryFetchPropBgRes();
        }
    }

    /**
     * 清除当前的等待队列
     */
    public void clearPendingPropCommentItem() {
        mPendingPreviewPropQueue.clear();
        mPendingFetchBgResQueue.clear();
        mWaitingBgResCommentInfo = null;
    }

    private void clearTooMuchPendingData() {
        if (mPendingFetchBgResQueue.size() > 50) {
            mPendingFetchBgResQueue.clear();
        }
        if (mPendingPreviewPropQueue.size() > 50) {
            mPendingPreviewPropQueue.clear();
        }
    }

    ImageFetcher.IBitmapLoadListener mFetchPropBgResListener = new ImageFetcher.IBitmapLoadListener() {
        @Override
        public void onBitmapLoaded(String bitmapUrl, Bitmap resultBitmap) {
            if (mWaitingBgResCommentInfo != null && !TextUtils.isEmpty(bitmapUrl) && bitmapUrl.equals(mWaitingBgResCommentInfo.getTxtPropItemBgResUrl())) {
                mPendingPreviewPropQueue.offer(mWaitingBgResCommentInfo);
                tryPlayPendingProp();
            }
            mWaitingBgResCommentInfo = null;
            tryFetchPropBgRes();
        }

        @Override
        public void onBitmapLoadFailed(String bitmapUrl) {
            mWaitingBgResCommentInfo = null;
            tryFetchPropBgRes();
        }
    };

    private void tryFetchPropBgRes() {
        Loger.d(TAG, "-->tryFetchPropBgRes(), pending cnt=" + getPendingFetchBgResPropCnt() + ", mWaitingBgResCommentInfo=" + mWaitingBgResCommentInfo);
        if (isUiVisible() && getPendingFetchBgResPropCnt() > 0 && mWaitingBgResCommentInfo == null) {
            mWaitingBgResCommentInfo = mPendingFetchBgResQueue.poll();
            if (mWaitingBgResCommentInfo != null) {
                if (!ImageFetcher.loadBitmap(mWaitingBgResCommentInfo.getTxtPropItemBgResUrl(), 0, 0, mFetchPropBgResListener)) {
                    mWaitingBgResCommentInfo = null;
                    tryFetchPropBgRes();
                }
            }
        }
    }

    private void tryPlayPendingProp() {
        Loger.d(TAG, "-->tryPlayPendingProp(), isUiVisible = " + isUiVisible() + ", queue size=" + getPendingPropCnt()
                + ", isFullScreen=" + isContainerActivityFullScreen());
        if (isUiVisible() && !isContainerActivityFullScreen()) {
            if (getPendingPropCnt() > 0) {
                mHandler.removeMessages(MSG_ADD_ITEM); //清除等待播放的条目
                long currTime = System.currentTimeMillis();
                long displayInterval = getItemDisplayIntervalInMs();
                if (currTime - mLastItemDisplayTime < displayInterval) {
                    mHandler.sendEmptyMessageDelayed(MSG_ADD_ITEM, displayInterval - (currTime - mLastItemDisplayTime));
                    return;
                }

                CommentInfo commentInfo = mPendingPreviewPropQueue.poll();
                if (isValidEnterEffectProp(commentInfo)) {
                    mLastItemDisplayTime = System.currentTimeMillis();
                    mHandler.sendEmptyMessageDelayed(MSG_ADD_ITEM, displayInterval);
                    ImageFetcher.loadBitmap(commentInfo.getTxtPropItemBgResUrl(), 0, 0, new ImageFetcher.IBitmapLoadListener() {
                        @Override
                        public void onBitmapLoaded(String bitmapUrl, Bitmap resultBitmap) {
                            previewWhenBitmapLoaded(commentInfo, bitmapUrl, resultBitmap);
                        }

                        @Override
                        public void onBitmapLoadFailed(String bitmapUrl) {
                            previewWhenBitmapLoaded(commentInfo, bitmapUrl, null);
                        }
                    });
                } else {
                    tryPlayPendingProp();
                }
            }
        }
    }


    private long getItemDisplayIntervalInMs() {
        if (mItemDisplayIntervalInMs <= 0) {
            mItemDisplayIntervalInMs = VersionUtils.hasLOLLIPOP() ? 500 : 2200;
        }
        return mItemDisplayIntervalInMs;
    }

    private void previewWhenBitmapLoaded(CommentInfo commentInfo, String bitmapUrl, Bitmap resultBitmap) {
        Loger.d(TAG, "-->previewWhenBitmapLoaded(), commentInfo=" + commentInfo + ", resultBitmap=" + resultBitmap
                + ", current preview cnt=" + mRootView.getChildCount());
        String currentTargetBgUrl = commentInfo != null ? commentInfo.getTxtPropItemBgResUrl() : null;
        if (!TextUtils.isEmpty(currentTargetBgUrl) && currentTargetBgUrl.equals(bitmapUrl) && resultBitmap != null) {
            TxtPropPreviewView previewView = createOrReuseView();
            previewView.hidePreviewView();
            previewView.updateContentData(commentInfo, resultBitmap);
            previewView.setTag(commentInfo);
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mTxtPropItemPreviewHeight);
            lp.gravity = Gravity.BOTTOM;
            lp.bottomMargin = mPreviewAreaMarginBottom;

            mRootView.addView(previewView, lp);
            previewView.playAnimation(YoyoPlayer.ANIM_TYPE_SCALE_IN_SHAKE_SCALE_OUT);
        } else {
            tryPlayPendingProp();
        }
    }

    private TxtPropPreviewView createOrReuseView() {
        initReuseCache();
        TxtPropPreviewView previewView = mPreviewReuseCache.obtainCachedObj();
        if (previewView == null) {
            previewView = new TxtPropPreviewView(getContext());
            previewView.addAnimationListener(this);
        }
        return previewView;
    }

    private void initReuseCache() {
        if (mPreviewReuseCache == null) {
            mPreviewReuseCache = new ObjectReuseCache<>(5);
        }
    }

    private int getPendingPropCnt() {
        return mPendingPreviewPropQueue != null ? mPendingPreviewPropQueue.size() : 0;
    }

    private int getPendingFetchBgResPropCnt() {
        return mPendingFetchBgResQueue != null ? mPendingFetchBgResQueue.size() : 0;
    }

    private boolean isValidEnterEffectProp(CommentInfo commentInfo) {
        return commentInfo != null && commentInfo.getTxtPropInfo() != null && commentInfo.getTxtPropInfo().isEnterEffectType();
    }

    @Override
    public void onAnimationStart(BaseViewAnimator animation) {
        if (mEEListener != null) {
            mEEListener.onEEEnterBegin(mCurrentPlayCommentInfo);
        }
    }

    @Override
    public void onAnimationStageChanged(BaseViewAnimator animation, int stage) {
        Loger.d(TAG, "-->onAnimationStageChanged(), animation=" + animation + ",stage=" + stage);
        if (mEEListener != null && animation != null && animation.isInExitStage(stage)) {
            mEEListener.onEEExitBegin(mCurrentPlayCommentInfo);
        }
    }

    @Override
    public void onAnimationEnd(BaseViewAnimator animation) {
        mCurrentPlayCommentInfo = null;
        if (animation != null && animation.getTargetView() instanceof TxtPropPreviewView) {
            TxtPropPreviewView targetView = (TxtPropPreviewView) animation.getTargetView();
            mRootView.removeView(targetView);
            initReuseCache();
            mPreviewReuseCache.recycledObj(targetView);
        }
        tryPlayPendingProp();
    }

    @Override
    public void onAnimationCancel(BaseViewAnimator animation) {
        if (mEEListener != null && animation != null && animation.isInExitStage(ScaleInFromLB_Shake_ScaleOutAnimator.ANIMATION_STAGE_EXIT)) {
            mEEListener.onEEExitBegin(mCurrentPlayCommentInfo);
        }
    }

    public void setEEListener(IEnterEffectListener listener) {
        this.mEEListener = listener;
    }

    public interface IEnterEffectListener {
        /**
         * 进入动画开始播放
         */
        void onEEEnterBegin(CommentInfo commentInfo);

        /**
         * 退出动画开始播放
         */
        void onEEExitBegin(CommentInfo commentInfo);
    }
}
