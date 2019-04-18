package com.tencent.qqsports.commentbar;

public interface ICustomCommentControlBar {
    void initControlBar();
    void applyTheme();
    Object getControlBarContentInfo();
    void onTextContentChanged(String contentStr);
    String getContentTextSuffix();
}
