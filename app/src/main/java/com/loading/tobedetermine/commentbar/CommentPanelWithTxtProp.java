package com.loading.tobedetermine.commentbar;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.loading.common.utils.Loger;
import com.loading.deletelater.TxtPropItem;
import com.loading.modules.interfaces.photoselector.PhotoSelectorModuleMgr;
import com.loading.tobedetermine.commentbar.txtprop.CommentTxtPropControlBar;
import com.loading.tobedetermine.commentbar.txtprop.data.TxtPropItemListModel;

public class CommentPanelWithTxtProp {
    private CommentTxtPropControlBar mTxtPropControlBar;
    private TxtPropItemListModel mTxtPropListModel;

    void onCreate() {
        mTxtPropListModel = new TxtPropItemListModel(null, this, false);
        mTxtPropControlBar = null;
//                mRootView.findViewById(R.id.prop_control_bar);
    }

    void initControlBar() {
        if (mTxtPropControlBar != null && supportProp() && !isFullScreenMode) {
            mTxtPropControlBar.setVisibility(View.VISIBLE);
            mTxtPropControlBar.setInitTxtPropInfo(mCommentDraftAccessor != null ? mCommentDraftAccessor.getLastTxtPropItem() : null);
            mTxtPropControlBar.updateCommentContent(draftContentStr);
            mTxtPropControlBar.setTxtPropControlBarListener(this);
        }
    }

    void applyTheme() {
        if (mTxtPropControlBar != null) {
            mTxtPropControlBar.applyTheme(mCurrentTheme);
        }
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        if (supportProp() && !isFullScreenMode) {
            //全屏下暂不支持文字特效，屏蔽数据请求
            mTxtPropListModel.loadData();
        }
    }

    public void onDestroyView() {
        if (mTxtPropControlBar != null) {
            mTxtPropControlBar.onDestroy();
        }
    }

    private TxtPropItem getCurrentTxtProp() {
        return mTxtPropControlBar != null ? mTxtPropControlBar.getSelectedTxtProp() : null;
    }

    void onTextContentChanged(String contentStr){
        if (mTxtPropControlBar != null) {
            mTxtPropControlBar.updateCommentContent(contentStr);
        }

    }

    @Override
    public void onDataComplete(BaseDataModel data, int dataType) {
        Loger.d(TAG, "-->onDataComplete(), model=" + data + ", dataType=" + dataType);
        if (data == mTxtPropListModel && mTxtPropControlBar != null) {
            mTxtPropControlBar.updateTxtPropListData(mTxtPropListModel.getPropBeanList());
        }
    }

    @Override
    public void onDataError(BaseDataModel data, int retCode, String retMsg, int dataType) {
        if (data == mTxtPropListModel && mTxtPropControlBar != null) {
            mTxtPropControlBar.updateTxtPropListData(null);
        }
    }

    @Override
    public void onLockTipsClicked(TxtPropItem propItem) {
        dismissPanel();
        if (mCommentPanelListener instanceof CommentInterface.CommentPanelListenerForProp) {
            ((CommentInterface.CommentPanelListenerForProp) mCommentPanelListener).onLockTipsClicked(propItem);
        }
    }

    @Override
    public void onLockTipsShown(TxtPropItem propItem) {
        if (mCommentPanelListener instanceof CommentInterface.CommentPanelListenerForProp) {
            ((CommentInterface.CommentPanelListenerForProp) mCommentPanelListener).onLockTipsShown(propItem);
        }
    }

    String getContentTextSuffix(){
        return mTxtPropControlBar != null ? mTxtPropControlBar.getAutoSuffixStr() : null;
    }

}
