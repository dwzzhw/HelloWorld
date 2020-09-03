package com.tencent.mtt.external.reader.thirdcall;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.loading.helloworld.R;
import com.loading.common.component.BaseActivity;
import com.loading.common.utils.Loger;

public class ThirdCallDispatchActivity extends BaseActivity {
    private static final String TAG = "ThirdCallDispatchActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qq_browser_test);
    }

    public void onBtnClicked(View view) {
        if (view.getId() == R.id.btn_mibrowser) {
            startMiBrowser();
        } else if (view.getId() == R.id.btn_check_intent) {
            checkDeepLink();
        }
    }

    private void startMiBrowser() {
        Loger.d(TAG, "-->startMiBrowser: ");
    }


    private void checkDeepLink() {
        Loger.d(TAG, "-->checkDeepLink: ");
    }

}
