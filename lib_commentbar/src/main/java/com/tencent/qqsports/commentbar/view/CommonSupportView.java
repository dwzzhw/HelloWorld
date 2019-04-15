package com.tencent.qqsports.commentbar.view;

import android.animation.Animator;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.tencent.qqsports.commentbar.R;
import com.tencent.qqsports.common.CApplication;
import com.tencent.qqsports.common.LottieAnimConstants;
import com.tencent.qqsports.common.util.CommonUtil;
import com.tencent.qqsports.common.util.LottieHelper;
import com.tencent.qqsports.common.util.SystemUtil;
import com.tencent.qqsports.logger.Loger;

/**
 * Created by loading on 2019/3/20.
 */
public class CommonSupportView extends RelativeLayout implements View.OnClickListener, Animator.AnimatorListener {
    private static final String TAG = "CommonSupportView";
    private static final String THUMB_UP_COMMUNITY_FILE = "lottie_thumbup_community.json"; //社区点赞
    private static final String THUMB_UP_COMMUNITY_FILE_NIGHT_STYLE = "lottie_thumbup_communityblack.json"; //社区点赞_黑色主题

    public static final int ANIMATION_STYLE_HOME = 1;
    public static final int ANIMATION_STYLE_HOME_NIGHT = 2;
    public static final int ANIMATION_STYLE_COMMUNITY = 4;
    public static final int ANIMATION_STYLE_COMMUNITY_NIGHT = 5;

    private Context mContext;
    private LottieAnimationView mLottieSupportView;
    private ImageView mStaticSupportView;
    private TextView mNumberTv;
    private int mAnimationStyle;

    private ICommonSupportViewListener mSupportListener;

    private boolean isSupported = false;
    private long mSupportCnt = 0;

    public CommonSupportView(Context context) {
        this(context, null);
        initView(context);
    }

    public CommonSupportView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CommonSupportView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(context, attrs);
        initView(context);
    }

    private void initAttr(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typeArray = context.obtainStyledAttributes(attrs, R.styleable.CommonSupportView);
            if (typeArray != null) {
                try {
                    mAnimationStyle = typeArray.getInt(R.styleable.CommonSupportView_animation_style, 0);
                } catch (Exception e) {
                    Loger.e(TAG, "exception: " + e);
                } finally {
                    typeArray.recycle();
                }
            }
        }
    }

    private void initView(Context context) {
        mContext = context;
        LayoutInflater.from(getContext()).inflate(R.layout.layout_common_support_view, this, true);
        mStaticSupportView = findViewById(R.id.static_support_icon);
        mLottieSupportView = findViewById(R.id.lottie_support_view);
        mNumberTv = findViewById(R.id.number_tv);
        mLottieSupportView.addAnimatorListener(this);
    }

    private String getLottieAssetPath() {
        String assetPath = null;
        switch (mAnimationStyle) {
            case ANIMATION_STYLE_HOME:
                assetPath = LottieAnimConstants.THUMB_UP_HOME_FILE;
                break;
            case ANIMATION_STYLE_HOME_NIGHT:
                assetPath = LottieAnimConstants.THUMB_UP_HOME_FILE_NIGHT_STYLE;
                break;
            case ANIMATION_STYLE_COMMUNITY:
                assetPath = THUMB_UP_COMMUNITY_FILE;
                break;
            case ANIMATION_STYLE_COMMUNITY_NIGHT:
                assetPath = THUMB_UP_COMMUNITY_FILE_NIGHT_STYLE;
                break;
            default:
                assetPath = LottieAnimConstants.THUMB_UP_HOME_FILE;
        }
        return assetPath;
    }

    private boolean isAlreadySupported() {
        return isSupported;
    }

    private String getSupportCntStr() {
        return mSupportCnt > 0 ? CommonUtil.tenTh2wan(mSupportCnt) : "";
    }

    private boolean onSupportBtnClicked() {
        return mSupportListener == null || mSupportListener.onSupportBtnClicked();
    }

    private void updateSupportState() {
        boolean isNightMode = isNightMode();
        if (isAlreadySupported()) {
            mStaticSupportView.setImageResource(R.drawable.input_icon_liked);
//            mLottieSupportView.setProgress(1.0f);
            setOnClickListener(null);
            mNumberTv.setTextColor(CApplication.getColorFromRes(R.color.std_blue2));
        } else {
//            mLottieSupportView.setProgress(0.0f);
            mStaticSupportView.setImageResource(isNightMode ? R.drawable.input_icon_like_gray : R.drawable.input_icon_like);
            setOnClickListener(this);
            mNumberTv.setTextColor(CApplication.getColorFromRes(isNightMode ? R.color.std_grey1 : R.color.black2));
        }
        mLottieSupportView.setVisibility(View.GONE);
        mStaticSupportView.setVisibility(View.VISIBLE);

        String supportCntStr = getSupportCntStr();
//        Loger.d(TAG, "-->updateSupportState(), isSupported=" + isAlreadySupported() + ", supportCntStr=" + supportCntStr + ", isNightMode=" + isNightMode());
        mNumberTv.setText(supportCntStr);
    }

    private boolean isNightMode() {
        return mAnimationStyle == ANIMATION_STYLE_COMMUNITY_NIGHT || mAnimationStyle == ANIMATION_STYLE_HOME_NIGHT;
    }

    private void onThumbViewClick() {
        if (!isAlreadySupported() && SystemUtil.checkAndTipNetwork() && onSupportBtnClicked()) {
            if (!isAlreadySupported()) {
                //Mock support success
                isSupported = true;
                mSupportCnt++;
            }
            loadLottieRes();
        }
    }

    private void loadLottieRes() {
        LottieHelper.setAnimation(mContext, mLottieSupportView, getLottieAssetPath(), () -> {
            LottieHelper.playLottieAnimation(mLottieSupportView);
        });
    }

    public void fillDataToView(boolean hasSupported, long supportCnt) {
        Loger.d(TAG, "-->fillDataToView(), hasSupported=" + hasSupported + ", supportCnt=" + supportCnt);
        isSupported = hasSupported;
        mSupportCnt = supportCnt;
        if (!mLottieSupportView.isAnimating()) {
            updateSupportState();
        }
    }

    public void updateAnimationStyle(int animationStyle) {
        Loger.d(TAG, "-->updateAnimationStyle(), animationStyle=" + animationStyle);
        mAnimationStyle = animationStyle;
    }

    public void setSupportListener(ICommonSupportViewListener supportListener) {
        mSupportListener = supportListener;
        updateSupportState();
    }

    @Override
    public void onClick(View v) {
        if (v == this) {
            onThumbViewClick();
        }
    }

    @Override
    public void onAnimationStart(Animator animation) {
        Loger.d(TAG, "-->onAnimationStart()");
        mLottieSupportView.setVisibility(View.VISIBLE);
        mStaticSupportView.setVisibility(View.GONE);
    }

    @Override
    public void onAnimationEnd(Animator animation) {
        Loger.d(TAG, "-->onAnimationEnd()");
        updateSupportState();
    }

    @Override
    public void onAnimationCancel(Animator animation) {
        Loger.d(TAG, "-->onAnimationCancel()");
        updateSupportState();
    }

    @Override
    public void onAnimationRepeat(Animator animation) {
        Loger.d(TAG, "-->onAnimationRepeat()");
    }

    public interface ICommonSupportViewListener {
        /**
         * @return true 可以点赞，后续将播放点赞动画
         */
        boolean onSupportBtnClicked();
    }
}
