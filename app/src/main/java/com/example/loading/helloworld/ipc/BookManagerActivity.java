package com.example.loading.helloworld.ipc;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.view.View;

import com.example.loading.helloworld.R;
import com.loading.common.component.BaseActivity;
import com.loading.common.utils.Loger;

import java.util.List;

public class BookManagerActivity extends BaseActivity {
    public static final String TAG = "BookManagerActivity_dwz";
    private static final int NEW_BOOK_ARRIVED = 1;

    // 客户端需要注册IOnNewBookArrivedListener到远程服务端，当有新书时服务端才能通知客户端
    // 在Activity退出时解除注册

    private IBookManager mRemoteBookManager;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == NEW_BOOK_ARRIVED) {
                Loger.d(TAG, "new book arrived: " + msg.obj);
            }
        }
    };

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Loger.d(TAG, "-->onServiceConnected(), name=" + name);
            // 绑定成功后将Binder对象转换成AIDL接口
            IBookManager bookManager = IBookManager.Stub.asInterface(service);
            try {
                mRemoteBookManager = bookManager;
                BookEntity bookEntity = new BookEntity("3", "Android AIDL");
                bookManager.addBook(bookEntity);
                List<BookEntity> list = bookManager.getBookList();
                for (BookEntity book : list) {
                    Loger.d(TAG, "Found book: " +
                            (book != null ? (book.getId() + "," + book.getName()) : "Null"));
                }
                bookManager.registerListener(mNewBookArrivedListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Loger.d(TAG, "-->onServiceDisconnected(), name=" + name);
            mRemoteBookManager = null;
        }
    };

    private IOnNewBookArrivedListener mNewBookArrivedListener = new IOnNewBookArrivedListener.Stub() {
        @Override
        public void onNewBookArrived(BookEntity newBook) throws RemoteException {
            mHandler.obtainMessage(NEW_BOOK_ARRIVED, newBook.getId()).sendToTarget();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ipc_book_ui);
    }

    public void onBtnClicked(View view) {
        Loger.d(TAG, "-->onBtnClicked()");
        if (view.getId() == R.id.btn_connect) {
            connectToService();
        } else if (view.getId() == R.id.btn_disconnect) {
            disconnectionService();
        }
    }

    private void connectToService() {
        Loger.d(TAG, "-->connectToService(): ");
        Intent intent = new Intent(this, BookManagerService.class);
        bindService(intent, mServiceConnection, BIND_AUTO_CREATE);
    }

    private void disconnectionService() {
        Loger.d(TAG, "-->disconnectionService(), mRemoteBookManager=" + mRemoteBookManager);
        if (mRemoteBookManager != null) {
            unbindService(mServiceConnection);
            mRemoteBookManager = null;
        }
    }

    @Override
    protected void onDestroy() {
        if (mRemoteBookManager != null && mRemoteBookManager.asBinder().isBinderAlive()) {
            try {
                mRemoteBookManager.unRegisterListener(mNewBookArrivedListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        unbindService(mServiceConnection);
        super.onDestroy();
    }
}

