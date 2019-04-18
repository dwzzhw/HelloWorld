package com.loading.common.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by loading on 3/14/17.
 */

public class CommonUtils {
    public static final String TAG = "CommonUtils";
    public static final String FILE_SCHEME_PREFIX = "file://";
    public static final long BYTE_SIZE_KB = 1 << 10;
    public static final long BYTE_SIZE_MB = 1 << 20;
    public static final long BYTE_SIZE_GB = 1 << 30;

    /**
     * 从assert下的json文件读取测试数据
     *
     * @param strAssertFileName
     * @return
     */
    public static String readAssertResource(Context context, String strAssertFileName) {
        AssetManager assetManager = context.getAssets();
        String strResponse = "";
        try {
            InputStream ims = assetManager.open(strAssertFileName);
            strResponse = getStringFromInputStream(ims);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return strResponse;
    }

    private static String getStringFromInputStream(InputStream a_is) {
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            br = new BufferedReader(new InputStreamReader(a_is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                }
            }
        }
        return sb.toString();
    }

    /**
     * 对字符串进行MD5转化
     *
     * @param srcString the input String
     * @return the md5 the input string
     */
    public static String md5String(String srcString) {
        String resultStr = null;
        if (srcString != null && srcString.length() > 0) {
//            char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
            try {
                byte[] btInput = srcString.getBytes();
                // 获得MD5摘要算法的 MessageDigest 对象
                MessageDigest mdInst = MessageDigest.getInstance("MD5");
                // 使用指定的字节更新摘要
                mdInst.update(btInput);
                // 获得密文
                byte[] md = mdInst.digest();
                // 把密文转换成十六进制的字符串形式
                resultStr = bytesToHexString(md);
            } catch (Exception e) {
                e.printStackTrace();
                resultStr = null;
            }
        }
        return resultStr;
    }

