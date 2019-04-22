package com.loading.comp.commentbar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loading.common.component.CApplication;
import com.loading.common.utils.Loger;
import com.loading.common.utils.SystemUtil;
import com.loading.common.utils.ViewUtils;
import com.loading.common.widget.ImageSpanEx;
import com.loading.common.widget.TipsToast;
import com.loading.modules.interfaces.commentpanel.CommentInterface;
import com.loading.modules.interfaces.commentpanel.data.CommentConstants;
import com.loading.modules.interfaces.login.LoginModuleMgr;
import com.loading.modules.interfaces.upload.data.UploadParams;
import com.loading.comp.commentbar.utils.CommentDraftHelper;

@SuppressWarnings({"unused", "FieldCanBeLocal"})
public class CommentEntranceBar extends RelativeLayout implements View.OnClickListener {
    private static final String TAG = "CommentEntranceBar";
    private static final String HINT_DRAWABLE_PLACE_HOLDER = "_";
    private TextView mContentTextView;
    private ImageView mFaceIcon;
    private View mContentContainer; //Bar中间带有背景的部分
    private View mEditAreaMaskView;
    private View mTopDividerLine;
    private View mBelowTopLineContainer;

    protected CommentInterface.CommentPanelListener mCommentPanelListener;
    private CommentInterface.IDraftAccessor mDraftAccessor = null;

    private int mAutoCompleteMode = CommentConstants.AUTO_COMPLETED_MODE_NONE;
    private String mAutoCompleteTrigger;
    private String mAutoCompleteTarget;
    private int mBarMode = CommentConstants.MODE_NONE;
    private int mSupportPicCnt = 1;
    private String mCurrentTxtHint = null;   //本次展示有效，如 "回复***"
    private String mDefaultTxtHint = "";

    private int mMaxTextLength = 0;  //允许输入的最长字符数，<=0代表无限制
    protected int mCurrentTheme = 0;
    private int mMaxLines;
    private Drawable mHintDrawable;
    private boolean mUseSingleLineControlBar;
    private int mUploadSourceChannel;

    public CommentEntranceBar(Context context) {
        this(context, null);
    }

