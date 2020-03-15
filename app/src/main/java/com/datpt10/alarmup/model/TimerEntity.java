package com.datpt10.alarmup.model;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import com.datpt10.alarmup.receiver.TimerReceiver;

public class TimerEntity implements Parcelable {
    public static final Creator<TimerEntity> CREATOR = new Creator<TimerEntity>() {
        @Override
        public TimerEntity createFromParcel(Parcel in) {
            return new TimerEntity(in);
        }

        @Override
        public TimerEntity[] newArray(int size) {
            return new TimerEntity[size];
        }
    };
    private int id;
    private String labelTimer;
    private long time;
    private long duration;

    public TimerEntity(int id) {
        this.id = id;
    }

    public TimerEntity(int id, Context context) {
        this.id = id;
        labelTimer = PreferenceEntity.TIMER_LABEL.getSpecificValue(context, id);
        time = PreferenceEntity.TIMER_SET_TIME.getSpecificValue(context, id);
        duration = PreferenceEntity.TIMER_DURATION.getSpecificValue(context, id);
    }

    protected TimerEntity(Parcel in) {
        id = in.readInt();
        labelTimer = in.readString();
        time = in.readLong();
        duration = in.readLong();
    }

    /**
     * Moves this TimerData's preferences to another "id".
     *
     * @param id      The new id to be assigned
     * @param context An active context instance.
     */
    public void onIdChanged(int id, Context context) {
        PreferenceEntity.TIMER_LABEL.setValue(context, labelTimer, id);
        PreferenceEntity.TIMER_SET_TIME.setValue(context, time, id);
        PreferenceEntity.TIMER_DURATION.setValue(context, duration, id);
//        onRemoved(context);
        this.id = id;
    }

    /**
     * Removes this TimerData's preferences.
     *
     * @param context An active context instance.
     */
    public void onRemoved(Context context) {
        cancel(context, (AlarmManager) context.getSystemService(Context.ALARM_SERVICE));
        PreferenceEntity.TIMER_LABEL.setValue(context, null, id);
        PreferenceEntity.TIMER_SET_TIME.setValue(context, null, id);
        PreferenceEntity.TIMER_DURATION.setValue(context, null, id);
    }

    /**
     * Decides if the Timer has been set or should be ignored.
     *
     * @return True if the timer should go off at some time in the future.
     */
    public boolean isSet() {
        return time > System.currentTimeMillis();
    }

    /**
     * Get the remaining amount of milliseconds before the timer should go off. This
     * may return a negative number.
     *
     * @return The amount of milliseconds before the timer should go off.
     */
    public long getRemainingMillis() {
        return time;
    }

    public long getDuration() {
        return duration;
    }

    /**
     * The total length of the timer.
     *
     * @return The total length of the timer, in milliseconds.
     */
    public String getContentTimer() {
        return labelTimer;
    }

    /**
     * Set the duration of the timer.
     *
     * @param content The total length of the timer, in milliseconds.
     * @param context An active Context instance.
     */
    public void setContentTimer(String content, Context context) {
        this.labelTimer = content;
        PreferenceEntity.TIMER_LABEL.setValue(context, content, id);
    }

    /**
     * Set the next time for the timer to ring.
     *
     * @param context An active context instance.
     */
    public void set(Context context, long millis) {
        time = millis;
        PreferenceEntity.TIMER_SET_TIME.setValue(context, time, id);
    }

    /**
     * Set the time duration progressBar
     *
     * @param context An active context instance.
     */
    public void setDuration(Context context, long millisDuration) {
        duration = millisDuration;
        PreferenceEntity.TIMER_DURATION.setValue(context, duration, id);
    }

    /**
     * Schedule a time for the alert to ring at.
     *
     * @param context An active context instance.
     * @param manager The AlarmManager to schedule the alert on.
     */
    public void setAlarm(Context context, AlarmManager manager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            manager.setExact(AlarmManager.RTC_WAKEUP, time, getIntent(context));
        }
    }

    /**
     * Cancel the pending alert.
     *
     * @param context An active context instance.
     * @param manager The AlarmManager that the alert was scheduled on.
     */
    public void cancel(Context context, AlarmManager manager) {
        time = 0;
        manager.cancel(getIntent(context));
        PreferenceEntity.TIMER_SET_TIME.setValue(context, time, id);
    }

    /**
     * The intent to fire when the alert should ring.
     *
     * @param context An active context instance.
     * @return A PendingIntent that will open the alert screen.
     */
    private PendingIntent getIntent(Context context) {
        Intent intent = new Intent(context, TimerReceiver.class);
        intent.putExtra(TimerReceiver.EXTRA_TIMER_ID, id);
        return PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(labelTimer);
        parcel.writeLong( time);
        parcel.writeLong( duration);
    }
}
