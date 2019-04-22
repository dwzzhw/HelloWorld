package com.loading.comp.commentbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.loading.common.component.CApplication;
import com.loading.common.utils.CommonUtil;
import com.loading.common.utils.Loger;
import com.loading.comp.commentbar.view.CommonSupportView;

/**
 * 包含“点赞”，“评论”，“分享”等功能按钮，适用于资讯详情页与帖子详情页
 * Created by loading on 3/29/16.
 */
public class CommentEntranceBarWithOprBtn extends CommentEntranceBar implements View.OnClickListener, CommonSupportView.ICommonSupportViewListener {
    private static final String TAG = "CommentEntranceBarWithOprBtn";
    public static final int COMMENT_BTN_STYLE_SHOW_COMMENT_LIST = 1;
    public static final int COMMENT_BTN_STYLE_BACK_FROM_COMMENT_LIST = 2;

    //Warning: 此处的变量不能=null，因为其会在父类中被初始化，此处显式赋值会覆盖父类的操作
    private View mOperationBtnContainer;
    private CommonSupportView mSupportView;

    private View mCommentBtnContainer;
    private ImageView mCommentBtnIcon;
    private TextView mCommentCntTxt;

    private ImageView mShareBtnIcon;

    private boolean mIncludeSupportBtn;
    private boolean mIncludeCommentBtn;
    private boolean mIncludeShareBtn;

    private int mCommentBtnStyle = COMMENT_BTN_STYLE_SHOW_COMMENT_LIST;

    private CommentBarOperationBtnListener mOperationBtnListener;

    public CommentEntranceBarWithOprBtn(Context context) {
        this(context, null);
    }

    public CommentEntranceBarWithOprBtn(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CommentEntranceBarWithOprBtn(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.comment_bar_layout_with_operate_btn;
    }

    @Override
    protected void extraAttrs(TypedArray typeArray) {
        if (typeArray != null) {
            mIncludeSupportBtn = typeArray.getBoolean(R.styleable.CommentBar_includeSupportBtn, false);
            mIncludeCommentBtn = typeArray.getBoolean(R.styleable.CommentBar_includeCommentNumBtn, true);
            mIncludeShareBtn = typeArray.getBoolean(R.styleable.CommentBar_includeShareBtn, false);
        }
    }

    @Override
    protected void initView(Context context) {
        super.initView(context);
        initSubclassSpecialView();
    }

    private void initSubclassSpecialView() {
        mOperationBtnContainer = findViewById(R.id.opr_btn_layout);
        mSupportView = findViewById(R.id.support_view);

        mCommentBtnContainer = findViewById(R.id.comment_switch_btn_container);
        mCommentBtnIcon = findViewById(R.id.comment_switch_icon);
        mCommentCntTxt = findViewById(R.id.comment_switch_txt);
        mCommentBtnContainer.setOnClickListener(this);

        mShareBtnIcon = findViewById(R.id.share_btn);
        mShareBtnIcon.setOnClickListener(this);

        if (mCommentBtnContainer != null) {
            mCommentBtnContainer.setVisibility(mIncludeCommentBtn ? VISIBLE : GONE);
        }
        if (mShareBtnIcon != null) {
            mShareBtnIcon.setVisibility(mIncludeShareBtn ? VISIBLE : GONE);
        }
        if (mSupportView != null) {
            mSupportView.setVisibility(mIncludeSupportBtn ? VISIBLE : GONE);
            mSupportView.setSupportListener(this);
        }

        Loger.d(TAG, "-->initSubclassSpecialView(), include support btn=" + mIncludeSupportBtn + ", mIncludeCommentBtn=" + mIncludeCommentBtn + ", mIncludeShareBtn=" + mIncludeShareBtn);
    }

    @Override
    protected void applyTheme(int theme) {
        super.applyTheme(theme);
        boolean isDarkMode = isDarkMode();
        if (mCommentCntTxt != null) {
            mCommentCntTxt.setTextColor(CApplication.getColorFromRes(isDarkMode ? R.color.std_grey1 : R.color.black2));
        }

        if (mCommentBtnIcon != null) {
            if (mCommentBtnStyle == COMMENT_BTN_STYLE_BACK_FROM_COMMENT_LIST) {
                mCommentBtnIcon.setImageResource(isDarkMode ? R.drawable.input_icon_text_gray : R.drawable.input_icon_text);
            } else {
                mCommentBtnIcon.setImageResource(isDarkMode ? R.drawable.input_icon_comment_gray : R.drawable.input_icon_comment);
            }
        }
        if (mShareBtnIcon != null) {
            mShareBtnIcon.setImageResource(isDarkMode ? R.drawable.input_icon_share_gray : R.drawable.input_icon_share);
        }

        if (mSupportView != null) {
            mSupportView.updateAnimationStyle(isDarkMode ? CommonSupportView.ANIMATION_STYLE_COMMUNITY_NIGHT : CommonSupportView.ANIMATION_STYLE_COMMUNITY);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.comment_switch_btn_container) {
            if (mOperationBtnListener != null) {
                Loger.d(TAG, "-->comment switch btn is clicked, mSwitchListener=" + mCommentPanelListener);
                mOperationBtnListener.onCommentBarSwitchLabelClicked();
                reset();
            }
        } else if (v.getId() == R.id.share_btn) {
            Loger.d(TAG, "-->share btn in comment panel is clicked, mSwitchListener=" + mCommentPanelListener);
            if (mOperationBtnListener != null) {
                mOperationBtnListener.onCommentBarShareClicked();
            }
        } else {
            super.onClick(v);
        }
    }

    public void setCommentNumber(long commentNumber) {
        if (mCommentCntTxt != null && mCommentBtnContainer != null) {
            if (commentNumber < 1) {
                mCommentCntTxt.setText("");
            } else {
                mCommentCntTxt.setText(CommonUtil.tenTh2wan(commentNumber));
            }
        }
    }

    public void setCommentBtnStyle(int commentBtnStyle) {
        mCommentBtnStyle = commentBtnStyle;
        applyTheme(getCurrentTheme());
        if (mCommentCntTxt != null) {
            mCommentCntTxt.setText("原文");
        }
    }

    public void setSupportNum(boolean hasSupported, long supportCnt) {
        if (mSupportView != null) {
            mSupportView.fillDataToView(hasSupported, supportCnt);
        }
    }

    public void setShareBtnVisibility(boolean visible) {
        Loger.d(TAG, "-->setShareBtnVisibility(), visible=" + visible + ", mIncludeShareBtn=" + mIncludeShareBtn);
        if (mShareBtnIcon != null) {
            mShareBtnIcon.setVisibility(visible && mIncludeShareBtn ? VISIBLE : GONE);
        }
    }

    public void setSupportBtnVisibility(boolean visible) {
        Loger.d(TAG, "-->setSupportBtnVisibility(), visible=" + visible + ", mIncludeSupportBtn=" + mIncludeSupportBtn);
        if (mSupportView != null) {
            mSupportView.setVisibility(visible && mIncludeSupportBtn ? VISIBLE : GONE);
        }
    }

    public void setOperationBtnListener(CommentBarOperationBtnListener operationBtnListener) {
        this.mOperationBtnListener = operationBtnListener;
    }

    @Override
    public boolean onSupportBtnClicked() {
        return mOperationBtnListener != null && mOperationBtnListener.onCommentBarSupportClicked();
    }

    public interface CommentBarOperationBtnListener {
        void onCommentBarSwitchLabelClicked();

        boolean onCommentBarSupportClicked();

        void onCommentBarShareClicked();
    }
}
