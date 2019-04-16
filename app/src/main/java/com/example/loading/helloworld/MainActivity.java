package com.example.loading.helloworld;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.loading.helloworld.activity.MiscTestActivity;
import com.example.loading.helloworld.activity.SportsTestActivity;
import com.example.loading.helloworld.activity.UITestActivity;
import com.example.loading.helloworld.download.SocketClientActivity;
import com.example.loading.helloworld.download.SocketServerActivity;
import com.loading.common.utils.Loger;
import com.loading.flutterbridge.demo.FlutterTestActivity;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;


public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        closeAndroidPDialog();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    private void closeAndroidPDialog() {
        try {
            Class aClass = Class.forName("android.content.pm.PackageParser$Package");
            Constructor declaredConstructor = aClass.getDeclaredConstructor(String.class);
            declaredConstructor.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Class cls = Class.forName("android.app.ActivityThread");
            Method declaredMethod = cls.getDeclaredMethod("currentActivityThread");
            declaredMethod.setAccessible(true);
            Object activityThread = declaredMethod.invoke(null);
            Field mHiddenApiWarningShown = cls.getDeclaredField("mHiddenApiWarningShown");
            mHiddenApiWarningShown.setAccessible(true);
            mHiddenApiWarningShown.setBoolean(activityThread, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onBtnClicked(View view) {
        Loger.d(TAG, "-->onBtnClicked()");
        Class targetClass = null;

        int viewId = view.getId();
        if (viewId == R.id.socket_server) {
            targetClass = SocketServerActivity.class;
        } else if (viewId == R.id.socket_client) {
            targetClass = SocketClientActivity.class;
        } else if (viewId == R.id.btn_sports_test) {
            targetClass = SportsTestActivity.class;
        } else if (viewId == R.id.btn_ui_test) {
            targetClass = UITestActivity.class;
        } else if (viewId == R.id.btn_misc_test) {
            targetClass = MiscTestActivity.class;
        } else if (viewId == R.id.bnt_open_flutter_page) {
            targetClass = FlutterTestActivity.class;
        }
        if (targetClass != null) {
            Log.d(TAG, "onBtnClicked: target class=" + targetClass);
            Intent intent = new Intent(this, targetClass);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Not impl yet!", Toast.LENGTH_SHORT).show();
        }
    }
}
