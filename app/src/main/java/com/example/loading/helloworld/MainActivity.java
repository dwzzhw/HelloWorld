package com.example.loading.helloworld;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.loading.helloworld.download.SocketClientActivity;
import com.example.loading.helloworld.download.SocketServerActivity;
import com.example.loading.helloworld.lottie.LottieTestActivity;
import com.example.loading.helloworld.utils.Loger;
import com.example.loading.helloworld.utils.Utils;
import com.example.loading.helloworld.view.CustomizedTextDrawable;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedHashSet;


public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName() + "_dwz";
    private CountDownCircleBar mCountingBar = null;
    private EditText testTextView = null;
    private TextView titleTextView = null;

    private boolean destroyFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCountingBar = (CountDownCircleBar) findViewById(R.id.counting_bar);
//        mCountingBar.setcountingListener(new CountDownCircleBar.ICountingListener() {
//            @Override
//            public void onCountingFinished() {
//                Log.d(TAG, "-->onCountingFinished()");
//            }
//
//            @Override
//            public void onCountingCanceled() {
//                Log.d(TAG, "-->onCountingCanceled()");
//            }
//        });
        testTextView = (EditText) findViewById(R.id.text_test);
        titleTextView = (TextView) findViewById(R.id.title);
        titleTextView.setOnClickListener(mTitleClickListener);

        mCountingBar.setOnClickListener(mClickListener);
//        testTextView.setOnClickListener(mClickListener);
        mCountingBar.setEnabled(true);

//        logTest();
//        jumpToMatchDetailPage();
//        bgTest();
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
        }
    }

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i(TAG, "dwz View is clicked");
//            openKbsMatchDetailPage();
//            changeTitle();
//            openSurfacePage();
//            openLottiePage();
//            gpower(5);
//            openViewDrawingOrderPage();
            doBinarySearch();
        }
    };

    private View.OnClickListener mTitleClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i(TAG, "Title view is clicked");
            changeTitle();

        }
    };

    int cnt = 0;

    private void changeTitle() {
        String titleStr = null;
        if (cnt % 2 == 0) {
            titleStr = "Hello";
        } else {
            titleStr = "Hello World Hello World Hello World";
        }
        Log.d(TAG, "set title:" + titleStr);
        cnt++;
        titleTextView.setText(titleStr);
    }

    //你好·studio
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCountingBar.startCounting(10);


//        mCountingBar.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                mCountingBar.stopCounting();
//            }
//        }, 5000);
        textTest();
//        deleteShortCut();
//        testHashSet();
//        startMemoryService();

//        deadLockTest();

