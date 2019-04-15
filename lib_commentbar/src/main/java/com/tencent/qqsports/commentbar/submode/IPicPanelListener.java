package com.tencent.qqsports.commentbar.submode;

public interface IPicPanelListener {
    void onShowPicSelectDialog();

    void onSelectedPicChanged(int size);

    void beforeJump2PSPage();

    void onPicSelectDialogCancel();
}
