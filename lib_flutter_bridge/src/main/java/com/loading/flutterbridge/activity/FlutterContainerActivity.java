package com.loading.flutterbridge.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.loading.common.component.ActivityHelper;
import com.loading.common.component.BaseActivity;
import com.loading.common.utils.Loger;
import com.loading.common.utils.UiThreadUtil;
import com.loading.flutterbridge.R;
import com.loading.flutterbridge.channel.FlutterChannelManager;

import io.flutter.app.FlutterActivityDelegate;
import io.flutter.facade.Flutter;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.view.FlutterView;

public class FlutterContainerActivity extends BaseActivity {
    public static final String TAG = "FlutterContainerActivity";

    private static final String EXTRA_KEY_ROUTE_NAME = "route_name";

    private FlutterChannelManager mFlutterChannelManager;
    private String mTargetRouteStr;
    private FlutterView mFlutterView;

    private View mLaunchView;
    private static final WindowManager.LayoutParams LAUNCH_VIEW_LP = new WindowManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT);

    public static void startActivity(Context context, String routeStr) {
        if (context != null) {
            Intent intent = new Intent(context, FlutterContainerActivity.class);
            intent.putExtra(EXTRA_KEY_ROUTE_NAME, routeStr);
            ActivityHelper.startActivityByIntent(context, intent);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initIntentData();
        initFlutterView();
        if (mFlutterView != null) {
            setContentView(mFlutterView);

            mLaunchView = createLaunchView();
            if (mLaunchView != null) {
                addLaunchView();
            }
        } else {
            quitActivity();
        }
    }

    private void initIntentData() {
        Intent intent = getIntent();
        if (intent != null) {
            mTargetRouteStr = intent.getStringExtra(EXTRA_KEY_ROUTE_NAME);
        }
    }

    private void initFlutterView() {
        Loger.d(TAG, "-->initFlutterView(), mTargetRouteStr=" + mTargetRouteStr);

        if (TextUtils.isEmpty(mTargetRouteStr)) {
            mTargetRouteStr = getDefaultFlutterRouterStr();
        }

        mFlutterView = Flutter.createView(
                FlutterContainerActivity.this,
                getLifecycle(),
                mTargetRouteStr
        );
        mFlutterChannelManager = new FlutterChannelManager(this, mFlutterView);
    }

    protected String getDefaultFlutterRouterStr() {
        return null;
    }

    @Override
    public void onBackPressed() {
        Loger.d(TAG, "-->onBackPressed(), flutterView=" + mFlutterView);
        if (mFlutterView != null) {
            mFlutterView.popRoute();
        } else {
            super.onBackPressed();
        }
    }

    private View createLaunchView() {
        View launcherView = null;
        if (needLaunchView()) {
            Drawable launchDrawable = getResources().getDrawable(R.drawable.splash_bg_layer_2);
            if (launchDrawable != null) {
                launcherView = new View(this);
                launcherView.setLayoutParams(LAUNCH_VIEW_LP);
                launcherView.setBackground(launchDrawable);
            }
        }
        return launcherView;
    }

    protected boolean needLaunchView() {
        return true;
    }

    private void addLaunchView() {
        Loger.d(TAG, "-->addLaunchView(), launchView=" + mLaunchView);
        if (mLaunchView != null && mFlutterView != null) {
            addContentView(this.mLaunchView, LAUNCH_VIEW_LP);
            mFlutterView.addFirstFrameListener(new FlutterView.FirstFrameListener() {
                public void onFirstFrame() {
                    Loger.d(TAG, "-->onFirstFrame()");
                    mLaunchView.animate().alpha(0.0F).setListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animation) {
                            Loger.d(TAG, "-->addLaunchView(): onAnimationEnd");
                            if (mLaunchView != null && mLaunchView.getParent() instanceof ViewGroup) {
                                ((ViewGroup) mLaunchView.getParent()).removeView(mLaunchView);
                                mLaunchView = null;
                            }
                        }
                    });
                    mFlutterView.removeFirstFrameListener(this);
                }
            });
        }
    }
}
