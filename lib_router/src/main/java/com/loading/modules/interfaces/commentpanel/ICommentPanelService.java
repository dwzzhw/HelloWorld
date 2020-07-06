package com.loading.modules.interfaces.commentpanel;

import androidx.fragment.app.FragmentActivity;
import android.view.ViewStub;

import com.loading.modules.IModuleInterface;
import com.loading.modules.annotation.Repeater;

@Repeater
public interface ICommentPanelService extends IModuleInterface {
    void initEntranceView(ViewStub entranceViewStub, int panelMode);

    void showCommentPanel(FragmentActivity containerActivity, int panelModel, CommentInterface.CommentPanelListener listener);
}
