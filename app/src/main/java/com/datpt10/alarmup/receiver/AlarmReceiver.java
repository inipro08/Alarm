package com.datpt10.alarmup.receiver;

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.datpt10.alarmup.Alarmup;
import com.datpt10.alarmup.activity.AlarmActivity;
import com.datpt10.alarmup.model.AlarmEntity;

/**
 * create by datpt on 1/16/2020.
 */
public class AlarmReceiver extends BroadcastReceiver {
    public static final String EXTRA_ALARM_ID = "com.datpt10.alarmup.receiver.AlarmReceiver.EXTRA_ALARM_ID";
    public static final int NOTIFICATION_ID = 123;

    @Override
    public void onReceive(Context context, Intent intent) {
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Alarmup alarmup = (Alarmup) context.getApplicationContext();
        AlarmEntity alarm = alarmup.getAlarms().get(intent.getIntExtra(EXTRA_ALARM_ID, 0));
        if (alarm.isRepeat())
            alarm.set(context, manager);
        else alarm.setEnabled(context, manager, false);
        alarmup.onAlarmsChanged();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Alarmup.getInstance().showNotification(alarm);
        } else {
            Intent ringer = new Intent(context, AlarmActivity.class);
            ringer.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ringer.putExtra(AlarmActivity.EXTRA_ALARM, alarm);
            context.startActivity(ringer);
        }
    }
}
