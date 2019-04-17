package com.loading.common.widget.ime;

/**
 * Created by loading on 5/24/17.
 */

public interface IMEWindowMonitor {
//    void setInputMethodChangeListener(InputMethodChangeListener mInputMethodChangeListener);
    void addMeasureHeightChangeListener(IBeforeMeasureHeightChangeListener listener);
    void removeMeasureHeightChangeListener(IBeforeMeasureHeightChangeListener listener);
}
