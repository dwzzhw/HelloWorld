package com.example.loading.libjava.utils;

public class Utils {
    public static boolean isEmpty(String msg) {
        return msg == null || msg.length() == 0;
    }

    public static byte[] hex2byte(final String str) {
        if (str == null) {
            return new byte[]{};
        }
        String newStr = str.trim();
        int len = newStr.length();
        if (len <= 0 || len % 2 != 0) {
            return new byte[]{};
        }
        byte[] b = new byte[len / 2];
        for (int i = 0; i < newStr.length(); i += 2) {
            b[i / 2] = (byte) Integer.decode("0x" + newStr.substring(i, i + 2)).intValue();
        }
        return b;
    }

    /**
     * 字节数组转String
     */
    public static String byte2hex(byte[] b) {
        StringBuilder hs = new StringBuilder();
        String stmp;
        for (int n = 0; n < b.length; n++) {
            // 整数转成十六进制表示
            stmp = Integer.toHexString(b[n] & 0XFF);
            if (stmp.length() == 1) {
                hs.append("0" + stmp);
            } else {
                hs.append(stmp);
            }
        }
        // 转成大写
        return hs.toString().toUpperCase();
    }
}