//        GsonTest.testGson(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyFlag = true;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        Log.w(TAG, "dwz -->onKeyUp(), keyCode=" + keyCode);
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.w(TAG, "dwz -->onKeyDown(), keyCode=" + keyCode);

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        Log.w(TAG, "dwz -->onBackPressed()");
        super.onBackPressed();
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
        Log.i(TAG, "dwz -->openKbsMatchDetailPage(), uri=" + uri);
        startActivity(intent);
    }

    private void openSurfacePage() {
        Intent intent = new Intent(this, SurfaceViewTestActivity.class);
        startActivity(intent);
    }

    private void openLottiePage() {
        Intent intent = new Intent(this, LottieTestActivity.class);
        startActivity(intent);
    }

    private void openViewDrawingOrderPage() {
        Intent intent = new Intent(this, ViewDrawingOrderTestActivity.class);
        startActivity(intent);
    }

    private void doBinarySearch() {
        int[] arrays = new int[]{1, 3, 6, 8, 9, 11};
        int result = BiSearch(arrays, 6, 7);
        Loger.d(TAG, "-->doBinarySearch(), result=" + result);
    }

    private int BiSearch(int a[], int n, int key) {
        int low = 0, high = n - 1;
        int mid;
        while (low <= high) {
            mid = (low + high) / 2;
            if (a[mid] == key)
                return mid;
            else if (a[mid] < key) low = mid + 1;
            else
                high = mid - 1;
        }
        return -1;
    }

    private long gpower(int n) {
        Loger.d(TAG, "dwz gpower result, n=" + n);
        if (n == 0)
            return 1;
        if (n > 31) {
            Loger.d(TAG, "error from power(" + n + "): integer overflow");
            return 0;
        }
        long val = gpower(n >> 1) * gpower(n >> 1);
        if ((n & 1) > 0)
            val *= 2;
        Loger.d(TAG, "dwz gpower result, n=" + n + ", result=" + val);
        return val;
    }

    private TextWatcher mTextWatcher = new TextWatcher() {
        private int atSymbleIndex = -1;    //该位置出现了一个@，可能是手动输入或黏贴的最后一个字符，也可能是从后面开始删除到该位置

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            Log.d(TAG, "dwz-->onTextChanged(), s=" + s + ", start=" + start + ", before=" + before + ", count=" + count);
            atSymbleIndex = -1;
            if (before == 0 && count > 0) {   //手动输入或黏贴的最后一个字符 @
                if (s.charAt(start + count - 1) == '@') {
                    Log.i(TAG, "input @");
                    atSymbleIndex = start + count - 1;
                }
            } else if (before > 0 && count == 0) {  //从后面开始删除字符，遇到@
                if (start > 0 && s.charAt(start - 1) == '@') {
                    Log.i(TAG, "delete then encount @");
                    atSymbleIndex = start - 1;
                }
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            Log.d(TAG, "dwz-->beforeTextChanged(), s=" + s + ", start=" + start + ", after=" + after + ", count=" + count);
        }

        @Override
        public void afterTextChanged(Editable s) {
            Log.d(TAG, "dwz-->afterTextChanged(), s=" + s);
            int encountedAtIndex = atSymbleIndex;
            if (encountedAtIndex >= 0 && s.charAt(encountedAtIndex) == '@') {
                Log.i(TAG, "Do auto replace now, atSymbleIndex=" + encountedAtIndex);
                String faceContent = "@主持人";
                s.replace(encountedAtIndex, encountedAtIndex + 1, faceContent);

                CustomizedTextDrawable faceDrawable = new CustomizedTextDrawable(MainActivity.this, faceContent, testTextView.getPaint());

                faceDrawable.setBounds(0, 0, faceDrawable.getIntrinsicWidth(), faceDrawable.getIntrinsicHeight());
                ImageSpan imageSpan = new ImageSpan(faceDrawable, DynamicDrawableSpan.ALIGN_BOTTOM);
                Log.i(TAG, "before crash, atSymbleIndex=" + encountedAtIndex + ", string length=" + s.length());
                s.setSpan(imageSpan, encountedAtIndex, encountedAtIndex + faceContent.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    };

    private void textTest() {
        testTextView.removeTextChangedListener(mTextWatcher);
        testTextView.addTextChangedListener(mTextWatcher);

        String formerPart = "大个红字123";
        String laterPart = "small blue";

        SpannableString spannableString = new SpannableString(formerPart + laterPart);
        AbsoluteSizeSpan bigSizeSpan = new AbsoluteSizeSpan(20, true);
        AbsoluteSizeSpan smallSizeSpan = new AbsoluteSizeSpan(12, true);
        ForegroundColorSpan redSpan = new ForegroundColorSpan(0xffff0000);
        ForegroundColorSpan blueSpan = new ForegroundColorSpan(0xff0000ff);

        Bitmap faceIcon = BitmapFactory.decodeResource(getResources(), R.drawable.emo_banku);
//        Drawable faceDrawable = new BitmapDrawable(getResources(), faceIcon);
        String faceContent = "@主持人";
        CustomizedTextDrawable faceDrawable = new CustomizedTextDrawable(this, faceContent, testTextView.getPaint());

        faceDrawable.setBounds(0, 0, faceDrawable.getIntrinsicWidth(), faceDrawable.getIntrinsicHeight());
        ImageSpan imageSpan = new ImageSpan(faceDrawable, DynamicDrawableSpan.ALIGN_BOTTOM);


        spannableString.setSpan(bigSizeSpan, 0, formerPart.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(smallSizeSpan, formerPart.length(), formerPart.length() + laterPart.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(redSpan, 0, formerPart.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(blueSpan, formerPart.length(), formerPart.length() + laterPart.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(imageSpan, 3, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

//        SpannableStringBuilder builder = new SpannableStringBuilder(spannableString);
//        builder.setSpan(imageSpan, 1, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        String contentStr = "<font size='" + 50 + "'>" + 12 + "</font> <font size='" + 30 + "'>K币</font>";

        // color='#000000'

//        testTextView.setText(Html.fromHtml(contentStr));
        testTextView.setText(spannableString);
//        for(;;){
//            int i =999;
//            int j = 999;
//            int k = i*j;
////              Log.d(TAG, " only print log");
//        }

//        Log.d(TAG, "dwz long sleep begin");
//        try {
//            Thread.sleep(30000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        Log.d(TAG, "dwz long sleep end");
    }

    private void deleteShortCut() {
        Log.i(TAG, "dwz -->deleteShortCut()");
        Intent shortcut = new Intent("com.android.launcher.action.UNINSTALL_SHORTCUT");
        Intent tIntent = null;
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, "QQSports");

        tIntent = getLauncherIntent();
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, tIntent);
        Log.d(TAG, "dwz send delete short cut broadcast");
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


    private void testHashSet() {
        LinkedHashSet<String> hashSet = new LinkedHashSet<String>();
        hashSet.add("Item1");
        hashSet.add("Item2");
        hashSet.add("Item3");
        hashSet.add("Item4");
        hashSet.add("Item5");

        Log.i(TAG, "testHashSet(), set size=" + hashSet.size());
        Iterator<String> iterator = hashSet.iterator();
        if (iterator.hasNext()) {
            String item = iterator.next();
            Log.i(TAG, "testHashSet(), after next()1, item=" + item);
            String item2 = hashSet.iterator().next();
            Log.i(TAG, "testHashSet(), after next()2, item=" + item2);
            String item3 = iterator.next();
            Log.i(TAG, "testHashSet(), after next()3, item=" + item2);

            iterator.remove();
            Log.i(TAG, "testHashSet(), after remove, size=" + hashSet.size());
            for (String t : hashSet) {
                Log.i(TAG, "item=" + t);
            }
        }
    }

    private void startMemoryService() {
        Log.i(TAG, "startMemoryService()");
        Intent intent = new Intent(this, MemoryMonitorService.class);
        startService(intent);
    }

    public void deadLockTest() {
        Object obj1 = new Object();
        Object obj2 = new Object();
        Object obj3 = new Object();

        Thread t1 = new Thread(new SyncThread(obj1, obj2), "t1");
        Thread t2 = new Thread(new SyncThread(obj2, obj3), "t2");
        Thread t3 = new Thread(new SyncThread(obj3, obj1), "t3");

        t1.start();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        t2.start();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        t3.start();

        System.out.println("UI acquiring lock on " + obj1);
        synchronized (obj1) {
            System.out.println("UI acquired lock on " + obj1);
        }

    }

    class SyncThread implements Runnable {
        private Object obj1;
        private Object obj2;

        public SyncThread(Object o1, Object o2) {
            this.obj1 = o1;
            this.obj2 = o2;
        }

        @Override
        public void run() {
            String name = Thread.currentThread().getName();
            System.out.println(name + " acquiring lock on " + obj1);
            synchronized (obj1) {
                System.out.println(name + " acquired lock on " + obj1);
                work();
                System.out.println(name + " acquiring lock on " + obj2);
                synchronized (obj2) {
                    System.out.println(name + " acquired lock on " + obj2);
                    work();
                }
                System.out.println(name + " released lock on " + obj2);
            }
            System.out.println(name + " released lock on " + obj1);
            System.out.println(name + " finished execution.");
        }

        private void work() {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void logTest() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Log.i(TAG, "dwz catch log begin");
            runtime.exec("logcat -ce");
            Process process = runtime.exec("logcat -v time");
            printStream(process.getInputStream());
            Log.i(TAG, "dwz catch log end");
        } catch (IOException e) {
            e.printStackTrace();
            Log.i(TAG, "dwz catch log fail");
        }
    }

    private void printStream(final InputStream in) {
        Log.i(TAG, "printStream(), in=" + in);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (in != null) {

                    File sdRoot = Environment.getExternalStorageDirectory();
                    Log.i(TAG, "dwz sdRoot=" + sdRoot);
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
                    Log.i(TAG, "dwz log file path =" + logFile.getAbsolutePath() + ", exist=" + logFile.exists());

                    BufferedOutputStream bos = null;
                    BufferedInputStream bin = new BufferedInputStream(in);
                    byte[] buffer = new byte[4096];
                    try {
                        bos = new BufferedOutputStream(new FileOutputStream(logFile));
                        Log.i(TAG, "dwz bos=" + bos);
                        if (bos != null) {
                            int cnt = 0;
                            while (!destroyFlag && (cnt = bin.read(buffer)) > 0) {
                                bos.write(buffer, 0, cnt);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.i(TAG, "printStream(), IOException");
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


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void jumpToMatchDetailPage() {
        Log.i(TAG, "dwz1 -->jumpToMatchDetailPage()");
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

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void bgTest() {
        if (titleTextView != null) {
            Drawable bgDrawable = Utils.getTL2BRGradientMaskDrawable(new int[]{0x22ff0000, 0x2200ff00}, R.drawable.emo_banku, this);
            Log.i(TAG, "dwz -->bgTest(), bgDrawable=" + bgDrawable);
            titleTextView.setBackground(bgDrawable);
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
}
