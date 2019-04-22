package com.loading.comp.commentbar;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.loading.common.component.ActivityHelper;
import com.loading.common.component.BaseActivity;
import com.loading.common.component.CApplication;
import com.loading.common.utils.CommonUtil;
import com.loading.common.component.FragmentHelper;
import com.loading.common.utils.Loger;
import com.loading.common.utils.NotchPhoneUtil;
import com.loading.common.utils.SystemUtil;
import com.loading.common.utils.UiThreadUtil;
import com.loading.common.widget.TipsToast;
import com.loading.common.widget.dialog.MDDialogFragment;
import com.loading.comp.commentbar.submode.FacePanelFragment;
import com.loading.comp.commentbar.submode.MultiPicPanelFragment;
import com.loading.comp.commentbar.submode.PanelModeBaseFragment;
import com.loading.comp.commentbar.submode.SinglePicPanelFragment;
import com.loading.comp.commentbar.view.FacePanelPreviewView;
import com.loading.modules.data.MediaEntity;
import com.loading.modules.interfaces.commentpanel.CommentInterface;
import com.loading.modules.interfaces.commentpanel.data.CommentConstants;
import com.loading.modules.interfaces.login.LoginModuleMgr;
import com.loading.modules.interfaces.photoselector.IPSListener;
import com.loading.modules.interfaces.photoselector.PhotoSelectorModuleMgr;
import com.loading.modules.interfaces.upload.IUploadListener;
import com.loading.modules.interfaces.upload.UploadModuleMgr;
import com.loading.modules.interfaces.upload.UploadProgressMonitorListener;
import com.loading.modules.interfaces.upload.data.UploadParams;
import com.loading.modules.interfaces.upload.data.UploadPicPojo;
import com.loading.modules.interfaces.upload.data.UploadVideoPojo;
import com.loading.comp.commentbar.submode.IPicPanelListener;

import java.util.ArrayList;
import java.util.List;

