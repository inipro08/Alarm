package com.datpt10.alarmup.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.widget.RemoteViews;

import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.datpt10.alarmup.R;
import com.datpt10.alarmup.activity.AlarmActivity;
import com.datpt10.alarmup.model.AlarmEntity;
import com.datpt10.alarmup.receiver.AlarmReceiver;
import com.datpt10.alarmup.receiver.NotiAlarmReceiver;

public class AlarmService extends Service {
    private static final String CHANNEL_ID = "CHANNEL_ID";
    private NotificationManager notificationManager;
    private AlarmEntity alarmEntity;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        alarmEntity = intent.getParcelableExtra(AlarmActivity.EXTRA_ALARM);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            assert notificationManager != null;
            notificationManager.createNotificationChannel(new NotificationChannel(CHANNEL_ID, "Alarms", NotificationManager.IMPORTANCE_HIGH));
        }
        RemoteViews notificationLayout = new RemoteViews(getPackageName(), R.layout.custom_notification_alarm);
        notificationLayout.setTextViewText(R.id.tv_noti_title, "Alarm");
        notificationLayout.setTextViewText(R.id.tv_noti_content, alarmEntity.getContent(getApplicationContext()));
        notificationLayout.setOnClickPendingIntent(R.id.bt_noti_after_5, onNotificationClick(R.id.bt_noti_after_5));
        notificationLayout.setOnClickPendingIntent(R.id.bt_noti_cancel, onNotificationClick(R.id.bt_noti_cancel));

        Notification notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSmallIcon(R.drawable.ic_alarm)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCustomContentView(notificationLayout)
                .setFullScreenIntent(getFullScreenIntent(getApplicationContext()), true)
                .build();

        startForeground(AlarmReceiver.NOTIFICATION_ID, notification);

        return START_NOT_STICKY;
    }

    private PendingIntent onNotificationClick(@IdRes int id) {
        Intent intentNoti = new Intent(getApplicationContext(), NotiAlarmReceiver.class);
        intentNoti.putExtra(NotiAlarmReceiver.EXTRA_BUTTON_CLICKED, id);
        intentNoti.putExtra(AlarmActivity.EXTRA_ALARM, alarmEntity);
        return PendingIntent.getBroadcast(getApplicationContext(), id, intentNoti, 0);
    }

    private PendingIntent getFullScreenIntent(Context applicationContext) {
        Intent showActivityIntent = new Intent(applicationContext, AlarmActivity.class);
        showActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return PendingIntent.getActivity(applicationContext, 0, showActivityIntent, 0);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
    }
}
