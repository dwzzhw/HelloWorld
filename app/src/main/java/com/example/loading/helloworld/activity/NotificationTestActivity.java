package com.example.loading.helloworld.activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.RemoteViews;

import com.example.loading.helloworld.R;
import com.loading.common.component.BaseActivity;
import com.loading.common.utils.Loger;

//https://www.jianshu.com/p/c0b0ed829241
public class NotificationTestActivity extends BaseActivity {

    private static final String TAG = "SportsTestActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_test);
    }

    public void onBtnClicked(View view) {
        int viewId = view.getId();
        if (viewId == R.id.show_common_notification) {
            showCommonNotification(viewId);
        } else if (viewId == R.id.show_large_pic_notification) {
            showLargePicNotification(viewId);
        } else if (viewId == R.id.show_progress_notification) {
            showProgressNotification(viewId);
        } else if (viewId == R.id.show_small_custom_notification) {
            showCustomNotification(viewId, true, false);
        } else if (viewId == R.id.show_large_custom_notification) {
            showCustomNotification(viewId, false, true);
        } else if (viewId == R.id.show_both_custom_notification) {
            showCustomNotification(viewId, true, true);
        } else if (viewId == R.id.show_full_screen_notification) {
            showFullScreenNotification(viewId);
        }


    }

    private void showCommonNotification(int id) {
        Loger.d(TAG, "-->showCommonNotification");
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle("NormalContentTitle")
                .setContentText("NormalContentText")
                .setContentIntent(getPendingIntent())
                .setSmallIcon(R.drawable.ic_launcher)
                .setTicker("I am ticker text")
                .setNumber(919)
                .setAutoCancel(false);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(id, builder.build());
    }

    private Bitmap getLargeBitmap() {
        return BitmapFactory.decodeResource(getResources(), R.drawable.medium_img);
    }

    private Bitmap getLargeIcon() {
        return BitmapFactory.decodeResource(getResources(), R.drawable.flower);
    }

    private void showLargePicNotification(int id) {
        Loger.d(TAG, "-->showLargePicNotification");
        Bitmap bitmap = getLargeBitmap();
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle("LargePicContentTitle")
                .setContentText("LargePicContentText")
                .setContentIntent(getPendingIntent())
                .setSmallIcon(R.drawable.ic_launcher)
                .setLargeIcon(getLargeIcon())
                .setPriority(Notification.PRIORITY_MAX)
                .setStyle(new Notification.BigPictureStyle().bigPicture(bitmap))
                .setAutoCancel(false);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(id, builder.build());
    }

    private void showProgressNotification(int id) {
        Loger.d(TAG, "-->showProgressNotification");
        Notification.Builder builder = new Notification.Builder(this);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        builder.setContentTitle("ProgressContentTitle")
                .setContentText("ProgressContentText")
                .setContentIntent(getPendingIntent())
                .setSmallIcon(R.drawable.ic_launcher)
                .setTicker("I am progress ticker text")
                .setAutoCancel(false);

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 100; i++) {
                    builder.setProgress(100, i + 1, false);
                    manager.notify(id, builder.build());
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void showFullScreenNotification(int id) {
        Loger.d(TAG, "-->showFullScreenNotification");
        Notification.Builder builder = new Notification.Builder(this);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        builder.setContentTitle("FullScreenContentTitle")
                .setContentText("FullScreenContentText")
                .setFullScreenIntent(getPendingIntent(), false)
                .setSmallIcon(R.drawable.ic_launcher)
                .setTicker("I am FullScreen ticker text")
                .setAutoCancel(false);

        manager.notify(id, builder.build());
    }

    private void showCustomNotification(int id, boolean withSmall, boolean withLarge) {
        Loger.d(TAG, "-->showCustomNotification, withSmall=" + withSmall + ", withLarge=" + withLarge);
        Notification.Builder builder = new Notification.Builder(this);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        RemoteViews smallRemoteViews = new RemoteViews("com.loading.appshell", R.layout.custom_small_remote_view);
        RemoteViews largeRemoteViews = new RemoteViews("com.loading.appshell", R.layout.custom_large_remote_view);

        builder.setContentTitle("CustomContentTitle")
                .setContentText("CustomContentText")
                .setContentIntent(getPendingIntent())
                .setSmallIcon(R.drawable.ic_launcher)
                .setTicker("I am Custom ticker text")
                .setAutoCancel(false);
        if (withSmall) {
            builder.setCustomContentView(smallRemoteViews);
        }
        if (withLarge) {
            builder.setCustomBigContentView(largeRemoteViews);
        }

        manager.notify(id, builder.build());
    }

    private PendingIntent getPendingIntent() {
        Intent clickIntent = new Intent(this, MiscTestActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        return pendingIntent;
    }
}
