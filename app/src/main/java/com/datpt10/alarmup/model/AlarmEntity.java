package com.datpt10.alarmup.model;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import com.datpt10.alarmup.R;
import com.datpt10.alarmup.activity.HomeActivity;
import com.datpt10.alarmup.receiver.AlarmReceiver;
import com.datpt10.alarmup.service.SleepReminderService;

import java.util.Calendar;
import java.util.Date;

public class AlarmEntity implements Parcelable {

    public static final Creator<AlarmEntity> CREATOR = new Creator<AlarmEntity>() {
        @Override
        public AlarmEntity createFromParcel(Parcel in) {
            return new AlarmEntity(in);
        }

        @Override
        public AlarmEntity[] newArray(int size) {
            return new AlarmEntity[size];
        }
    };
    public Calendar timerAlarm;
    public boolean isEnabledToggle = true;
    public boolean[] daysSelect = new boolean[7];
    public SoundEntity soundAlarm;
    private String contentAlarm;
    private String vibrate;
    private int id;

    public AlarmEntity(int id, Calendar time) {
        this.id = id;
        this.timerAlarm = time;
    }

    public AlarmEntity(int id, Context context) {
        this.id = id;
        contentAlarm = PreferenceEntity.ALARM_NAME.getSpecificOverriddenValue(context, getContent(context), id);
        vibrate = PreferenceEntity.ALARM_VIBRATE.getSpecificOverriddenValue(context, getVibrate(context), id);
        timerAlarm = Calendar.getInstance();
        timerAlarm.setTimeInMillis(PreferenceEntity.ALARM_TIME.getSpecificValue(context, id));
        isEnabledToggle = PreferenceEntity.ALARM_ENABLED.getSpecificValue(context, id);
        for (int i = 0; i < 7; i++) {
            daysSelect[i] = PreferenceEntity.ALARM_DAY_ENABLED.getSpecificValue(context, id, i);
        }
        soundAlarm = SoundEntity.fromString(PreferenceEntity.ALARM_SOUND.getSpecificOverriddenValue(context, PreferenceEntity.DEFAULT_ALARM_RINGTONE.getValue(context, ""), id));
    }

    protected AlarmEntity(Parcel in) {
        id = in.readInt();
        contentAlarm = in.readString();
        vibrate = in.readString();
        timerAlarm = Calendar.getInstance();
        timerAlarm.setTimeInMillis(in.readLong());
        isEnabledToggle = in.readByte() != 0;
        daysSelect = in.createBooleanArray();
        if (in.readByte() == 1)
            soundAlarm = SoundEntity.fromString(in.readString());
    }

    /**
     * Moves this AlarmData's preferences to another "id".
     *
     * @param id      The new id to be assigned
     * @param context An active context instance.
     */
    public void onIdChanged(int id, Context context) {
        PreferenceEntity.ALARM_NAME.setValue(context, getContent(context), id);
        PreferenceEntity.ALARM_VIBRATE.setValue(context, getVibrate(context), id);
        PreferenceEntity.ALARM_TIME.setValue(context, timerAlarm != null ? timerAlarm.getTimeInMillis() : null, id);
        PreferenceEntity.ALARM_ENABLED.setValue(context, isEnabledToggle, id);
        for (int i = 0; i < 7; i++) {
            PreferenceEntity.ALARM_DAY_ENABLED.setValue(context, daysSelect[i], id, i);
        }
        PreferenceEntity.ALARM_SOUND.setValue(context, soundAlarm != null ? soundAlarm.toString() : null, id);
        onRemoved(context);
        this.id = id;
        if (isEnabledToggle)
            set(context, (AlarmManager) context.getSystemService(Context.ALARM_SERVICE));
    }

