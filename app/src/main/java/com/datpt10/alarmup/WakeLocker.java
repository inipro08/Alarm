package com.datpt10.alarmup;

import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.content.Context;
import android.os.PowerManager;

public class WakeLocker {
    private static PowerManager.WakeLock wakeLock;
    private static KeyguardManager.KeyguardLock keyguardLock;

    @SuppressLint("InvalidWakeLockTag")
    public static void acquire(Context ctx) {
        if (wakeLock != null) wakeLock.release();
        KeyguardManager keyguardManager = (KeyguardManager) ctx.getSystemService(Context.KEYGUARD_SERVICE);
        keyguardLock = keyguardManager.newKeyguardLock("KeyguardLock");
        keyguardLock.disableKeyguard();

        PowerManager pm = (PowerManager) ctx.getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK |
                PowerManager.ACQUIRE_CAUSES_WAKEUP |
                PowerManager.ON_AFTER_RELEASE, "tag");
        wakeLock.acquire();
    }

    public static void release() {
        if (wakeLock != null)
            wakeLock.release();
        wakeLock = null;
    }
}
