package com.loading.common.lifecycle;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;

import com.loading.common.BuildConfig;
import com.loading.common.component.CApplication;
import com.loading.common.manager.ListenerManager;
import com.loading.common.utils.Loger;

import java.lang.ref.SoftReference;
import java.util.Collection;
import java.util.Collections;
import java.util.Stack;

@SuppressWarnings("unused")
public class CActivityManager implements Application.ActivityLifecycleCallbacks {
    private final static String TAG = "ActivityManager";
    private final static int LIFECYCLE_CREATED = 1;
    private final static int LIFECYCLE_STARTED = 2;
    private final static int LIFECYCLE_RESUMEED = 3;
    private final static int LIFECYCLE_PAUSED = 4;
    private final static int LIFECYCLE_STOPPED = 5;
    private final static int LIFECYCLE_DESTROYED = 6;
    private final static int LIFECYCLE_SAVE_INSTANCE_STATE = 7;

    private static final String KEY_INNER_START_FLAG = "inner_start";  //标识页面是否是内部启动的标记
    private static final String CHUNK_DEBUG_ACTIVITY_CLS_NAME_PREFIX = "com.readystatesoftware.chuck.internal.ui";

    private static Class homeActivityCls;
    private static Class transferActivityCls;
    private static Class launcherActivityCls;
    private Stack<SoftReference<Activity>> mActivityStack;
    private ListenerManager<IActivityLifecycleCallback> mListenerMgr = new ListenerManager<>();

    private CActivityManager() {
    }

    public static CActivityManager getInstance() {
        return InstanceHolder.instance;
    }

    private static class InstanceHolder {
        private static CActivityManager instance = new CActivityManager();
    }

    public static Bundle getRetToMainBundle() {
        Bundle bundle = new Bundle();
        bundle.putBoolean(KEY_INNER_START_FLAG, true);
        return bundle;
    }

    /**
     * 是否是外部拉起来返回的首页，如果是返回 true，否则返回 false
     * 首页此种情况不再查询播放历史
     */
    public static boolean isExtraLaunchRetMain(Intent intent) {
        return intent != null && intent.getBooleanExtra(KEY_INNER_START_FLAG, false);
    }

    public static void setMainActivityCls(Class tHomeActivityCls, Class transActivityCls, Class launcherActCls) {
        homeActivityCls = tHomeActivityCls;
        transferActivityCls = transActivityCls;
        launcherActivityCls = launcherActCls;
    }

    public static Class getHomeActivityCls() {
        return homeActivityCls;
    }

    public boolean bringAppToFront() {
        boolean isConsumed = false;
        String transferActivityName = transferActivityCls != null ? transferActivityCls.getCanonicalName() : null;
        if (!TextUtils.isEmpty(transferActivityName) && mActivityStack != null) {
            int activityStackSize = mActivityStack.size();
            for (int i = activityStackSize - 1; i >= 0; i--) {
                SoftReference<Activity> refActivity = mActivityStack.get(i);
                Activity tActivity = refActivity != null ? refActivity.get() : null;
                if (tActivity != null && !transferActivityName.equals(tActivity.getClass().getCanonicalName())) {
                    android.app.ActivityManager activityManager = (android.app.ActivityManager) CApplication.getAppContext().getSystemService(Context.ACTIVITY_SERVICE);
                    if (activityManager != null) {
                        activityManager.moveTaskToFront(tActivity.getTaskId(), android.app.ActivityManager.MOVE_TASK_WITH_HOME);
                        isConsumed = true;
                        break;
                    }
                }
            }
        }
        if (!isConsumed) {
            isConsumed = startLauncherActivity();
        }
        return isConsumed;
    }

    @SuppressWarnings({"UnusedReturnValue", "WeakerAccess"})
    public static boolean startLauncherActivity() {
        boolean isSuccess = false;
        if (launcherActivityCls != null) {
            Intent intent = new Intent(CApplication.getAppContext(), launcherActivityCls);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            CApplication.getAppContext().startActivity(intent);
            isSuccess = true;
        }
        return isSuccess;
    }

    @SuppressWarnings({"UnusedReturnValue", "WeakerAccess"})
    public static boolean startHomeActivity() {
        boolean isSuccess = false;
        if (homeActivityCls != null) {
            Intent intent = new Intent(CApplication.getAppContext(), homeActivityCls);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            CApplication.getAppContext().startActivity(intent);
            isSuccess = true;
        }
        return isSuccess;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        if (mActivityStack == null) {
            mActivityStack = new Stack<>();
        }
        mActivityStack.push(new SoftReference<>(activity));
        Loger.d(TAG, "onActivityCreated" + ", activity: " + activity + ", stackSize: " + getActivityStackSize());
        notifyActivityLifeCycleChanged(LIFECYCLE_CREATED, activity, savedInstanceState);
    }

