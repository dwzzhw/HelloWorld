package com.example.loading.helloworld.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;

import com.example.loading.helloworld.R;
import com.loading.common.component.BaseActivity;
import com.loading.common.utils.Loger;

public class WebViewTestActivity extends BaseActivity {
    private static final String TAG = "WebViewTestActivity";

    private EditText mUrlView;
    private WebView mWebView;
    private Button mLoadBtn;

    private String mDefaultUrl = "https://hot.browser.miui.com/rec/content?ref=browser_news&s=mb" +
            "&itemtype=news&mibusinessId=miuibrowser&docid=wemedia-mistories_5e285d253128e6776c4e0f1a" +
            "&contentId=wemedia-mistories_5e285d253128e6776c4e0f1a&env=production&version=2" +
            "&ccc=cn-wemedia-mistories&cateCode=rec&category=%E7%A7%91%E6%8A%80&subCategory=%E5%B0%8F%E7%B1%B3%E7%A7%91%E6%8A%80" +
            "&traceId=1400677C932CCF9B989A60B2C1BC7918&source=%E7%A7%91%E6%8A%80%E5%A4%A7%E4%BD%AC" +
            "&eid=0%3A556%3A99%3A0%3A0%3A567%3A0%3A0%3A1071%3A895%3A758%3A0%3A0%3A0%3A198%3A%7C12385%2C19795%2C21971%2C21898%2C11831%2C12338" +
            "&authorId=1175684028&style=3&_miui_bottom_bar=comment&mi_source=miuibrowser&share=wechat";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Loger.d(TAG, "-->onCreate(): ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview_test);

        mUrlView = findViewById(R.id.input_content);
        mWebView = findViewById(R.id.web_view);
        mLoadBtn = findViewById(R.id.load_btn);
        mLoadBtn.setOnClickListener(v ->loadUrlInWebView(mUrlView.getText().toString()));

        initWebView();
    }

    private void initWebView() {
        Loger.d(TAG, "-->initWebView(): ");

        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setDomStorageEnabled(true);
        settings.setSupportMultipleWindows(true);
        settings.setNeedInitialFocus(true);
        settings.setGeolocationEnabled(true);

        settings.setDatabaseEnabled(true);
        String dir = getDir("database", Context.MODE_PRIVATE).getPath();
        settings.setDatabasePath(dir);

        mWebView.setScrollbarFadingEnabled(true);
        mWebView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        mWebView.setMapTrackballToArrowKeys(false); // use trackball directly

        mWebView.loadUrl(mDefaultUrl);
    }

    private void loadUrlInWebView(String url){
        Loger.d(TAG, "-->loadUrlInWebView(): url="+url);
        mWebView.loadUrl(url);
    }


}
