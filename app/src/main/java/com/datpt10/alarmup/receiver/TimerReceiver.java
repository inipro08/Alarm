package com.datpt10.alarmup.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.datpt10.alarmup.Alarmup;
import com.datpt10.alarmup.WakeLocker;
import com.datpt10.alarmup.activity.AlarmActivity;
import com.datpt10.alarmup.model.TimerEntity;
import com.datpt10.alarmup.service.TimerEndService;

public class TimerReceiver extends BroadcastReceiver {
    public static final String EXTRA_TIMER_ID = "com.datpt10.alarmup.receiver.TimerReceiver.EXTRA_TIMER_ID";

    @Override
    public void onReceive(Context context, Intent intent) {
        WakeLocker.acquire(context);
        Alarmup alarmup = (Alarmup) context.getApplicationContext();
        TimerEntity timer = alarmup.getTimers().get(intent.getIntExtra(EXTRA_TIMER_ID, 0));

        Intent startAlarmService = new Intent(context, TimerEndService.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(AlarmActivity.EXTRA_TIMER, timer);
        startAlarmService.putExtras(bundle);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(startAlarmService);
        }

        WakeLocker.release();
    }
}