    /**
     * @param activity new created activity or stoped activity
     * @return true if filter this activity to correct the foreground behaviour
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean isFilterActivity(Activity activity) {
        boolean isFilter = false;
        if (BuildConfig.DEBUG) {
            String activityClsName = activity != null ? activity.getClass().getCanonicalName() : null;
            isFilter = !TextUtils.isEmpty(activityClsName) && activityClsName.startsWith(CHUNK_DEBUG_ACTIVITY_CLS_NAME_PREFIX);
        }
        return isFilter;
    }

    @Override
    public void onActivityStarted(Activity activity) {
        Loger.d(TAG, "onActivityStarted" + ", activity: " + activity + ", stackSize: " + getActivityStackSize());
        if (!isFilterActivity(activity)) {
            Foreground.getInstance().onActivityStarted(activity);
        }
        notifyActivityLifeCycleChanged(LIFECYCLE_STARTED, activity, null);
    }

    @Override
    public void onActivityResumed(Activity activity) {
        Loger.d(TAG, "onActivityResumed" + ", activity: " + activity + ", stackSize: " + getActivityStackSize());
        notifyActivityLifeCycleChanged(LIFECYCLE_RESUMEED, activity, null);
    }

    @Override
    public void onActivityPaused(Activity activity) {
        Loger.d(TAG, "onActivityPaused" + ", activity: " + activity + ", stackSize: " + getActivityStackSize());
        notifyActivityLifeCycleChanged(LIFECYCLE_PAUSED, activity, null);
    }

    @Override
    public void onActivityStopped(Activity activity) {
        Loger.d(TAG, "onActivityStopped" + ", activity: " + activity + ", stackSize: " + getActivityStackSize());
        if (!isFilterActivity(activity)) {
            Foreground.getInstance().onActivityStopped(activity);
        }
        notifyActivityLifeCycleChanged(LIFECYCLE_STOPPED, activity, null);
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        Loger.d(TAG, "onActivitySaveInstanceState" + ", activity: " + activity + ", stackSize: " + getActivityStackSize());
        notifyActivityLifeCycleChanged(LIFECYCLE_SAVE_INSTANCE_STATE, activity, outState);
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        boolean isSuccess = false;
        if (mActivityStack != null) {
            int tSize = mActivityStack.size();
            SoftReference<Activity> refActivity;
            for (int i = tSize - 1; i >= 0; i--) {
                refActivity = mActivityStack.get(i);
                if (refActivity != null) {
                    if (refActivity.get() == activity) {
                        mActivityStack.remove(i);
                        isSuccess = true;
                        break;
                    } else if (refActivity.get() == null) {
                        mActivityStack.remove(i);
                    }
                }
            }
        }
        Loger.d(TAG, "onActivityDestroyed, removeFromStack: " + isSuccess + ", activity: " + activity + ", stackSize: " + getActivityStackSize());
        notifyActivityLifeCycleChanged(LIFECYCLE_DESTROYED, activity, null);
    }

    public void clearAllActivity() {
        Loger.d(TAG, "clearAllActivity(), stackSize = " + getActivityStackSize());
        if (mActivityStack != null) {
            SoftReference<Activity> refActivity;
            int tSize = mActivityStack.size();
            for (int i = tSize - 1; i >= 0; i--) {
                refActivity = mActivityStack.get(i);
                if (refActivity != null && refActivity.get() != null) {
                    Activity activity = refActivity.get();
                    if (activity != null && !activity.isFinishing()) {
                        activity.finish();
                    }
                }
            }
            mActivityStack.clear();
        }
    }

    public int getActivityStackSize() {
        return mActivityStack != null ? mActivityStack.size() : 0;
    }

    public Activity getTopActivity() {
        Activity topActivity = null;
        if (mActivityStack != null) {
            SoftReference<Activity> refActivity;
            int tSize = mActivityStack.size();
            for (int i = tSize - 1; i >= 0; i--) {
                refActivity = mActivityStack.get(i);
                if (refActivity != null) {
                    topActivity = refActivity.get();
                    if (topActivity != null) {
                        break;
                    }
                }
            }
        }
        return topActivity;
    }

    public Collection<SoftReference<Activity>> obtainCurrentActivityStack() {
        return Collections.unmodifiableCollection(mActivityStack); //禁止修改
    }

    public Activity getMainActivity() {
        Activity mainActivity = null;
        if (mActivityStack != null) {
            SoftReference<Activity> refActivity;
            int tSize = mActivityStack.size();
            String homeActivityName = homeActivityCls != null ? homeActivityCls.getCanonicalName() : null;
            if (!TextUtils.isEmpty(homeActivityName)) {
                for (int i = 0; i < tSize; i++) {
                    refActivity = mActivityStack.get(i);
                    if (refActivity != null && refActivity.get() != null) {
                        Activity tActivity = refActivity.get();
                        if (tActivity != null && homeActivityName.equals(tActivity.getClass().getCanonicalName())) {
                            mainActivity = tActivity;
                            break;
                        }
                    }
                }
            }
        }
        return mainActivity;
    }

    public void addListener(IActivityLifecycleCallback tListener) {
        mListenerMgr.addListener(tListener);
    }

    public void removeListener(IActivityLifecycleCallback tListener) {
        mListenerMgr.removeListener(tListener);
    }

    public void notifyConfigurationChanged(final Configuration configuration) {
        mListenerMgr.loopListenerList(listener -> listener.onConfigurationChanged(configuration));
    }

    private void notifyActivityLifeCycleChanged(final int type,
                                                final Activity activity,
                                                final Bundle bundle) {
        mListenerMgr.loopListenerList(listener -> {
            switch (type) {
                case LIFECYCLE_CREATED:
                    listener.onActivityCreated(activity, bundle);
                    break;
                case LIFECYCLE_STARTED:
                    listener.onActivityStarted(activity);
                    break;
                case LIFECYCLE_RESUMEED:
                    listener.onActivityResumed(activity);
                    break;
                case LIFECYCLE_PAUSED:
                    listener.onActivityPaused(activity);
                    break;
                case LIFECYCLE_STOPPED:
                    listener.onActivityStopped(activity);
                    break;
                case LIFECYCLE_DESTROYED:
                    listener.onActivityDestroyed(activity);
                    break;
                case LIFECYCLE_SAVE_INSTANCE_STATE:
                    listener.onActivitySaveInstanceState(activity, bundle);
                    break;
            }
        });
    }

    public static boolean isTransferActivity(Activity activity) {
        return activity != null && activity.getClass().isAssignableFrom(transferActivityCls);
    }
}
