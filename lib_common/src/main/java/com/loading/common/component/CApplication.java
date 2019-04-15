package com.loading.common.component;

import android.app.Application;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;

import com.loading.common.utils.Loger;

public class CApplication {
    private static final String TAG = "CApplication";
    private static Context mAppContext;
    private static Application mApplication;

    public static void onAppCreate(Application application) {
        if (application == null) {
            throw new RuntimeException("the application context should not be null !!");
        }
        mAppContext = application.getApplicationContext();
        mApplication = application;
        Loger.i(TAG, "mAppContext: " + mAppContext);
    }

    public static Context getAppContext() {
        return mAppContext;
    }

    public static Application getApplication() {
        return mApplication;
    }

    public static Resources getRes() {
        return CApplication.getAppContext().getResources();
    }

    public static int getDimensionPixelSize(int dimenResId) {
        return CApplication.getAppContext().getResources().getDimensionPixelSize(dimenResId);
    }

    public static String getStringFromRes(int strResId) {
        return CApplication.getAppContext().getResources().getString(strResId);
    }

    public String getString(@StringRes int id, Object... formatArgs) {
        return CApplication.getAppContext().getResources().getString(id, formatArgs);
    }

    public static String getStringFromRes(int strResId, Object... formatArgs) {
        return CApplication.getAppContext().getResources().getString(strResId, formatArgs);
    }

    public static int getColorFromRes(@ColorRes int colorResId) {
        return ContextCompat.getColor(CApplication.getAppContext(), colorResId);
    }

    public static ColorStateList getColorStateListFromRes(@ColorRes int colorResId) {
        return ContextCompat.getColorStateList(CApplication.getAppContext(), colorResId);
    }

    public static Object getSysService(String name) {
        return CApplication.getAppContext().getSystemService(name);
    }

    public static Drawable getDrawableFromRes(@DrawableRes int drawableRes) {
        return ContextCompat.getDrawable(CApplication.getAppContext(), drawableRes);
    }
}