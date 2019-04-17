package com.loading.common.widget.ime;

/**
 * Created by loading on 5/24/17.
 * 输入法窗口展开、收起回调
 */

public interface InputMethodChangeListener {
    void onInputMethodOpen(int sizeChange);

    void onInputMethodClose(int sizeChange);
}
