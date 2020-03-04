package com.datpt10.alarmup.view.event;

/**
 * Created by Dell on 6/9/2017.
 */
public interface OnHomeBackToView extends OnCallBackToView {

    void showFrgScreen(String tagSource, String tagChild);

    void closeApp();

    void hideBottomBar();

    void showWaringDialog(String txt, boolean isActive);

    void showM001Landing(int isActive);
}
