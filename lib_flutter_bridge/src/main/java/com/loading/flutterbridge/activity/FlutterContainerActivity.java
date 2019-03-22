package com.loading.flutterbridge.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.loading.common.component.ActivityHelper;
import com.loading.common.component.BaseActivity;
import com.loading.common.utils.Loger;
import com.loading.common.utils.UiThreadUtil;

import io.flutter.facade.Flutter;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.view.FlutterView;

public class FlutterContainerActivity extends BaseActivity {
    public static final String TAG = "FlutterContainerActivity";
    private static final String FLUTTER_METHOD_CHANNEL_SAY_HELLO = "demo.integrate/sayhello";
    private static final String FLUTTER_METHOD_FLUTTER_SAY_HELLO = "flutterSayHello";
    private static final String FLUTTER_METHOD_ANDROID_SAY_HELLO = "androidSayHello";

    private static final String EXTRA_KEY_ROUTE_NAME = "route_name";

    private MethodChannel methodChannel;
    private MethodChannel.MethodCallHandler methodCallHandler;
    private String mTargetRouteStr;
    private FlutterView mFlutterView;

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
        Loger.d(TAG, "initFlutterView");
        mFlutterView = Flutter.createView(
                FlutterContainerActivity.this,
                getLifecycle(),
                mTargetRouteStr
        );

        initFlutterMethodCallHandler();
        methodChannel = new MethodChannel(mFlutterView, FLUTTER_METHOD_CHANNEL_SAY_HELLO);
        methodChannel.setMethodCallHandler(methodCallHandler);
    }

    private void initFlutterMethodCallHandler() {
        if (methodCallHandler == null) {
            methodCallHandler = (call, result) -> {
                Loger.d(TAG, "-->receive method from flutter, method=" + call.method);
                if (FLUTTER_METHOD_FLUTTER_SAY_HELLO.equals(call.method)) {
                    Toast.makeText(FlutterContainerActivity.this, "Receive sayHello from flutter", Toast.LENGTH_SHORT).show();
                    result.success("Nice to meet you!");

                    UiThreadUtil.postDelay(this::sayHelloToFlutter, 3000);
                } else {
                    result.notImplemented();
                }
            };
        }
    }

    private void sayHelloToFlutter() {
        Loger.d(TAG, "-->sayHelloToFlutter()");
        if (methodChannel != null) {
            methodChannel.invokeMethod(FLUTTER_METHOD_ANDROID_SAY_HELLO, "loading", new MethodChannel.Result() {
                @Override
                public void success(@Nullable Object result) {
                    Loger.d(TAG, "-->sayHelloToFlutter() success, result=" + result);
                    Toast.makeText(FlutterContainerActivity.this, "Receive resp from flutter: " + result, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void error(String errorCode, @Nullable String errorMessage, @Nullable Object errorDetails) {
                    Loger.d(TAG, "-->sayHelloToFlutter() error, errorMessage=" + errorMessage + ", errorDetails=" + errorDetails);
                }

                @Override
                public void notImplemented() {
                    Loger.d(TAG, "-->sayHelloToFlutter(), target notImplemented");
                }
            });
        }
    }
}
