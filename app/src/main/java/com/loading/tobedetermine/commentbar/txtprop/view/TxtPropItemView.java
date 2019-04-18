package com.loading.tobedetermine.commentbar.txtprop.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.loading.common.component.CApplication;
import com.loading.common.utils.Loger;
import com.loading.common.utils.ViewUtils;
import com.tencent.qqsports.commentbar.R;

public class TxtPropItemView extends FrameLayout {
    private static final String TAG = "TxtPropItemView";
    public static final int STYLE_EE = 1;
    public static final int STYLE_BG = 2;
    public static final int STYLE_FONT = 3;

    public static final int STATE_SELECTED = 1;
    public static final int STATE_NORMAL = 2;
    public static final int STATE_RUNNING_OUT = 3;
    public static final int STATE_LOCKED = 4;

    private RecyclingImageView mContentView;
    private TextView mRedPointView;
    private View mRedPointViewContainer;
    private ImageView mLockView;

    private GradientDrawable mNormalBorderDrawable;        //可用、未选中态
    private GradientDrawable mSelectedBorderDrawable;      //选中态
    private GradientDrawable mUnavailableBorderDrawable;   //锁定态、用完等不可用态
    private int mSelectedBorderWidth = 0;
    private int mDefaultSelectedBorderColor = 0;
    private int mContentViewPadding = 0;
    private int mEEItemRadius = 0;

    private int mCurrentStyle = -1;
    private int mCurrentState = -1;
    private TxtPropItem mPropData;

    private int mRedPointPaddingTB = 0;
    private int mRedPointPaddingLR_1 = 0;
    private int mRedPointPaddingLR_2 = 0;
    private int mRedPointPaddingLR_3 = 0;

    public TxtPropItemView(@NonNull Context context) {
        this(context, null);
    }

