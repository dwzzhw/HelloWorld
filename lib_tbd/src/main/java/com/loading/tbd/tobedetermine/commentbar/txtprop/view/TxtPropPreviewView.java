package com.loading.tobedetermine.commentbar.txtprop.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loading.common.component.CApplication;
import com.loading.common.utils.Loger;
import com.loading.common.utils.SystemUtils;
import com.tencent.qqsports.commentbar.BuildConfig;
import com.tencent.qqsports.commentbar.R;
import com.tencent.qqsports.commentbar.anim.IAnimationPlayListener;
import com.tencent.qqsports.commentbar.anim.YoyoPlayer;
import com.tencent.qqsports.face.FaceManager;

/**
 * 背景图保持高度固定，宽度等比拉伸，气泡背景出现在距顶部约0.4H处，以此约束后台下发的背景图
 */
public class TxtPropPreviewView extends RelativeLayout implements View.OnLayoutChangeListener {
    private static final String TAG = "TxtPropPreviewView";
    private static final float PADDING_LEFT_TO_WIDTH_RATIO = 0.14f; // 宽度占比目测值
    private static final float PADDING_RIGHT_TO_WIDTH_RATIO = 0.3f; // 宽度占比目测值
    private RecyclingImageView mUserIcon;
    private TextView mUserName;
    private TextView mCommentContent;

    private YoyoPlayer mYoyoPlayer;
    private CommentInfo mCommentInfo;
    private Drawable mBackgroundDrawable;
    private String mBackgroundResUrl = null;
    private int mTargetAnimationType = -1;

    private boolean isWaitingViewReady = false; //等待View就绪，包括高度、背景图
    private IAnimationPlayListener mAnimationPlayListener = null;
    private float mTargetWHRatio = 0;
    private int mContentPaddingTop = 0;
    private boolean mEnableAutoSuffix;

    public TxtPropPreviewView(Context context) {
        this(context, null);
    }

