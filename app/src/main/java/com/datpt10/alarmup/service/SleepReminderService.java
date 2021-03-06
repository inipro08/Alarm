package com.datpt10.alarmup.service;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.datpt10.alarmup.Alarmup;
import com.datpt10.alarmup.R;
import com.datpt10.alarmup.model.AlarmEntity;
import com.datpt10.alarmup.model.PreferenceEntity;
import com.datpt10.alarmup.util.FormatUtils;

import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SleepReminderService extends Service {
    private Alarmup alarmup;
    private PowerManager powerManager;
    private ScreenReceiver receiver;

    /**
     * Get a sleepy alarm. Well, get the next alarm that should trigger a sleep alert.
     *
     * @param alarmup The active Application instance.
     * @return The next [AlarmData](../data/AlarmData) that should trigger a
     * sleep alert, or null if there isn't one.
     */
    @Nullable
    public static AlarmEntity getSleepyAlarm(Alarmup alarmup) {
        if (PreferenceEntity.SLEEP_REMINDER.getValue(alarmup)) {
            AlarmEntity nextAlarm = getNextWakeAlarm(alarmup);
            if (nextAlarm != null) {
                Calendar nextTrigger = nextAlarm.getNext();
                nextTrigger.set(Calendar.MINUTE, nextTrigger.get(Calendar.MINUTE) - (int) TimeUnit.MILLISECONDS.toMinutes((long) PreferenceEntity.SLEEP_REMINDER_TIME.getValue(alarmup)));

                if (Calendar.getInstance().after(nextTrigger))
                    return nextAlarm;
            }
        }

        return null;
    }

    /**
     * Get the next scheduled [AlarmData](../data/AlarmData) that will ring.
     *
     * @param alarmup The active Application instance.
     * @return The next AlarmData that will wake the user up.
     */
    @Nullable
    public static AlarmEntity getNextWakeAlarm(Alarmup alarmup) {
        Calendar nextNoon = Calendar.getInstance();
        nextNoon.set(Calendar.HOUR_OF_DAY, 12);
        if (nextNoon.before(Calendar.getInstance()))
            nextNoon.set(Calendar.DAY_OF_YEAR, nextNoon.get(Calendar.DAY_OF_YEAR) + 1);
        else return null;

        Calendar nextDay = Calendar.getInstance();
        nextDay.set(Calendar.HOUR_OF_DAY, 0);
        while (nextDay.before(Calendar.getInstance()))
            nextDay.set(Calendar.DAY_OF_YEAR, nextDay.get(Calendar.DAY_OF_YEAR) + 1);

        List<AlarmEntity> alarms = alarmup.getAlarms();
        AlarmEntity nextAlarm = null;
        for (AlarmEntity alarm : alarms) {
            Calendar next = alarm.getNext();
            if (alarm.isEnabledToggle && next.before(nextNoon) && next.after(nextDay) && (nextAlarm == null || nextAlarm.getNext().after(next)))
                nextAlarm = alarm;
        }

        return nextAlarm;
    }

    /**
     * To be called whenever an alarm is changed, might change, or when time might have
     * unexpectedly leaped forwards. This will start the service if there is a
     * [sleepy alarm](#getsleepyalarm) present.
     *
     * @param context An active context instance.
     */
    public static void refreshSleepTime(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && ContextCompat.checkSelfPermission(context, Manifest.permission.FOREGROUND_SERVICE) != PackageManager.PERMISSION_GRANTED)
            return;

        Alarmup alarmup;
        if (context instanceof Alarmup)
            alarmup = (Alarmup) context;
        else alarmup = (Alarmup) context.getApplicationContext();

        if (getSleepyAlarm(alarmup) != null)
            ContextCompat.startForegroundService(context, new Intent(alarmup, SleepReminderService.class));
    }

    @Override
    public void onCreate() {
        super.onCreate();
        alarmup = (Alarmup) getApplicationContext();
        powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        receiver = new ScreenReceiver(this);
        refreshState();

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        registerReceiver(receiver, filter);
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        refreshState();
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * Refresh the state of the sleepy stuff. This will either show a notification if a notification
     * should be shown, or stop the service if it shouldn't.
     */
    public void refreshState() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? powerManager.isInteractive() : powerManager.isScreenOn()) {
            AlarmEntity nextAlarm = getSleepyAlarm(alarmup);
            if (nextAlarm != null) {
                NotificationCompat.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    if (manager != null)
                        manager.createNotificationChannel(new NotificationChannel("sleepReminder", getString(R.string.title_sleep_reminder), NotificationManager.IMPORTANCE_DEFAULT));

                    builder = new NotificationCompat.Builder(this, "sleepReminder");
                } else builder = new NotificationCompat.Builder(this);

                startForeground(540, builder.setContentTitle(getString(R.string.title_sleep_reminder))
                        .setContentText(String.format(getString(R.string.msg_sleep_reminder),
                                FormatUtils.formatUnit(this, (int) TimeUnit.MILLISECONDS.toMinutes(nextAlarm.getNext().getTimeInMillis() - System.currentTimeMillis()))))
                        .setSmallIcon(R.drawable.ic_alarm)
                        .setPriority(NotificationCompat.PRIORITY_LOW)
                        .setCategory(NotificationCompat.CATEGORY_SERVICE)
                        .build());
                return;
            }
        }

        stopForeground(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            stopSelf();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private static class ScreenReceiver extends BroadcastReceiver {

        private WeakReference<SleepReminderService> serviceReference;

        public ScreenReceiver(SleepReminderService service) {
            serviceReference = new WeakReference<>(service);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            SleepReminderService service = serviceReference.get();
            if (service != null)
                service.refreshState();
        }
    }

}