    public TxtPropItemView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TxtPropItemView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.txt_prop_item_view, this, true);
        mContentView = (RecyclingImageView) findViewById(R.id.txt_prop_content_view);
        mRedPointView = (TextView) findViewById(R.id.txt_prop_red_point_tips);
        mRedPointViewContainer = findViewById(R.id.txt_prop_red_point_tips_container);
        mLockView = (ImageView) findViewById(R.id.txt_prop_lock_icon);

        mSelectedBorderWidth = CApplication.getDimensionPixelSize(R.dimen.txt_prop_item_view_border_width);
        mDefaultSelectedBorderColor = CApplication.getColorFromRes(R.color.comment_bar_txt_prop_item_border_selected);
        mEEItemRadius = CApplication.getDimensionPixelSize(R.dimen.comment_txt_prop_bar_ee_item_radius);
        mRedPointPaddingTB = CApplication.getDimensionPixelSize(R.dimen.txt_prop_item_view_rp_padding_tb);
        mRedPointPaddingLR_1 = CApplication.getDimensionPixelSize(R.dimen.txt_prop_item_view_rp_padding_lr_1);
        mRedPointPaddingLR_2 = CApplication.getDimensionPixelSize(R.dimen.txt_prop_item_view_rp_padding_lr_2);
        mRedPointPaddingLR_3 = CApplication.getDimensionPixelSize(R.dimen.txt_prop_item_view_rp_padding_lr_3);
    }

    public void updatePropData(TxtPropItem propData, boolean isSelected) {
        if (propData != null) {
            mPropData = propData;
            int uiStyle = -1;
            int selectedBorderColor = mPropData.getSelectedBorderColor();
            switch (mPropData.getType()) {
                case TxtPropItem.TXT_PROP_TYPE_EE:
                    uiStyle = TxtPropItemView.STYLE_EE;
                    break;
                case TxtPropItem.TXT_PROP_TYPE_BG:
                    uiStyle = TxtPropItemView.STYLE_BG;
                    break;
                case TxtPropItem.TXT_PROP_TYPE_COLOR:
                    uiStyle = TxtPropItemView.STYLE_FONT;
                    break;
            }
            updateStyle(uiStyle, selectedBorderColor);

            int propState;
            if (mPropData.isLocked()) {
                propState = TxtPropItemView.STATE_LOCKED;
            } else if (mPropData.isRunningOut()) {
                propState = TxtPropItemView.STATE_RUNNING_OUT;
            } else if (isSelected) {
                propState = TxtPropItemView.STATE_SELECTED;
            } else {
                propState = TxtPropItemView.STATE_NORMAL;
            }
            setCurrentState(propState);

            setContentAlpha(mPropData.isLocked(), mPropData.isRunningOut());
            setAvailPropCnt(mPropData.getNum());
            loadPropIcon(mPropData.getImg(), R.drawable.shape_txt_prop_item_view_place_holder);
        }
    }

    public void updateStyle(int style, int selectedBorderColor) {
        mCurrentStyle = style;
        if (selectedBorderColor == 0) {
            selectedBorderColor = mDefaultSelectedBorderColor;
        }
        //选中态
        mSelectedBorderDrawable = createBorderDrawable(mSelectedBorderDrawable, R.drawable.shape_txt_prop_item_view_selected_border);
        mSelectedBorderDrawable.setStroke(mSelectedBorderWidth, selectedBorderColor);
        if (style == STYLE_EE) {
            ViewUtils.setVisibility(mRedPointViewContainer, View.VISIBLE);
            mContentViewPadding = SystemUtil.dpToPx(3);
            mContentView.setRoundedCornerRadius(mEEItemRadius);
        } else {
            ViewUtils.setVisibility(mRedPointViewContainer, View.GONE);
//            mContentViewPadding = style == STYLE_BG ? SystemUtil.dpToPx(2) : SystemUtil.dpToPx(2);
            mContentViewPadding = SystemUtil.dpToPx(2);
            mContentView.disableRoundCorner();
        }
        //Normal态
        mNormalBorderDrawable = createBorderDrawable(mNormalBorderDrawable, R.drawable.shape_txt_prop_item_view_normal_border);
        //锁定态
        mUnavailableBorderDrawable = createBorderDrawable(mUnavailableBorderDrawable, R.drawable.shape_txt_prop_item_view_locked_border);
        mContentView.setPadding(mContentViewPadding, mContentViewPadding, mContentViewPadding, mContentViewPadding);
    }

    public void setCurrentState(int state) {
        mCurrentState = state;
        GradientDrawable borderDrawable = null;
        switch (state) {
            case STATE_SELECTED:
                borderDrawable = mSelectedBorderDrawable;
                ViewUtils.setVisibility(mLockView, View.GONE);
                break;
            case STATE_LOCKED:
                ViewUtils.setVisibility(mLockView, View.VISIBLE);
                borderDrawable = mUnavailableBorderDrawable;
                break;
            case STATE_RUNNING_OUT:
                ViewUtils.setVisibility(mLockView, View.GONE);
                borderDrawable = mUnavailableBorderDrawable;
                break;
            case STATE_NORMAL:
            default:
                ViewUtils.setVisibility(mLockView, View.GONE);
                borderDrawable = mNormalBorderDrawable;
                break;
        }
        setBackground(borderDrawable);
    }

    /**
     * 进入特效道具，锁定态置灰
     * 颜色、背景道具，用完时置灰
     */
    private void setContentAlpha(boolean isLocked, boolean isRunningOut) {
        Loger.d(TAG, "-->setContentAlpha(), current style=" + mCurrentStyle + ", isLocked=" + isLocked + ", isRunningOut=" + isRunningOut);
        float targetAlpha = 1;
        if (isLocked || isRunningOut) {
            targetAlpha = 0.3f;
        }
        mContentView.setAlpha(targetAlpha);

    }

    public void setAvailPropCnt(int cnt) {
        if (mRedPointViewContainer.getVisibility() == View.VISIBLE) {
            int paddingLR;
            String cntStr;
            if (cnt > 99) {
                cntStr = "99+";
                paddingLR = mRedPointPaddingLR_3;
            } else if (cnt > 9) {
                cntStr = String.valueOf(cnt);
                paddingLR = mRedPointPaddingLR_2;
            } else {
                cntStr = String.valueOf(cnt);
                paddingLR = mRedPointPaddingLR_1;
            }
            mRedPointView.setPadding(paddingLR, mRedPointPaddingTB, paddingLR, mRedPointPaddingTB);
            mRedPointView.setText(cntStr);
            mRedPointView.setBackgroundResource(cnt > 0 ? R.drawable.shape_txt_prop_item_red_point_bg : R.drawable.shape_txt_prop_item_red_point_bg_disable);

            mRedPointView.setVisibility(cnt >= 0 ? View.VISIBLE : View.GONE);
        }
    }

    public void loadPropIcon(String iconUrl, int placeHolderResId) {
        Loger.d(TAG, "-->loadPropIcon(), iconUrl=" + iconUrl + ", placeHolderResId=" + placeHolderResId);
        ImageFetcher.loadImage(mContentView, iconUrl, placeHolderResId);
    }

    private GradientDrawable createBorderDrawable(GradientDrawable formerDrawable, int shapeResId) {
        if (formerDrawable != null) {
            return formerDrawable;
        }
        GradientDrawable borderDrawable = null;
        Drawable drawable = CApplication.getDrawableFromRes(shapeResId);
        if (drawable instanceof GradientDrawable) {
            borderDrawable = (GradientDrawable) drawable;
        }
        return borderDrawable;
    }
}
