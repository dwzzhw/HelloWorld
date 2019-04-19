package com.loading.common.utils;

/**
 * Created by loading on 10/02/2018.
 */

public abstract class TagRunnable<T> implements Runnable {
    private T tagObj;

    protected TagRunnable() {
    }

    protected TagRunnable(T tagObj) {
        this.tagObj = tagObj;
    }

    public T getTagObj() {
        return tagObj;
    }

    public void setTagObj(T tag) {
        this.tagObj = tag;
    }
}
