package com.loading.common.utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;

public class FragmentHelper {
    private static final String TAG = "FragmentHelper";
    public static void addWithoutAnim(FragmentManager fragmentManager,
                                      int containerViewId,
                                      Fragment fragment,
                                      String tag) {
        addWithCustomAnim(fragmentManager,
                containerViewId,
                fragment,
                tag,
                0,
                0);
    }

    public static void addWithCustomAnim(FragmentManager fragmentManager,
                                         int containerViewId,
                                         Fragment fragment,
                                         String tag,
                                         int enterAnimResId,
                                         int exitAnimResId) {
        addWithCustomAnim(fragmentManager,
                containerViewId,
                fragment,
                tag,
                enterAnimResId,
                exitAnimResId,
                0,
                0);
    }

    private static void addWithCustomAnim(FragmentManager fragmentManager,
                                          int containerViewId,
                                          Fragment fragment,
                                          String tag,
                                          int enterAnimResId,
                                          int exitAnimResId,
                                          int popEnterAnimResId,
                                          int popExitAnimResId) {
        Loger.d(TAG, "--->addWithCustomAnim()--, containerViewId:" + containerViewId
                + ",fragment:" + fragment
                + ", fragmentManager=" + fragmentManager
                + ",tag:" + tag
                + ",enterAnimResId:" + enterAnimResId
                + ",exitAnimResId:" + exitAnimResId
                + ",popEnterAnimResId:" + popEnterAnimResId
                + ",popExitAnimResId:" + popExitAnimResId);
        try {
            if (fragmentManager != null && fragment != null) {
                if (TextUtils.isEmpty(tag) || fragmentManager.findFragmentByTag(tag) == null) {
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.setCustomAnimations(enterAnimResId, exitAnimResId, popEnterAnimResId, popExitAnimResId);
                    fragmentTransaction.add(containerViewId, fragment, tag);
                    fragmentTransaction.commitAllowingStateLoss();
                } else {
                    Loger.w(TAG, " addWithCustomAnim(), fragment with tag " + tag + " already exist, ignore dup one");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Loger.e(TAG, "Exception in addWithCustomAnim():" + e.toString());

        }

    }
}