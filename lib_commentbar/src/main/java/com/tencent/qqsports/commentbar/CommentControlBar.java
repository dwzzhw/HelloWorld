package com.tencent.qqsports.commentbar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntRange;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loading.common.component.CApplication;
import com.loading.common.config.SpConfig;
import com.loading.common.utils.CommonUtil;
import com.loading.common.utils.Loger;
import com.loading.common.utils.SystemUtil;
import com.loading.common.utils.UiThreadUtil;
import com.loading.common.utils.ViewUtils;
import com.loading.common.widget.ImageSpanEx;
import com.loading.common.widget.TipsToast;
import com.loading.modules.interfaces.face.FaceModuleMgr;
import com.loading.modules.interfaces.login.LoginModuleMgr;

public class CommentControlBar extends LinearLayout
        implements View.OnClickListener,
        KeyboardPanelInterHelper.IBeforeIMEChangeListener {
    private static final String TAG = "CommentControlBar";

    public static final int AUTO_COMPLETED_MODE_NONE = 0;
    public static final int AUTO_COMPLETED_MODE_AT_SYMBOL = 1;
    private static final String HINT_DRAWABLE_PLACE_HOLDER = "_";
    private static final int WARNING_REMAIN_TXT_LENGTH_THRESHOLD = 20;
    public static final String SP_KEY_IME_HEIGHT = "ime_height";

    private View mContentContainer; //中间带有颜色的部分
    protected EditText mEditText;
    protected ViewGroup mAttachBtnContainer;
    private ViewGroup mModeIconContainer;
    private ImageView mFaceIcon;
    private ImageView mKeyboardIcon;
    private ViewGroup mPicIconContainer;
    private ImageView mPicIcon;
    private TextView mSelectedPicNumView;//标示当前选中图片个数
    private View mSinglePicAreaPlaceHolder;
    private View mFinishBtn;
    private TextView mLimitTxtLengthView;

    private InputMethodManager imm;

    //外部传入的控制参数
    private int mBarMode = 0;
    private int mInitBarMode = CommentConstants.MODE_NONE;
    private int mMaxTextLength = 0;
    private int mMaxLines;
    private String mDefaultTxtHint;  //未设置或没有焦点时展示的内容
    private String mCurrentHintText;
    @Nullable
    private Drawable mHintDrawable;
    @SuppressWarnings("FieldCanBeLocal")
    private SpannableString mHintSpannableString;

    private int mAutoCompleteMode = AUTO_COMPLETED_MODE_NONE;
    private String mAutoCompleteTrigger;
    private String mAutoCompleteTarget;
    private AutoCompletedTextWatcher mAutoCompletedTextWatcher = null;

    private boolean mUserInjectEditTextView = false;

    protected KeyboardPanelInterHelper mKeyboardPanelInterHelper = null;
    //输入法展开高度，用于确定表情面板高度，全局变量，保存输入法高度，避免创建面板时没有值
    private static int mIMEHeightPort = 0;
    private static int mIMEHeightLand = 0;

    private IControlBarListener mControlBarListener;
    protected View.OnClickListener mFinishBtnListener = null;

    private Activity mAttachedActivity;
    private int mLimitTxtLengthValidColor = 0;
    private int mLimitTxtLengthInvalidColor = 0;

    private boolean isSingleLineMode;

    public CommentControlBar(Context context) {
        this(context, null);
    }

    public CommentControlBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CommentControlBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void initView(Context context, boolean singleLineMode) {
        Loger.d(TAG, "-->initView(), singleLineMode=" + singleLineMode);
        isSingleLineMode = singleLineMode;
        imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        mAttachedActivity = SystemUtil.getAttachedActivity(context);
        setOrientation(isSingleLineMode ? LinearLayout.HORIZONTAL : LinearLayout.VERTICAL);
        LayoutInflater.from(context).inflate(getLayoutResId(), this, true);

        mContentContainer = findViewById(R.id.input_content_container);
        mEditText = findViewById(R.id.edit_area);

        mAttachBtnContainer = findViewById(R.id.attach_btn_layout);
        mModeIconContainer = findViewById(R.id.icon_mode_container);
        mFaceIcon = findViewById(R.id.icon_face);
        mKeyboardIcon = findViewById(R.id.icon_keyboard);
        mPicIconContainer = findViewById(R.id.icon_pic_container);
        mPicIcon = findViewById(R.id.icon_pic);
        mSelectedPicNumView = findViewById(R.id.select_pic_num);
        mSinglePicAreaPlaceHolder = findViewById(R.id.single_pic_place_holder);
        mLimitTxtLengthView = findViewById(R.id.limit_txt_length);
        mFinishBtn = findViewById(R.id.send_btn);
        if (mFinishBtn != null) {
            mFinishBtn.setOnClickListener(mFinishBtnListener);
        }

        mKeyboardPanelInterHelper = new KeyboardPanelInterHelper(this, this);
        mDefaultTxtHint = getDefaultTxtHint();

        mLimitTxtLengthValidColor = CApplication.getColorFromRes(R.color.std_black1);
        mLimitTxtLengthInvalidColor = CApplication.getColorFromRes(R.color.red);

        initEditText();
        initViewStateForMode();

        triggerBarModeInitView();
        updateSinglePicPlaceHolderView();
        updateFinishBtnEnableStatus();
    }

//    private void initAttr(Context context, @Nullable AttributeSet attrs) {
//        if (attrs != null) {
//            TypedArray typeArray = context.obtainStyledAttributes(attrs, R.styleable.CommentBar);
//            if (typeArray != null) {
//                try {
//                    isSingleLineMode = typeArray.getBoolean(R.styleable.CommentBar_singleLineControlBarMode, false);
//                } catch (Exception e) {
//                    Loger.e(TAG, "exception: " + e);
//                } finally {
//                    typeArray.recycle();
//                }
//            }
//        }
//    }

    private void triggerBarModeInitView() {
        //Post to wait initMode data ready
        post(() -> {
            Loger.d(TAG, "-->triggerBarModeInitView(), mInitBarMode=" + mInitBarMode);
            if (mInitBarMode == CommentConstants.MODE_EMOJO) {
                toggleFaceIcon();
            } else {
                showKeyboard();
            }
        });
    }

    protected int getLayoutResId() {
        return isSingleLineMode ? R.layout.comment_control_bar_single_line_layout : R.layout.comment_control_bar_layout;
    }

    protected String getDefaultTxtHint() {
        return CApplication.getStringFromRes(R.string.saysth_style3);
    }

    private void initEditText() {
        setEditTextImeOptions(isSingleLineMode ? (EditorInfo.IME_ACTION_SEND | EditorInfo.IME_FLAG_NO_EXTRACT_UI) : EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        updateMaxEditTextFilter(mMaxTextLength);
        updateEditTextMaxLine();
        updateEditTextViewHint(true);

        //Do not auto show keyboard, to avoid flicker when need to show face panel at the very begining
        if (mEditText != null) {
            mEditText.setFocusable(false);
        }
    }

    public void setEditTextImeOptions(int imeOptions) {
        if (mEditText != null) {
            mEditText.setImeOptions(imeOptions);
            mEditText.setRawInputType(InputType.TYPE_CLASS_TEXT);
        }
    }

    public void setMaxTextLength(int length) {
        Loger.d(TAG, "-->setMaxTextLength(), length=" + length + ", former length=" + mMaxTextLength);
        if (mMaxTextLength != length) {
            mMaxTextLength = length;
            updateMaxEditTextFilter(length);
        }
    }

    private void updateMaxEditTextFilter(int maxLength) {
        if (mEditText != null && maxLength > 0) {
            mEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength) {
                @Override
                public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
//                    Loger.i(TAG, "filter, source: " + source + ", start: " + start + ", end: " + end + ", dest: " + dest + ", dstart: " + dstart + ", dend: " + dend);
                    int keep = maxLength - (dest.length() - (dend - dstart));
                    if (keep < (end - start)) {
                        onMaxLengthReached(maxLength);
                    }
                    return super.filter(source, start, end, dest, dstart, dend);
                }
            }
            });
        }
    }

    protected void onMaxLengthReached(int maxLength) {
        TipsToast.getInstance().showTipsText(getResources().getString(R.string.comment_max_size_hint, maxLength));
    }

    public void setMaxTextLines(@IntRange(from = 1) int maxTextLines) {
        if (maxTextLines != mMaxLines) {
            mMaxLines = maxTextLines;
            updateEditTextMaxLine();
        }
    }

    private void updateEditTextMaxLine() {
        if (mEditText != null && mMaxLines >= 1) {
            mEditText.setMaxLines(mMaxLines);
        }
    }

    public void setSelectedMediaNumTextView(int size) {
        Loger.d(TAG, "-->setSelectedMediaNumTextView(), size=" + size + ")");
        if (mSelectedPicNumView != null) {
            if (size <= 0) {
                mSelectedPicNumView.setVisibility(View.GONE);
            } else {
                mSelectedPicNumView.setVisibility(View.VISIBLE);
                mSelectedPicNumView.setText(String.valueOf(size));
            }
        }
        updateFinishBtnEnableStatus();
    }

    public void reset() {
        Loger.d(TAG, "reset() called");
        if (mEditText != null) {
            mEditText.setText("");
            updateEditTextViewHint(true);
            mEditText.clearFocus();
        }
    }

    public void updateAutoCompleteConfig(int autoCompleteMode, String autoCompleteTrigger, String autoCompleteTarget) {
        mAutoCompleteMode = autoCompleteMode;
        mAutoCompleteTrigger = autoCompleteTrigger;
        mAutoCompleteTarget = autoCompleteTarget;
        if (mEditText != null) {
            updateAutoCompleteStatus();
        }
    }

    public void updateHintConfig(Drawable hintDrawable, String hintStr) {
        mHintDrawable = hintDrawable;
        mCurrentHintText = hintStr;
        updateEditTextViewHint(false);
    }

    /**
     * 输入框可由外界注入，如社区发表主贴页
     *
     * @param editText the edit text view to inject
     */
    public void injectCustomEditTextView(EditText editText) {
        if (editText != null) {
            mUserInjectEditTextView = true;
            setViewVisibility(mEditText, false);
            mEditText = editText;
            initListener();
        }
    }

    private void updateSinglePicPlaceHolderView() {
        if (mSinglePicAreaPlaceHolder != null) {
            mSinglePicAreaPlaceHolder.setVisibility(supportSinglePic() ? View.VISIBLE : View.GONE);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initListener() {
        Loger.d(TAG, "-->initListener(), mUserInjectEditTextView: " + mUserInjectEditTextView);
        setClickListener(mPicIcon, this);
        setClickListener(mFaceIcon, this);
        setClickListener(mKeyboardIcon, this);
        if (mEditText != null) {
            mEditText.removeTextChangedListener(mTextWatcher);
            mEditText.addTextChangedListener(mTextWatcher);
            if (!mUserInjectEditTextView && mHintDrawable != null) {
                mEditText.setOnFocusChangeListener(mEditTextFocusListener);
            }

            mEditText.setOnTouchListener((v, event) -> {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    onClickEditTextArea();
                }
                return false;
            });

            mEditText.setOnEditorActionListener((v, actionId, event) -> {
                boolean handled = false;
                Loger.d(TAG, "actionId:" + actionId + " event:" + event);
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    sendComment();
                    handled = true;
                }
                return handled;
            });
//            mEditText.setOnKeyListener((v, keyCode, event) -> {
//                if (keyCode == KeyEvent.KEYCODE_ENTER && KeyEvent.ACTION_DOWN == event.getAction()) {
//                    Loger.d(TAG, "keycode " + keyCode);
//                    return sendComment();
//                }
//                return false;
//            });

            mEditText.removeTextChangedListener(mAutoCompletedTextWatcher);
            updateAutoCompleteStatus();
        }
    }

    private void unInitListener() {
        Loger.d(TAG, "-->unInitListener()");
        if (mEditText != null) {
            //This change listner may cause memory leak
            mEditText.removeTextChangedListener(mTextWatcher);
        }
    }

    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
