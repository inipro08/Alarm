package com.datpt10.alarmup.presenter;

import android.util.Log;
import android.widget.Toast;

import com.datpt10.alarmup.Alarmup;
import com.datpt10.alarmup.util.CommonUtil;
import com.datpt10.alarmup.util.StorageCommon;
import com.datpt10.alarmup.view.event.OnCallBackToView;
import com.datpt10.alarmup.widget.ProgressLoading;

import java.util.Locale;

/**
 * create by datpt on 3/27/2019.
 */
public abstract class BasePresenter<T extends OnCallBackToView> {

    public static final String TAG = BasePresenter.class.getName();
    transient T mListener;

    String tagRequest;

    public BasePresenter(T event) {
        mListener = event;
    }

    protected void handleSuccess(String data) {
        // do nothing
    }

    protected final void showNotify(int text) {
        Toast.makeText(Alarmup.getInstance(), text, Toast.LENGTH_SHORT).show();
    }

    protected final void showNotify(String text) {
        if (text == null) return;
        Toast.makeText(Alarmup.getInstance(), text, Toast.LENGTH_SHORT).show();
    }


    public final void showLockDialog() {
        Log.d(TAG, "showLockDialog ..");
        mListener.showLockDialog();
    }

    public void hideLockDialog(String key) {
        ProgressLoading.dismiss();
    }


    public final StorageCommon getStorage() {
        return Alarmup.getInstance().getStorageCommonAlarmUp();
    }

    String generateJson(String body, Object... data) {
        try {
            for (int i = 0; i < data.length; i++) {
                if (data[i] instanceof String) {
                    data[i] = "\"" + data[i] + "\"";
                }
            }
            String out = String.format(Locale.US, body, data);
            CommonUtil.wtfi(TAG, "BODY: " + out);
            return out;
        } catch (Exception e) {
            CommonUtil.wtfe(TAG, e.getLocalizedMessage());
        }
        return null;
    }

}