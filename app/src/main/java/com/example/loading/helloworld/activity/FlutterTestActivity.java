package com.example.loading.helloworld.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.loading.helloworld.R;
import com.example.loading.helloworld.utils.Loger;

import io.flutter.facade.Flutter;

public class FlutterTestActivity extends FragmentActivity {
    public static final String TAG = "FlutterTestActivity";
    private LinearLayout mViewContainer;
    private EditText mRouteNameView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_flutter_test);
        mViewContainer = findViewById(R.id.view_container);
        mRouteNameView = findViewById(R.id.route_name);
    }

    public void onBtnClicked(View view) {
        Loger.d(TAG, "-->onBtnClicked()");
        switch (view.getId()) {
            case R.id.btn_add_flutter_view:
                addFlutterView();
//                addFlutterFragment();
                break;
        }
    }

    private void addFlutterFragment() {
        Loger.d(TAG, "addFlutterFragment: ");
        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        tx.replace(R.id.view_container, Flutter.createFragment("route1"));
        tx.commit();
    }

    private void addFlutterView() {
        Loger.d(TAG, "addFlutterView 3: ");
        View flutterView = Flutter.createView(
                FlutterTestActivity.this,
                getLifecycle(),
//                new android.arch.lifecycle.Lifecycle() {
//                    @Override
//                    public void addObserver(@NonNull LifecycleObserver observer) {
//                        Loger.d(TAG, "addObserver: observer=" + observer);
//                    }
//
//                    @Override
//                    public void removeObserver(@NonNull LifecycleObserver observer) {
//                        Loger.d(TAG, "removeObserver: observer=" + observer);
//                    }
//
//                    @NonNull
//                    @Override
//                    public State getCurrentState() {
//                        Loger.d(TAG, "getCurrentState: ");
//                        return null;
//                    }
//                },
//        null,
                mRouteNameView.getText().toString()
        );
//        FrameLayout.LayoutParams layout = new FrameLayout.LayoutParams(600, 800);
//        layout.leftMargin = 100;
//        layout.topMargin = 200;
//        flutterView.setBackgroundColor(Color.parseColor("#22ff0000"));
//        addContentView(flutterView, layout);

        View testView = new View(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        mViewContainer.removeAllViews();
        mViewContainer.addView(flutterView, lp);
    }
}
