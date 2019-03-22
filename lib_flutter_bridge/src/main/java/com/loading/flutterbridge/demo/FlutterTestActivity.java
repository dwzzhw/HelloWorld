package com.loading.flutterbridge.demo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.loading.common.utils.Loger;
import com.loading.common.utils.UiThreadUtil;
import com.loading.flutterbridge.R;

import io.flutter.facade.Flutter;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.view.FlutterView;

public class FlutterTestActivity extends FragmentActivity {
    public static final String TAG = "FlutterTestActivity";
    private static final String FLUTTER_METHOD_CHANNEL_SAY_HELLO = "demo.integrate/sayhello";
    private static final String FLUTTER_METHOD_FLUTTER_SAY_HELLO = "flutterSayHello";
    private static final String FLUTTER_METHOD_ANDROID_SAY_HELLO = "androidSayHello";

    private LinearLayout mViewContainer;
    private EditText mRouteNameView;

    private MethodChannel methodChannel;
    private MethodChannel.MethodCallHandler methodCallHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_flutter_test);
        mViewContainer = findViewById(R.id.view_container);
        mRouteNameView = findViewById(R.id.route_name);
    }

    public void onBtnClicked(View view) {
        Loger.d(TAG, "-->onBtnClicked()");
        if (view.getId() == R.id.btn_add_flutter_view) {
            addFlutterView();
        }
    }

    @SuppressWarnings("unused")
    private void addFlutterFragment() {
        Loger.d(TAG, "addFlutterFragment: ");
        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        tx.replace(R.id.view_container, Flutter.createFragment("route1"));
        tx.commit();
    }

    private void addFlutterView() {
        Loger.d(TAG, "addFlutterView 3: ");
        FlutterView flutterView = Flutter.createView(
                FlutterTestActivity.this,
                getLifecycle(),
                mRouteNameView.getText().toString()
        );

        initFlutterMethodCallHandler();
        methodChannel = new MethodChannel(flutterView, FLUTTER_METHOD_CHANNEL_SAY_HELLO);
        methodChannel.setMethodCallHandler(methodCallHandler);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        mViewContainer.removeAllViews();
        mViewContainer.addView(flutterView, lp);
    }

    private void initFlutterMethodCallHandler() {
        if (methodCallHandler == null) {
            methodCallHandler = (call, result) -> {
                Loger.d(TAG, "-->receive method from flutter, method=" + call.method);
                if (FLUTTER_METHOD_FLUTTER_SAY_HELLO.equals(call.method)) {
                    Toast.makeText(FlutterTestActivity.this, "Receive sayHello from flutter", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(FlutterTestActivity.this, "Receive resp from flutter: " + result, Toast.LENGTH_SHORT).show();
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
