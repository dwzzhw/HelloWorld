package com.loading.common.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;

import com.loading.common.component.CApplication;
import com.loading.common.lifecycle.CActivityManager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.Method;

public class SystemUtil {
    public static final String TAG = "SystemUtils";
    private static final int APP_MAIN_VERSION_CODE_LENGTH = 3;//eg:5.9.6
    private static final int SOFTKEY_MIN_HEIGHT = SystemUtil.dpToPx(60);
    private static final String OPERATOR_UNKNOWN = "";   //未知
    private static final String OPERATOR_CMCC = "CMCC";      //移动
    private static final String OPERATOR_CU = "CU";        //联通
    private static final String OPERATOR_CT = "CT";        //电信
    private static final String OPERATOR_CMT = "CMT";       //铁通

    private static String IMEI = "";
    private static String IMSI = "";

    private static DisplayMetrics sDisplayMertics;
    private static int screenWidth = 0;
    private static int screenHeight = 0;
    private static String versionName = null;
    private static int versionCode = 0;
    private static int mainVersionCode = 0;
    private static int mStatusBarHeight = 0;

    public static String getCurrentProcessName() {
        String processName = null;
        BufferedReader reader = null;
        try {
            int pid = android.os.Process.myPid();
            reader = new BufferedReader(new FileReader("/proc/" + pid + "/cmdline"));
            processName = reader.readLine();
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim();
            }
            return processName;
        } catch (Throwable throwable) {
            Loger.e(TAG, "throwable: " + throwable);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (Exception exception) {
                Loger.e(TAG, "exception: " + exception);
            }
        }
        return processName;
    }

    public static boolean isSdcardExist() {
        boolean isSdexist = false;
        try {
            isSdexist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        } catch (Exception e) {
            Loger.e(TAG, "isSdcardExist exception: " + e);
        }
        return isSdexist;
    }


    public static boolean isMainProcess() {
        // 获取当前包名
        String packageName = CApplication.getAppContext().getPackageName();
        // 获取当前进程名
        String processName = SystemUtil.getCurrentProcessName();
        return TextUtils.isEmpty(processName) || TextUtils.equals(packageName, processName);
    }

    public static Activity getAttachedActivity(Context context) {
        Activity activity = null;
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                activity = (Activity) context;
                break;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return activity;
    }

    public static int dpToPx(float dp) {
        int outResult;
        DisplayMetrics disMetric = getDeviceDisplayMetrics(CApplication.getAppContext());
        if (disMetric != null) {
            outResult = (int) (dp * disMetric.density + 0.5f);
        } else {
            outResult = (int) dp;
        }
        return outResult;
    }

    public static int getScreenWidthIntPx() {
        if (screenWidth <= 0) {
            setScreenSize();
        }
        return screenWidth;
    }

    public static int getScreenHeightIntPx() {
        if (screenHeight <= 0) {
            setScreenSize();
        }
        return screenHeight;
    }

    public static void setScreenSize() {
        android.view.WindowManager windowsManager = (android.view.WindowManager) CApplication.getAppContext().getSystemService(
                Context.WINDOW_SERVICE);
        android.view.Display display = windowsManager.getDefaultDisplay();
        if (display != null) {
            DisplayMetrics dm = new DisplayMetrics();
            display.getMetrics(dm);

            int mWidth = dm.widthPixels;
            int mHeight = dm.heightPixels;
            Loger.d(TAG, "----->setScreenSize-----width in dp: " + mWidth / dm.density + ", height in dp: " + mHeight / dm.density);
            if (mHeight > mWidth) {// layout port
                // 竖屏 .......
                screenWidth = mWidth;
                screenHeight = mHeight;

            } else {// layout land
                // 横屏
                screenWidth = mHeight;
                screenHeight = mWidth;
            }
        }
    }

    public static int getRealTimeScreenWidthIntPx(Context context) {
        android.view.WindowManager windowsManager = (android.view.WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        android.view.Display display = windowsManager.getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        return outMetrics.widthPixels;
    }

    private static DisplayMetrics getDeviceDisplayMetrics(Context context) {
        if (sDisplayMertics == null && context != null) {
            android.view.WindowManager windowsManager = (android.view.WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            if (windowsManager != null) {
                android.view.Display display = windowsManager.getDefaultDisplay();
                if (display != null) {
                    sDisplayMertics = new DisplayMetrics();
                    display.getMetrics(sDisplayMertics);
                }
            }
        }
        return sDisplayMertics;
    }

    public static boolean isLandscapeOrientation() {
        boolean isLandscape = false;
        android.view.WindowManager windowsManager = (android.view.WindowManager) CApplication.getAppContext().getSystemService(Context.WINDOW_SERVICE);
        if (windowsManager != null) {
            android.view.Display display = windowsManager.getDefaultDisplay();
            if (display != null) {
                isLandscape = (display.getRotation() == Surface.ROTATION_90 || display.getRotation() == Surface.ROTATION_270);
            }
        }
        return isLandscape;
    }

    /**
     * 隐藏控制栏
     */
    public static void hideVirtualNavigationBars(View view) {
        Loger.d(TAG, "-->hideVirtualNavigationBars()");
        if (view != null && VersionUtils.hasKitKat()) {
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                    View.SYSTEM_UI_FLAG_FULLSCREEN |
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    /**
     * 显示控制栏
     */
    public static void showVirtualNavigationBars(View view) {
        Loger.d(TAG, "-->showVirtualNavigationBars()");
        if (view != null && VersionUtils.hasKitKat()) {
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
    }

    /**
     * 获取手机状态栏高度
     */
    @SuppressLint("PrivateApi")
    public static int getStatusBarHeight() {
        if (mStatusBarHeight > 0) {
            return mStatusBarHeight;
        }
        try {
            int resourceId = CApplication.getAppContext().getResources().getIdentifier("status_bar_height", "dimen", "android");
            mStatusBarHeight = resourceId > 0 ? CApplication.getDimensionPixelSize(resourceId) : 0;
        } catch (Exception ignored) {
        }

        if (mStatusBarHeight <= 0) {
            mStatusBarHeight = getStatusBar(CActivityManager.getInstance().getTopActivity());
        }
        return mStatusBarHeight;
    }

    public static int getStatusBar(final Activity activity) {
        int barHeight = 0;
        if (activity != null && activity.getWindow() != null && activity.getWindow().getDecorView() != null) {
            Rect rectangle = new Rect();
            activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rectangle);
            barHeight = rectangle.top;
        }
        return barHeight;
    }

    /**
     * check device has navigation bar
     *
     * @param
     * @return
     */
    public static boolean isNavigationBarShow() {
        if (MobileUtil.isMeizu() && (Build.DEVICE.equals("mx2") || Build.DEVICE.equals("mx3"))) {
            return false;
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && CActivityManager.getInstance().getTopActivity() != null && !MobileUtil.isHuaWeiDevice()) {
                Display display = CActivityManager.getInstance().getTopActivity().getWindowManager().getDefaultDisplay();
                Point size = new Point();
                Point realSize = new Point();
                display.getSize(size);
                display.getRealSize(realSize);
                return realSize.y != size.y;
            } else {
                boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
                boolean hasHomeKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_HOME);
                return (!(hasBackKey && hasHomeKey));
            }
        }
    }

    public static void hideKeyboard(Activity activity) {
        Window window = activity != null ? activity.getWindow() : null;
        View decorView = window != null ? window.getDecorView() : null;
        if (decorView != null) {
            InputMethodManager inputMethodManager = ((InputMethodManager) CApplication.getAppContext().getSystemService(Context.INPUT_METHOD_SERVICE));
            if (inputMethodManager != null) {
                inputMethodManager.hideSoftInputFromWindow(decorView.getWindowToken(), 0);
            }
        }
    }

    public static boolean isKeyBoardShow(Activity activity) {
        boolean isShowing = false;
        if (activity != null) {
//            InputMethodManager imm = (InputMethodManager) CApplication.getAppContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//            isShowing = imm != null && imm.isActive();
//            Loger.i(TAG, "isActive: " + isShowing);
//            if ( !isShowing ) {
            int imeVisibleHeight = getIMEVisibleHeight();
            if (imeVisibleHeight >= 0) {
                isShowing = imeVisibleHeight > 0;
            } else {
                if (activity.getWindow() != null && activity.getWindow().getDecorView() != null) {
                    Rect localRect = new Rect();
                    activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(localRect);
                    int currentScreenHeight = getCurrentScreenHeight();
                    isShowing = currentScreenHeight - localRect.top - localRect.height() > SOFTKEY_MIN_HEIGHT;
                    Loger.d(TAG, "-->isKeyBoardShow(), visible area top=" + localRect.top + ", height=" + localRect.height() + ", screen height=" + currentScreenHeight + ", isShowing=" + isShowing);
                }
            }
//            }
        }
        return isShowing;
    }

    private static boolean reflectWork = true;

    /**
     * 这个方法利用了系统隐藏的方法，因此不太靠谱
     *
     * @return the height of input soft keyboard
     */
    public static int getIMEVisibleHeight() {
        int visibleHeight = -1;
        if (reflectWork) {
            InputMethodManager imm = (InputMethodManager) CApplication.getAppContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                try {
                    Method method = imm.getClass().getDeclaredMethod("getInputMethodWindowVisibleHeight");
                    if (method != null) {
                        method.setAccessible(true);
                        Object value = method.invoke(imm);
                        if (value != null && value instanceof Integer) {
                            visibleHeight = (Integer) value;
                        }
                    }
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    Loger.e(TAG, "exception: " + e);
                } finally {
                    reflectWork = (visibleHeight != -1);
                }
            }
            Loger.d(TAG, "<--getIEMVisibleHeight(), visibleHeight=" + visibleHeight);
        }
        return visibleHeight;
    }

    public static int getCurrentScreenHeight() {
        android.view.WindowManager windowsManager = (android.view.WindowManager) CApplication.getAppContext().getSystemService(
                Context.WINDOW_SERVICE);
        android.view.Display display = windowsManager.getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        display.getMetrics(dm);
        return dm.heightPixels;
    }


    public static int getCurrentScreenWidth() {
        android.view.WindowManager windowsManager = (android.view.WindowManager) CApplication.getAppContext().getSystemService(
                Context.WINDOW_SERVICE);
        android.view.Display display = windowsManager.getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        display.getMetrics(dm);
        return dm.widthPixels;
    }

    /**
     * Mac address or IMEI
     *
     * @return the user deivce unique id, may be wifi addr or imei...
     */
    @SuppressLint("HardwareIds")
    public static String getUniqueID() {
        String uniqueStr = getIMEI();
        if (TextUtils.isEmpty(uniqueStr)) {
            WifiManager wifi = (WifiManager) CApplication.getAppContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if (wifi != null) {
                WifiInfo info = wifi.getConnectionInfo();
                if (info != null) {
                    uniqueStr = info.getMacAddress();
                }
            }
        }
        if (!TextUtils.isEmpty(uniqueStr)) {
            uniqueStr = CommonUtils.md5String(uniqueStr);
        }
        uniqueStr = uniqueStr == null ? "" : uniqueStr;
        return uniqueStr;
    }

    public static String getOSVersion() {
        return android.os.Build.VERSION.RELEASE;
    }

    public static String getDeviceName() {
        return android.os.Build.DEVICE;
    }

    public static String getDeviceProductName() {
        return Build.PRODUCT;
    }

    public static String getDeviceManufacturer() {
        return Build.MANUFACTURER;
    }

    public static String getDeviceModel() {
        return Build.MODEL;
    }

    public static String getManufacturer() {
        return Build.MANUFACTURER;
    }

    @SuppressLint({"MissingPermission", "HardwareIds"})
    public static String getIMEI() {
        if (TextUtils.isEmpty(IMEI)) {
            try {
                if (PermissionUtils.hasPermission(Manifest.permission.READ_PHONE_STATE)) {
                    TelephonyManager telephonyManager = (TelephonyManager) CApplication.getAppContext().getSystemService(Context.TELEPHONY_SERVICE);
                    IMEI = telephonyManager != null ? telephonyManager.getDeviceId() : "";
                }
            } catch (Exception e) {
                Loger.w(TAG, "-->getIMEI(), exception happened, e=" + e);
            }
        }
        return IMEI != null ? IMEI : "";
    }

    public static String getOperatorName() {
        String operatorName = OPERATOR_UNKNOWN;
        TelephonyManager telephonyManager = ((TelephonyManager) CApplication.getAppContext().getSystemService(Context.TELEPHONY_SERVICE));
        if (telephonyManager != null) {
            operatorName = telephonyManager.getNetworkOperatorName();
        }
        return operatorName;
    }

    public static String getOperatorType() {
        String operatorType = OPERATOR_UNKNOWN;
        String operatorCode = null;
        TelephonyManager telephonyManager = ((TelephonyManager) CApplication.getAppContext().getSystemService(Context.TELEPHONY_SERVICE));
        if (telephonyManager != null) {
            operatorCode = telephonyManager.getNetworkOperator();
            operatorType = getOperatorType(operatorCode);
        }
        Loger.d(TAG, "-->getOperatorType(), operatorCode=" + operatorCode + ", operatorType=" + operatorType);
        return operatorType;
    }

    private static String getOperatorType(String operatorCode) {
        String operatorType = OPERATOR_UNKNOWN;
        if (!TextUtils.isEmpty(operatorCode)) {
            if (!TextUtils.isEmpty(operatorCode)) {
                if (operatorCode.startsWith("46000") || operatorCode.startsWith("46002") || operatorCode.startsWith("46004") || operatorCode.startsWith("46007")) {
                    operatorType = OPERATOR_CMCC;
                } else if (operatorCode.startsWith("46001") || operatorCode.startsWith("46006") || operatorCode.startsWith("46009")) {
                    operatorType = OPERATOR_CU;
                } else if (operatorCode.startsWith("46003") || operatorCode.startsWith("46005") || operatorCode.startsWith("46011")) {
                    operatorType = OPERATOR_CT;
                } else if (operatorCode.startsWith("46020")) {
                    operatorType = OPERATOR_CMT;
                }
            }
            Loger.d(TAG, "-->getOperatorType(), operatorCode=" + operatorCode + ", operatorType=" + operatorType);
        }
        return operatorType;
    }

    @SuppressLint({"MissingPermission", "HardwareIds"})
    public static String getIMSI() {
        if (TextUtils.isEmpty(IMSI)) {
            try {
                if (PermissionUtils.hasPermission(Manifest.permission.READ_PHONE_STATE)) {
                    TelephonyManager telephonyManager = ((TelephonyManager) CApplication.getAppContext().getSystemService(Context.TELEPHONY_SERVICE));
                    IMSI = telephonyManager != null ? telephonyManager.getSubscriberId() : "";
                }
            } catch (Exception e) {
                Loger.e(TAG, "exception: " + e);
            }
        }
        return IMSI;
    }

    public static String getAppVersion() {
        if (TextUtils.isEmpty(versionName)) {
            try {
                String packageName = CApplication.getAppContext().getPackageName();
                PackageInfo pm = CApplication.getAppContext().getPackageManager()
                        .getPackageInfo(packageName, PackageManager.GET_CONFIGURATIONS);
                if (pm != null) {
                    versionName = pm.versionName;
                }
            } catch (PackageManager.NameNotFoundException e) {
                Loger.e(TAG, e.toString());
            }
        }
        return versionName;
    }

    public static int getVersionCode() {
        if (versionCode <= 0) {
            try {
                String packageName = CApplication.getAppContext().getPackageName();
                PackageManager pm = CApplication.getAppContext().getPackageManager();
                if (pm != null) {
                    PackageInfo packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_CONFIGURATIONS);
                    if (packageInfo != null) {
                        versionCode = packageInfo.versionCode;
                    }
                }
            } catch (PackageManager.NameNotFoundException e) {
                Loger.e(TAG, e.toString());
            }
        }
        return versionCode;
    }
}
