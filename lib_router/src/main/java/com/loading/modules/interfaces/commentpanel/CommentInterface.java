package com.loading.modules.interfaces.commentpanel;

import com.loading.modules.data.MediaEntity;
import com.loading.modules.interfaces.upload.data.UploadPicPojo;
import com.loading.modules.interfaces.upload.data.UploadVideoPojo;

import java.util.ArrayList;

public class CommentInterface {
    public interface CommentPanelListener {
        void onSendComment(String content, UploadPicPojo.UpPicRespData upPicRespData, UploadVideoPojo uploadVideoPojo, Object txtPropItem); //发送文字、图片列表回调

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

    public interface CommentPanelListenerWithMedia extends CommentPanelListener {
        void onUploadMediaBegin();

        void onUploadMediaDone(boolean success, String errorMsg);
    }

    public interface IDraftAccessor {
        void saveDraft(String commentContentStr, ArrayList<MediaEntity> selectedMediaList, Object txtPropItem);

        void clearNoPersistDraft();

        String getCommentContentStr();

        ArrayList<MediaEntity> getSelectedMediaList();
    }

    public interface IDraftAccessorSupplier {
        IDraftAccessor getCommentDraftAccessor();
    }

    public interface IOnPanelSelectedPicChangeListener {
        void onPanelSelectedPicChanged(ArrayList<MediaEntity> pickResults);
    }
}
