package com.example.loading.libjava.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

public class RSAUtils {

    private static final String TAG = "RSAUtils_dwz";
    //构建Cipher实例时所传入的的字符串，默认为"RSA/NONE/PKCS1Padding"
    private static final String sTransform = "RSA/NONE/PKCS1Padding";
    private static final String sCharSet = "UTF-8";

    /**
     * 从字符串中加载公钥
     *
     * @param publicKeyStr 公钥数据字符串
     * @throws Exception 加载公钥时产生的异常
     */
    public static RSAPublicKey loadPublicKey(String publicKeyStr) {
        long sTime = System.nanoTime();
        RSAPublicKey publicKey = null;
        if (!Utils.isEmpty(publicKeyStr)) {
            try {
                byte[] buffer = publicKeyStr.getBytes();
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
                publicKey = (RSAPublicKey) keyFactory.generatePublic(keySpec);
            } catch (NoSuchAlgorithmException e) {
                if (LogUtil.enable()) {
                    LogUtil.d(TAG, "-->loadPublicKey(): AlgorithmException", e);
                }
            } catch (InvalidKeySpecException e) {
                if (LogUtil.enable()) {
                    LogUtil.d(TAG, "-->loadPublicKey(): InvalidKeySpec", e);
                }
            }
        }
        long eTime = System.nanoTime();
        if (LogUtil.enable()) {
            LogUtil.d(TAG, "-->loadPublicKey(): cost time = ", (eTime - sTime) / 1000, " us");
        }

        return publicKey;
    }

    public static RSAPrivateKey loadPrivateKey(String privateKeyStr) {
        long sTime = System.nanoTime();
        RSAPrivateKey privateKey = null;
        try {
            byte[] buffer = privateKeyStr.getBytes();
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buffer);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            privateKey = (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
        } catch (Exception e) {
            if (LogUtil.enable()) {
                LogUtil.d(TAG, "-->loadPrivateKey(): ", e);
            }
        }
        long eTime = System.nanoTime();
        if (LogUtil.enable()) {
            LogUtil.d(TAG, "-->loadPrivateKey(): cost time = ", (eTime - sTime) / 1000, " us");
        }
        return privateKey;
    }

    /**
     * 加密数据
     *
     * @param inputStr     原始内容
     * @param publicKeyStr 公钥字串
     * @return
     */
    public static String encrypt(String inputStr, String publicKeyStr) {
        String encryptedStr = null;
        if (!Utils.isEmpty(inputStr)) {
            Key publicKey = loadPublicKey(publicKeyStr);
            long sTime = System.nanoTime();
            try {
                byte[] plainTextData = inputStr.getBytes(sCharSet);
                Cipher cipher = Cipher.getInstance(sTransform);
                cipher.init(Cipher.ENCRYPT_MODE, publicKey);
                byte[] output = cipher.doFinal(plainTextData);
                encryptedStr = Utils.byte2hex(output);
            } catch (Exception e) {
                if (LogUtil.enable()) {
                    LogUtil.d(TAG, "-->encrypt() fail: ", e);
                }
            }
            long eTime = System.nanoTime();
            if (LogUtil.enable()) {
                LogUtil.d(TAG, "-->encrypt(): cost time = ", (eTime - sTime) / 1000, " us");
            }
        }
        return encryptedStr;
    }

    /**
     * 解密过程
     *
     * @param privateKeyStr 私钥
     * @param encodeStr     Base64编码后的密文数据
     * @return 明文
     */
    public static String decrypt(String encodeStr, String privateKeyStr) {
        String decryptedStr = null;
        if (!Utils.isEmpty(encodeStr)) {
            Key privateKey = loadPrivateKey(privateKeyStr);
            long sTime = System.nanoTime();
            try {
                Cipher cipher = Cipher.getInstance(sTransform);
                cipher.init(Cipher.DECRYPT_MODE, privateKey);
                byte[] cipherData = Utils.hex2byte(encodeStr);
                byte[] output = cipher.doFinal(cipherData);
                decryptedStr = new String(output, StandardCharsets.UTF_8);
            } catch (Exception e) {
                if (LogUtil.enable()) {
                    LogUtil.d(TAG, "-->decrypt(): ", e);
                }
            }
            long eTime = System.nanoTime();
            if (LogUtil.enable()) {
                LogUtil.d(TAG, "-->decrypt(): cost time = ", (eTime - sTime) / 1000, " us");
            }
        }
        return decryptedStr;
    }

    //产生随机密钥对
    public static KeyPair generateRSAKeyPair(int keyLength) {
        KeyPair keyPair = null;
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            //设置密钥长度
            keyPairGenerator.initialize(keyLength);
            //产生密钥对
            keyPair = keyPairGenerator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return keyPair;
    }

    /**
     * 从文件中输入流中加载密钥
     *
     * @param in 公钥输入流
     */
    public static RSAKey loadKeyFromStream(InputStream in, boolean publicKey) throws Exception {
        RSAKey key = null;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String readLine;
            StringBuilder sb = new StringBuilder();
            while ((readLine = br.readLine()) != null) {
                if (readLine.charAt(0) == '-') {
                    continue;
                } else {
                    sb.append(readLine);
                    sb.append('\r');
                }
            }
            key = publicKey ? loadPublicKey(sb.toString()) : loadPrivateKey(sb.toString());
        } catch (IOException e) {
            throw new Exception("公钥数据流读取错误");
        } catch (NullPointerException e) {
            throw new Exception("公钥输入流为空");
        }
        return key;
    }
}
