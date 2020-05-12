package com.datpt10.alarmup.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.datpt10.alarmup.activity.HomeActivity;

public class RefreshActivityReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent refreshActivity = new Intent(context, HomeActivity.class);
        refreshActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(refreshActivity);
    }
}
