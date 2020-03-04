package com.datpt10.alarmup.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.view.Gravity;
import android.view.Window;

import com.datpt10.alarmup.util.CommonUtil;
import com.datpt10.alarmup.R;

public class ProgressLoading {

    private static Dialog pd_loading;
    private static boolean isHide;

    private ProgressLoading() {
        // do nothing
    }

    public static void dontShow() {
        isHide = true;
    }

    public static void show(Context context) {
        if (!isLoading() && context != null && !isHide) {
            try {
                if (pd_loading == null) {
                    pd_loading = new Dialog(context);
                    pd_loading.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    pd_loading.setContentView(R.layout.view_base_loading);
                    if (pd_loading.getWindow() != null) {
                        pd_loading.getWindow()
                                .setBackgroundDrawableResource(R.drawable.bg_dialog_transparent);
                    }
                    pd_loading.setCanceledOnTouchOutside(false);
                    pd_loading.getWindow().setGravity(Gravity.CENTER);
                    pd_loading.setCancelable(false);
                }
                pd_loading.show();
                CommonUtil.getInstance().doKeepDialog(pd_loading);
            } catch (Exception ignored) {
                ignored.printStackTrace();
            }
        }

        isHide = false;
    }

    public static void dismiss() {
        new Handler().postDelayed(() -> {
            try {
                if (pd_loading != null && pd_loading.isShowing()) {
                    pd_loading.dismiss();
                    pd_loading = null;
                }
            } catch (Exception ignored) {
                //ignored.printStackTrace();
            }
        }, 800);

    }

    private static boolean isLoading() {
        return pd_loading != null && pd_loading.isShowing();
    }
}