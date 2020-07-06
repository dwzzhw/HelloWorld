package com.loading.common.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;

import com.loading.common.R;
import com.loading.common.component.CApplication;
import com.loading.common.lifecycle.CActivityManager;
import com.loading.common.widget.dialog.MDAlertDialogFragment;
import com.loading.common.widget.dialog.MDDialogInterface;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Class for permission management.
 */
@SuppressWarnings("unused")
public class PermissionUtils {
    private static final String TAG = "PermissionUtils";
    private static final String PERMISSION_FILE_NAME = "permissions_log";
    private static Map<String, String> PERMISSION_MAP_CODE_DESC = new HashMap<>(5);
    private static final SharedPreferences sharedPreferences =
            CApplication.getAppContext().getSharedPreferences(PERMISSION_FILE_NAME, Activity.MODE_PRIVATE);

    static {
        PERMISSION_MAP_CODE_DESC.put(Manifest.permission.RECORD_AUDIO, "麦克风");
        PERMISSION_MAP_CODE_DESC.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, "写存储空间");
        PERMISSION_MAP_CODE_DESC.put(Manifest.permission.CAMERA, "相机");
        PERMISSION_MAP_CODE_DESC.put(Manifest.permission.READ_PHONE_STATE, "电话");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            PERMISSION_MAP_CODE_DESC.put(Manifest.permission.READ_EXTERNAL_STORAGE, "读存储空间");
        }
    }

    /**
     * 在申请某权限前，先向用户解释一下申请该权限的原因，然后才正式申请
     * <p/>
     * 适用于nice to have的权限
     */
    public static void requestPermissionWithTips(final Activity activity, final String[] permissions, String tips, final PermissionCallback callback) {
        Loger.d(TAG, "-->requestPermissionWithTips(), permission=" + Arrays.toString(permissions) + ", tips=" + tips);
        if (!TextUtils.isEmpty(tips) && permissions != null && permissions.length > 0) {
            if (VersionUtils.hasMarshmallow() && activity instanceof FragmentActivity) {
                if (!hasPermission(permissions)) {
                    MDAlertDialogFragment dialogFragment = MDAlertDialogFragment.newInstance(null,
                            tips,
                            "确定",
                            "取消");
                    dialogFragment.setDismissOnConfigChange(true);
                    dialogFragment.setOnDialogClickListener((dialog, which, requestCode) -> {
                        if (which == MDDialogInterface.BUTTON_POSITIVE) {
                            Loger.d(TAG, "-->positive button is clicked");
                            requestPermission(activity, permissions, callback);
                        } else if (which == MDDialogInterface.BUTTON_NEGATIVE) {
                            Loger.d(TAG, "-->negative button is clicked");
                            if (callback != null) {
                                callback.onPermissionResult(false);
                            }
                        }
                    });
                    dialogFragment.show(((FragmentActivity) activity).getSupportFragmentManager());
                } else {
                    if (callback != null) {
                        callback.onPermissionResult(true);
                    }
                }
            }
        }
    }

    /**
     * 请在主线程调用，否则RxPermission<init>可能出现crash
     * 申请权限，三种情况
     * 1、若已有权限，直接返回
     * 2、若用户之前拒绝过权限申请，且点击“不再提示”，则引导用户打开权限页面
     * 3、正常流程，申请权限，回调结果
     */
    @SuppressLint("CheckResult")
    public static void requestPermission(Activity activity, String[] permissions, final PermissionCallback callback) {
        if (activity != null && permissions != null && permissions.length > 0) {
            if (hasPermission(permissions)) {//已有权限
                if (callback != null) {
                    callback.onPermissionResult(true);
                }
            } else if (shouldShowGuideAlert(activity, permissions)) { //用户拒绝过权限申请，并选择不再提示时
                if (activity instanceof FragmentActivity) {
                    showJumpPermissionSettingDialog((FragmentActivity) activity, permissions, callback);
                } else {
                    Loger.e(TAG, "context is not activity!");
                }
            } else {//申请权限
                for (String permission : permissions) {
                    setHasRequestedPermission(permission);
                }
                new RxPermissions(activity).request(permissions).subscribe(aBoolean -> {
                    if (callback != null) {
                        callback.onPermissionResult(aBoolean);
                    }
                });
            }
        }
    }

    /**
     * 告诉用户需要申请权限的名称，用户点击设置之后，跳转到权限设置页面
     */
    private static void showJumpPermissionSettingDialog(FragmentActivity activity, String[] permissions, final PermissionCallback callback) {
        if (activity != null && permissions != null && permissions.length > 0) {
            CharSequence message = getPermissionReqDesc(permissions);
            MDAlertDialogFragment dialogFragment = MDAlertDialogFragment.newInstance("权限管理",
                    message, "设置", "取消");
            dialogFragment.setOnDialogClickListener((dialog, which, requestCode) -> {
                if (which == MDDialogInterface.BUTTON_POSITIVE) {
                    jumpToSystemPermissionPage();
                } else if (which == MDDialogInterface.BUTTON_NEGATIVE) {
                    if (callback != null) {
                        callback.onPermissionResult(false);
                    }
                }
            });
            dialogFragment.show(activity.getSupportFragmentManager());
        }
    }

    /**
     * @return 根据需要申请的权限，返回拼接后的文案。
     * 注意： permission与文案的对应存储在 PERMISSION_MAP_CODE_DESC中, 若MAP中找不到，则不对此权限进行拼接展示。
     */
    private static CharSequence getPermissionReqDesc(String[] permissions) {
        String prefixStr = "本功能需要";
        String suffixStr = "权限，现在就去设置？";
        StringBuilder stringBuilder = new StringBuilder();
        if (permissions != null && permissions.length > 0) {
            for (String permission : permissions) {
                String name = PERMISSION_MAP_CODE_DESC.get(permission);
                if (!TextUtils.isEmpty(name)) {
                    stringBuilder.append("[").append(name).append("] ");
                }
            }
        }

        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(prefixStr + stringBuilder.toString() + suffixStr);
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(CApplication.getColorFromRes(R.color.blue_primary));
        spannableStringBuilder.setSpan(colorSpan, prefixStr.length(), prefixStr.length() + stringBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableStringBuilder;
    }

    /**
     * 跳转到当前app的权限设置页面
     */
    private static void jumpToSystemPermissionPage() {
        Loger.d(TAG, "-->jumpToSystemPermissionPage()");
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            Uri uri = Uri.parse("package:" + CApplication.getAppContext().getPackageName());
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(uri);
            Loger.d(TAG, "-->try to start settings page, uri=" + uri);
            CApplication.getAppContext().startActivity(intent);
        } catch (Exception e1) {
            Loger.w(TAG, "-->try to start settings page with play A fail, exception=" + e1);
            try {
                intent.setAction(Settings.ACTION_APPLICATION_SETTINGS);
                CApplication.getAppContext().startActivity(intent);
            } catch (Exception e2) {
                Loger.w(TAG, "-->try to start settings page with play B fail again, exception=" + e1);
            }
        }
    }

    /**
     * 当一个权限申请过，且用户点击过“不再提醒”后，才会返回true
     */
    private static boolean shouldShowGuideAlert(Context context, String[] permissions) {
        boolean showDialog = false;
        if (permissions != null && permissions.length > 0 && context instanceof Activity) {
            for (String permission : permissions) {
                if (!ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, permission) &&
                        hasPermissionRequested(permission) && !hasPermission(permission)) {
                    showDialog = true;
                    break;
                }
            }
        }
        Loger.i(TAG, "shouldShowGuideAlert: " + showDialog);
        return showDialog;
    }

    /**
     * @param permissions 权限列表
     * @return 只有列表中全部权限被授权后才会返回true
     */
    public static boolean hasPermission(String... permissions) {
        boolean result = false;
        try {
            if (permissions != null && permissions.length > 0) {
                //非主线程使用新activity调用RxPermission<init>容易出现crash，故此次优先尝试采用已经存在的页面
                Activity targetActivity = CActivityManager.getInstance().getTopActivity();
                if (targetActivity == null) {
                    targetActivity = CActivityManager.getInstance().getMainActivity();
                }
                if (targetActivity != null) {
                    result = true;
                    for (String permission : permissions) {
                        result = !VersionUtils.hasMarshmallow() || targetActivity.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
                        if (!result) {
                            break;
                        }
                    }
                }
            } else {
                result = false;
            }
        } catch (Exception e) {
            Loger.w(TAG, "Exception happen when check permission, thread=" + Thread.currentThread().getName() + ", exception=" + e);
            result = false;
        }
        return result;
    }

    private static void setHasRequestedPermission(String permission) {
        sharedPreferences.edit().putBoolean(getPermissionReferenceKey(permission), true).apply();
    }

    private static boolean hasPermissionRequested(String permission) {
        return sharedPreferences.getBoolean(getPermissionReferenceKey(permission), false);
    }

    private static String getPermissionReferenceKey(String permission) {
        return "permission_req_key_" + permission;
    }

    public static boolean isValidPermissionStr(String permission) {
        return !TextUtils.isEmpty(permission) && PERMISSION_MAP_CODE_DESC.containsKey(permission);
    }

    public interface PermissionCallback {
        /**
         * @param granted 是否授权成功
         */
        void onPermissionResult(boolean granted);
    }
}