public class CommentPanel extends MDDialogFragment
        implements IPicPanelListener,
        IUploadListener,
        View.OnClickListener,
        IPSListener,
        CommentControlBar.IControlBarListener {
    private static final String TAG = "CommentPanel";
    public static final int THEME_DEFAULT = 0;
    public static final int THEME_NIGHT = 1;
    public static final String KEY_BAR_MODE = "bar_mode";
    public static final String KEY_INIT_BAR_MODE = "init_bar_mode";
    public static final String KEY_MAX_PIC_CNT = "max_pic_cnt";
    public static final String KEY_MAX_TXT_LENGTH = "max_txt_length";
    public static final String KEY_FULL_SCREEN_MODE = "full_screen";
    public static final String KEY_SINGLE_LINE_CONTROL_BAR_MODE = "single_line_control_bar";
    public static final String KEY_UPLOAD_SOURCE_CHANNEL = "upload_source_channel";

    private static final String TAG_SINGLE_PIC_PANEL = "SINGLE_PIC_PANEL";
    private static SparseArray<String> SUB_PANEL_TAG_MODE_MAP = new SparseArray<>();

    static {
        SUB_PANEL_TAG_MODE_MAP.put(CommentConstants.MODE_EMOJO, "FACE_PANEL");
        SUB_PANEL_TAG_MODE_MAP.put(CommentConstants.MODE_PIC, "MULTI_PIC_PANEL");
    }

    protected CommentControlBar mControlBar;
    protected FrameLayout mPanelContainer = null;
    private View mSubPanelHeightPlaceHolderView;
    protected View mRootView;
    private View mTopDivider;
    private View mMiddleDivider;

    //dwz test
    private ICustomCommentControlBar mCustomControlBar;

    private int mBarMode = 0;
    private int mInitBarMode = 0;
    private int mMaxPicCnt = 0;
    private int mMaxTextLength = 0;
    private boolean isFullScreenMode = false;
    private boolean isSingleLineControlBarMode = false;
    private int mCurrentTheme = THEME_DEFAULT;

    protected int mUploadSourceChannel;
    private int mAutoCompleteMode = CommentConstants.AUTO_COMPLETED_MODE_NONE;
    private String mAutoCompleteTrigger;
    private String mAutoCompleteTarget;
    private String mCurrentHintText = null;
    private Drawable mCurrentHintDrawable = null;

    private boolean mCanSendPicOrVideoWithoutText = true;
    private ContentFilter mContentFilter;

    //外界注入的回调接口
    protected CommentInterface.CommentPanelListener mCommentPanelListener = null;
    private CommentInterface.IOnPanelSelectedPicChangeListener mSelectedPicChangeListener = null;

    private String userTopicStr;
    protected MediaEntity mVideoEntity;
    private CommentInterface.IDraftAccessor mCommentDraftAccessor;
    private boolean needDismissPanelWhenPagePaused = true; //页面不可见时，主动关闭输入面板
    private boolean isFirstCreated = true;

    //TODO 先缓存起来，因为dismiss之后里面的fragment通过遍历的方法有可能已经拿不到了
    private MultiPicPanelFragment mMultiPicPanel = null;
    private SinglePicPanelFragment mSinglePicPanel = null;

    private FacePanelPreviewView mFacePanelPreviewView;

    public static CommentPanel newInstance(int barMode, int maxPicCnt) {
        return newInstance(barMode, maxPicCnt, 0, false, false, CommentConstants.MODE_NONE, UploadParams.UPLOAD_CHANNEL_COMMENT);
    }

    public static CommentPanel newInstance(int barMode, int maxPicCnt, boolean fullScreenMode, int initBarMode) {
        return newInstance(barMode, maxPicCnt, 0, fullScreenMode, false, initBarMode, UploadParams.UPLOAD_CHANNEL_COMMENT);
    }

    public static CommentPanel newInstance(int barMode, int maxPicCnt, int maxTxtLength, boolean fullScreenMode, int initBarMode) {
        return newInstance(barMode, maxPicCnt, maxTxtLength, fullScreenMode, false, initBarMode, UploadParams.UPLOAD_CHANNEL_COMMENT);
    }

    public static CommentPanel newInstance(int barMode, int maxPicCnt, int maxTxtLength, boolean fullScreenMode, boolean singleLineControlBar, int initBarMode, int uploadSourceChannel) {
        CommentPanel panelInstance = new CommentPanel();
        Bundle arguments = new Bundle();
        arguments.putInt(KEY_BAR_MODE, barMode);
        arguments.putInt(KEY_INIT_BAR_MODE, initBarMode);
        arguments.putInt(KEY_MAX_PIC_CNT, maxPicCnt);
        arguments.putInt(KEY_MAX_TXT_LENGTH, maxTxtLength);
        arguments.putBoolean(KEY_FULL_SCREEN_MODE, fullScreenMode);
        arguments.putBoolean(KEY_SINGLE_LINE_CONTROL_BAR_MODE, singleLineControlBar);
        arguments.putInt(KEY_UPLOAD_SOURCE_CHANNEL, uploadSourceChannel);
        panelInstance.setArguments(arguments);
        return panelInstance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Loger.d(TAG, "-->onCreate()");
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.CustomDialog_Bottom);
        Bundle arguments = getArguments();
        if (arguments != null) {
            mBarMode = arguments.getInt(KEY_BAR_MODE, CommentConstants.MODE_NONE);
            mInitBarMode = arguments.getInt(KEY_INIT_BAR_MODE, CommentConstants.MODE_NONE);
            mMaxPicCnt = arguments.getInt(KEY_MAX_PIC_CNT, 0);
            mMaxTextLength = arguments.getInt(KEY_MAX_TXT_LENGTH, 0);
            isFullScreenMode = arguments.getBoolean(KEY_FULL_SCREEN_MODE, false);
            isSingleLineControlBarMode = arguments.getBoolean(KEY_SINGLE_LINE_CONTROL_BAR_MODE, false);
            mUploadSourceChannel = arguments.getInt(KEY_UPLOAD_SOURCE_CHANNEL);
        }

        mDismissOnConfigChange = true;
        isFirstCreated = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Loger.d(TAG, "-->onCreateView(), bar mode=" + mBarMode + ", isFullScreen=" + isFullScreenMode + ", mMaxPicCnt="
                + mMaxPicCnt + ", mMaxTextLength=" + mMaxTextLength + ", isSingleLineControlBarMode=" + isSingleLineControlBarMode + ", source channel=" + mUploadSourceChannel);
        mRootView = inflater.inflate(getLayoutResId(), container, false);
        mControlBar = mRootView.findViewById(R.id.control_bar);
        mPanelContainer = mRootView.findViewById(R.id.panel_container);
        mSubPanelHeightPlaceHolderView = mRootView.findViewById(R.id.sub_panel_height_place_holder);
        if (clickOutsideToDismiss()) {
            mRootView.setOnClickListener(this);
        }
        mTopDivider = mRootView.findViewById(R.id.top_divider);
        mMiddleDivider = mRootView.findViewById(R.id.middle_divider);
        mFacePanelPreviewView = mRootView.findViewById(R.id.face_panel_preview_view);

        initControlBar();
        addSingPicFragment();
        applyTheme();
        return mRootView;
    }

    protected int getLayoutResId() {
        return isFullScreenMode ? R.layout.comment_panel_fullscreen_layout : R.layout.comment_panel_layout;
    }

    protected boolean clickOutsideToDismiss() {
        return true;
    }

    protected void initControlBar() {
        String draftContentStr = mCommentDraftAccessor != null ? mCommentDraftAccessor.getCommentContentStr() : null;
        if (mControlBar != null) {
            mControlBar.initView(getContext(), isSingleLineControlBarMode());
            mControlBar.refreshModeView(mBarMode, mInitBarMode);
            mControlBar.setControlBarListener(this);
            mControlBar.setFinishClickListener(mFinishBtnClickListener);
            mControlBar.setCommentContent(draftContentStr);
            mControlBar.updateHintConfig(mCurrentHintDrawable, mCurrentHintText);
            mControlBar.updateAutoCompleteConfig(mAutoCompleteMode, mAutoCompleteTrigger, mAutoCompleteTarget);
            mControlBar.setMaxTextLength(mMaxTextLength);

            if (supportPic() && mCommentDraftAccessor != null) {
                onSelectedPicChanged(getSelectedMediaCnt());
            }
            NotchPhoneUtil.rectForCutout(getActivity(), mControlBar);
        }

        if (mCustomControlBar != null) {
            mCustomControlBar.initControlBar();
        }

        if (mPanelContainer != null) {
            NotchPhoneUtil.rectForCutout(getActivity(), mPanelContainer);
        }
    }

    private void addSingPicFragment() {
        if (mControlBar.supportSinglePic()) {
            View singlePicContainer = mRootView.findViewById(R.id.single_pic_container);
            if (singlePicContainer != null) {
                ViewGroup.LayoutParams lp = singlePicContainer.getLayoutParams();
                if (lp != null) {
                    int targetWidth = 0;
                    int targetHeight = 0;
                    if (isSingleLineControlBarMode()) {
                        //Not needed at present
                        targetHeight = targetWidth = CApplication.getDimensionPixelSize(R.dimen.comment_bar_edit_text_height_full_screen);
                    } else {
                        targetWidth = CApplication.getDimensionPixelSize(R.dimen.comment_bar_send_btn_width);
                        targetHeight = CApplication.getDimensionPixelSize(R.dimen.comment_bar_edit_text_height);
                    }
                    lp.width = targetWidth;
                    lp.height = targetHeight;
                    singlePicContainer.setLayoutParams(lp);
                }
                singlePicContainer.setVisibility(View.VISIBLE);

                List<MediaEntity> selectedMediaList = getSelectedMediaList();
                mSinglePicPanel = SinglePicPanelFragment.newInstance(supportVideo());
                mSinglePicPanel.setPicPanelListener(this);
                mSinglePicPanel.addSelectedMediaItem(selectedMediaList != null && selectedMediaList.size() > 0 ? selectedMediaList.get(0) : null);
                FragmentHelper.replaceWithoutAnim(getChildFragmentManager(), R.id.single_pic_container, mSinglePicPanel, TAG_SINGLE_PIC_PANEL);
            }
        }
    }

    public void setEditTextImeOptions(int imeOptions) {
        if (mControlBar != null) {
            mControlBar.setEditTextImeOptions(imeOptions);
        }
    }

    private void applyTheme() {
        boolean darkMode = (THEME_NIGHT == mCurrentTheme);
        mPanelContainer.setBackgroundColor(CApplication.getColorFromRes(darkMode ? R.color.comment_bar_night_background : R.color.comment_bar_background));
        if (mControlBar != null) {
            mControlBar.applyTheme(mCurrentTheme);
        }
        if (mTopDivider != null) {
            mTopDivider.setBackgroundColor(CApplication.getColorFromRes(darkMode ? R.color.comment_divider_night : R.color.comment_divider));
        }
        if (mMiddleDivider != null) {
            mMiddleDivider.setBackgroundColor(CApplication.getColorFromRes(darkMode ? R.color.comment_divider_night : R.color.comment_divider));
        }
        if (mCustomControlBar != null) {
            mCustomControlBar.applyTheme();
        }
    }

    public void setTheme(int theme) {
        mCurrentTheme = theme;
    }

    public void updateAutoCompleteConfig(int autoCompleteMode, String autoCompleteTrigger, String autoCompleteTarget) {
        mAutoCompleteMode = autoCompleteMode;
        mAutoCompleteTrigger = autoCompleteTrigger;
        mAutoCompleteTarget = autoCompleteTarget;
        if (mControlBar != null) {
            mControlBar.updateAutoCompleteConfig(autoCompleteMode, autoCompleteTrigger, autoCompleteTarget);
        }
    }

    public void setHintInfo(Drawable hintDrawable, String hintText) {
        mCurrentHintText = hintText;
        mCurrentHintDrawable = hintDrawable;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Loger.d(TAG, "-->onActivityCreated(), isCancelable=" + isCancelable());
        //在onCreateDialog中设置dialog width的match_parent属性时，会因为时机问题被系统再次覆盖掉，故推迟到setContentView之后再设定dialog样式
        initDialog();
    }

    private void initDialog() {
        Dialog dialog = getDialog();
        Window w = dialog != null ? dialog.getWindow() : null;
        WindowManager.LayoutParams lp = w != null ? w.getAttributes() : null;
        if (lp != null) {
//            lp.dimAmount = 0.3f;
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
//            lp.gravity = Gravity.BOTTOM;
            lp.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE;

            dialog.onWindowAttributesChanged(lp);
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(true);

            //dwz test
//            if (getActivity() instanceof AbsActivity && ((AbsActivity) getActivity()).isFullScreen()) {
//                Loger.d(TAG, "-->init comment panel dialog, container Activity in full screen mode");
//                w.clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
//                w.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//            }
        }
    }

    @Override
    public void onPause() {
        Loger.d(TAG, "onPause(), needDismissPanelWhenPagePaused=" + needDismissPanelWhenPagePaused + ", shouldDismissPanelWhenPagePaused=" + shouldDismissPanelWhenPagePaused());
        super.onPause();
        if (shouldDismissPanelWhenPagePaused() && needDismissPanelWhenPagePaused) {
            dismissPanel();
        }
        UiThreadUtil.removeRunnable(mResumeAutoDismissPanelFunctionRunnable);
        UiThreadUtil.removeRunnable(mTryResumeKeyboardRunnable);
    }

    @Override
    public void onResume() {
        Loger.d(TAG, "-->onResume(), needDismissPanelWhenPagePaused=" + needDismissPanelWhenPagePaused);
        super.onResume();
        UiThreadUtil.postDelay(mResumeAutoDismissPanelFunctionRunnable, 500);
        if (!isFirstCreated) {
            tryResumeAutoDismissedKeyboard(500);
        }
        isFirstCreated = false;
    }

    //面板拉起选图或拍照页面时，不自动关闭评论面板，在用户切后台时，主动关闭评论面板
    //单纯依赖onResume, onPause存在一个问题，即首次拉起Camera时，会有几个请求授权的
    //系统弹窗，由此产生多次连续的onResume/onPause，此处通过延时过滤这种情况
    private Runnable mResumeAutoDismissPanelFunctionRunnable = () -> {
        needDismissPanelWhenPagePaused = true;
    };

    public void tryResumeAutoDismissedKeyboard(long delayInMilSec) {
        if (!isPanelExpanded()) {
            UiThreadUtil.postDelay(mTryResumeKeyboardRunnable, delayInMilSec);
        }
    }

    //切其他页面时会自动收起键盘，带回来后尝试恢复该状态
    private Runnable mTryResumeKeyboardRunnable = () -> {
        if (!isPanelExpanded() && mControlBar != null) {
            Loger.d(TAG, "-->auto show keyboard when resumed");
            mControlBar.showKeyboard();
        }
    };

    protected boolean shouldDismissPanelWhenPagePaused() {
        return true;
    }

    protected BaseActivity getAttachedActivity() {
        return getActivity() instanceof BaseActivity ? (BaseActivity) getActivity() : null;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        PhotoSelectorModuleMgr.addPSListener(this);
    }

    @Override
    public void onDestroyView() {
        Loger.d(TAG, "-->onDestroyView()");
        super.onDestroyView();
        PhotoSelectorModuleMgr.removePSListener(this);

        UploadModuleMgr.cancelUpload(null);
    }

    /**
     * 打开或关闭详情展示面板
     */
    @Override
    public void toggleDetailPanel(int panelMode, boolean toShow) {
        Loger.d(TAG, "-->toggleDetailPanel(), panelMode=" + panelMode + ", toShow=" + toShow);
        if (!supportPanelMode(panelMode)
                || getContext() == null
                || (getContext() instanceof Activity && ActivityHelper.isActivityFinished((Activity) getContext()))) {
            return;
        }
        PanelModeBaseFragment targetPanel = getModeDetailPanel(panelMode, toShow);
        if (targetPanel != null) {
            if (toShow) {
                for (int i = 0; i < SUB_PANEL_TAG_MODE_MAP.size(); i++) {
                    String tag = SUB_PANEL_TAG_MODE_MAP.valueAt(i);
                    Fragment subFragment = getChildFragmentManager().findFragmentByTag(tag);
                    if (subFragment instanceof PanelModeBaseFragment) {
                        if (subFragment == targetPanel) {
                            targetPanel.setTargetHeight(mControlBar.getIMEHeight());
                            targetPanel.show();
                        } else {
                            ((PanelModeBaseFragment) subFragment).hide();
                        }
                    }
                }
                // BUG FIX: 按home键退出，再返回程序，面板和软键盘都弹起
                if (getAttachedWindow() != null) {
                    getAttachedWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                }
            } else {
                targetPanel.hide();
            }
        }
        if (mControlBar != null) {
            mControlBar.updateSwitchBtnStatus(toShow ? panelMode : 0);
        }

        if (mCommentPanelListener instanceof CommentInterface.CommentPanelSubSwitchListener && toShow) {
            ((CommentInterface.CommentPanelSubSwitchListener) mCommentPanelListener).onShowDetailPanel(panelMode);
        }
    }

    protected void hideExpandedPanelAndKeyBoard() {
        Loger.d(TAG, "-->hideExpandedPanelAndKeyBoard()");
        hideAllExpandPanel();
        hideKeyboard();
    }

    @Override
    public void hideAllExpandPanel() {
        toggleDetailPanel(CommentConstants.MODE_EMOJO, false);
        toggleDetailPanel(CommentConstants.MODE_PIC, false);
//        toggleDetailPanel(CommentConstants.MODE_TXT_PROP, false);
    }

    public void hideKeyboard() {
        Loger.d(TAG, "-->hideKeyboard()");
        if (mControlBar != null) {
            mControlBar.hideKeyboard();
        }
    }

    private PanelModeBaseFragment getModeDetailPanel(int panelMode, boolean createIfNotExist) {
        String fragmentTag = SUB_PANEL_TAG_MODE_MAP.get(panelMode);
        Fragment fragment = getChildFragmentManager().findFragmentByTag(fragmentTag);
        PanelModeBaseFragment detailPanel = null;
        if (fragment instanceof PanelModeBaseFragment) {
            detailPanel = (PanelModeBaseFragment) fragment;
        } else if (createIfNotExist) {
            int detailPanelTargetHeight = mControlBar != null ? mControlBar.getIMEHeight() : 0;
            switch (panelMode) {
                case CommentConstants.MODE_EMOJO:
                    FacePanelFragment facePanel = FacePanelFragment.newInstance(detailPanelTargetHeight);
                    FragmentHelper.addWithoutAnim(getChildFragmentManager(), R.id.panel_container, facePanel, fragmentTag);
                    facePanel.setEditText(mPanelContainer, getEditTextView());
                    facePanel.setFinishClickListener(mFinishBtnClickListener);
                    if (mFacePanelPreviewView != null) {
                        //Init preview size
                        mFacePanelPreviewView.setVisibility(View.INVISIBLE);
                    }
                    facePanel.setFaceItemLongPressListener(mFacePanelPreviewView);
                    detailPanel = facePanel;
                    break;
                case CommentConstants.MODE_PIC:
                    ArrayList<MediaEntity> selectedMediaList = getSelectedMediaList();
                    MultiPicPanelFragment picPanel = MultiPicPanelFragment.newInstance(detailPanelTargetHeight);
                    picPanel.setAddPicMaxCnt(mMaxPicCnt);
                    picPanel.setSupportVideo(mControlBar != null && mControlBar.supportVideo());
                    picPanel.setPicPanelListener(this);
                    FragmentHelper.addWithoutAnim(getChildFragmentManager(), R.id.panel_container, picPanel, fragmentTag);
                    picPanel.setEditText(mPanelContainer, getEditTextView());
                    picPanel.setFinishClickListener(mFinishBtnClickListener);
                    picPanel.addSelectedMedia(selectedMediaList);
                    detailPanel = picPanel;
                    mMultiPicPanel = picPanel;
                    break;
            }
            if (detailPanel != null && mControlBar != null) {
                detailPanel.setTargetHeight(detailPanelTargetHeight);
                showSubPanelHeightPlaceHolderView(detailPanelTargetHeight);
            }
            Loger.d(TAG, "-->create detail panel for mode: " + panelMode + ", detailPanel=" + detailPanel);
        }
        return detailPanel;
    }

    /**
     * sub panel container的height是wrap content的，在sub panel的fragment未能初始化之前，面板区域高度跳变带来闪烁，
     * 通过增加一个固定高度的展位View来保持高度的稳定
     *
     * @param targetHeight
     */
    private void showSubPanelHeightPlaceHolderView(int targetHeight) {
        Loger.d(TAG, "-->showSubPanelHeightPlaceHolderView(), targetHeight=" + targetHeight);
        if (mSubPanelHeightPlaceHolderView != null && targetHeight > 0) {
            ViewGroup.LayoutParams lp = mSubPanelHeightPlaceHolderView.getLayoutParams();
            if (lp != null) {
                lp.height = targetHeight;
                mSubPanelHeightPlaceHolderView.setLayoutParams(lp);
                mSubPanelHeightPlaceHolderView.setVisibility(View.VISIBLE);
                mSubPanelHeightPlaceHolderView.postDelayed(() -> {
                    mSubPanelHeightPlaceHolderView.setVisibility(View.GONE);
                }, 200);
            }
        }
    }

    private MultiPicPanelFragment getPicPanel() {
        if (isAdded()) {
            Fragment picPanelFragment = getChildFragmentManager().findFragmentByTag(SUB_PANEL_TAG_MODE_MAP.get(CommentConstants.MODE_PIC));
            return picPanelFragment instanceof MultiPicPanelFragment ? (MultiPicPanelFragment) picPanelFragment : null;
        } else {
            return mMultiPicPanel;
        }
    }

    private FacePanelFragment getFacePanel() {
        FacePanelFragment facePanel = null;
        if (isAdded()) {
            Fragment facePanelFragment = getChildFragmentManager().findFragmentByTag(SUB_PANEL_TAG_MODE_MAP.get(CommentConstants.MODE_EMOJO));
            facePanel = facePanelFragment instanceof FacePanelFragment ? (FacePanelFragment) facePanelFragment : null;
        }
        return facePanel;
    }

    private void showPicPanel() {
        Loger.d(TAG, "-->showPicPanel()");
        toggleDetailPanel(CommentConstants.MODE_PIC, true);
    }

    private boolean supportPanelMode(int mode) {
        return mControlBar != null && mControlBar.supportPanelMode(mode);
    }

    public boolean supportProp() {
        return mControlBar != null && mControlBar.supportProp();
    }

    public boolean supportPic() {
        return mControlBar != null && mControlBar.supportPic();
    }

    public boolean supportVideo() {
        return mControlBar != null && mControlBar.supportVideo();
    }

    private EditText getEditTextView() {
        return mControlBar != null ? mControlBar.getEditText() : null;
    }

    public String getCommentTxtContent() {
        return mControlBar != null ? mControlBar.getCommentContent() : null;
    }

    public String getCommentTxtContentWithAutoSuffix() {
        String commentContent = mControlBar != null ? mControlBar.getCommentContent() : null;
        String autoSuffix = mCustomControlBar != null ? mCustomControlBar.getContentTextSuffix() : null;
        if (!TextUtils.isEmpty(autoSuffix) && commentContent != null && !commentContent.endsWith(autoSuffix)) {
            commentContent += autoSuffix;
        }
        return commentContent;
    }

    public void setCommentDraftAccessor(CommentInterface.IDraftAccessor draftHelper) {
        mCommentDraftAccessor = draftHelper;
    }

    public void setTopicStr(String topicStr) {
        userTopicStr = topicStr;
    }

    private Object getCustomControlBarAttachInfo() {
        return mCustomControlBar != null ? mCustomControlBar.getControlBarContentInfo() : null;
    }

    protected boolean isCanSendVideo() {
        return hasValidVideoEntity();
    }

    /**
     * 发送按钮位于面板外侧时，通过该方法触发发送流程
     *
     * @return true for success, false for failed
     */
    public boolean sendComment() {
        boolean isRealSend = false;
        if (canSendComment()) {
            String content = getCommentTxtContentWithAutoSuffix();

//            //生成本地表情包需要的代码，勿删
//            if ("记录表情包".equals(content)) {
//                FaceManager.getInstance().saveCurrentFacePackageToLocal();
//                return true;
//            }

            List<String> selectedPisList = getSelectedPicPathList();
            Loger.d(TAG, "-->sendComment(), content=" + content);
            boolean contentNotEmpty = mContentFilter != null ? !mContentFilter.isContentEmpty(content) : !TextUtils.isEmpty(content);
            boolean hasSelectPic = selectedPisList != null && selectedPisList.size() > 0;
            if ((contentNotEmpty || mCanSendPicOrVideoWithoutText) && (hasSelectPic || isCanSendVideo())) {
                if (!needUploader()) {
                    isRealSend = true;
                    if (mCommentPanelListener != null) {
                        mCommentPanelListener.onSendComment(content, getBuildImageData(), getBuildVideoData(), getCustomControlBarAttachInfo());
                    }
                } else {
                    BaseActivity baseActivity = getAttachedActivity();
                    UploadProgressMonitorListener uploadProgressMonitorListener = baseActivity instanceof UploadProgressMonitorListener ? (UploadProgressMonitorListener) baseActivity : null;
                    UploadModuleMgr.startUpload(selectedPisList, getVideoEntity(), userTopicStr, this, uploadProgressMonitorListener);
                }
            } else if (isArticleLinkType() || contentNotEmpty) {
                isRealSend = true;
                if (mCommentPanelListener != null) {
                    mCommentPanelListener.onSendComment(content, null, null, getCustomControlBarAttachInfo());
                }
            }
            if (isRealSend) {
                dismissPanel();
            }
        }
        return isRealSend;
    }

    protected boolean canSendComment() {
        return checkLoginStatus();
    }

    protected View.OnClickListener mFinishBtnClickListener = (view) -> {
        Loger.d(TAG, "-->finish button in comment panel is clicked.");
        sendComment();
    };

    @Override
    public void onShowPicSelectDialog() {
        hideKeyboard();
    }

    @Override
    public void onSelectedPicChanged(int size) {
        if (mControlBar != null) {
            mControlBar.setSelectedMediaNumTextView(size);
        }
    }

    @Override
    public void beforeJump2PSPage() {
        Loger.d(TAG, "-->beforeJump2PSPage()");
        hideKeyboard();
        needDismissPanelWhenPagePaused = false;
    }

    @Override
    public void onPicSelectDialogCancel() {
        tryResumeAutoDismissedKeyboard(200);
    }

    public void reset() {
        Loger.d(TAG, "reset() called");
        if (mControlBar != null) {
            mControlBar.reset();
        }
        MultiPicPanelFragment picPanel = getPicPanel();
        if (picPanel != null) {
            picPanel.resetAllViews();
        }
        if (mSinglePicPanel != null) {
            mSinglePicPanel.clearContent();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Loger.i(TAG, "onConfigurationChanged and hideCommentBarPanel ...");
        super.onConfigurationChanged(newConfig);
        dismissPanel();
    }

    private boolean checkLoginStatus() {
        boolean isLogined = true;
        if (!LoginModuleMgr.isLogined()) {
            LoginModuleMgr.startLoginActivity(getContext());
            isLogined = false;
        }
        return isLogined;
    }

    //============================= 对外暴露的控制接口 =============================

    /**
     * 判断输评论面板是否已经弹起
     */
    @Override
    public boolean isPanelExpanded() {
        boolean expanded = false;
        if (isAdded()) {
            for (int i = 0; i < SUB_PANEL_TAG_MODE_MAP.size(); i++) {
                String tag = SUB_PANEL_TAG_MODE_MAP.valueAt(i);
                Fragment subFragment = getChildFragmentManager().findFragmentByTag(tag);
                if (subFragment instanceof PanelModeBaseFragment && ((PanelModeBaseFragment) subFragment).isShowing()) {
                    Loger.d(TAG, "-->panel[" + tag + "] is expanded");
                    expanded = true;
                    break;
                }
            }
        }
        return expanded;
    }

    /**
     * 判断输入法是否已经弹起
     */
    @Override
    public boolean isKeyboardExpanded() {
        Context context = getContext();
        boolean expanded = context instanceof Activity && SystemUtil.isKeyBoardShow((Activity) context);
        Loger.i(TAG, "keyboard expanded: " + expanded);
        return expanded;
    }

    @Override
    public void dismissPanel() {
        Loger.d(TAG, "-->dismissPanel()");
        mRootView.setVisibility(View.GONE);
        hideExpandedPanelAndKeyBoard();
        dismiss();
    }

    @Override
    public Window getAttachedWindow() {
        return getDialog() != null ? getDialog().getWindow() : (getAttachedActivity() != null ? getAttachedActivity().getWindow() : null);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        Loger.d(TAG, "-->onDismiss()");
        if (mCommentDraftAccessor != null) {
            mCommentDraftAccessor.saveDraft(getCommentTxtContent(), getSelectedMediaList(), getCustomControlBarAttachInfo());
        }
        needDismissPanelWhenPagePaused = false;
        super.onDismiss(dialog);
    }

    public void setCanSendPicOrVideoWithoutText(boolean canSendPicOrVideoWithoutText) {
        mCanSendPicOrVideoWithoutText = canSendPicOrVideoWithoutText;
    }

    public void setCommentPanelListener(CommentInterface.CommentPanelListener listener) {
        this.mCommentPanelListener = listener;
    }

    public void setContentFilter(ContentFilter contentFilter) {
        this.mContentFilter = contentFilter;
    }

    public void onCommentResult(boolean success, String errorMsg) {
        Loger.d(TAG, "-->onCommentResult(), success=" + success + ", errorMsg=" + errorMsg);
        if (success) {
            reset();
        } else {
            if (!TextUtils.isEmpty(errorMsg)) {
                TipsToast.getInstance().showTipsError(errorMsg);
            } else {
                TipsToast.getInstance().showTipsError("内容上传失败");
            }
        }
    }

    @Override
    public void onEditTextContentChanged(Editable content) {
        MultiPicPanelFragment picPanel = getPicPanel();
        if (picPanel != null) {
            picPanel.updateFinishBtnEnableStatus();
        }
        FacePanelFragment facePanel = getFacePanel();
        if (facePanel != null) {
            facePanel.updateFinishBtnEnableStatus();
        }
        if (mCustomControlBar != null) {
            mCustomControlBar.onTextContentChanged(content.toString());
        }
    }

    @Override
    public boolean onSendBtnClicked() {
        return sendComment();
    }

    @Override
    public int getSelectedMediaCnt() {
        MultiPicPanelFragment picPanel = getPicPanel();
        int mediaCnt = 0;
        if (picPanel != null) {
            mediaCnt = picPanel.getSelectedMediaCnt();
        } else if (mSinglePicPanel != null) {
            mediaCnt = mSinglePicPanel.getSelectedMediaCnt();
        } else if (mCommentDraftAccessor != null) {
            mediaCnt = CommonUtil.sizeOf(mCommentDraftAccessor.getSelectedMediaList());
        }
        return mediaCnt;
    }

    public ArrayList<MediaEntity> getSelectedMediaList() {
        MultiPicPanelFragment picPanel = getPicPanel();
        ArrayList<MediaEntity> selectedMediaList = null;
        if (picPanel != null) {
            selectedMediaList = picPanel.getSelectedMediaList();
        } else if (mSinglePicPanel != null) {
            selectedMediaList = mSinglePicPanel.getSelectedMediaList();
        } else if (mCommentDraftAccessor != null) {
            selectedMediaList = mCommentDraftAccessor.getSelectedMediaList();
        }
        return selectedMediaList;
    }

    public MediaEntity getSelectedVideoItem() {
        ArrayList<MediaEntity> mediaEntities = getSelectedMediaList();
        MediaEntity mediaEntity = null;
        if (mediaEntities != null && mediaEntities.size() > 0) {
            for (int i = 0; i < mediaEntities.size(); i++) {
                MediaEntity entity = mediaEntities.get(i);
                if (entity != null && entity.isVideo()) {
                    mediaEntity = entity;
                    break;
                }
            }
        }
        return mediaEntity;
    }

    final public ArrayList<String> getSelectedPicPathList() {
        ArrayList<String> paths = null;
        List<MediaEntity> lists = getSelectedMediaList();
        if (!CommonUtil.isEmpty(lists)) {
            int size = lists.size();
            paths = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                if (lists.get(i).isImage()) {
                    paths.add(lists.get(i).getPath());
                }
            }
        }
        return paths;
    }

    public boolean isArticleLinkType() {
        return false;
    }

    private boolean needUploader() {
        List<MediaEntity> lists = getSelectedMediaList();
        if (!CommonUtil.isEmpty(lists) && lists.size() == 1) { // 分享过来的可能是http:路径，并且只有一张图
            MediaEntity mediaEntity = lists.get(0);
            if (mediaEntity != null) {
                if (mediaEntity.getPath() != null) {
                    return mediaEntity.isLocalResource();
                } else if (mediaEntity.getThumbnailsPath() != null) {
                    return mediaEntity.getThumbnailsPathLocal();
                }
            }
        }

        return true; // 默认是需要上传
    }

    private UploadPicPojo.UpPicRespData getBuildImageData() {
        List<MediaEntity> lists = getSelectedMediaList();
        if (!CommonUtil.isEmpty(lists) && lists.size() == 1) {
            MediaEntity mediaEntity = lists.get(0);

            if (mediaEntity != null && mediaEntity.isImage()) {
                List<UploadPicPojo.UpPicInfo> infoList = new ArrayList<>();
                UploadPicPojo.UpPicInfo info = new UploadPicPojo.UpPicInfo();
                info.setUrl(mediaEntity.getPath());
                info.setWidth(mediaEntity.getWidth());
                info.setHeight(mediaEntity.getHeight());
                info.setSize(mediaEntity.getSize());
                info.setType(mediaEntity.getMimeType());
                infoList.add(info);

                UploadPicPojo.UpPicRespData upPicRespData = new UploadPicPojo.UpPicRespData();
                upPicRespData.setPicture(infoList);

                return upPicRespData;
            }
        }

        return null;
    }

    private UploadVideoPojo getBuildVideoData() {
        if (hasValidVideoEntity()) {
            MediaEntity videoEntity = getVideoEntity();
            UploadVideoPojo uploadVideoPojo = new UploadVideoPojo();
            if (videoEntity != null) {
                //local video data
                UploadVideoPojo.UploadVideoLocalData uploadVideoLocalData = new UploadVideoPojo.UploadVideoLocalData();
                uploadVideoLocalData.videoPath = videoEntity.getPath();
                uploadVideoLocalData.picUrl = videoEntity.getThumbnailsPath();
                uploadVideoLocalData.aspect = videoEntity.getVideoAspect();
                uploadVideoLocalData.durationL = videoEntity.getDurationL();
                uploadVideoPojo.setVideoLocalData(uploadVideoLocalData);

                //video resp data
                UploadVideoPojo.UploadVideoRespData uploadVideoRespData = new UploadVideoPojo.UploadVideoRespData();
                uploadVideoRespData.setVid(mVideoEntity.getItemId());
                uploadVideoPojo.setRespData(uploadVideoRespData);
            }
            return uploadVideoPojo;
        }

        return null;
    }

    public MediaEntity getVideoEntity() {
        return mVideoEntity != null ? mVideoEntity : getSelectedVideoItem();
    }

    public void setVideoEntity(MediaEntity mediaEntity) {
        mVideoEntity = mediaEntity;
    }

    public boolean hasValidVideoEntity() {
        MediaEntity videoEntity = getVideoEntity();
        return videoEntity != null && !TextUtils.isEmpty(videoEntity.getPath());
    }

    @Override
    public void onPrepareMediaBegin(int estimatedPrepareTimeInMs) {
        Loger.d(TAG, "-->onPrepareMediaBegin(), estimatedPrepareTimeInMs=" + estimatedPrepareTimeInMs);
    }

    @Override
    public void uploadBegin() {
        if (mCommentPanelListener instanceof CommentInterface.CommentPanelListenerWithMedia) {
            ((CommentInterface.CommentPanelListenerWithMedia) mCommentPanelListener).onUploadMediaBegin();
        } else {
            //dwz test
//            if (getContext() instanceof BaseActivity) {
//                ((BaseActivity) getContext()).showProgressDialog();
//            }
        }
    }

    @Override
    public void uploadEnd(boolean success, String msg, UploadPicPojo.UpPicRespData upPicRespData, UploadVideoPojo uploadVideoPojo) {
        Loger.i(TAG, "uploadEnd, isSuccess: " + success + ", msg: " + msg);
        if (!(mCommentPanelListener instanceof CommentInterface.CommentPanelListenerWithMedia)) {
            if (getContext() instanceof BaseActivity) {
                //dwz test
//                ((BaseActivity) getContext()).dismissProgressDialog();
                if (!success) {
                    TipsToast.getInstance().showTipsText(msg);
                }
            }
        }
        if (success) {
            if (mCommentPanelListener instanceof CommentInterface.CommentPanelListenerWithMedia) {
                ((CommentInterface.CommentPanelListenerWithMedia) mCommentPanelListener).onUploadMediaDone(true, "");
            }
            dismissPanel();
            if (mCommentPanelListener != null) {
                mCommentPanelListener.onSendComment(getCommentTxtContentWithAutoSuffix(), upPicRespData, uploadVideoPojo, getCustomControlBarAttachInfo());
            }
        } else {
            if (mCommentPanelListener instanceof CommentInterface.CommentPanelListenerWithMedia) {
                ((CommentInterface.CommentPanelListenerWithMedia) mCommentPanelListener).onUploadMediaDone(false, msg);
            }
        }
    }

    @Override
    public void onSelectedPhotoChanged(ArrayList<MediaEntity> data) {
        Loger.d(TAG, "onSelectedPicRet, data size: " + (data != null ? data.size() : 0));
        showPicPanel();
        MultiPicPanelFragment picPanel = getPicPanel();
        if (picPanel != null) {
            picPanel.onPhotoSelectDone(data);
        }
        if (mSinglePicPanel != null) {
            mSinglePicPanel.addSelectedMediaItem(data != null && data.size() > 0 ? data.get(0) : null);
        }
        if (mSelectedPicChangeListener != null) {
            mSelectedPicChangeListener.onPanelSelectedPicChanged(data);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.comment_panel_root_view) {
            dismissPanel();
        }
    }

    public void setSelectedPicChangeListener(CommentInterface.IOnPanelSelectedPicChangeListener selectedPicChangeListener) {
        this.mSelectedPicChangeListener = selectedPicChangeListener;
    }

    public interface ContentFilter {
        boolean isContentEmpty(String content);
    }

    public boolean isSingleLineControlBarMode() {
        return isSingleLineControlBarMode || isFullScreenMode;
    }
}
