package com.loading.common.utils;

import android.text.TextUtils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

/**
 * Http请求的各种静态工具类
 *
 * @author ervinwang
 */
@SuppressWarnings("unused")
public class HttpUtils {
    public static final String HTTP_ACCEPT_RANGE_OPTION   = "bytes";
    public static final String HTTP_NO_ACCEPT_RANGE_VALUE = "none";

    public static final String GET_METHOD_NAME = "GET";
    public static final String POST_METHOD_NAME = "POST";
    public static final String HEAD_METHOD_NAME = "HEAD";
    
    private static final String PROJECT_ENCODING = "UTF-8";
    private static final String TAG = "HttpUtils";
    private static final String APP_USER_AGENT = "qqsports_android_client";
    private static final String APP_VERNAME_UA_KEY = "qqsports_android_ver";
    private static final int HTTP_TEMPORARY_REDIRECT = 307;
    private static final int HTTP_PERMANENT_REDIRECT = 308;

    private static String appUserAgent;

    private HttpUtils() {}

    public static boolean isHttpSuccess(int responseCode) {
        return (responseCode >= HttpURLConnection.HTTP_OK &&
                responseCode < HttpURLConnection.HTTP_MULT_CHOICE);
    }

    public static boolean isHttpRedirect(int responseCode) {
        switch (responseCode) {
            case HttpURLConnection.HTTP_MULT_CHOICE:
            case HttpURLConnection.HTTP_MOVED_PERM:
            case HttpURLConnection.HTTP_MOVED_TEMP:
            case HttpURLConnection.HTTP_SEE_OTHER:
            case HTTP_TEMPORARY_REDIRECT:
            case HTTP_PERMANENT_REDIRECT:
                return true;
            default:
                return false;
        }
    }

    public static boolean isGzipUrlConnection(URLConnection connection) {
        return connection != null && TextUtils.equals("gzip", connection.getContentEncoding());
    }

    public static String getAppUserAgent() {
        if ( TextUtils.isEmpty(appUserAgent) ) {
            appUserAgent = APP_USER_AGENT + "/" + SystemUtil.getVersionCode() + "; " +
                    APP_VERNAME_UA_KEY + "/" + SystemUtil.getAppVersion();
        }
        return appUserAgent;
    }

    public static byte[] encodeParameters(Map<String, String> params) {
        byte[] result = null;
        if (params != null && params.size() > 0) {
            StringBuilder encodedParams = new StringBuilder();
            try {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    encodedParams.append(URLEncoder.encode(entry.getKey(), PROJECT_ENCODING));
                    encodedParams.append('=');
                    if (entry.getValue() == null) {
                        encodedParams.append("");
                    } else {
                        encodedParams.append(URLEncoder.encode(String.valueOf(entry.getValue()), PROJECT_ENCODING));
                    }
                    encodedParams.append('&');
                }
                int len = encodedParams.length();
                if (len > 0) {
                    encodedParams.deleteCharAt(len - 1);
                }
                Loger.d(TAG, "encodeParameters = " + encodedParams);
                result = encodedParams.toString().getBytes(PROJECT_ENCODING);
            } catch (UnsupportedEncodingException uee) {
                throw new RuntimeException("Encoding not supported: " + PROJECT_ENCODING, uee);
            }
        }
        return result;
    }

    public static String getHostFromUrl(String reqUrl) {
        String host = null;
        if (!TextUtils.isEmpty(reqUrl)) {
            try {
                URI uri = new URI(reqUrl);
                host = uri.getHost();
            } catch (Exception e) {
                Loger.e(TAG, "getHostFromUrl exception ", e);
            }
        }
        return host;
    }

    public static String getHostFromUrl(URL reqUrl) {
        String host = null;
        if (reqUrl != null) {
            host = reqUrl.getHost();
        }
        return host;
    }

    public static boolean isNeedRetry(boolean isDirectIp, int stateCode) {
        boolean isNeedRetry = false;
        if (isDirectIp) {
            if (stateCode == HttpURLConnection.HTTP_INTERNAL_ERROR ||
                    stateCode == HttpURLConnection.HTTP_BAD_GATEWAY ||
                    stateCode == HttpURLConnection.HTTP_FORBIDDEN) {
                isNeedRetry = true;
            }
        }
        return isNeedRetry;
    }

    public static String httpFileName(String tFileName) {
        String result = tFileName;
        int lastIdx = tFileName.lastIndexOf(File.separator);
        if ( lastIdx >= 0 && lastIdx+1 < tFileName.length() ) {
            result = tFileName.substring(lastIdx+1);
        }
        return result;
    }

    public static String httpContentType(String tFileName) {
        String contentType = "application/octet-stream";
        String fileExt;
        int lastIdx = tFileName.lastIndexOf('.');
        if ( lastIdx >= 0 && lastIdx+1 < tFileName.length() ) {
            fileExt = tFileName.substring(lastIdx+1);
            if ( !TextUtils.isEmpty(fileExt) ) {
                fileExt = fileExt.toLowerCase();
                switch (fileExt) {
                    case "jpg":
                    case "jpeg":
                    case "png":
                    case "gif":
                        contentType = "image/" + fileExt;
                        break;
                    case "mp4":
                    case "avi":
                    case "mp3":
                        contentType = "media/" + fileExt;
                        break;
                }
            }
        }
        return contentType;
    }

    public static int getContentLength(Map<String, List<String>> headersMap) {
        List<String> valuesList =  headersMap != null ? headersMap.get(HttpHeadersDef.CONTENT_LENGTH) : null;
        return valuesList != null && valuesList.size() > 0 ? CommonUtil.optInt(valuesList.get(0)) : 0;
    }

    /**
     * 有些server不支持range请求, 会反回 Accept-Range：none， 但是有些返回头中没有这个字段，但可以支持range请求
     * @return true for support, else false
     */
    public static boolean isSupportRange(Map<String, List<String>> headersMap) {
        List<String> valuesList = headersMap != null ?  headersMap.get(HttpHeadersDef.ACCEPT_RANGE) : null;
        return valuesList != null && !valuesList.contains(HTTP_NO_ACCEPT_RANGE_VALUE);
    }
}
