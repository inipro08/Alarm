package com.datpt10.alarmup.receiver;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.datpt10.alarmup.Alarmup;
import com.datpt10.alarmup.R;
import com.datpt10.alarmup.WakeLocker;
import com.datpt10.alarmup.activity.AlarmActivity;
import com.datpt10.alarmup.model.AlarmEntity;
import com.datpt10.alarmup.service.AlarmService;

public class NotiAlarmReceiver extends BroadcastReceiver {
    public static final String EXTRA_BUTTON_CLICKED = "EXTRA_BUTTON_CLICKED";
    private AlarmEntity alarmEntity;

    @Override
    public void onReceive(Context context, Intent intent) {
        alarmEntity = intent.getParcelableExtra(AlarmActivity.EXTRA_ALARM);
        int id = intent.getIntExtra(EXTRA_BUTTON_CLICKED, -1);
        assert alarmEntity != null;
        switch (id) {
            case R.id.bt_noti_after_5:
                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                alarmEntity.setTime(context, alarmManager, System.currentTimeMillis() + 5 * 60 * 1000);
                alarmEntity.setEnabled(context, alarmManager, true);
                Alarmup.getInstance().stopAnnoyances();
                context.stopService(new Intent(context, AlarmService.class));
                cancelNotification(context);
                break;
            case R.id.bt_noti_cancel:
                Alarmup.getInstance().stopAnnoyances();
                context.stopService(new Intent(context, AlarmService.class));
                cancelNotification(context);
                break;
        }
        WakeLocker.release();
    }

    private void cancelNotification(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        assert notificationManager != null;
        notificationManager.cancel(AlarmReceiver.NOTIFICATION_ID);
    }
}
