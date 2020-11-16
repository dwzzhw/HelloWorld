package com.example.loading.helloworld.activity;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.loading.helloworld.R;
import com.example.loading.helloworld.activity.misc.RxJavaTestActivity;
import com.example.loading.helloworld.activity.misc.SortTestActivity;
import com.example.loading.helloworld.activity.misc.StorageTester;
import com.example.loading.helloworld.ipc.BookManagerActivity;
import com.example.loading.helloworld.mykotlin.ui.HelloKotlinActivity;
import com.example.loading.libjava.utils.LogUtil;
import com.loading.common.component.BaseActivity;
import com.loading.common.utils.AsyncOperationUtil;
import com.loading.common.utils.Loger;
import com.loading.common.utils.PermissionUtils;
import com.loading.common.utils.UiThreadUtil;
import com.loading.common.widget.TipsToast;
import com.loading.modules.interfaces.face.FaceModuleMgr;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.regex.Pattern;

import miui.browser.core.ShareMemoryController;

public class MiscTestActivity extends BaseActivity {
    private static final String TAG = "MiscTestActivity";
    private TextView titleTextView = null;
    private TextView bbsBoardView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_misc_test);

        titleTextView = (TextView) findViewById(R.id.title);
        titleTextView.setOnClickListener(mTitleClickListener);
        bbsBoardView = findViewById(R.id.bbs_board);
    }

    private View.OnClickListener mTitleClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i(TAG, "Title view is clicked");
            printMemoryInfo();
            doMiscTest();
//            doINetTest();
            doTryCatchTest();
            doReflectArrayTest();
