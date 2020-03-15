package com.datpt10.alarmup.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.datpt10.alarmup.Alarmio;
import com.datpt10.alarmup.R;
import com.datpt10.alarmup.activity.HomeActivity;
import com.datpt10.alarmup.receiver.TimerReceiver;
import com.datpt10.alarmup.util.FormatUtils;

public class TimerService extends Service {
    public static final String TAG = TimerService.class.getName();
    public final static String COUNTDOWN_BR = "com.datpt10.alarmnow.util.Constant.COUNTDOWN_BR";
    private static final int NOTIFICATION_ID = 427;
    private final IBinder binder = new LocalBinder();
    public Intent bi = new Intent(COUNTDOWN_BR);
    public CountDownTimer countDownTimer = null;
    private String notificationString;
    private long mTimerLeftInMillis;
    private NotificationManager notificationManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "Starting timer...");
        super.onCreate();
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    public boolean bindService(Intent intentService, ServiceConnection conn, int flags) {
        return super.bindService(intentService, conn, flags);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        countDownTimer = new CountDownTimer(mTimerLeftInMillis, 1000) {
            @Override
            public void onTick(long millis) {
                mTimerLeftInMillis = millis;
                bi.putExtra("CountDown", mTimerLeftInMillis);
                sendBroadcast(bi);
                getNotification();
            }

            @Override
            public void onFinish() {
                Log.i(TAG, "Timer finished");
            }
        };
        countDownTimer.start();
        return START_STICKY;
    }


    @Nullable
    private Notification getNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            notificationManager.createNotificationChannel(new NotificationChannel(Alarmio.NOTIFICATION_CHANNEL_TIMERS, "Timers", NotificationManager.IMPORTANCE_LOW));

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        String string = "";
        String time = FormatUtils.formatMillis(mTimerLeftInMillis);
        time = time.substring(0, time.length() - 3);
        inboxStyle.addLine(time);
        string += "/" + time + "/";

        if (notificationString != null && notificationString.equals(string))
            return null;

        notificationString = string;

        Intent intent = new Intent(this, HomeActivity.class);

        intent.putExtra(TimerReceiver.EXTRA_TIMER_ID, 0);

        return new NotificationCompat.Builder(this, Alarmio.NOTIFICATION_CHANNEL_TIMERS)
                .setSmallIcon(R.drawable.ic_timer)
                .setContentTitle(getString(R.string.title_timer))
                .setContentText("")
                .setContentIntent(PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT))
                .setStyle(inboxStyle)
                .build();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        countDownTimer.cancel();
        Log.i(TAG, "Timer cancelled");
        super.onDestroy();
    }

    public class LocalBinder extends Binder {
        public TimerService getService() {
            return TimerService.this;
        }
    }
}
