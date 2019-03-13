package com.example.loading.helloworld;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.loading.helloworld.activity.MiscTestActivity;
import com.example.loading.helloworld.activity.SportsTestActivity;
import com.example.loading.helloworld.activity.UITestActivity;
import com.example.loading.helloworld.download.SocketClientActivity;
import com.example.loading.helloworld.download.SocketServerActivity;
import com.example.loading.helloworld.utils.Loger;


public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName() + "_dwz";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onBtnClicked(View view) {
        Loger.d(TAG, "-->onBtnClicked()");
        switch (view.getId()) {
            case R.id.socket_server:
                startSocketServer();
                break;
            case R.id.socket_client:
                startSocketClient();
                break;
            case R.id.btn_sports_test:
                startSportsTestPage();
                break;
            case R.id.btn_ui_test:
                startUITestPage();
                break;
            case R.id.btn_misc_test:
                startMiscTestPage();
                break;
        }
    }

    private void startSocketServer() {
        Loger.d(TAG, "-->startSocketServer()");
        Intent intent = new Intent(this, SocketServerActivity.class);
        startActivity(intent);
    }

    private void startSocketClient() {
        Loger.d(TAG, "-->startSocketClient()");
        Intent intent = new Intent(this, SocketClientActivity.class);
        startActivity(intent);
    }

    private void startSportsTestPage() {
        Loger.d(TAG, "-->startSportsTestPage()");
        Intent intent = new Intent(this, SportsTestActivity.class);
        startActivity(intent);
    }

    private void startUITestPage() {
        Loger.d(TAG, "-->startUITestPage()");
        Intent intent = new Intent(this, UITestActivity.class);
        startActivity(intent);
    }

    private void startMiscTestPage() {
        Loger.d(TAG, "-->startMiscTestPage()");
        Intent intent = new Intent(this, MiscTestActivity.class);
        startActivity(intent);
    }
}
