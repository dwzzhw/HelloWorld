package com.loading.comp.commentbar.view;

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

import com.loading.common.component.CApplication;
import com.loading.common.utils.CommonUtil;
import com.loading.common.utils.Loger;
import com.loading.comp.commentbar.R;

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
        mNumberTv = findViewById(R.id.number_tv);
    }

    private String getLottieAssetPath() {
        String assetPath = null;
        switch (mAnimationStyle) {
            case ANIMATION_STYLE_HOME:
                break;
            case ANIMATION_STYLE_HOME_NIGHT:
                break;
            case ANIMATION_STYLE_COMMUNITY:
                assetPath = THUMB_UP_COMMUNITY_FILE;
                break;
            case ANIMATION_STYLE_COMMUNITY_NIGHT:
                assetPath = THUMB_UP_COMMUNITY_FILE_NIGHT_STYLE;
                break;
            default:
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
        mStaticSupportView.setVisibility(View.VISIBLE);

        String supportCntStr = getSupportCntStr();
//        Loger.d(TAG, "-->updateSupportState(), isSupported=" + isAlreadySupported() + ", supportCntStr=" + supportCntStr + ", isNightMode=" + isNightMode());
        mNumberTv.setText(supportCntStr);
    }

    private boolean isNightMode() {
        return mAnimationStyle == ANIMATION_STYLE_COMMUNITY_NIGHT || mAnimationStyle == ANIMATION_STYLE_HOME_NIGHT;
    }

    private void onThumbViewClick() {
        if (!isAlreadySupported() && onSupportBtnClicked()) {
            if (!isAlreadySupported()) {
                //Mock support success
                isSupported = true;
                mSupportCnt++;
            }
            loadLottieRes();
        }
    }

    private void loadLottieRes() {
    }

    public void fillDataToView(boolean hasSupported, long supportCnt) {
        Loger.d(TAG, "-->fillDataToView(), hasSupported=" + hasSupported + ", supportCnt=" + supportCnt);
        isSupported = hasSupported;
        mSupportCnt = supportCnt;
        updateSupportState();
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
