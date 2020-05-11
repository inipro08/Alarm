package com.datpt10.alarmup.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.datpt10.alarmup.Alarmup;
import com.datpt10.alarmup.R;
import com.datpt10.alarmup.activity.HomeActivity;
import com.datpt10.alarmup.model.TimerEntity;
import com.datpt10.alarmup.util.FormatUtils;

import java.util.List;

public class TimerService extends Service {
    private static final int NOTIFICATION_ID = 427;
    private final IBinder binder = new LocalBinder();
    private Handler handler = new Handler();
    private List<TimerEntity> timers;
    private NotificationManager notificationManager;
    private String notificationString;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (timers.size() > 0) {
                Notification notification = getNotification();
                if (notification != null)
                    startForeground(NOTIFICATION_ID, notification);
                handler.removeCallbacks(this);
                handler.postDelayed(this, 10);
            } else stopForeground(true);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        timers = ((Alarmup) getApplicationContext()).getTimers();
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handler.removeCallbacks(runnable);
        runnable.run();
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    private Notification getNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            notificationManager.createNotificationChannel(new NotificationChannel(Alarmup.NOTIFICATION_CHANNEL_TIMERS, "Timers", NotificationManager.IMPORTANCE_LOW));
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

        String string = "";
        for (int i = 0; i < timers.size(); i++) {
            String time = FormatUtils.formatMillis(timers.get(i).getRemainingMillis());
            time = time.substring(0, time.length() - 3);
            inboxStyle.addLine(timers.get(i).getContentTimer() + " " + time);
            string += "/" + time + "/";
        }
        if (notificationString != null && notificationString.equals(string)) return null;
        notificationString = string;
        Intent intent = new Intent(this, HomeActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("EXTRA_TIMER_IN", "");
        intent.putExtras(bundle);

        return new NotificationCompat.Builder(this, Alarmup.NOTIFICATION_CHANNEL_TIMERS)
                .setSmallIcon(R.drawable.ic_timer)
                .setContentTitle(getString(R.string.title_timer))
                .setContentIntent(PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT))
                .setStyle(inboxStyle)
                .build();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    public class LocalBinder extends Binder {
        public TimerService getService() {
            return TimerService.this;
        }
    }
}
