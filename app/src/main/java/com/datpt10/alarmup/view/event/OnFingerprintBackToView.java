package com.datpt10.alarmup.view.event;

public interface OnFingerprintBackToView extends OnCallBackToView {
    void showNotify(int sms);

    void showNotify(String sms);

    void updateFingerPrintResult();
}
