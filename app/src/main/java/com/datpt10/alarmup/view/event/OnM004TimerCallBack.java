package com.datpt10.alarmup.view.event;

import com.datpt10.alarmup.model.TimerEntity;

/**
 * create by datpt on 10/25/2019.
 */
public interface OnM004TimerCallBack extends OnCallBackToView {
    void removeTimer(TimerEntity timerEntity);

    void showNumberPicker(TimerEntity timerEntity);
}
