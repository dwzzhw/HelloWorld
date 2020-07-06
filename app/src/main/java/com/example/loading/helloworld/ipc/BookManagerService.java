package com.example.loading.helloworld.ipc;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import androidx.annotation.Nullable;

import com.loading.common.utils.Loger;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class BookManagerService extends Service {
    public static final String TAG = "BookManagerService_dwz";

    // CopyOnWriterArrayList支持并发读写，AIDL方法是在服务端的Binder线程池中执行的，因此多个客户端同时连接的
    // 时候会存在多个线程同时访问的情形
    private CopyOnWriteArrayList<BookEntity> mBookList = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<IOnNewBookArrivedListener> mListenerList = new CopyOnWriteArrayList<>();
    private AtomicBoolean isServiceDestroyed = new AtomicBoolean(false);
    private Binder mBinder = new IBookManager.Stub() {
        @Override
        public List<BookEntity> getBookList() throws RemoteException {
            return mBookList;
        }

        @Override
        public void addBook(BookEntity book) throws RemoteException {
            mBookList.add(book);
        }

        @Override
        public void registerListener(IOnNewBookArrivedListener listener) throws RemoteException {
            if (!mListenerList.contains(listener)) {
                mListenerList.add(listener);
            }

            Loger.d(TAG, "registerListener: " + mListenerList.size());
        }

        @Override
        public void unRegisterListener(IOnNewBookArrivedListener listener) throws RemoteException {
            if (mListenerList.contains(listener)) {
                mListenerList.remove(listener);
            }

            Loger.d(TAG, "unRegisterListener: " + mListenerList.size());
        }
    };

    @Override
    public void onCreate() {
        Loger.d(TAG, "-->onCreate()");
        super.onCreate();
        mBookList.add(new BookEntity("1", "Hello Android"));
        mBookList.add(new BookEntity("2", "Hello Flutter"));
        new Thread(new AddBookWorker()).start();
    }

    @Override
    public void onDestroy() {
        isServiceDestroyed.set(true);
        Loger.d(TAG, "-->onDestroy()");
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Loger.d(TAG, "-->onBind(), intent=" + intent);
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Loger.d(TAG, "-->onUnbind(), intent=" + intent);
        return super.onUnbind(intent);
    }

    private class AddBookWorker implements Runnable {

        @Override
        public void run() {
            while (!isServiceDestroyed.get()) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                int bookId = mBookList.size() + 1;
                BookEntity bookEntity = new BookEntity(bookId + "", "new Book#" + bookId);
                try {
                    onNewBookArrived(bookEntity);
                } catch (RemoteException e) {
                    Loger.e(TAG, "-->AddBookWorker.run() exception, ", e);
                    e.printStackTrace();
                }
            }
            Loger.d(TAG, "-->AddBookWorker task finished, is service destroyed=" + isServiceDestroyed.get());
        }
    }

    private void onNewBookArrived(BookEntity bookEntity) throws RemoteException {
        Loger.d(TAG, "-->onNewBookArrived(), book=" + bookEntity);
        mBookList.add(bookEntity);
        for (int i = 0; i < mListenerList.size(); i++) {
            IOnNewBookArrivedListener listener = mListenerList.get(i);
            listener.onNewBookArrived(bookEntity);
        }
    }
}
