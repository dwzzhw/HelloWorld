package com.loading.common.utils;

@SuppressWarnings("WeakerAccess")
public abstract class NameRunnable implements Runnable {
    private String name;
    private long addTime;
    private long beginTime;

    public NameRunnable(String name) {
        this.name = name;
    }

    public void setBeginTime(long time) {
        beginTime = time;
    }

    public long getAddTime() {
        return addTime;
    }

    public void setAddTime(long addTime) {
        this.addTime = addTime;
    }

    public long getBeginTime() {
        return beginTime;
    }

    public String getName() {
        return name;
    }
}
