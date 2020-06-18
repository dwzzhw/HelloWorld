package com.example.loading.helloworld.activity.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.example.loading.helloworld.R;
import com.example.loading.helloworld.activity.misc.SecurityTestActivity;
import com.loading.common.component.BaseActivity;

public class BrowserTestActivity extends BaseActivity {
    private static final String TAG = "BrowserTestActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser_test);
    }

    public void onBtnClicked(View view) {
        int viewId = view.getId();
        if (viewId == R.id.encrypt_page) {
            startEncryptPage();
        }
    }

    private void startEncryptPage() {
        Intent intent = new Intent(this, SecurityTestActivity.class);
        startActivity(intent);
    }

}
