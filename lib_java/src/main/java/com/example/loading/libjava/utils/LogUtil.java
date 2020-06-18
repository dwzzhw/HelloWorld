package com.example.loading.libjava.utils;

public class LogUtil {
    public static boolean DEBUG = true;

    public static boolean enable() {
        return DEBUG;
    }

    public static void d(String tag, Object... msgList) {
        if (DEBUG) {
            if (msgList != null && msgList.length > 0) {
                StringBuilder builder = new StringBuilder();
                builder.append("==[").append(tag).append("]==");
                for (int i = 0; i < msgList.length; i++) {
                    builder.append(msgList[i]);
                }
                System.out.println(builder.toString());
            }
        }
    }
}
