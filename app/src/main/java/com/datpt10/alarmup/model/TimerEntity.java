package com.datpt10.alarmup.model;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

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
    public boolean isVibrate = true;
    private int id;
    private String sound;
    private String labelTimer;
    private long time;

    public TimerEntity(int id) {
        this.id = id;
    }

    public TimerEntity(int id, Context context) {
        this.id = id;
        labelTimer = PreferenceEntity.TIMER_LABEL.getSpecificValue(context, id);
        try {
            time = PreferenceEntity.TIMER_SET_TIME.getSpecificValue(context, id);
        } catch (ClassCastException e) {
            time = (int) PreferenceEntity.TIMER_SET_TIME.getSpecificValue(context, id);
        }
        isVibrate = PreferenceEntity.TIMER_VIBRATE.getSpecificValue(context, id);
        sound = PreferenceEntity.TIMER_SOUND.getSpecificValue(context, id);
    }

    protected TimerEntity(Parcel in) {
        id = in.readInt();
        labelTimer = in.readString();
        time = in.readLong();
        isVibrate = in.readByte() != 0;
        sound = in.readString();
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
        PreferenceEntity.TIMER_VIBRATE.setValue(context, isVibrate, id);
        PreferenceEntity.TIMER_SOUND.setValue(context, sound != null ? sound.toString() : null, id);
        onRemoved(context);
        this.id = id;
        if (isSet())
            set(context, (AlarmManager) context.getSystemService(Context.ALARM_SERVICE));
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
        PreferenceEntity.TIMER_VIBRATE.setValue(context, null, id);
        PreferenceEntity.TIMER_SOUND.setValue(context, null, id);
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
     * @param duration The total length of the timer, in milliseconds.
     * @param context  An active Context instance.
     */
    public void setContentTimer(String duration, Context context) {
        this.labelTimer = duration;
        PreferenceEntity.TIMER_LABEL.setValue(context, duration, id);
    }

    /**
     * Set whether the timer should vibrate when it goes off.
     *
     * @param context   An active Context instance.
     * @param isVibrate Whether the timer should vibrate.
     */
    public void setVibrate(Context context, boolean isVibrate) {
        this.isVibrate = isVibrate;
        PreferenceEntity.TIMER_VIBRATE.setValue(context, isVibrate, id);
    }

    /**
     * Return whether the timer has a sound or not.
     *
     * @return A boolean defining whether a sound has been set
     * for the timer.
     */
    public boolean hasSound() {
        return sound != null;
    }

    /**
     * Get the [SoundData](./SoundData) sound specified for the timer.
     *
     * @return An instance of SoundData describing the sound that
     * the timer should make (or null).
     */
    @Nullable
    public String getSound() {
        return sound;
    }

    /**
     * Set the sound that the timer should make.
     *
     * @param context An active context instance.
     * @param sound   A [SoundData](./SoundData) defining the sound that
     *                the timer should make.
     */
    public void setSound(Context context, String sound) {
        this.sound = sound;
        PreferenceEntity.TIMER_SOUND.setValue(context, sound != null ? sound.toString() : null, id);
    }

    /**
     * Set the next time for the timer to ring.
     *
     * @param context An active context instance.
     * @param manager The AlarmManager to schedule the timer on.
     */
    public void set(Context context, AlarmManager manager) {
        time = System.currentTimeMillis();
        setAlarm(context, manager);

        PreferenceEntity.TIMER_SET_TIME.setValue(context, time, id);
    }

    /**
     * Schedule a time for the alert to ring at.
     *
     * @param context An active context instance.
     * @param manager The AlarmManager to schedule the alert on.
     */
    public void setAlarm(Context context, AlarmManager manager) {
        manager.setExact(AlarmManager.RTC_WAKEUP, time, getIntent(context));
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
        parcel.writeLong(time);
        parcel.writeByte((byte) (isVibrate ? 1 : 0));
        parcel.writeString(sound);
    }
}