    /**
     * Removes this AlarmData's preferences.
     *
     * @param context An active context instance.
     */
    public void onRemoved(Context context) {
        cancel(context, (AlarmManager) context.getSystemService(Context.ALARM_SERVICE));
        PreferenceEntity.ALARM_NAME.setValue(context, null, id);
        PreferenceEntity.ALARM_VIBRATE.setValue(context, null, id);
        PreferenceEntity.ALARM_TIME.setValue(context, null, id);
        PreferenceEntity.ALARM_ENABLED.setValue(context, null, id);
        for (int i = 0; i < 7; i++) {
            PreferenceEntity.ALARM_DAY_ENABLED.setValue(context, null, id, i);
        }
        PreferenceEntity.ALARM_SOUND.setValue(context, null, id);
    }

    /**
     * Returns whether the alarm should repeat on a set interval
     * or not.
     *
     * @return If repeat is enabled for this alarm.
     */
    public boolean isRepeat() {
        for (boolean day : daysSelect) {
            if (day)
                return true;
        }
        return false;
    }

    /**
     * Sets the user-defined "name" of the alarm.
     *
     * @param context An active context instance.
     * @param name    The new name to be set.
     */
    public void setContent(Context context, String name) {
        this.contentAlarm = name;
        PreferenceEntity.ALARM_NAME.setValue(context, name, id);
    }

    /**
     * Returns the user-defined "name" of the alarm, defaulting to
     * "Alarm (1..)" if unset.
     *
     * @param context An active context instance.
     * @return The alarm name, as a string.
     */
    public String getContent(Context context) {
        if (contentAlarm != null)
            return contentAlarm;
        else return context.getString(R.string.title_alarm, id + 1);
    }

    public void setVibrate(Context context, String vibrate) {
        this.vibrate = vibrate;
        PreferenceEntity.ALARM_VIBRATE.setValue(context, vibrate, id);
    }

    public String getVibrate(Context context) {
        if (vibrate != null)
            return vibrate;
        else return "Vibrate";
    }

    /**
     * Change the scheduled alarm time,
     *
     * @param context    An active context instance.
     * @param manager    An AlarmManager to schedule the alarm on.
     * @param timeMillis The UNIX time (in milliseconds) that the alarm should ring at.
     *                   This is independent to days; if the time correlates to 9:30 on
     *                   a Tuesday when the alarm should only repeat on Wednesdays and
     *                   Thursdays, then the alarm will next ring at 9:30 on Wednesday.
     */
    public void setTime(Context context, AlarmManager manager, long timeMillis) {
        timerAlarm.setTimeInMillis(timeMillis);
        PreferenceEntity.ALARM_TIME.setValue(context, timeMillis, id);
        if (isEnabledToggle)
            set(context, manager);
    }

    /**
     * Set whether the alarm is enabled.
     *
     * @param context   An active context instance.
     * @param manager   An AlarmManager to schedule the alarm on.
     * @param isEnabled Whether the alarm is enabled.
     */
    public void setEnabled(Context context, AlarmManager manager, boolean isEnabled) {
        this.isEnabledToggle = isEnabled;
        PreferenceEntity.ALARM_ENABLED.setValue(context, isEnabled, id);
        if (isEnabled)
            set(context, manager);
        else cancel(context, manager);
    }

    /**
     * Sets the days of the week that the alarm should ring on. If
     * no days are specified, the alarm will act as a one-time alert
     * and will not repeat.
     *
     * @param context An active context instance.
     * @param days    A boolean array, with a length of 7 (seven days of the week)
     *                specifying whether repeat is enabled for that day.
     */
    public void setDays(Context context, boolean[] days) {
        this.daysSelect = days;
        for (int i = 0; i < 7; i++) {
            PreferenceEntity.ALARM_DAY_ENABLED.setValue(context, days[i], id, i);
        }
    }

    /**
     * Return whether the alarm has a sound or not.
     *
     * @return A boolean defining whether a sound has been set
     * for the alarm.
     */
    public boolean hasSound() {
        return soundAlarm != null;
    }

    /**
     * Get the [SoundData](./SoundData) sound specified for the alarm.
     *
     * @return An instance of SoundData describing the sound that
     * the alarm should make (or null).
     */
    @Nullable
    public SoundEntity getSoundAlarm() {
        return soundAlarm;
    }