//            doShareMemoryTest();
            testStringFormat();
        }
    };

    public void onBtnClicked(View view) {
        Loger.d(TAG, "-->onBtnClicked()");
        if (view.getId() == R.id.misc_test_01) {
//            doINetTest();
            doEmojoFaceTest();
        } else if (view.getId() == R.id.btn_rxjava) {
            enterRxJavaPage();
        } else if (view.getId() == R.id.btn_string) {
            doStringTest();
        } else if (view.getId() == R.id.btn_order) {
            enterOrderTestPage();
        } else if (view.getId() == R.id.btn_ipc) {
            enterIpcTestPage();
        } else if (view.getId() == R.id.btn_kotlin) {
            enterKotlinTestPage();
        } else if (view.getId() == R.id.btn_storage) {
            doStorageTest();
        } else if (view.getId() == R.id.btn_export_log) {
            exportLog();
        } else if (view.getId() == R.id.btn_gen_log) {
            genLog();
        } else if (view.getId() == R.id.btn_pdf) {
            openPdf();
        }
    }

    private void doINetTest() {
        Loger.d(TAG, "-->doINetTest()");
        AsyncOperationUtil.asyncOperation(
                () -> {
                    try {
                        InetAddress address = InetAddress.getByName("1.2.3.4.");
                        Loger.d(TAG, "-->doINetTest(), address=" + address);
                        Toast.makeText(MiscTestActivity.this, "address=" + address, Toast.LENGTH_SHORT).show();
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                        Loger.e(TAG, "-->doINetTest(), exception=" + e, e);
                    }
                }
        );
    }

    private void doTryCatchTest() {
        Loger.d(TAG, "-->doTryCatchTest(), result=" + getTryCatchResult());

        int a = 10;
        Integer b = 10;
        Integer c = new Integer(10);
        Loger.d(TAG, "-->Integer test(), int10==Integer10?" + (a == b)
                + ",\n AutoInteger(10)==new Integer10?" + (b == c)
                + ", \nint10==new Integer10?" + (a == c));
    }

    private String getTryCatchResult() {
        String result = "Init";
        try {
            result += " try ";
            Loger.d(TAG, "-->before try return");
            return result = result + " return1 ";
        } catch (Exception e) {
            result += " catch";
            return result = result + " return2 ";
        } finally {
            result += " finally";
            Loger.d(TAG, "-->before finally return");
            return result = result + " return3 ";
        }

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
        Loger.d(TAG, "gpower result, n=" + n);
        if (n == 0)
            return 1;
        if (n > 31) {
            Loger.d(TAG, "error from power(" + n + "): integer overflow");
            return 0;
        }
        long val = gpower(n >> 1) * gpower(n >> 1);
        if ((n & 1) > 0)
            val *= 2;
        Loger.d(TAG, "gpower result, n=" + n + ", result=" + val);
        return val;
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

    private void doEmojoFaceTest() {
        String testStr = "一个表情[坏笑]";
        bbsBoardView.setText(testStr);

        UiThreadUtil.postDelay(new Runnable() {
            @Override
            public void run() {
                bbsBoardView.setText(FaceModuleMgr.convertToSpannableStr(testStr, bbsBoardView));
            }
        }, 2000);
    }

    private void doStringTest() {
        Loger.d(TAG, "-->doStringTest()");
        String s0 = "Hello" + "World";
        String s1 = "HelloWorld";
        String s2 = "Hello" + new String("World");
        String s3 = new String("Hello") + "World";
        String s4 = new String("HelloWorld");
        Loger.d(TAG, "-->doStringTest: s0==s1?" + (s1 == s0) + ",s1==s2?" + (s1 == s2) + ", s2=s3?" + (s2 == s3) + ", s3=s4?" + (s3 == s4));

    }

    private void enterOrderTestPage() {
        Intent intent = new Intent(this, SortTestActivity.class);
        startActivity(intent);
    }

    private void enterIpcTestPage() {
        Intent intent = new Intent(this, BookManagerActivity.class);
        startActivity(intent);
    }

    private void enterKotlinTestPage() {
        HelloKotlinActivity.Companion.startActivity(this);
//        Intent intent = new Intent(this, HelloKotlinActivity.class);
//        startActivity(intent);
    }

    private void doStorageTest() {
        Loger.d(TAG, "-->doStorageTest()");
        new StorageTester().getExternalSDCardPath(this);
    }

    private void enterRxJavaPage() {
        Intent intent = new Intent(this, RxJavaTestActivity.class);
        startActivity(intent);

        FutureTask task = new FutureTask(new Callable() {
            @Override
            public Object call() throws Exception {
                return null;
            }
        });
        new Thread(task).run();
    }

    private void printMemoryInfo() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        int memoryClass = activityManager.getMemoryClass();

        long maxMemoryFromRun = Runtime.getRuntime().maxMemory();
        long totalMemoryFromRun = Runtime.getRuntime().totalMemory();
        long freeMemoryFromRun = Runtime.getRuntime().freeMemory();
        Loger.d(TAG, "-->printMemoryInfo(), memory class=" + memoryClass
                + ", runtime memory max=" + maxMemoryFromRun
                + ", total=" + totalMemoryFromRun
                + ", free=" + freeMemoryFromRun);
        TipsToast.getInstance().showTipsText("memory class=" + memoryClass
                + ", runtime memory max=" + maxMemoryFromRun / 1024 / 1024);
    }

    private void doMiscTest() {
        Loger.d(TAG, "-->doMiscTest(), ");

        int a = (1 << 31);
        int b = Math.abs(a);
        TipsToast.getInstance().showTipsText("Math.abs(" + a + ") is " + b);
        Loger.d(TAG, "-->doMiscTest(), a=" + a + ", b=" + b);

        String hostPatStr = ".*-test$";
//        String host = "aaamgzgmyzzaaaaa";
//        String hostPatStr = ".*\\.mi\\.com$";
//        String host = "mm.mi.com";
        String host = "feature-novel-test";
        Pattern pattern = Pattern.compile(hostPatStr);

        String jsonStr = "{value:[[a,111],[b,222]]}";
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            JSONArray jsonArray = jsonObject.optJSONArray("value");
            Object obj = jsonArray.get(1);
            String value = null;
            if (obj instanceof JSONArray) {
                value = ((JSONArray) obj).getString(1);
            }

            JSONArray rules = null;
            JSONObject ruleObj = new JSONObject("{rules:[[\".*mgzgmyzz\\\\.com$\",\"\"],[\"\",\"com.taobao.taobao\"],[\"hostT\",\"packageT\"]]}");
            if (ruleObj != null) {
                rules = ruleObj.optJSONArray("rules");

                String hostPt1 = ((JSONArray) rules.get(0)).optString(0);
                String hostPt2 = ((JSONArray) rules.get(1)).optString(1);
                Pattern pattern1 = Pattern.compile(hostPt1);
                Pattern pattern2 = Pattern.compile(hostPt2);
                Loger.d(TAG, "-->doMiscTest(): match 1=" + pattern1.matcher("main.mgzgmyzz.com").matches()
                        + ", match2=" + pattern2.matcher("com.taobao.taobao").matches() + ", hostPt1=" + hostPt1);
            }


            Loger.d(TAG, "-->doMiscTest(): jsonObject=" + jsonObject + ", value=" + value + ", array=" + jsonArray + ", rules=" + rules);
        } catch (JSONException e) {
            Loger.e("Parse json fail", e);
            e.printStackTrace();
        }

        Loger.d(TAG, "-->doMiscTest(), host=" + host + ", pattern=" + hostPatStr + ", match=" + pattern.matcher(host).matches());
    }

    public static void testStringFormat() {
        String format = "%_hello1 %s";
        String result = String.format(format, "World");
        LogUtil.d(TAG, "-->testStringFormat(), result=" + result);
    }

    private void doReflectArrayTest() {
        try {
            Class intArray = Class.forName("[I");
            Class strArray = Class.forName("[Ljava.lang.String;");
            Class activityArray = Class.forName("[Landroid.app.Activity;");
            Loger.d(TAG, "-->doMiscTest(), intArray class=" + intArray + ", comp type=" + intArray.getComponentType()
                    + ", strArray class=" + strArray + ", comp type=" + strArray.getComponentType()
                    + ", activityArray class=" + activityArray + ", comp type=" + activityArray.getComponentType());
        } catch (ClassNotFoundException e) {
            Loger.w(TAG, "-->fail parse array, ", e);
            e.printStackTrace();
        }
    }

    private void doShareMemoryTest() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 10000; i++) {
                    String key = ShareMemoryController.writeData(MiscTestActivity.this, new MyCard(2 * i));
                    MyCard result = ShareMemoryController.readData(MiscTestActivity.this, key);
                    Loger.d(TAG, "-->doShareMemoryTest(), index=" + i + ", result=" + result.mIndex);
                    if (2 * i != result.mIndex) {
                        Loger.w(TAG, "-->doShareMemoryTest(), found error, index=" + i + ", result=" + result.mIndex);
                    }
                }
            }
        }).start();

        for (int i = 0; i < 10000; i++) {
            String key = ShareMemoryController.writeData(this, new MyCard(i));
            MyCard result = ShareMemoryController.readData(this, key);
            Loger.d(TAG, "-->doShareMemoryTest(), index=" + i + ", result=" + result.mIndex);
            if (i != result.mIndex) {
                Loger.w(TAG, "-->doShareMemoryTest(), found error, index=" + i + ", result=" + result.mIndex);
            }
        }
    }


    private static class MyCard {
        public int mIndex;

        public MyCard(int index) {
            mIndex = index;
        }
    }

    public void exportLog() {
        Loger.d(TAG, "-->exportLog()");

        if (!PermissionUtils.hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            PermissionUtils.requestPermissionWithTips(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, "需要外卡权限以存储日志",
                    granted -> {
                        if (granted) {
                            Loger.writeLogToFile();
                        } else {
                            Loger.d(TAG, "No granted storage permission.");
                        }
                    });
        } else {
            Loger.writeLogToFile();
        }


    }

    private static int sGlobalIndex = 0;

    public void genLog() {
        for (int i = 0; i < 103; i++) {
            Loger.d(TAG, "mock log _ " + sGlobalIndex++);
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void openPdf() {
        if (LogUtil.enable()) {
            LogUtil.d(TAG, "-->openPdf(): ");
        }

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("content://com.tencent.mm.external.fileprovider/external/Android/data/com.tencent.mm/" +
                "MicroMsg/7b2777cde732fa815edd625212f226b2/favorite/0/3b6eae56c67115cc2115672e267e25bb.pdf"));

        PackageManager packageManager = getPackageManager();

        List<ResolveInfo> resolveInfoList = packageManager.queryIntentActivities(intent, 0);

        if (LogUtil.enable()) {
            LogUtil.d(TAG, "-->openPdf(): resolveInfo=" + resolveInfoList);
        }


        startActivity(intent);

    }
}
