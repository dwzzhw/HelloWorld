package com.loading.comp.commentbar.utils;

import android.text.TextUtils;

import com.loading.common.utils.CommonUtil;
import com.loading.modules.data.MediaEntity;
import com.loading.modules.interfaces.commentpanel.data.DraftItem;
import com.loading.modules.interfaces.commentpanel.CommentInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * CommentPanel不保存已经输入的评论信息，交由该类来临时保存
 *
 * @Author loading, 2018.10.02
 */
public class CommentDraftHelper implements CommentInterface.IDraftAccessor {
    private Map<String, DraftItem> mDraftItemMap = new HashMap<>(1);
    private static final String DEFAULT_KEY = "default";

    @Override
    public void saveDraft(String contentStr, ArrayList<MediaEntity> selectedMediaList, Object txtPropItem) {
        saveDraft(DEFAULT_KEY, contentStr, selectedMediaList, txtPropItem);
    }

    /**
     * 清除不需要记录的草稿信息
     * 像文字颜色、背景之类的道具是可以记录下来的
     */
    @Override
    public void clearNoPersistDraft() {
        clearDraft(DEFAULT_KEY);
    }

    public void saveDraft(String key, String contentStr, ArrayList<MediaEntity> selectedMediaList, Object txtPropItem) {
        if (key == null) {
            key = DEFAULT_KEY;
        }
        if (!TextUtils.isEmpty(contentStr) || !CommonUtil.isEmpty(selectedMediaList) || txtPropItem != null) {
            DraftItem existingItem = mDraftItemMap.get(key);
            if (existingItem != null) {
                existingItem.updateContent(contentStr, selectedMediaList, txtPropItem);
            } else {
                existingItem = new DraftItem(contentStr, selectedMediaList, txtPropItem);
                mDraftItemMap.put(key, existingItem);
            }
        } else {
            clearDraft(key);
        }
    }

    public void clearDraft(String key) {
        if (key == null) {
            key = DEFAULT_KEY;
        }
        mDraftItemMap.remove(key);
    }

    @Override
    public String getCommentContentStr() {
        return getCommentContentStr(DEFAULT_KEY);
    }

    public String getCommentContentStr(String key) {
        if (key == null) {
            key = DEFAULT_KEY;
        }
        DraftItem draftItem = mDraftItemMap.get(key);
        return draftItem != null ? draftItem.getContentStr() : null;
    }

    @Override
    public ArrayList<MediaEntity> getSelectedMediaList() {
        return getSelectedMediaList(DEFAULT_KEY);
    }

    public ArrayList<MediaEntity> getSelectedMediaList(String key) {
        if (key == null) {
            key = DEFAULT_KEY;
        }
        DraftItem draftItem = mDraftItemMap.get(key);
        return draftItem != null ? draftItem.getSelectedMediaList() : null;
    }

    public interface IDraftChangeListener {
        void onDraftChanged();
    }
}
