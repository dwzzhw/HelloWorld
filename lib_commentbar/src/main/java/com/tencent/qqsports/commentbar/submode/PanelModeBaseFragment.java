package com.tencent.qqsports.commentbar.submode;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.loading.common.component.CApplication;
import com.loading.common.utils.Loger;
import com.loading.common.utils.SystemUtils;
import com.tencent.qqsports.commentbar.CommentBaseFragment;
import com.tencent.qqsports.commentbar.CommentPanel;
import com.tencent.qqsports.commentbar.R;

public abstract class PanelModeBaseFragment extends CommentBaseFragment {
    private static final String TAG = "PanelModeBaseFragment";
    public static final String KEY_TARGET_PANEL_HEIGHT = "target_panel_height";

    protected View mRootView;
    protected EditText mEditText;
    protected Button mFinishBtn;
    protected View.OnClickListener mFinishBtnListener = null;

    protected int mPanelTargetHeight;

    public PanelModeBaseFragment() {
        mPanelTargetHeight = CApplication.getDimensionPixelSize(R.dimen.cmt_panel_height);
    }

    protected abstract int getLayoutRes();

    protected abstract void initView(Context context);

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(getLayoutRes(), container, false);
        initView(getContext());
        show();
        return mRootView;
    }

    public void setEditText(ViewGroup rootLayout, EditText paramEditText) {
        this.mEditText = paramEditText;
    }

    public void setTargetHeight(int panelTargetHeight) {
        Loger.d(TAG, "-->setTargetHeight(), panelTargetHeight=" + panelTargetHeight + ", former height=" + mPanelTargetHeight);
        if (panelTargetHeight > 0) {
            mPanelTargetHeight = panelTargetHeight;
        }
    }

    public void updateFinishBtnEnableStatus() {
        CommentPanel commentPanel = null;
        if (getParentFragment() instanceof CommentPanel) {
            commentPanel = (CommentPanel) getParentFragment();
        }
        if (commentPanel != null && mFinishBtn != null && commentPanel.isSingleLineControlBarMode()) {
            boolean existPic = commentPanel.getSelectedMediaCnt() > 0;
            boolean existTxtContent = mEditText != null && mEditText.getText() != null && !TextUtils.isEmpty(mEditText.getText().toString());
            boolean enableFinishBtn = existPic || existTxtContent;

//            Loger.d(TAG, "-->updateFinishBtnEnableStatus(), existPic=" + existPic + ", existTxtContent=" + existTxtContent);
            mFinishBtn.setVisibility(View.VISIBLE);
            mFinishBtn.setClickable(enableFinishBtn);
            mFinishBtn.setOnClickListener(enableFinishBtn ? mFinishBtnListener : null);
            mFinishBtn.setEnabled(enableFinishBtn);
        }
    }

    public void show() {
        if (mRootView != null) {
            ViewGroup.LayoutParams lp = mRootView.getLayoutParams();
            Loger.d(TAG, "-->show(), mPanelTargetHeight=" + mPanelTargetHeight + ", lp.height=" + (lp != null ? lp.height : "Null"));
            if (lp != null && lp.height != mPanelTargetHeight) {
                lp.height = mPanelTargetHeight;
                mRootView.setLayoutParams(lp);
            }
            if (getContext() instanceof Activity) {
                SystemUtils.hideKeyboard((Activity) getContext());
            }
            mRootView.setVisibility(View.VISIBLE);
        }
    }

    public void hide() {
        if (mRootView.getVisibility() != View.GONE) {
            mRootView.setVisibility(View.GONE);
        }
    }

    public boolean isShowing() {
        return isVisible() && mRootView != null && mRootView.getVisibility() == View.VISIBLE;
    }

    public void setFinishClickListener(View.OnClickListener clickListener) {
        mFinishBtnListener = clickListener;
        if (mFinishBtn != null) {
            mFinishBtn.setOnClickListener(mFinishBtnListener);
        }
    }
}