    /**
     * Set the sound that the alarm should make.
     *
     * @param context An active context instance.
     * @param sound   A [SoundData](./SoundData) defining the sound that
     *                the alarm should make.
     */
    public void setSound(Context context, @Nullable SoundEntity sound) {
        this.soundAlarm = sound;
        PreferenceEntity.ALARM_SOUND.setValue(context, sound != null ? sound.toString() : null, id);
    }

    /**
     * Get the next time that the alarm should wring.
     *
     * @return A Calendar object defining the next time that the alarm should ring at.
     * @see [java.util.Calendar Documentation](https://developer.android.com/reference/java/util/Calendar)
     */
    @Nullable
    public Calendar getNext() {
        if (isEnabledToggle) {
            Calendar now = Calendar.getInstance();
            Calendar next = Calendar.getInstance();
            next.set(Calendar.HOUR_OF_DAY, timerAlarm.get(Calendar.HOUR_OF_DAY));
            next.set(Calendar.MINUTE, timerAlarm.get(Calendar.MINUTE));
            next.set(Calendar.SECOND, 0);
            while (now.after(next))
                next.add(Calendar.DATE, 1);

            if (isRepeat()) {
                int nextDay = next.get(Calendar.DAY_OF_WEEK) - 1; // index on 0-6, rather than the 1-7 returned by Calendar

                for (int i = 0; i < 7 && !daysSelect[nextDay]; i++) {
                    nextDay++;
                    nextDay %= 7;
                }
                next.set(Calendar.DAY_OF_WEEK, nextDay + 1); // + 1 = back to 1-7 range

                while (now.after(next))
                    next.add(Calendar.DATE, 7);
            }
            return next;
        }
        return null;
    }

    /**
     * Set the next time for the alarm to ring.
     *
     * @param context An active context instance.
     * @param manager The AlarmManager to schedule the alarm on.
     * @return The next [Date](https://developer.android.com/reference/java/util/Date)
     * at which the alarm will ring.
     */
    public Date set(Context context, AlarmManager manager) {
        Calendar nextTime = getNext();
        assert nextTime != null;
        setAlarm(context, manager, nextTime.getTimeInMillis());
        return nextTime.getTime();
    }

    /**
     * Schedule a time for the alarm to ring at.
     *
     * @param context    An active context instance.
     * @param manager    The AlarmManager to schedule the alarm on.
     * @param timeMillis A UNIX timestamp specifying the next time for the alarm to ring.
     */
    private void setAlarm(Context context, AlarmManager manager, long timeMillis) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            manager.setAlarmClock(new AlarmManager.AlarmClockInfo
                    (timeMillis, PendingIntent.getActivity(context, 0, new Intent(context, HomeActivity.class), 0)), getIntent(context));
        }
        manager.set(AlarmManager.RTC_WAKEUP, timeMillis - (long) PreferenceEntity.SLEEP_REMINDER_TIME.getValue(context), PendingIntent.getService(context, 0, new Intent(context, SleepReminderService.class), 0));
        SleepReminderService.refreshSleepTime(context);
    }

    /**
     * Cancel the next time for the alarm to ring.
     *
     * @param context An active context instance.
     * @param manager The AlarmManager that the alarm was scheduled on.
     */
    public void cancel(Context context, AlarmManager manager) {
        manager.cancel(getIntent(context));

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            Intent intent = new Intent("android.intent.action.ALARM_CHANGED");
            intent.putExtra("alarmSet", false);
            context.sendBroadcast(intent);
        }
    }

    /**
     * The intent to fire when the alarm should ring.
     *
     * @param context An active context instance.
     * @return A PendingIntent that will open the alert screen.
     */
    private PendingIntent getIntent(Context context) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(AlarmReceiver.EXTRA_ALARM_ID, id);
        return PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(id);
        parcel.writeString(contentAlarm);
        parcel.writeString(vibrate);
        parcel.writeLong(timerAlarm.getTimeInMillis());
        parcel.writeByte((byte) (isEnabledToggle ? 1 : 0));
        parcel.writeBooleanArray(daysSelect);
        parcel.writeByte((byte) (soundAlarm != null ? 1 : 0));
        if (soundAlarm != null)
            parcel.writeString(soundAlarm.toString());
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
