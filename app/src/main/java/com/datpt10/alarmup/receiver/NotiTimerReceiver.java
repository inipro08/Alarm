package com.datpt10.alarmup.receiver;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.datpt10.alarmup.Alarmup;
import com.datpt10.alarmup.R;
import com.datpt10.alarmup.model.TimerEntity;
import com.datpt10.alarmup.service.TimerEndService;
import com.datpt10.alarmup.service.TimerService;

import java.util.concurrent.TimeUnit;

import static com.datpt10.alarmup.receiver.NotiAlarmReceiver.EXTRA_BUTTON_CLICKED;

public class NotiTimerReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        context.stopService(new Intent(context, TimerService.class));
        Alarmup alarmup = (Alarmup) context.getApplicationContext();
        int idTimer = intent.getIntExtra("idTimer", 0);
        TimerEntity timerEntity = alarmup.getTimers().get(idTimer);
        int id = intent.getIntExtra(EXTRA_BUTTON_CLICKED, -1);
        switch (id) {
            case R.id.bt_noti_timer_one_more:
                long mTimeMillisMore = TimeUnit.MINUTES.toMillis(1);
                timerEntity.set(context, (AlarmManager) context.getSystemService(Context.ALARM_SERVICE), System.currentTimeMillis() + mTimeMillisMore);
                timerEntity.setDuration(context, mTimeMillisMore);
                context.stopService(new Intent(context, TimerEndService.class));
                context.startService(new Intent(context, TimerService.class));
                cancelNotification(context);
                break;
            case R.id.bt_noti_timer_cancel:
                context.stopService(new Intent(context, TimerEndService.class));
                Alarmup.getInstance().removeTimer(timerEntity);
                cancelNotification(context);
                break;
        }
    }

    private void cancelNotification(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        assert notificationManager != null;
        notificationManager.cancel(1);
    }
}
