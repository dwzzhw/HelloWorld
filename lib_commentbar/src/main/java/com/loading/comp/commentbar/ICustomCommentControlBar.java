package com.loading.comp.commentbar;

public interface ICustomCommentControlBar {
    void initControlBar();
    void applyTheme();
    Object getControlBarContentInfo();
    void onTextContentChanged(String contentStr);
    String getContentTextSuffix();
}
