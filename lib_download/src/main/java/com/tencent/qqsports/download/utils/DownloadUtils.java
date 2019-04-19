package com.tencent.qqsports.download.utils;

import android.text.TextUtils;
import android.text.format.DateUtils;

import com.loading.common.component.CApplication;
import com.loading.common.utils.AsyncOperationUtil;
import com.loading.common.utils.CommonUtil;
import com.loading.common.utils.HttpUtils;
import com.loading.common.utils.Loger;
import com.loading.modules.interfaces.download.IQueryFileInfoListener;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by loading on 2018/8/29.
 */

@SuppressWarnings("UnusedReturnValue")
public class DownloadUtils {
    private static final String TAG = "DownloadUtils";
    private final static int CONNECT_TIME_OUT = (int) (15 * DateUtils.SECOND_IN_MILLIS);
    private final static int READ_TIME_OUT = (int) (20 * DateUtils.SECOND_IN_MILLIS);

    //    private static final String LOCAL_TMP_FILE_SUFFIX = ".temp";
    private static final String TYPE_WML = "text/vnd.wap.wml";
    private static final String TYPE_WMLC = "application/vnd.wap.wmlc";

    public static void asyncQueryFileInfo(String url, IQueryFileInfoListener queryFileInfoListener) {
        asyncQueryFileInfo(url, null, queryFileInfoListener);
    }

    public static void asyncQueryFileInfo(String url, Map<String, String> requestHeader, IQueryFileInfoListener queryFileInfoListener) {
        AsyncOperationUtil.asyncOperation(() -> {
            syncQueryFileInfo(url, requestHeader, queryFileInfoListener);
        });
    }

    public static void syncQueryFileInfo(String url, Map<String, String> requestHeader, IQueryFileInfoListener queryFileInfoListener) {
        boolean success = false;
        Map<String, List<String>> paramMap = null;
        if (!TextUtils.isEmpty(url)) {
            HttpURLConnection connection = getHttpConnection(url, false, false,
                    CONNECT_TIME_OUT, READ_TIME_OUT, HttpUtils.HEAD_METHOD_NAME, requestHeader, true);
            if (connection != null) {
                paramMap = connection.getHeaderFields();
                success = true;
            }
            disConnect(connection);
        }
        Loger.d(TAG, "-->syncQueryFileInfo(), url=" + url + ", result=" + success + ", paramMap=" + paramMap);
        if (queryFileInfoListener != null) {
            queryFileInfoListener.onQueryFileInfoDone(url, success, paramMap);
        }
    }

    public static HttpURLConnection getHttpConnection(String url,
                                                      boolean doInput,
                                                      boolean allowUserInteraction,
                                                      int connectTimeout,
                                                      int readTimeout,
                                                      Map<String, String> requestHeader,
                                                      boolean toRetry) {
        return getHttpConnection(url, doInput, allowUserInteraction, connectTimeout, readTimeout, HttpUtils.GET_METHOD_NAME, requestHeader, toRetry);
    }

    public static HttpURLConnection getHttpConnection(String url,
                                                      boolean doInput,
                                                      boolean allowUserInteraction,
                                                      int connectTimeout,
                                                      int readTimeout,
                                                      String requestMethod,
                                                      Map<String, String> requestHeader,
                                                      boolean toRetry) {
        HttpURLConnection uc = null;
        try {
            // 代理
            boolean useProxy = DownloadAPNUtil.hasProxy(CApplication.getAppContext());
            if (useProxy) {
                final int hostIndex = "https://".length();
                String proxyIP = DownloadAPNUtil.getApnProxy(CApplication.getAppContext());
                String proxyPort = DownloadAPNUtil.getApnPort(CApplication.getAppContext());
                String host;
                String path;
                int pathIndex = url.indexOf('/', hostIndex);
                if (pathIndex < 0) {
                    host = url.substring(hostIndex);
                    path = "";
                } else {
                    host = url.substring(hostIndex, pathIndex);
                    path = url.substring(pathIndex);
                }
                URL u = new URL("http://" + proxyIP + ":" + proxyPort + path);
                uc = (HttpURLConnection) u.openConnection();
                uc.setRequestProperty("X-Online-Host", host);
            } else {
                URL u = new URL(url);
                uc = (HttpURLConnection) u.openConnection();
            }
            uc.setRequestMethod(requestMethod);
            uc.setDoInput(doInput);// 设置是否从httpUrlConnection读入
            uc.setAllowUserInteraction(allowUserInteraction);
            uc.setConnectTimeout(connectTimeout);
            uc.setReadTimeout(readTimeout);
            if (!CommonUtil.isEmpty(requestHeader)) {
                for (String key : requestHeader.keySet()) {
                    if (!TextUtils.isEmpty(key)) {
                        uc.setRequestProperty(key, requestHeader.get(key));
                    }
                }
            }

            // uc.setConnectTimeout(2 * 60 * 1000);
            int reponseCode = uc.getResponseCode();
            Loger.d(TAG, "responseCode: " + reponseCode);
//            TipsToast.getInstance().showTipsText("网络错误："+reponseCode);
            if (reponseCode == HttpURLConnection.HTTP_MOVED_TEMP ||
                    reponseCode == HttpURLConnection.HTTP_MOVED_PERM) {
                // 302代表暂时性转移,301代表永久性转移
                String loc = uc.getHeaderField("Location");
                disConnect(uc);
                uc = null;
                if (loc != null) {
                    uc = getHttpConnection(loc, doInput, allowUserInteraction,
                            connectTimeout, readTimeout, requestMethod, requestHeader, false);
                }
            } else if (reponseCode == HttpsURLConnection.HTTP_OK ||
                    reponseCode == HttpsURLConnection.HTTP_PARTIAL) {
                // 206 Partial
                // Content服务器已经接受请求GET请求资源的部分。请求必须包含一个Range头信息以指示获取范围可能必须包含If-Range头信息以成立请求条件
                String contentType = uc.getContentType();
                contentType = contentType == null ? null : contentType.toLowerCase(Locale.US);
                boolean isTxtType = !TextUtils.isEmpty(contentType) && (contentType.contains(TYPE_WML) || contentType.contains(TYPE_WMLC));
                Loger.d(TAG, "contentType: " + contentType + ", isTxtType: " + isTxtType + ", toRetry: " + toRetry);
                if (isTxtType && toRetry) {
                    disConnect(uc);
                    uc = null;
                    uc = getHttpConnection(url,
                            doInput,
                            allowUserInteraction,
                            connectTimeout,
                            readTimeout,
                            requestMethod,
                            requestHeader,
                            false);

                }
            } else {
                disConnect(uc);
                uc = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            disConnect(uc);
            uc = null;
        }
        return uc;
    }

    private static void disConnect(HttpURLConnection uc) {
        try {
            if (uc != null) {
                uc.disconnect();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}