    public static String md5File(File file) {
        String result = null;
        if (file != null && file.isFile()) {
            MessageDigest digest = null;
            byte[] buffer = new byte[1024];
            FileInputStream in = null;
            int len;
            try {
                digest = MessageDigest.getInstance("MD5");
                in = new FileInputStream(file);
                while ((len = in.read(buffer, 0, 1024)) != -1) {
                    digest.update(buffer, 0, len);
                }

                result = bytesToHexString(digest.digest());
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            Loger.d(TAG, "-->md5File(), file=" + file.getAbsolutePath() + ", md5Str=" + result);
        }
        return result;
    }

    /**
     * 对byte数组进行MD5转化
     *
     * @param srcByteArray
     * @return
     */
    public final static String md5ByteArray(byte[] srcByteArray) {
        String resultStr = null;
        if (srcByteArray != null && srcByteArray.length > 0) {
            try {
                // 获得MD5摘要算法的 MessageDigest 对象
                MessageDigest mdInst = MessageDigest.getInstance("MD5");
                // 使用指定的字节更新摘要
                mdInst.update(srcByteArray);
                // 获得密文
                byte[] md = mdInst.digest();
                // 把密文转换成十六进制的字符串形式
                resultStr = bytesToHexString(md);
            } catch (Exception e) {
                e.printStackTrace();
                resultStr = null;
            }
        }
        return resultStr;
    }

    public static String bytesToHexString(byte[] bytes) {
        // http://stackoverflow.com/questions/332079
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    private static GradientDrawable getTL2BRGradientDrawable(int[] colors) {
        GradientDrawable gradientDrawable
                = new GradientDrawable(GradientDrawable.Orientation.TL_BR, colors);
        gradientDrawable.setCornerRadius(0.0f);//rectangle
        return gradientDrawable;
    }

    public static LayerDrawable getTL2BRGradientMaskDrawable(int[] colors, int maskResId, Context context) {
        return new LayerDrawable(new Drawable[]{
                getTL2BRGradientDrawable(colors),
                context.getResources().getDrawable(maskResId)
        });
    }

    public static boolean isMainThread() {
        boolean isMainThread = false;
        Thread curThread = Thread.currentThread();
        Looper mainLooper = Looper.getMainLooper();
        if (mainLooper != null) {
            Thread mainThread = mainLooper.getThread();
            isMainThread = (curThread.getId() == mainThread.getId());
        }
        return isMainThread;
    }

    public static <T> int sizeOf(final Collection<T> collection) {
        return collection == null ? 0 : collection.size();
    }

    public static <T> boolean isEmpty(final Collection<T> collection) {
        return sizeOf(collection) <= 0;
    }

    public static int optInt(final String string) {
        return optInt(string, 0);
    }

    public static int optInt(final String string, final int def) {
        try {
            if (!TextUtils.isEmpty(string)) {
                return Integer.parseInt(string);
            }
        } catch (NumberFormatException e) {
            Loger.e(TAG, "optInt NumberFormatException: " + e);
        }
        return def;
    }

    public static float optFloat(final String string) {
        return optFloat(string, 0.0f);
    }

    public static float optFloat(final String string, final float def) {
        try {
            if (!TextUtils.isEmpty(string)) {
                return Float.parseFloat(string);
            }
        } catch (NumberFormatException e) {
            Loger.e(TAG, "optFloat NumberFormatException: " + e);
        }
        return def;
    }


    public static long optLong(final String string) {
        return optLong(string, 0L);
    }

    public static long optLong(final String string, final long def) {
        try {
            if (!TextUtils.isEmpty(string)) {
                return Long.parseLong(string);
            }
        } catch (NumberFormatException e) {
            Loger.e(TAG, "optLong NumberFormatException: " + e);
        }
        return def;
    }

    public static double optDouble(String string) {
        try {
            if (!TextUtils.isEmpty(string)) {
                return Double.parseDouble(string);
            }
        } catch (Exception e) {
            Loger.d(TAG, "optDouble Exception = " + e);
        }
        return 0f;
    }

    public static <T> T requireNonNull(T obj) {
        if (obj == null)
            throw new NullPointerException();
        return obj;
    }

    private static Pattern EMPTY_LINE_PATTERN = null;

    public static boolean filterEmptyLineForEditable(Editable s, boolean autoReplace) {
        boolean foundEmptyLine = false;
        if (s != null && s.length() > 0) {
            if (EMPTY_LINE_PATTERN == null) {
                EMPTY_LINE_PATTERN = Pattern.compile("((^(\\s*)" + System.lineSeparator() + ")|(" + System.lineSeparator() + "(\\s*)" + System.lineSeparator() + "))");
            }

            Matcher matcher = EMPTY_LINE_PATTERN.matcher(s);
            if (matcher.find()) {
                int start = matcher.start();
                int end = matcher.end();
                foundEmptyLine = true;
                Loger.d(TAG, "-->filterEmptyLineForEditable(), found empty line, start=" + start + ", end=" + end);
                if (autoReplace && end >= 1) {
                    s.replace(end - 1, end, "");
                }
            }
        }
        return foundEmptyLine;
    }

    public static String tenTh2wan(long num) {
        DecimalFormat df = new DecimalFormat("0.0");
        if (num >= 10000000) {
            String str = df.format(num / 100000000.0D);

            return (!str.contains(".0") ? str : str.substring(0, str.length() - 2)) + "亿";
        } else if (num >= 10000 && num < 10000000) {
            String str = df.format(num / 10000.0D);
            return (!str.contains(".0") ? str : str.substring(0, str.length() - 2)) + "万";
        } else {
            return num + "";
        }
    }

    public static String tenTh2wan(String num) {
        if (!TextUtils.isEmpty(num)) {
            try {
                long numLong = Long.parseLong(num);
                return tenTh2wan(numLong);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    //    //保留两位小数
    public static String tenTh2wan2(long num) {
        DecimalFormat df = new DecimalFormat("0.00");
        df.setRoundingMode(RoundingMode.HALF_UP);
        if (num >= 100000000) {
            String str = df.format(num / 100000000.00D);
            if (str.endsWith("00")) {
                return (str.substring(0, str.length() - 3)) + "亿";
            } else if (str.endsWith("0")) {
                return (str.substring(0, str.length() - 1)) + "亿";
            } else {
                return str + "亿";
            }
        } else if (num >= 10000) {
            String str = df.format(num / 10000.00D);
            if (str.endsWith("00")) {
                return (str.substring(0, str.length() - 3)) + "万";
            } else if (str.endsWith("0")) {
                return (str.substring(0, str.length() - 1)) + "万";
            } else {
                return str + "万";
            }
        } else {
            return String.valueOf(num);
        }
    }

    public static String tenTh2wan2(String num) {
        if (!TextUtils.isEmpty(num)) {
            try {
                long numLong = Long.parseLong(num);
                return tenTh2wan2(numLong);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static String urlEncode(String str) throws UnsupportedEncodingException {
        return !TextUtils.isEmpty(str) ?
                (URLEncoder.encode(str, "utf-8").
                        replaceAll("\\+", "%20").
                        replaceAll("%7E", "~").
                        replaceAll("\\*", "%2A").
                        replaceAll("\\|", "%7C")) : "";
    }

    public static boolean isUrl(String url) {
        return !TextUtils.isEmpty(url) && (url.startsWith("http:") || url.startsWith("https:"));
    }

    public static String filterNullToEmptyStr(String str) {
        return str == null ? "" : str;
    }
}