//            Loger.d(TAG, "onTextChanged: s=" + s + ", start=" + start + ", before=" + before + ", count=" + count);
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            Loger.d(TAG, "beforeTextChanged: s=" + s + ", start=" + start + ", after=" + after + ", count=" + count);
        }

        @Override
        public void afterTextChanged(Editable s) {
            CommonUtil.filterEmptyLineForEditable(s, true);
            onEditTextContentChanged(s);
//            Loger.d(TAG, "afterTextChanged: text " + mEditText.getText());
            updateTextLengthMonitorView();
        }
    };

    private void updateTextLengthMonitorView() {
        int currentLength = mEditText != null && mEditText.getText() != null ? mEditText.getText().length() : 0;
        Loger.d(TAG, "-->updateTextLengthMonitorView(), current length=" + currentLength);
        if (mLimitTxtLengthView != null) {
            if (mMaxTextLength > 0 && mMaxTextLength - currentLength <= WARNING_REMAIN_TXT_LENGTH_THRESHOLD) {
                mLimitTxtLengthView.setVisibility(View.VISIBLE);
                if (mMaxTextLength >= currentLength) {
                    mLimitTxtLengthView.setText(String.valueOf(mMaxTextLength - currentLength));
                    mLimitTxtLengthView.setTextColor(mLimitTxtLengthValidColor);
                } else {
                    mLimitTxtLengthView.setText(String.valueOf(currentLength - mMaxTextLength));
                    mLimitTxtLengthView.setTextColor(mLimitTxtLengthInvalidColor);
                }
            } else {
                mLimitTxtLengthView.setVisibility(View.GONE);
            }
        }
    }

    public void applyTheme(int theme) {
        boolean darkMode = (CommentConstants.THEME_NIGHT == theme);
        setBackgroundColor(CApplication.getColorFromRes(darkMode ? R.color.comment_bar_night_background : R.color.comment_bar_background));
        if (mEditText != null) {
            mEditText.setHintTextColor(CApplication.getColorFromRes(darkMode ? R.color.comment_bar_night_hint_color : R.color.comment_bar_hint_color));
            mEditText.setTextColor(CApplication.getColorFromRes(darkMode ? R.color.comment_bar_night_text_color : R.color.comment_bar_text_color));
        }
        if (mContentContainer != null) {
            mContentContainer.setBackgroundResource(darkMode ? R.drawable.shape_comment_entrance_bar_night : R.drawable.shape_comment_entrance_bar);
        }
        if (mFaceIcon != null) {
            mFaceIcon.setImageResource(darkMode ? R.drawable.public_icon_emoji_gray : R.drawable.public_icon_emoji);
        }

        if (mKeyboardIcon != null) {
            mKeyboardIcon.setImageResource(darkMode ? R.drawable.public_icon_keyboard_gray : R.drawable.public_icon_keyboard);
        }
    }

    private void updateEditTextViewHint(boolean forceShowDefaultHint) {
        if (mEditText != null) {
            String hintStr = !forceShowDefaultHint && !TextUtils.isEmpty(mCurrentHintText) ? mCurrentHintText : mDefaultTxtHint;
            if (!mEditText.hasFocus() && mHintDrawable != null) {
                //NOTE: It seems spannable str only takes effect when attached to window for bugfix
                if (ViewCompat.isAttachedToWindow(this)) {
                    mHintSpannableString = new SpannableString(HINT_DRAWABLE_PLACE_HOLDER + hintStr);
                    mHintDrawable.setBounds(0, 0, mHintDrawable.getIntrinsicWidth(), mHintDrawable.getIntrinsicHeight());
                    ImageSpan tImageSpan = new ImageSpanEx(mHintDrawable, ImageSpanEx.ALIGN_CENTER);
                    mHintSpannableString.setSpan(tImageSpan, 0, HINT_DRAWABLE_PLACE_HOLDER.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    mEditText.setHint(mHintSpannableString);
                } else {
                    mEditText.setHint(hintStr);
                }
            } else {
                mEditText.setHint(hintStr);
            }
        }
    }

    public void refreshModeView(int mode, int initMode) {
        Loger.d(TAG, "-->refreshModeView, barMode = " + mBarMode + ", initMode=" + initMode);
        if (mBarMode != mode) {
            this.mBarMode = mode;
            initViewStateForMode();
        }

        if (initMode != mInitBarMode) {
            mInitBarMode = initMode;
        }
    }

    private void initViewStateForMode() {
        setViewVisibility(mFaceIcon, supportEmojo());
        setViewVisibility(mKeyboardIcon, false);
        ViewUtils.showSelfIfHasChildVisible(mModeIconContainer);

        setViewVisibility(mPicIconContainer, supportPic());
        ViewUtils.showSelfIfHasChildVisible(mAttachBtnContainer);
        updateSinglePicPlaceHolderView();
    }

    public void updateSwitchBtnStatus(int currentShowingPanelMode) {
        if (mFaceIcon != null) {
            mFaceIcon.setSelected(currentShowingPanelMode == CommentConstants.MODE_EMOJO);
        }
        if (mPicIcon != null) {
            mPicIcon.setSelected(currentShowingPanelMode == CommentConstants.MODE_PIC);
        }
        switchFaceOrKeyboardIcon(currentShowingPanelMode != CommentConstants.MODE_EMOJO);
    }

    public void setControlBarListener(IControlBarListener controlBarListener) {
        this.mControlBarListener = controlBarListener;
    }

    public void setFinishClickListener(View.OnClickListener clickListener) {
        mFinishBtnListener = clickListener;
        if (mFinishBtn != null) {
            mFinishBtn.setOnClickListener(clickListener);
        }
    }

    public void updateFinishBtnEnableStatus() {
        if (mFinishBtn != null) {
            boolean hasSelectedMedia = mControlBarListener != null ? mControlBarListener.getSelectedMediaCnt() > 0 : false;
            boolean isCommentContentExist = !TextUtils.isEmpty(getCommentContent());

            Loger.d(TAG, "-->updateFinishBtnEnableStatus(), hasSelectedMedia=" + hasSelectedMedia + ", isCommentContentExist=" + isCommentContentExist);
            mFinishBtn.setEnabled(hasSelectedMedia || isCommentContentExist);
        }
    }

    public void setCommentContent(String commentContentStr) {
        if (mEditText != null && !TextUtils.isEmpty(commentContentStr)) {
            SpannableStringBuilder contentBuilder = FaceModuleMgr.convertToSpannableStr(commentContentStr, mEditText);
            mEditText.setText(contentBuilder);

            Editable text = mEditText.getText();
            if (text != null) {
                int length = text.length();
                mEditText.setSelection(length, length);
            }
            updateFinishBtnEnableStatus();
        }
    }

    public String getCommentContent() {
        return mEditText != null ? mEditText.getText().toString() : null;
    }

    private CharSequence ellipsizeText(CharSequence orig) {
        return mEditText != null ? TextUtils.ellipsize(orig,
                mEditText.getPaint(),
                getEditTextWidth(),
                TextUtils.TruncateAt.END) : orig;
    }

    public EditText getEditText() {
        return mEditText;
    }

    private float getEditTextWidth() {
        int width = mEditText != null ? mEditText.getWidth() : 0;
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
            if (supportVideo() && !mUserInjectEditTextView) {
                iconTotalWidth += iconWidth;
            }
            width = SystemUtil.getScreenWidthIntPx() - iconTotalWidth - totalPaddingAndMargin;
        }
        return width;
    }

    public boolean supportPanelMode(int mode) {
        return (mBarMode & mode) != 0;
    }

    public boolean supportPic() {
        return (mBarMode & CommentConstants.MODE_PIC) != 0;
    }

    public boolean supportSinglePic() {
        return (mBarMode & CommentConstants.MODE_SINGLE_PIC) != 0;
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

    public boolean supportProp() {
        return (mBarMode & CommentConstants.MODE_TXT_PROP) != 0;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
//        updateEditTextViewHint(true);
        initListener();
        if (mKeyboardPanelInterHelper != null) {
            mKeyboardPanelInterHelper.onAttachToWindow();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        unInitListener();
        if (mKeyboardPanelInterHelper != null) {
            mKeyboardPanelInterHelper.onDetachFromWindow();
        }
    }

    private Window getAttachedWindow() {
        return mControlBarListener != null ? mControlBarListener.getAttachedWindow() : null;
    }

    private void setClickListener(View view, OnClickListener clickListener) {
        if (view != null) {
            view.setOnClickListener(clickListener);
        }
    }

    private void updateWindowSoftInputMode(int targetMode) {
        Window window = getAttachedWindow();
        if (window != null && targetMode > 0) {
            Loger.d(TAG, "-->updateWindowSoftInputMode(), targetMode=" + targetMode);
            window.setSoftInputMode(targetMode);
        }
    }

    public void showKeyboard() {
        Loger.d(TAG, "-->showKeyboard()");
        if (mKeyboardPanelInterHelper != null) {
            mKeyboardPanelInterHelper.doBeforeKeyboardMeasured(() -> {
                updateWindowSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                hideAllExpandPanel();
            });
        }
//        updateWindowSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        if (mEditText != null) {
            editTextRequestFocus();
//            if (!isKeyboardExpanded()) {  //此处需要显式拉起键盘
//                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
//            }
            imm.showSoftInput(mEditText, InputMethodManager.SHOW_FORCED);
        }

        switchFaceOrKeyboardIcon(true);

//        hideExpandPanelInDelay();
    }

    //隐藏软键盘
    public void hideKeyboard() {
        int imeHeight = SystemUtil.getIMEVisibleHeight();
        if (imeHeight > 0) {
            if (SystemUtil.isLandscapeOrientation()) {
                mIMEHeightLand = imeHeight;
            } else {
                mIMEHeightPort = imeHeight;
                SpConfig.setValueToPreferences(SP_KEY_IME_HEIGHT, imeHeight);;
            }
        }
        Window window = getAttachedWindow();
        Loger.d(TAG, "-->hideKeyboard(), window: " + window);
//        if (window != null && mEditText != null && window.getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
        //转屏的情况下，window会重建，因此此处不对当前Window的input mode进行过滤
        if (window != null && mEditText != null) {
            imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
            updateWindowSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        }
    }

    private void hideExpandPanelInDelay() {
        UiThreadUtil.postDelay(new Runnable() {
            @Override
            public void run() {
//                hideAllExpandPanel();
                updateWindowSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            }
        }, 200);
    }

    /**
     * 点击事件本身会呼起键盘, 此处不需要显式拉起
     */
    protected void onClickEditTextArea() {
        Loger.d(TAG, "-->onClickEditTextArea()");
        if (mKeyboardPanelInterHelper != null) {
            mKeyboardPanelInterHelper.doBeforeKeyboardMeasured(() -> {
                updateWindowSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                hideAllExpandPanel();
            });
        }
        editTextRequestFocus();
//        hideExpandPanelInDelay();
    }

    protected void editTextRequestFocus() {
        if (mEditText != null) {
            mEditText.setFocusableInTouchMode(true);
            mEditText.setFocusable(true);
            mEditText.requestFocus();

            setAutoDismissRunnableWhenHideIME();
        }
    }

    private void updateAutoCompleteStatus() {
        if (mAutoCompleteMode == AUTO_COMPLETED_MODE_AT_SYMBOL) {
            enableAutoComplete(mAutoCompleteTrigger, mAutoCompleteTarget);
        } else {
            disableAutoComplete();
        }
    }

    public void enableAutoComplete(String triggerChar, String targetString) {
        if (!TextUtils.isEmpty(triggerChar) && !TextUtils.isEmpty(targetString) &&
                triggerChar.length() == 1 && targetString.startsWith(triggerChar)
                && mEditText != null) {
            mAutoCompletedTextWatcher = new AutoCompletedTextWatcher(mEditText, triggerChar.charAt(0), targetString);
            mAutoCompletedTextWatcher.setAutoCompletedTxtColor(CApplication.getColorFromRes(R.color.grey2));
            mEditText.addTextChangedListener(mAutoCompletedTextWatcher);
            mAutoCompleteMode = AUTO_COMPLETED_MODE_AT_SYMBOL;
        } else if (BuildConfig.DEBUG) {
            throw new IllegalArgumentException("Auto complete string params is illegal!");
        }
    }

    @SuppressWarnings("unused")
    public void disableAutoComplete() {
        if (mEditText != null && mAutoCompletedTextWatcher != null) {
            mAutoCompleteMode = AUTO_COMPLETED_MODE_AT_SYMBOL;
            mEditText.removeTextChangedListener(mAutoCompletedTextWatcher);
        }
    }

    @SuppressWarnings("unused")
    public boolean isAutoCompleteEnable() {
        return mAutoCompleteMode == AUTO_COMPLETED_MODE_AT_SYMBOL &&
                !TextUtils.isEmpty(mAutoCompleteTarget) && !TextUtils.isEmpty(mAutoCompleteTrigger);
    }

    @SuppressWarnings("unused")
    public String getAutoCompleteString() {
        return mAutoCompleteTarget == null ? "" : mAutoCompleteTarget;
    }

    public void show() {
        Loger.d(TAG, "-->show()");
        if (getVisibility() != View.VISIBLE) {
            this.setVisibility(View.VISIBLE);
        }
    }

    public int getIMEHeight() {
        int imeHeight = SystemUtil.isLandscapeOrientation() ? mIMEHeightLand : mIMEHeightPort;
        if (imeHeight <= 0) {
            imeHeight = SpConfig.getValueFromPreferences(SP_KEY_IME_HEIGHT, 0);
        }
        return imeHeight;
    }

    private boolean isPanelExpanded() {
        return mControlBarListener != null && mControlBarListener.isPanelExpanded();
    }

    private boolean isKeyboardExpanded() {
        return mControlBarListener != null && mControlBarListener.isKeyboardExpanded();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.icon_face) {
            toggleFaceIcon();
        } else if (i == R.id.icon_pic) {
            togglePicIcon();
        } else if (i == R.id.icon_keyboard) {
            showKeyboard();
        }
    }

    //call back method list begin
    private void toggleFaceIcon() {
        boolean isSelected = mFaceIcon.isSelected();

        onTogglePanelSwitchBtn(CommentConstants.MODE_EMOJO, !isSelected);
    }

    private void togglePicIcon() {
        Loger.d(TAG, "-->togglePicIcon()");
        boolean isSelected = mPicIcon.isSelected();

        onTogglePanelSwitchBtn(CommentConstants.MODE_PIC, !isSelected);
    }

    private void hideAllExpandPanel() {
        if (mControlBarListener != null) {
            mControlBarListener.hideAllExpandPanel();
        }
    }

    private void onTogglePanelSwitchBtn(int btnMode, final boolean toShow) {
        if (mControlBarListener != null) {
            if (toShow) {
                if (mKeyboardPanelInterHelper != null &&
                        SystemUtil.isKeyBoardShow(mAttachedActivity)) {
                    mKeyboardPanelInterHelper.doBeforeKeyboardMeasured(() -> {
                        notifyToggleDetailPanel(btnMode, true);
                    });
                    updateWindowSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                } else {
                    notifyToggleDetailPanel(btnMode, true);
                }
                hideKeyboard();
            } else {
                showKeyboard();
            }
        }
    }

    @SuppressWarnings("SameParameterValue")
    private void notifyToggleDetailPanel(int btnMode, boolean isToShow) {
        if (mControlBarListener != null) {
            mControlBarListener.toggleDetailPanel(btnMode, isToShow);
        }
    }


    private boolean sendComment() {
        boolean success = false;
        if (mControlBarListener != null) {
            success = mControlBarListener.onSendBtnClicked();
        }
        return success;
    }

    private void onEditTextContentChanged(Editable content) {
        if (mControlBarListener != null) {
            mControlBarListener.onEditTextContentChanged(content);
        }
        updateFinishBtnEnableStatus();
    }

    private void setViewVisibility(View view, boolean visible) {
        ViewUtils.setVisibility(view, visible ? View.VISIBLE : View.GONE);
    }

    //call back method list end

    @Override
    public void beforeIMEChanging(boolean visibleBefore, boolean visibleAfter) {
        if (visibleAfter) {
            setAutoDismissRunnableWhenHideIME();
        }
    }

    /**
     * 键盘隐藏时，是否同时隐藏输入面板
     *
     * @return
     */
    protected boolean needAutoDismissWhenHideIME() {
        return isSingleLineMode;
    }

    private void setAutoDismissRunnableWhenHideIME() {
        if (mKeyboardPanelInterHelper != null && needAutoDismissWhenHideIME()) {
            mKeyboardPanelInterHelper.setDefaultRunnableWhenImeHide(mAutoDismissRunnableWhenHideIME);
        }
    }

    private Runnable mAutoDismissRunnableWhenHideIME = () -> {
        if (!isPanelExpanded()) {
            Loger.d(TAG, "-->autoDismissRunnableWhenHideIME()");
            if (mControlBarListener != null) {
                mControlBarListener.dismissPanel();
            }
        }
    };

    //展示键盘时显示切换表情icon，反之亦然
    private void switchFaceOrKeyboardIcon(boolean showFaceIcon) {
        Loger.d(TAG, "-->switchFaceOrKeyboardIcon(), showFaceIcon=" + showFaceIcon);
        setViewVisibility(mFaceIcon, showFaceIcon && supportEmojo());
        setViewVisibility(mKeyboardIcon, !showFaceIcon);
        ViewUtils.showSelfIfHasChildVisible(mModeIconContainer);
        ViewUtils.showSelfIfHasChildVisible(mAttachBtnContainer);
    }

    private OnFocusChangeListener mEditTextFocusListener = (v, hasFocus) -> {
        Loger.i(TAG, "edit text, hasFocus: " + hasFocus + ", mUserInjectEditTextView: " + mUserInjectEditTextView);
        updateEditTextViewHint(!hasFocus);
    };

    private boolean checkLoginStatus() {
        boolean isLogined = true;
        if (!LoginModuleMgr.isLogined()) {
            LoginModuleMgr.startLoginActivity(getContext());
            isLogined = false;
        }
        return isLogined;
    }

    public interface IControlBarListener {
        void onEditTextContentChanged(Editable content);

        void toggleDetailPanel(int panelMode, boolean toShow);

        void hideAllExpandPanel();

        boolean onSendBtnClicked();

        boolean isPanelExpanded();

        boolean isKeyboardExpanded();

        void dismissPanel();

        Window getAttachedWindow();

        int getSelectedMediaCnt();
    }
}
