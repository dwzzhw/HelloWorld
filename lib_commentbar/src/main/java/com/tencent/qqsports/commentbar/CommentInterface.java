package com.tencent.qqsports.commentbar;

import com.tencent.qqsports.common.pojo.MediaEntity;
import com.tencent.qqsports.servicepojo.UploadPicPojo;
import com.tencent.qqsports.servicepojo.UploadVideoPojo;
import com.tencent.qqsports.servicepojo.prop.TxtPropItem;

import java.util.ArrayList;

public class CommentInterface {
    public interface CommentPanelListener {
        void onSendComment(String content, UploadPicPojo.UpPicRespData upPicRespData, UploadVideoPojo uploadVideoPojo, TxtPropItem txtPropItem); //发送文字、图片列表回调

        void onPanelShow();

        /**
         * 评论面板关闭时，若输入内容非空，则展示草稿，回复目标有效；否则全部重置
         */
        void onPanelHide(boolean needReset);
    }

    /**
     * 面板内容切换回调，如表情与键盘的切换
     */
    public interface CommentPanelSubSwitchListener extends CommentPanelListener {
        void onShowDetailPanel(int panelMode);
    }

    public interface CommentPanelListenerForProp extends CommentPanelListener, CommentPanelSubSwitchListener {
        void onLockTipsClicked(TxtPropItem propItem);

        void onLockTipsShown(TxtPropItem propItem);
    }

    public interface CommentPanelListenerWithMedia extends CommentPanelListener {
        void onUploadMediaBegin();

        void onUploadMediaDone(boolean success, String errorMsg);
    }

    public interface IDraftAccessor {
        void saveDraft(String commentContentStr, ArrayList<MediaEntity> selectedMediaList, TxtPropItem txtPropItem);

        void clearNoPersistDraft();

        String getCommentContentStr();

        ArrayList<MediaEntity> getSelectedMediaList();

        TxtPropItem getLastTxtPropItem();
    }

    public interface IDraftAccessorSupplier {
        IDraftAccessor getCommentDraftAccessor();
    }

    public interface IOnPanelSelectedPicChangeListener {
        void onPanelSelectedPicChanged(ArrayList<MediaEntity> pickResults);
    }
}
