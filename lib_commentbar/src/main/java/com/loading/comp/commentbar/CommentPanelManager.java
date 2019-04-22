package com.loading.comp.commentbar;

import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewStub;

import com.loading.common.utils.Loger;
import com.loading.modules.interfaces.commentpanel.CommentInterface;
import com.loading.modules.interfaces.commentpanel.ICommentPanelService;
import com.loading.modules.interfaces.commentpanel.data.CommentConstants;
import com.loading.comp.commentbar.utils.CommentDraftHelper;

public class CommentPanelManager implements ICommentPanelService {
    private static final String TAG = "CommentPanelManager";

    private CommentInterface.IDraftAccessor mDraftAccessor = null;
    private CommentEntranceBar mCommentEntranceBar;
    private int mPanelMode = -1;

    @Override
    public void initEntranceView(ViewStub entranceViewStub, int panelMode) {
        if (entranceViewStub != null) {
            entranceViewStub.setLayoutResource(R.layout.comment_entrance_bar_view);

            View inflateView = entranceViewStub.inflate();
            if (inflateView instanceof CommentEntranceBar) {
                mCommentEntranceBar = (CommentEntranceBar) inflateView;
            }
            mPanelMode = panelMode;
        }
        initEntranceBar();
    }

    @Override
    public void showCommentPanel(FragmentActivity containerActivity, int panelModel, CommentInterface.CommentPanelListener listener) {
        Loger.d(TAG, "-->showCommentPanel(), panel mode=" + panelModel + ", listener=" + listener);
        if (containerActivity != null) {
            mDraftAccessor = new CommentDraftHelper();
            CommentPanel commentPanel = CommentPanel.newInstance(panelModel, 1, false, CommentConstants.MODE_NONE);
            commentPanel.setCommentPanelListener(listener);
            commentPanel.setCommentDraftAccessor(mDraftAccessor);
            commentPanel.show(containerActivity.getSupportFragmentManager());
        }
    }

    private void initEntranceBar() {
        Loger.d(TAG, "-->initEntranceBar(), entrance bar=" + mCommentEntranceBar + ", panel mode=" + mPanelMode);
        if (mCommentEntranceBar != null && mPanelMode >= 0) {
            mCommentEntranceBar.setBarMode(mPanelMode);
        }
    }
}
