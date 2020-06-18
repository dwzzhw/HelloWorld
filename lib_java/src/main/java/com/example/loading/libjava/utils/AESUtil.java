
package com.example.loading.libjava.utils;

import java.io.UnsupportedEncodingException;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AESUtil {
    private static final String TAG = "AESUtil_dwz";
    private static final String KEY_PLACE_HOLDER = "0123456789abcdef";
    private static final String CIPHER_TRANSFORMATION = "AES/CBC/PKCS5Padding"; // 算法/模式/补码方式
    private static final byte[] IV = "0000000000000000".getBytes();
    private static final String CHARSET = "UTF-8";

    /**
     * AES加密
     *
     * @param str
     * @param key
     * @return
     */
    public static String encrypt(String str, String key) {
        try {
            long sTime1 = System.nanoTime();
            SecretKeySpec sks = createKey(key);
            long sTime2 = System.nanoTime();
            IvParameterSpec ivParameterSpec = new IvParameterSpec(IV);
            Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);


            cipher.init(Cipher.ENCRYPT_MODE, sks, ivParameterSpec);
            byte[] eb = cipher.doFinal(str.getBytes(CHARSET));
            long eTime = System.nanoTime();
            if (LogUtil.enable()) {
                LogUtil.d(TAG, "-->encrypt(): genKey time= ", (sTime2 - sTime1) / 1000, " us, encrypt cost time = ", (eTime - sTime2) / 1000, " us");
            }
            return Utils.byte2hex(eb);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * AES解密
     *
     * @param str
     * @return
     */
    public static String decrypt(String str, String key) {
        try {
            long sTime1 = System.nanoTime();
            SecretKeySpec sks = createKey(key);
            long sTime2 = System.nanoTime();
            IvParameterSpec ivParameterSpec = new IvParameterSpec(IV);
            Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, sks, ivParameterSpec);
            byte[] by = Utils.hex2byte(str);
            byte[] db = cipher.doFinal(by);
            long eTime = System.nanoTime();
            if (LogUtil.enable()) {
                LogUtil.d(TAG, "-->decrypt(): genKey time= ", (sTime2 - sTime1) / 1000, " us, encrypt cost time = ", (eTime - sTime2) / 1000, " us");
            }
            return new String(db, CHARSET);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /*  创建密钥  */
    private static SecretKeySpec createKey(String password) {
        byte[] data = null;
        if (password == null) {
            password = "";
        }

        if (password.length() != 16) {
            //兼容已有的16位长度密码
            if (password.length() < 16) {
                password = password + KEY_PLACE_HOLDER.substring(0, 16 - password.length());
            } else {
                password = password.substring(0, 16);
            }
        }

        try {
            data = password.getBytes(CHARSET);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return new SecretKeySpec(data, "AES");
    }

    public static String getRandomAESKey() {
        return String.valueOf((int) (Math.random() * 10000) + 1);
    }
}
