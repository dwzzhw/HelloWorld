package com.loading.common.manager;

import java.util.ArrayList;
import java.util.List;

public class ListenerManager<T> {
    private static final int DEFAULT_CAPACITY = 6;
    private List<T> mListenerList;

    synchronized public boolean prependListener(T listener) {
        return addListener(listener, true);
    }

    synchronized public boolean addListener(T listener) {
        return addListener(listener, false);
    }

    synchronized private boolean addListener(T listener, boolean atFront) {
        boolean isSuccess = false;
        if (listener != null) {
            if (mListenerList == null) {
                mListenerList = new ArrayList<>(DEFAULT_CAPACITY);
            }
            if (!mListenerList.contains(listener)) {
                if (atFront) {
                    mListenerList.add(0, listener);
                } else {
                    mListenerList.add(listener);
                }
                isSuccess = true;
            }
        }
        return isSuccess;
    }

    synchronized public boolean removeListener(T listener) {
        boolean success = false;
        if (listener != null && mListenerList != null) {
            success = mListenerList.remove(listener);
        }
        return success;
    }

    synchronized public T getListener(int index) {
        T resultListener = null;
        if (index >= 0 && getListenerSize() > index) {
            resultListener = mListenerList.get(index);
        }
        return resultListener;
    }

    synchronized public int getListenerSize() {
        return mListenerList != null ? mListenerList.size() : 0;
    }

    synchronized public void loopListenerList(ItemIterator<T> itemIterator) {
        int size = mListenerList != null ? mListenerList.size() : 0;
        if (itemIterator != null && size > 0) {
            for (int i = 0; i < size; i++) {
                T listener = mListenerList.get(i);
                if (listener != null) {
                    itemIterator.handleItem(listener);
                }
            }
        }
    }

    public interface ItemIterator<K> {
        void handleItem(K listener);
    }
}
