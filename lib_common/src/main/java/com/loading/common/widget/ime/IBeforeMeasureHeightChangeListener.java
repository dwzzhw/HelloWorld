package com.loading.common.widget.ime;

/**
 * Created by loading on 5/24/17.
 * 在布局的onMeasure之前调用，可避免输入法弹窗导致的页面闪烁
 */

public interface IBeforeMeasureHeightChangeListener {
    void onMeasureHeightChanged(int newHeight, int oldHeight);
}