    public CommentEntranceBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CommentEntranceBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initAttrAndLayout(context, attrs);
    }

    private void initAttrAndLayout(Context context, AttributeSet attrs) {
        if (attrs != null) {
            @SuppressLint("CustomViewStyleable")
            TypedArray typeArray = context.obtainStyledAttributes(attrs, R.styleable.CommentBar);
            if (typeArray != null) {
                try {
                    mBarMode = typeArray.getInt(R.styleable.CommentBar_comment_bar_mode, CommentConstants.MODE_NONE);
                    mSupportPicCnt = typeArray.getInt(R.styleable.CommentBar_maxPics, 1);
                    mMaxTextLength = typeArray.getInt(R.styleable.CommentBar_maxTextLength, -1);
                    mCurrentTheme = typeArray.getInt(R.styleable.CommentBar_commentTheme, CommentConstants.THEME_DEFAULT);
                    mAutoCompleteMode = typeArray.getInt(R.styleable.CommentBar_autoCompletedMode, CommentConstants.AUTO_COMPLETED_MODE_NONE);
                    mAutoCompleteTrigger = typeArray.getString(R.styleable.CommentBar_autoCompleteTrigger);
                    mAutoCompleteTarget = typeArray.getString(R.styleable.CommentBar_autoCompleteTarget);
                    mMaxLines = typeArray.getInt(R.styleable.CommentBar_maxLines, CommentConstants.DEFAULT_MAX_LINES);
                    //disable hint drawable
//                    mHintDrawable = typeArray.getDrawable(R.styleable.CommentBar_hintDrawable);
                    mDefaultTxtHint = typeArray.getString(R.styleable.CommentBar_defaultHint);
                    mUseSingleLineControlBar = typeArray.getBoolean(R.styleable.CommentBar_singleLineControlBarMode, false);
                    mUploadSourceChannel = typeArray.getInt(R.styleable.CommentBar_upload_source_channel, UploadParams.UPLOAD_CHANNEL_COMMENT);

                    extraAttrs(typeArray);
                } catch (Exception e) {
                    Loger.e(TAG, "exception: " + e);
                } finally {
                    typeArray.recycle();
                }
            }
        }
        if (TextUtils.isEmpty(mDefaultTxtHint)) {
            mDefaultTxtHint = CApplication.getStringFromRes(R.string.saysth);
        }
        Loger.d(TAG, "-->initAttrAndLayout(), mBarMode=" + mBarMode + ", mDefaultTxtHint: " + mDefaultTxtHint + ", theme=" + mCurrentTheme + ", uploadSourceChannel=" + mUploadSourceChannel);
        initView(context);
        applyTheme(mCurrentTheme);
    }

    //子类解析自己感兴趣的参数
    protected void extraAttrs(TypedArray typeArray) {
    }

    protected void initView(Context context) {
        LayoutInflater.from(context).inflate(getLayoutRes(), this, true);
        mContentTextView = findViewById(R.id.comment_content);
        mFaceIcon = findViewById(R.id.comment_face_icon);
        mContentContainer = findViewById(R.id.content_container);
        mEditAreaMaskView = findViewById(R.id.edit_area_mask_view);
        mTopDividerLine = findViewById(R.id.touyingline);
        mBelowTopLineContainer = findViewById(R.id.below_touyingline_container);
        if (mEditAreaMaskView != null) {
            mEditAreaMaskView.setOnClickListener(this);
        } else {
            setOnClickListener(this);
        }
        if (mFaceIcon != null) {
            if (supportEmojo()) {
                mFaceIcon.setVisibility(View.VISIBLE);
                mFaceIcon.setOnClickListener(this);
            } else {
                mFaceIcon.setVisibility(View.GONE);
            }
        }

        if (context instanceof CommentInterface.IDraftAccessorSupplier) {
            mDraftAccessor = ((CommentInterface.IDraftAccessorSupplier) context).getCommentDraftAccessor();
        }
        if (mDraftAccessor == null) {
            mDraftAccessor = new CommentDraftHelper();
        }
    }

    @Override
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        if (mTopDividerLine != null && params != null) {
            ViewGroup.LayoutParams topDividerLP = mTopDividerLine.getLayoutParams();
            if (topDividerLP != null && topDividerLP.height > 0) {
                if (params instanceof MarginLayoutParams) {
                    ((MarginLayoutParams) params).topMargin -= topDividerLP.height;
                } else {
                    Loger.w(TAG, "-->setLayoutParams(), Layout param is not MarginLayoutParams");
                }
            } else {
                Loger.w(TAG, "-->setLayoutParams(), Layout param not ready yet");
            }
        }
        super.setLayoutParams(params);
    }

    protected int getLayoutRes() {
        return R.layout.comment_entrance_bar_layout;
    }

    public void setCommentPanelListener(CommentInterface.CommentPanelListener commentPanelListener) {
        this.mCommentPanelListener = commentPanelListener;
    }

    public void updateAutoCompleteConfig(int autoCompleteMode, String autoCompleteTrigger, String autoCompleteTarget) {
        mAutoCompleteMode = autoCompleteMode;
        mAutoCompleteTrigger = autoCompleteTrigger;
        mAutoCompleteTarget = autoCompleteTarget;
    }

    public void setBarMode(int barMode) {
        mBarMode = barMode;
    }

    public void setSupportPicCnt(int supportPicCnt) {
        this.mSupportPicCnt = supportPicCnt;
    }

    @Override
    public void onClick(View v) {
        if (ViewUtils.isFastDoubleClick()) {
            return;
        }
        if (v.getId() == R.id.comment_face_icon) {
            showCommentPanel(true);
        } else {
            showCommentPanel(false);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    public void showCommentPanel() {
        showCommentPanel(false);
    }

    public void showCommentPanel(boolean showFacePanel) {
        if (LoginModuleMgr.isLogined()) {
            showCommentPanelInternal(showFacePanel);
        } else {
            //dwz test, ignore login module at present
            showCommentPanelInternal(showFacePanel);
            TipsToast.getInstance().showTipsText("allow comment even not login");
//            LoginModuleMgr.startLoginActivity(getContext());
        }
    }

    private void showCommentPanelInternal(boolean showFacePanel) {
        Loger.d(TAG, "-->showCommentPanelInternal(), showFacePanel=" + showFacePanel);
        CommentPanel commentPanel = CommentPanel.newInstance(mBarMode, mSupportPicCnt, mMaxTextLength, false, mUseSingleLineControlBar,
                showFacePanel ? CommentConstants.MODE_EMOJO : CommentConstants.MODE_NONE, mUploadSourceChannel);
        commentPanel.setCommentPanelListener(mCommentPanelListener);
        commentPanel.setCommentDraftAccessor(mDraftAccessor);
        commentPanel.setHintInfo(mHintDrawable, getHintStrForEditView());
        //Comment panel do not need to support night mode at present
//        commentPanel.setTheme(getCurrentTheme());
        commentPanel.updateAutoCompleteConfig(mAutoCompleteMode, mAutoCompleteTrigger, mAutoCompleteTarget);
        if (getContext() instanceof FragmentActivity) {
            commentPanel.show(((FragmentActivity) getContext()).getSupportFragmentManager());
            notifyPanelShow();
            commentPanel.setOnDismissLister(onDismissListener);
        }
    }

    public void setDraftAccessor(CommentInterface.IDraftAccessor draftAccessor) {
        this.mDraftAccessor = draftAccessor;
    }

    public void onCommentResult(boolean success, String errorMsg) {
        Loger.d(TAG, "-->onCommentResult(), success=" + success + ", errorMsg=" + errorMsg);
        if (success) {
            if (mDraftAccessor != null) {
                mDraftAccessor.clearNoPersistDraft();
            }
            reset();
        } else {
            if (!TextUtils.isEmpty(errorMsg)) {
                TipsToast.getInstance().showTipsText(errorMsg);
            } else {
                TipsToast.getInstance().showTipsText("内容上传失败");
            }
        }
    }

    public void reset() {
        mCurrentTxtHint = null;
        if (mContentTextView != null) {
            mContentTextView.setText(null);
            updateEditTextViewHint(false);
        }
    }

    private DialogInterface.OnDismissListener onDismissListener = dialog -> notifyPanelHide();

    private void notifyPanelShow() {
        if (mCommentPanelListener != null) {
            mCommentPanelListener.onPanelShow();
        }
    }

    private void notifyPanelHide() {
        String commentContent = mDraftAccessor != null ? mDraftAccessor.getCommentContentStr() : null;
        boolean needReset = false;  //评论面板关闭时，若输入内容非空，则展示草稿，回复目标有效；否则全部重置
        Loger.d(TAG, "-->notifyPanelHide(), commentContent=" + commentContent + ", default tipsStr=" + mDefaultTxtHint);
        if (TextUtils.isEmpty(commentContent)) {
            reset();
            needReset = true;
        } else {
            //Do not show draft in comment entrance bar, demand from designer
//            FaceManager.getInstance().fillTextWithFace(commentContent, mContentTextView);
        }

        if (mCommentPanelListener != null) {
            mCommentPanelListener.onPanelHide(needReset);
        }
    }

    protected void applyTheme(int theme) {
        mCurrentTheme = theme;
        boolean isDarkMode = isDarkMode();
        if (mBelowTopLineContainer != null) {
            mBelowTopLineContainer.setBackgroundColor(CApplication.getColorFromRes(isDarkMode ? R.color.comment_bar_night_background : R.color.comment_bar_background));
        } else {
            setBackgroundColor(CApplication.getColorFromRes(isDarkMode ? R.color.comment_bar_night_background : R.color.comment_bar_background));
        }
        if (mContentTextView != null) {
//            //This hintTextColor attribute not work well on TextView
            mContentTextView.setHintTextColor(CApplication.getColorFromRes(isDarkMode ? R.color.comment_bar_night_hint_color : R.color.comment_bar_hint_color));
            mContentTextView.setTextColor(CApplication.getColorFromRes(isDarkMode ? R.color.comment_bar_night_text_color : R.color.comment_bar_text_color));
        }

        if (mContentContainer != null) {
            mContentContainer.setBackgroundResource(isDarkMode ? R.drawable.shape_comment_entrance_bar_night : R.drawable.shape_comment_entrance_bar);
        }
        if (mTopDividerLine != null) {
            mTopDividerLine.setBackgroundResource(isDarkMode ? R.drawable.comment_bar_top_divider_bg_night : R.drawable.comment_bar_top_divider_bg);
        }
        if (mFaceIcon != null) {
            mFaceIcon.setImageResource(isDarkMode ? R.drawable.public_icon_emoji_gray : R.drawable.public_icon_emoji);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        updateEditTextViewHint(false);
    }

    protected int getCurrentTheme() {
        return mCurrentTheme;
    }

    protected boolean isDarkMode() {
        return CommentConstants.THEME_NIGHT == mCurrentTheme;
    }

    public void setContentViewHint(String hintStr) {
        mCurrentTxtHint = hintStr;
        updateEditTextViewHint(false);
    }

    public void setContentViewDefaultHint(String defaultHintStr) {
        mDefaultTxtHint = defaultHintStr;
        updateEditTextViewHint(false);
    }

    private void updateEditTextViewHint(@SuppressWarnings("SameParameterValue") boolean isEllipsize) {
        if (mContentTextView != null) {
            String hintStr = getHintStrForEditView();
            if (mHintDrawable != null) {
                //NOTE: It seems spannable str only takes effect when attached to window for bugfix
                if (ViewCompat.isAttachedToWindow(this)) {
                    SpannableString hintSpannableString = new SpannableString(HINT_DRAWABLE_PLACE_HOLDER + hintStr);
                    mHintDrawable.setBounds(0, 0, mHintDrawable.getIntrinsicWidth(), mHintDrawable.getIntrinsicHeight());
                    ImageSpan tImageSpan = new ImageSpanEx(mHintDrawable, ImageSpanEx.ALIGN_CENTER);
                    hintSpannableString.setSpan(tImageSpan, 0, HINT_DRAWABLE_PLACE_HOLDER.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    mContentTextView.setHint(isEllipsize ? ellipsizeText(hintSpannableString) : hintSpannableString);
                }
            } else {
                mContentTextView.setHint(isEllipsize ? ellipsizeText(hintStr) : hintStr);
            }
        }
    }

    private String getHintStrForEditView() {
        return !TextUtils.isEmpty(mCurrentTxtHint) ? mCurrentTxtHint : mDefaultTxtHint;
    }

    private CharSequence ellipsizeText(CharSequence orig) {
        return mContentTextView != null ? TextUtils.ellipsize(orig,
                mContentTextView.getPaint(),
                getEditTextWidth(),
                TextUtils.TruncateAt.END) : orig;
    }

    protected float getEditTextWidth() {
        int width = mContentTextView != null ? mContentTextView.getWidth() : 0;
        if (width <= 0) {
            int totalPaddingAndMargin = SystemUtil.dpToPx(18);
            int iconWidth = SystemUtil.dpToPx(34);
            int iconTotalWidth = 0;
            if (supportPic()) {
                iconTotalWidth += iconWidth;
            }
            if (supportEmojo()) {
                iconTotalWidth += iconWidth;
            }
            if (supportVideo()) {
                iconTotalWidth += iconWidth;
            }
            if (supportVoice()) {
                iconTotalWidth += iconWidth;
            }
            width = SystemUtil.getScreenWidthIntPx() - iconTotalWidth - totalPaddingAndMargin;
        }
        return width;
    }

    public String getCommentTxtContent() {
        return (mContentTextView != null && mContentTextView.getText() != null) ? mContentTextView.getText().toString() : null;
    }

    public void hide() {
        if (getVisibility() != View.GONE) {
            this.setVisibility(View.GONE);
        }
    }

    public void show() {
        Loger.d(TAG, "-->show()");
        if (getVisibility() != View.VISIBLE) {
            this.setVisibility(View.VISIBLE);
        }
    }

    public boolean supportVoice() {
        return false;
    }

    public boolean supportPic() {
        return (mBarMode & CommentConstants.MODE_PIC) != 0;
    }

    public boolean supportVideo() {
        return (mBarMode & CommentConstants.MODE_VIDEO) != 0;
    }

    public boolean supportEmojo() {
        return (mBarMode & CommentConstants.MODE_EMOJO) != 0;
    }

    public boolean supportLabel() {
        return (mBarMode & CommentConstants.MODE_LABEL) != 0;
    }

    public void setSinglePicSupportStatus(boolean supportSinglePic) {
        if (supportSinglePic) {
            mBarMode |= CommentConstants.MODE_SINGLE_PIC;
        } else {
            mBarMode &= ~CommentConstants.MODE_SINGLE_PIC;
        }
        Loger.d(TAG, "-->setSinglePicSupportStatus(), supportSinglePic=" + supportSinglePic + ", mBarMode=" + mBarMode);
    }
}