    public TxtPropPreviewView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TxtPropPreviewView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);

        addOnLayoutChangeListener(this);
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.txt_prop_preview_layout, this, true);
        mUserIcon = (RecyclingImageView) findViewById(R.id.txt_preview_user_icon);
        mUserName = (TextView) findViewById(R.id.txt_preview_user_name);
        mCommentContent = (TextView) findViewById(R.id.txt_preview_content);

        mYoyoPlayer = YoyoPlayer.newInstance();
        mContentPaddingTop = CApplication.getDimensionPixelSize(R.dimen.txt_prop_preview_content_margin_top);
        int paddingLeft = (int) (SystemUtils.getScreenWidthIntPx() * PADDING_LEFT_TO_WIDTH_RATIO);
        int paddingRight = (int) (SystemUtils.getScreenWidthIntPx() * PADDING_RIGHT_TO_WIDTH_RATIO);
        Loger.d(TAG, "-->initView(), paddingTop=" + mContentPaddingTop + ", paddingLeft=" + paddingLeft + ", paddingRight=" + paddingRight);
        setPadding(paddingLeft, mContentPaddingTop, paddingRight, 0);
    }

    public void addAnimationListener(IAnimationPlayListener listener) {
        mAnimationPlayListener = listener;
        mYoyoPlayer.addAnimationListener(listener);
    }

    public void updateContentData(CommentInfo commentInfo) {
        updateContentData(commentInfo, null);
    }

    public void updateContentData(CommentInfo commentInfo, Bitmap bgBitmap) {
        Loger.d(TAG, "-->updateContentData(), commentInfo=" + commentInfo);
        if (commentInfo != null) {
            mCommentInfo = commentInfo;
            CommentUserInfo userInfo = mCommentInfo.userinfo;
            if (userInfo != null) {
                ImageFetcher.loadImage(mUserIcon, userInfo.head);
                mUserName.setText(userInfo.nick);
            }

            if (!TextUtils.equals(mBackgroundResUrl, commentInfo.getTxtPropItemBgResUrl()) && bgBitmap != null && bgBitmap.getHeight() > 0) {
                //ignore duplicated request
                mBackgroundResUrl = commentInfo.getTxtPropItemBgResUrl();
                mTargetWHRatio = 1f * bgBitmap.getWidth() / bgBitmap.getHeight();
                mBackgroundDrawable = new BitmapDrawable(CApplication.getRes(), bgBitmap);
            }

            updateContentView();
        }
    }

    private void updateContentView() {
        if (mCommentInfo != null) {
            String commentContent = mCommentInfo.content;
            if (TextUtils.isEmpty(commentContent)) {
                if (mEnableAutoSuffix && !TextUtils.isEmpty(mCommentInfo.getTxtPropContentSuffix())) {
                    mCommentContent.setText(mCommentInfo.getTxtPropContentSuffix());
                } else {
                    mCommentContent.setText("评论内容预览区");
                }
            } else {
                String autoSuffix = mCommentInfo.getTxtPropContentSuffix();
                if (mEnableAutoSuffix && !TextUtils.isEmpty(autoSuffix) && !TextUtils.isEmpty(commentContent) && !commentContent.endsWith(autoSuffix)) {
                    commentContent += autoSuffix;
                }
                SpannableStringBuilder contentBuilder = FaceManager.getInstance().convertToSpannableStr(commentContent.replace("\n", " "),
                        mCommentContent);
                mCommentContent.setText(contentBuilder);
            }
        }
    }

    public void enableAutoSuffix(boolean enableAutoSuffix) {
        mEnableAutoSuffix = enableAutoSuffix;
    }

    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        Loger.d(TAG, "-->onLayoutChange(), isWaitingViewReady=" + isWaitingViewReady + ", view=" + v + ", this=" + this + ", old height=" + (oldBottom - oldTop) + ", new height=" + (bottom - top));
        if (isWaitingViewReady && bottom - top > 0) {
            playAnimationWhenViewReady();
        }
    }

    public boolean isReadyForNewAnimation() {
        boolean isReady = !mYoyoPlayer.isRunning() && !isWaitingViewReady;
        Loger.d(TAG, "-->isReadyForNewAnimation(), isReady=" + isReady);
        return isReady;
    }

    public void hidePreviewView() {
        setVisibility(View.GONE);
    }

    public void playAnimation(int animationType) {
        Loger.d(TAG, "-->playAnimation(), animationType=" + animationType + ", view height=" + getHeight() + ", x=" + getX() + ", y=" + getY()
                + ", visibility=" + getVisibility());

        if (mYoyoPlayer.isRunning()) {
            mYoyoPlayer.stop();
        }
        if (mBackgroundDrawable == null) {
            if (BuildConfig.DEBUG) {
                TipsToast.getInstance().showTipsText("Background res url is empty.");
            }
            notifyPreviewFail();
            return;
        }
        if (getVisibility() == View.GONE) {
            setVisibility(View.INVISIBLE);
        }

        mTargetAnimationType = animationType;
        playAnimationWhenViewReady();
    }

    private void playAnimationWhenViewReady() {
        if (isViewReadyForAnimation()) {
            isWaitingViewReady = false;
            updateLayoutAndBg();
            updateContentView();
            //Should wait for layout adjust ready
            UiThreadUtil.postRunnable(mPlayAnimationRunnable);
        } else {
            isWaitingViewReady = true;
        }
    }

    public void stopAnimation() {
        if (mYoyoPlayer.isRunning()) {
            mYoyoPlayer.stop();
        }
        isWaitingViewReady = false;
    }

    private boolean isViewReadyForAnimation() {
        return getHeight() > 0 && mBackgroundDrawable != null && mTargetWHRatio > 0;
    }

    private void updateLayoutAndBg() {
        ViewGroup.LayoutParams lp = getLayoutParams();
        if (mBackgroundDrawable != null && mTargetWHRatio > 0 && lp != null) {
            int height = getHeight();
            int targetWidth = Math.min((int) (height * mTargetWHRatio), SystemUtil.getCurrentScreenWidth());
            int paddingLeft = (int) (targetWidth * PADDING_LEFT_TO_WIDTH_RATIO);
            int paddingRight = (int) (targetWidth * PADDING_RIGHT_TO_WIDTH_RATIO);
            if (targetWidth > 0 && targetWidth != getWidth()) {
                setVisibility(View.INVISIBLE);
                lp.width = targetWidth;
                setLayoutParams(lp);
                setPadding(paddingLeft, mContentPaddingTop, paddingRight, 0);
            }
            setBackground(mBackgroundDrawable);
            Loger.d(TAG, "-->updateLayoutAndBg(), height=" + height + ",targetWidth=" + targetWidth + ", paddingLeft=" + paddingLeft + ", paddingRight=" + paddingRight + ", mTargetWHRatio=" + mTargetWHRatio);
        }
    }

    private Runnable mPlayAnimationRunnable = () -> {
        mYoyoPlayer.playAnimation(this, mTargetAnimationType);
    };

    private void notifyPreviewFail() {
        if (mAnimationPlayListener != null) {
            mAnimationPlayListener.onAnimationEnd(null);
        }
    }
}
