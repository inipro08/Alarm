package com.datpt10.alarmup.view.event;

import com.datpt10.alarmup.model.AlarmEntity;

/**
 * create by datpt on 10/24/2019.
 */
public interface OnM002AlarmCallBack extends OnCallBackToView {
    void removeAlarm(AlarmEntity alarmData);
}