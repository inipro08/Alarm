package com.datpt10.alarmup.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.datpt10.alarmup.R;
import com.datpt10.alarmup.activity.AlarmActivity;
import com.datpt10.alarmup.activity.HomeActivity;
import com.datpt10.alarmup.model.TimerEntity;
import com.datpt10.alarmup.receiver.NotiAlarmReceiver;
import com.datpt10.alarmup.receiver.NotiTimerReceiver;

public class TimerEndService extends Service {
    private static final String TIMER_ID = "TIMER_ID";
    private NotificationManager notificationManager;
    private TimerEntity timerEntity;
    private MediaPlayer mediaPlayer;

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
        timerEntity = intent.getParcelableExtra(AlarmActivity.EXTRA_TIMER);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            assert notificationManager != null;
            notificationManager.createNotificationChannel(new NotificationChannel(TIMER_ID, "Alarms", NotificationManager.IMPORTANCE_HIGH));
        }
        RemoteViews notificationLayout = new RemoteViews(getPackageName(), R.layout.custom_notification_timer);
        notificationLayout.setTextViewText(R.id.tv_noti_timer_title, "Timer");
        notificationLayout.setTextViewText(R.id.tv_noti_timer_content, timerEntity.getContentTimer());
        notificationLayout.setOnClickPendingIntent(R.id.bt_noti_timer_one_more, onNotificationClick(R.id.bt_noti_timer_one_more));
        notificationLayout.setOnClickPendingIntent(R.id.bt_noti_timer_cancel, onNotificationClick(R.id.bt_noti_timer_cancel));

        Notification notification = new NotificationCompat.Builder(getApplicationContext(), TIMER_ID)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSmallIcon(R.drawable.ic_alarm)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCustomContentView(notificationLayout)
                .setFullScreenIntent(getFullScreenIntent(getApplicationContext()), true)
                .build();

        startForeground(1, notification);
        int resId = getApplicationContext().getResources().getIdentifier("zero", "raw", getApplicationContext().getPackageName());
        mediaPlayer = MediaPlayer.create(getApplicationContext(), resId);
        mediaPlayer.start();

        return START_NOT_STICKY;
    }

    private PendingIntent getFullScreenIntent(Context applicationContext) {
        Intent showActivityIntent = new Intent(applicationContext, HomeActivity.class);
        showActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return PendingIntent.getActivity(applicationContext, 0, showActivityIntent, 0);
    }

    private PendingIntent onNotificationClick(int id) {
        Intent intentNoti = new Intent(getApplicationContext(), NotiTimerReceiver.class);
        intentNoti.putExtra(NotiAlarmReceiver.EXTRA_BUTTON_CLICKED, id);
        intentNoti.putExtra("idTimer", timerEntity.getId());
        return PendingIntent.getBroadcast(getApplicationContext(), id, intentNoti, 0);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        stopForeground(true);
    }
}
