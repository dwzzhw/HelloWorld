package com.example.loading.helloworld.activity;

import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.app.IntentService;
import android.app.job.JobService;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.example.loading.helloworld.R;
import com.example.loading.helloworld.activity.misc.RxJavaTestActivity;
import com.example.loading.helloworld.activity.misc.SortTestActivity;
import com.example.loading.helloworld.ipc.BookManagerActivity;
import com.loading.common.component.BaseActivity;
import com.loading.common.utils.AsyncOperationUtil;
import com.loading.common.utils.Loger;
import com.loading.common.utils.UiThreadUtil;
import com.loading.modules.interfaces.face.FaceModuleMgr;

import java.lang.ref.PhantomReference;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

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
            doINetTest();
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
}
