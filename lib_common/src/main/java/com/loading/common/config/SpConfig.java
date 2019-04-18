package com.loading.common.config;

import android.app.Activity;
import android.content.SharedPreferences;

import com.loading.common.component.CApplication;

/**
 * SharedPreference 快速访问帮助类
 */
public class SpConfig {
    private static final String CONFIGURATION_PREF_FILE = "Configuration";
    private static final SharedPreferences sharedPreferences =
            CApplication.getAppContext().getSharedPreferences(CONFIGURATION_PREF_FILE, Activity.MODE_PRIVATE);

    private static SharedPreferences getSpSharedPreference() {
        return sharedPreferences != null ? sharedPreferences :
                CApplication.getAppContext().getSharedPreferences(CONFIGURATION_PREF_FILE,
                        Activity.MODE_PRIVATE);
    }

    public static void setValueToPreferences(String key, boolean value) {
        SharedPreferences sp = getSpSharedPreference();
        if (sp != null) {
            sp.edit().putBoolean(key, value).apply();
        }
    }

    public static boolean getValueFromPreferences(String key, boolean defaultValue) {
        boolean result = defaultValue;
        SharedPreferences sp = getSpSharedPreference();
        if (sp != null) {
            result = sp.getBoolean(key, defaultValue);
        }
        return result;
    }

    public static void setValueToPreferences(String key, String value) {
        SharedPreferences sp = getSpSharedPreference();
        if (sp != null) {
            sp.edit().putString(key, value).apply();
        }
    }

    public static String getValueFromPreferences(String key, String defaultValue) {
        String result = defaultValue;
        SharedPreferences sp = getSpSharedPreference();
        if (sp != null) result = sp.getString(key, defaultValue);
        return result;
    }

    public static void setValueToPreferences(String key, int value) {
        SharedPreferences sp = getSpSharedPreference();
        if (sp != null) {
            sp.edit().putInt(key, value).apply();
        }
    }

    public static void setValueToPreferences(String key, long value) {
        SharedPreferences sp = getSpSharedPreference();
        if (sp != null) {
            sp.edit().putLong(key, value).apply();
        }
    }

    public static int getValueFromPreferences(String key, int defaultValue) {
        int result = defaultValue;
        SharedPreferences sp = getSpSharedPreference();
        if (sp != null) {
            result = sp.getInt(key, defaultValue);
        }
        return result;
    }

    public static long getValueFromPreferences(String key,
                                                @SuppressWarnings("SameParameterValue") long defaultValue) {
        long result = defaultValue;
        SharedPreferences sp = getSpSharedPreference();
        if (sp != null) {
            result = sp.getLong(key, defaultValue);
        }
        return result;
    }

    public static void removeSharePreferenceKey(String key) {
        SharedPreferences sp = getSpSharedPreference();
        if (sp != null) {
            sp.edit().remove(key).apply();
        }
    }
}