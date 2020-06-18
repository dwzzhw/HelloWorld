package com.example.loading.libjava;

import com.example.loading.libjava.utils.LogUtil;
import com.example.loading.libjava.utils.MD5;

public class SecurityHelper {
    private static final String TAG = "SecurityHelper";

    public static String guessKey(String pwd) {
        long sTime = System.nanoTime();
        int index = 0;
        String result = null;
        for (index = 1; index <= 10000; index++) {
            if (MD5.MD5_32(String.valueOf(index)).equals(pwd)) {
                result = String.valueOf(index);
                break;
            }
        }

        long eTime = System.nanoTime();
        LogUtil.d(TAG, "-->guessKey(), pwd=", pwd, ", key=", result, ", cost time=", (eTime - sTime) / 1000, "us");
        return result;
    }
}
