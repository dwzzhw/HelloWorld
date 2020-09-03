package com.tencent.mtt;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.loading.helloworld.R;
import com.loading.common.component.BaseActivity;
import com.loading.common.utils.Loger;

public class QQBrowserTestActivity extends BaseActivity {
    private static final String TAG = "QQBrowserTestActivity";
    private TextView mContentText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qq_browser_test);

        mContentText = findViewById(R.id.content);
        initData();
    }

    private void initData() {
        Intent intent = getIntent();
        String dataStr = intent.getDataString();
        if (!TextUtils.isEmpty(dataStr)) {
        }
        mContentText.setText("Data: " + dataStr);
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
