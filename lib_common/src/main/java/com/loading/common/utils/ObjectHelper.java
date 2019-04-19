package com.loading.common.utils;

import android.text.TextUtils;

public class ObjectHelper {
    private ObjectHelper() {}

    public static void assertTrue(boolean assertExpr, String msg) {
        if ( !assertExpr ) {
            throw new IllegalArgumentException(msg);
        }
    }

    public static void requireNotNull(Object iObj, String assertMsg) {
        if ( iObj == null ) {
            throw new IllegalArgumentException(!TextUtils.isEmpty(assertMsg) ? assertMsg : "this argument should not be null!");
        }
    }
}
