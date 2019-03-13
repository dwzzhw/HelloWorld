package com.example.loading.helloworld;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.loading.helloworld.activity.FlutterTestActivity;
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
        Class targetClass = null;
        switch (view.getId()) {
            case R.id.socket_server:
                targetClass = SocketServerActivity.class;
                break;
            case R.id.socket_client:
                targetClass = SocketClientActivity.class;
                break;
            case R.id.btn_sports_test:
                targetClass = SportsTestActivity.class;
                break;
            case R.id.btn_ui_test:
                targetClass = UITestActivity.class;
                break;
            case R.id.btn_misc_test:
                targetClass = MiscTestActivity.class;
                break;
            case R.id.bnt_open_flutter_page:
                targetClass = FlutterTestActivity.class;
                break;
        }
        if (targetClass != null) {
            Log.d(TAG, "onBtnClicked: target class=" + targetClass);
            Intent intent = new Intent(this, targetClass);
            startActivity(intent);
        }
    }
}
