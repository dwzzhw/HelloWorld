package com.loading.common.utils;

import android.annotation.SuppressLint;
import android.os.AsyncTask;


public class AsyncOperationUtil {
    public static final String TAG = "AsyncOperationUtil";

    public static AsyncTask asyncOperation(final Runnable runnable) {
        return asyncOperation(runnable, null, "");
    }

    public static AsyncTask asyncOperation(final Runnable runnable,
                                           final AsyncOperationListener listener) {
        return asyncOperation(runnable, listener, "");
    }

    public static AsyncTask asyncOperation(final Runnable runnable,
                                           final AsyncOperationListener listener,
                                           String tag) {
        @SuppressLint("StaticFieldLeak") final AsyncTask<Void, Void, Void> tTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    if (runnable != null) {
                        runnable.run();
                    }
                } catch (Exception e) {
                    Loger.e(TAG, "write object exception: " + e);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                if (listener != null) {
                    listener.onOperationComplete(runnable);
                }
            }
        };

        if (UiThreadUtil.isMainThread()) {
            tTask.execute();
        } else {
            UiThreadUtil.postRunnable(() -> tTask.execute());
        }
        return tTask;
    }

    public static abstract class RunnableTag implements Runnable {
        private Object tagObj;

        public Object getTagObj() {
            return tagObj;
        }

        public void setTagObj(Object tag) {
            this.tagObj = tag;
        }
    }

    public interface AsyncOperationListener {
        void onOperationComplete(Object objData);
    }
}
