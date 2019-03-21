package com.example.loading.helloworld.activity;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;

import com.example.loading.helloworld.MyTestParcel2;
import com.example.loading.helloworld.R;
import com.example.loading.helloworld.utils.Loger;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class SportsTestActivity extends BaseActivity {
    private static final String TAG = "SportsTestActivity";
    private boolean destroyFlag = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sports_test);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyFlag = true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            openKbsMatchDetailPage();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void openKbsMatchDetailPage() {
        Intent intent = new Intent();
//        Uri uri = Uri.parse("qqsports://kbsqqsports?type=11&matchId=23:819743");
        Uri uri = Uri.parse("qqsports://kbsqqsports?pageType=302&moduleId=135");
        intent.setData(uri);
        Loger.i(TAG, "-->openKbsMatchDetailPage(), uri=" + uri);
        startActivity(intent);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void jumpToMatchDetailPage() {
        Loger.d(TAG, "-->jumpToMatchDetailPage()");
        Intent intent = new Intent("qqsports.matchdetail");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle bundle = new Bundle();

        MyTestParcel2 parcel = new MyTestParcel2(87654);
//        bundle.putParcelable("dwz_par1", parcel);
        bundle.putString("dwz_par", "HelloBundle");
        bundle.putString("mid", "12345");

        intent.putExtra("dwz_par2", parcel);


        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void deleteShortCut() {
        Loger.i(TAG, "-->deleteShortCut()");
        Intent shortcut = new Intent("com.android.launcher.action.UNINSTALL_SHORTCUT");
        Intent tIntent = null;
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, "QQSports");

        tIntent = getLauncherIntent();
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, tIntent);
        Loger.d(TAG, "send delete short cut broadcast");
        sendBroadcast(shortcut);
    }

    public static Intent getLauncherIntent() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.addCategory("android.intent.category.MULTIWINDOW_LAUNCHER");
        ComponentName comp = new ComponentName("com.tencent.qqsports", "com.tencent.qqsports.ui.SplashActivity");
        intent.setComponent(comp);
        return intent;

//        Intent intent = new Intent();
//        String oldShortcutUri = "#Intent;action=android.intent.action.MAIN;category=android.intent.category.LAUNCHER;launchFlags=0x10200000;package=com.tencent.qqsports;component=com.tencent.qqsports/.ui.SplashActivity;end";
//        try {
//            Intent altShortcutIntent = Intent.parseUri(oldShortcutUri, 0);
//            intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, altShortcutIntent);
//            intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "QQSports");
//        } catch (URISyntaxException e) {
//        }
//        return intent;
    }

    private void logTest() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Loger.i(TAG, "catch log begin");
            runtime.exec("logcat -ce");
            Process process = runtime.exec("logcat -v time");
            printStream(process.getInputStream());
            Loger.i(TAG, "catch log end");
        } catch (IOException e) {
            e.printStackTrace();
            Loger.i(TAG, "catch log fail");
        }
    }

    private void printStream(final InputStream in) {
        Loger.i(TAG, "printStream(), in=" + in);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (in != null) {

                    File sdRoot = Environment.getExternalStorageDirectory();
                    Loger.i(TAG, "sdRoot=" + sdRoot);
                    File logRoot = new File(sdRoot, "qqsports_log");
                    if (!logRoot.exists()) {
                        logRoot.mkdirs();
                    }
                    File logFile = new File(logRoot, "log123.txt");
                    if (logFile.exists()) {
                        try {
                            logFile.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    Loger.i(TAG, "log file path =" + logFile.getAbsolutePath() + ", exist=" + logFile.exists());

                    BufferedOutputStream bos = null;
                    BufferedInputStream bin = new BufferedInputStream(in);
                    byte[] buffer = new byte[4096];
                    try {
                        bos = new BufferedOutputStream(new FileOutputStream(logFile));
                        Loger.i(TAG, "bos=" + bos);
                        if (bos != null) {
                            int cnt = 0;
                            while (!destroyFlag && (cnt = bin.read(buffer)) > 0) {
                                bos.write(buffer, 0, cnt);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        Loger.i(TAG, "printStream(), IOException");
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            in.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (bos != null) {
                            try {
                                bos.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        };

        new Thread(runnable).start();
    }
}
