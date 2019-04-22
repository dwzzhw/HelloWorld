
package com.loading.comp.download.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import java.util.Locale;

/**
 * APN工具类
 * <p>
 * </p>
 */
public class DownloadAPNUtil {
    @SuppressWarnings("unused")
    private static final String TAG = "APNUtil";
    /**
     * cmwap
     */
    public static final int MPROXYTYPE_CMWAP = 1;
    /**
     * wifi
     */
    public static final int MPROXYTYPE_WIFI = 2;
    /**
     * cmnet
     */
    public static final int MPROXYTYPE_CMNET = 4;
    /**
     * uninet服务器列表
     */
    public static final int MPROXYTYPE_UNINET = 8;
    /**
     * uniwap服务器列表
     */
    public static final int MPROXYTYPE_UNIWAP = 16;
    /**
     * net类服务器列表
     */
    public static final int MPROXYTYPE_NET = 32;
    /**
     * wap类服务器列表
     */
    public static final int MPROXYTYPE_WAP = 64;
    /**
     * 默认服务器列表
     */
    public static final int MPROXYTYPE_DEFAULT = 128;
    /**
     * cmda net
     */
    public static final int MPROXYTYPE_CTNET = 256;
    /**
     * cmda wap
     */
    public static final int MPROXYTYPE_CTWAP = 512;
    /**
     * 联通 3gwap
     */
    public static final int MPROXYTYPE_3GWAP = 1024;
    /**
     * 联通 3gnet
     */
    public static final int MPROXYTYPE_3GNET = 2048;

    // apn地址
    private static Uri PREFERRED_APN_URI = Uri.parse("content://telephony/carriers/preferapn");

    // apn属性代理
    public static final String APN_PROP_PROXY = "proxy";
    // apn属性端口
    public static final String APN_PROP_PORT = "port";

    /**
     * 获取系统APN代理IP
     *
     * @param context
     * @return
     */
    public static String getApnProxy(Context context) {
        Cursor c = context.getContentResolver().query(PREFERRED_APN_URI,
                null,
                null,
                null,
                null);
        String strResult = null;
        if (c != null) {
            c.moveToFirst();
            if (c.isAfterLast()) {
                c.close();
                return null;
            }
            strResult = c.getString(c.getColumnIndex(APN_PROP_PROXY));
            c.close();
        }
        return strResult;
    }

    /**
     * 获取系统APN代理端口
     * 
     * @param context
     * @return
     */
    public static String getApnPort(Context context) {
        Cursor c = context.getContentResolver().query(PREFERRED_APN_URI, null,
                null, null, null);
        String port = null;
        if (c != null) {
            c.moveToFirst();
            if (c.isAfterLast()) {
                c.close();
                return "80";
            }

            port = c.getString(c.getColumnIndex(APN_PROP_PORT));
            if (port == null) {
                c.close();
                port = "80";
            }
            c.close();
        }
        return port;
    }

    /**
     * 是否有网关代理
     * 
     * @param context
     * @return
     */
    public static boolean hasProxy(Context context) {
        int netType = getMProxyType(context);
        if (netType == MPROXYTYPE_CMWAP || netType == MPROXYTYPE_UNIWAP
                || netType == MPROXYTYPE_WAP || netType == MPROXYTYPE_CTWAP
                || netType == MPROXYTYPE_3GWAP) {
            return true;
        }
        return false;
    }

    /**
     * 获取自定义当前联网类型
     * 
     * @param act 当前活动Activity
     * @return 联网类型 -1表示未知的联网类型, 正确类型： MPROXYTYPE_WIFI | MPROXYTYPE_CMWAP |
     *         MPROXYTYPE_CMNET
     */
    public static int getMProxyType(Context act) {
        try {
            ConnectivityManager cm = (ConnectivityManager) act
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm == null)
                return MPROXYTYPE_DEFAULT;

            NetworkInfo info = cm.getActiveNetworkInfo();
            if (info == null)
                return MPROXYTYPE_DEFAULT;
            String typeName = info.getTypeName();
            if (typeName.toUpperCase(Locale.US).equals("WIFI")) { // wifi网络
                return MPROXYTYPE_WIFI;
            } else {
                String extraInfo = info.getExtraInfo().toLowerCase(Locale.US);
                if (extraInfo.startsWith("cmwap")) { // cmwap
                    return MPROXYTYPE_CMWAP;
                } else if (extraInfo.startsWith("cmnet")
                        || extraInfo.startsWith("epc.tmobile.com")) { // cmnet
                    return MPROXYTYPE_CMNET;
                } else if (extraInfo.startsWith("uniwap")) {
                    return MPROXYTYPE_UNIWAP;
                } else if (extraInfo.startsWith("uninet")) {
                    return MPROXYTYPE_UNINET;
                } else if (extraInfo.startsWith("wap")) {
                    return MPROXYTYPE_WAP;
                } else if (extraInfo.startsWith("net")) {
                    return MPROXYTYPE_NET;
                } else if (extraInfo.startsWith("ctwap")) {
                    return MPROXYTYPE_CTWAP;
                } else if (extraInfo.startsWith("ctnet")) {
                    return MPROXYTYPE_CTNET;
                } else if (extraInfo.startsWith("3gwap")) {
                    return MPROXYTYPE_3GWAP;
                } else if (extraInfo.startsWith("3gnet")) {
                    return MPROXYTYPE_3GNET;
                }
                else if (extraInfo.startsWith("#777")) { // cdma
                    String proxy = getApnProxy(act);
                    if (proxy != null && proxy.length() > 0) {
                        return MPROXYTYPE_CTWAP;
                    } else {
                        return MPROXYTYPE_CTNET;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return MPROXYTYPE_DEFAULT;
    }
}