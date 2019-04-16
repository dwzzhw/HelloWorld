package com.loading.flutterbridge.activity;

import com.loading.common.utils.Loger;

public class TinySportsMainActivity extends FlutterContainerActivity {
    private static final String TAG = "TinySportsMainActivity";

    @Override
    protected String getDefaultFlutterRouterStr() {
        Loger.d(TAG, "-->getDefaultFlutterRouterStr()");
        return "sports_main";
    }
}
