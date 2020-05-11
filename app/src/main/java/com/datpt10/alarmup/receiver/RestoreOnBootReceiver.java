package com.datpt10.alarmup.receiver;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.datpt10.alarmup.Alarmup;
import com.datpt10.alarmup.model.AlarmEntity;

public class RestoreOnBootReceiver extends BroadcastReceiver {
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {
        Alarmup alarmup = (Alarmup) context.getApplicationContext();
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        for (AlarmEntity alarm : alarmup.getAlarms()) {
            if (alarm.isEnabledToggle)
                alarm.set(context, manager);
        }
    }
}
