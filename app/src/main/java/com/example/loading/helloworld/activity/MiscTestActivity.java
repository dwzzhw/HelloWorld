package com.example.loading.helloworld.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.loading.helloworld.R;
import com.example.loading.helloworld.utils.Loger;

import java.util.Iterator;
import java.util.LinkedHashSet;

public class MiscTestActivity extends BaseActivity {
    private static final String TAG = "MiscTestActivity";
    private TextView titleTextView = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_misc_test);

        titleTextView = (TextView) findViewById(R.id.title);
        titleTextView.setOnClickListener(mTitleClickListener);
    }

    private View.OnClickListener mTitleClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i(TAG, "Title view is clicked");
            doBinarySearch();
        }
    };

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
}
