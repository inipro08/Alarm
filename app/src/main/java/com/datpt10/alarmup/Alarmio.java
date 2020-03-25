package com.datpt10.alarmup;

import android.Manifest;
import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.LocationManager;
import android.media.Ringtone;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import com.afollestad.aesthetic.Aesthetic;
import com.afollestad.aesthetic.AutoSwitchMode;
import com.datpt10.alarmup.model.AlarmEntity;
import com.datpt10.alarmup.model.PreferenceEntity;
import com.datpt10.alarmup.model.SoundEntity;
import com.datpt10.alarmup.model.TimerEntity;
import com.datpt10.alarmup.service.SleepReminderService;
import com.datpt10.alarmup.service.TimerService;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;
import com.luckycatlabs.sunrisesunset.dto.Location;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class Alarmio extends Application implements Player.EventListener {
    public static final int THEME_DAY_NIGHT = 0;
    public static final int THEME_DAY = 1;
    public static final int THEME_NIGHT = 2;
    public static final int THEME_AMOLED = 3;

    public static final String NOTIFICATION_CHANNEL_TIMERS = "timers";
    private static final String TAG = Alarmio.class.getName();
    private SharedPreferences prefs;
    private SunriseSunsetCalculator sunsetCalculator;
    private Ringtone currentRingtone;
    private List<AlarmEntity> alarms;
    private List<TimerEntity> timers;
    private List<AlarmListener> listeners;
    private ActivityListener listener;
    private SimpleExoPlayer player;
    private HlsMediaSource.Factory hlsMediaSourceFactory;
    private String currentStream;

    @Override
    public void onCreate() {
        super.onCreate();
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
//        DebugUtils.setup(this);
        Log.i(TAG, "Alarmio---");
        listeners = new ArrayList<>();
        alarms = new ArrayList<>();
        timers = new ArrayList<>();

        player = ExoPlayerFactory.newSimpleInstance(this, new DefaultTrackSelector());
        player.addListener(this);

        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "exoplayer2example"), null);
        hlsMediaSourceFactory = new HlsMediaSource.Factory(dataSourceFactory);

        int alarmLength = PreferenceEntity.ALARM_LENGTH.getValue(this);
        for (int id = 0; id < alarmLength; id++) {
            alarms.add(new AlarmEntity(id, this));
        }

        int timerLength = PreferenceEntity.TIMER_LENGTH.getValue(this);
        for (int id = 0; id < timerLength; id++) {
            timers.add(new TimerEntity(id, this));
        }

        SleepReminderService.refreshSleepTime(this);
    }

    public List<AlarmEntity> getAlarms() {
        return alarms;
    }

    public List<TimerEntity> getTimers() {
        return timers;
    }

    /**
     * Create a new alarm, assigning it an unused preference id.
     *
     * @return The newly instantiated [AlarmData](./data/AlarmData).
     */
    public AlarmEntity newAlarm() {
        AlarmEntity alarm = new AlarmEntity(alarms.size(), Calendar.getInstance());
        alarm.soundAlarm = SoundEntity.fromString(PreferenceEntity.DEFAULT_ALARM_RINGTONE.getValue(this, ""));
        alarms.add(alarm);
        onAlarmCountChanged();
        return alarm;
    }

    /**
     * Remove an alarm and all of its its preferences.
     *
     * @param alarm The alarm to be removed.
     */
    public void removeAlarm(AlarmEntity alarm) {
        alarm.onRemoved(this);

        int index = alarms.indexOf(alarm);
        alarms.remove(index);
        for (int i = index; i < alarms.size(); i++) {
            alarms.get(i).onIdChanged(i, this);
        }
        onAlarmCountChanged();
        onAlarmsChanged();
    }

    public void removeAlarmList(AlarmEntity alarmEntity) {
        alarmEntity.onRemoved(this);
    }

    /**
     * Update preferences to show that the alarm count has been changed.
     */
    public void onAlarmCountChanged() {
        PreferenceEntity.ALARM_LENGTH.setValue(this, alarms.size());
    }

    /**
     * Notify the application of changes to the current alarms.
     */
    public void onAlarmsChanged() {
        for (AlarmListener listener : listeners) {
            listener.onAlarmsChanged();
        }
    }

    /**
     * Create a new timer, assigning it an unused preference id.
     *
     * @return The newly instantiated [TimerData](./data/TimerData).
     */
    public TimerEntity newTimer() {
        TimerEntity timer = new TimerEntity(timers.size());
        timers.add(timer);
        onTimerCountChanged();
        return timer;
    }

    /**
     * Remove a timer and all of its preferences.
     *
     * @param timer The timer to be removed.
     */
    public void removeTimer(TimerEntity timer) {
        timer.onRemoved(this);

        int index = timers.indexOf(timer);
        timers.remove(index);
        for (int i = index; i < timers.size(); i++) {
            timers.get(i).onIdChanged(i, this);
        }
        onTimerCountChanged();
        onTimersChanged();
    }

    /**
     * Update the preferences to show that the timer count has been changed.
     */
    public void onTimerCountChanged() {
        PreferenceEntity.TIMER_LENGTH.setValue(this, timers.size());
    }

    /**
     * Notify the application of changes to the current timers.
     */
    public void onTimersChanged() {
        for (AlarmListener listener : listeners) {
            listener.onTimersChanged();
        }

    }

    /**
     * Starts the timer service after a timer has been set.
     */
    public void onTimerStarted() {
        startService(new Intent(this, TimerService.class));
    }

    /**
     * Get an instance of SharedPreferences.
     *
     * @return The instance of SharedPreferences being used by the application.
     * @see [android.content.SharedPreferences Documentation](https://developer.android.com/reference/android/content/SharedPreferences)
     */
    public SharedPreferences getPrefs() {
        return prefs;
    }

    /**
     * Update the application theme.
     */
    public void updateTheme() {
        if (isNight()) {
            Aesthetic.Companion.get()
                    .isDark(true)
                    .lightStatusBarMode(AutoSwitchMode.OFF)
                    .colorPrimary(ContextCompat.getColor(this, R.color.colorNightPrimary))
                    .colorPrimaryDark(ContextCompat.getColor(this, R.color.colorWhite))
                    .colorStatusBar(ContextCompat.getColor(this, R.color.colorWhite))
                    .colorNavigationBar(ContextCompat.getColor(this, R.color.colorBlack))
                    .colorAccent(ContextCompat.getColor(this, R.color.colorNightAccent))
                    .colorCardViewBackground(ContextCompat.getColor(this, R.color.colorNightForeground))
                    .colorWindowBackground(ContextCompat.getColor(this, R.color.colorNightPrimaryDark))
                    .textColorPrimary(ContextCompat.getColor(this, R.color.textColorPrimaryNight))
                    .textColorSecondary(ContextCompat.getColor(this, R.color.textColorSecondaryNight))
                    .textColorPrimaryInverse(ContextCompat.getColor(this, R.color.textColorPrimary))
                    .textColorSecondaryInverse(ContextCompat.getColor(this, R.color.textColorSecondary))
                    .apply();
        } else {
            int theme = getActivityTheme();
            if (theme == THEME_DAY || theme == THEME_DAY_NIGHT) {
                Aesthetic.Companion.get()
                        .isDark(false)
                        .lightStatusBarMode(AutoSwitchMode.ON)
                        .colorPrimary(ContextCompat.getColor(this, R.color.colorPrimary))
                        .colorPrimaryDark(ContextCompat.getColor(this, R.color.colorWhite))
                        .colorStatusBar(ContextCompat.getColor(this, R.color.colorWhite))
                        .colorNavigationBar(ContextCompat.getColor(this, R.color.colorBlack))
                        .colorAccent(ContextCompat.getColor(this, R.color.colorAccent))
                        .colorCardViewBackground(ContextCompat.getColor(this, R.color.colorForeground))
                        .colorWindowBackground(ContextCompat.getColor(this, R.color.colorPrimaryDark))
                        .textColorPrimary(ContextCompat.getColor(this, R.color.colorWhite))
                        .textColorSecondary(ContextCompat.getColor(this, R.color.textColorSecondary))
                        .textColorPrimaryInverse(ContextCompat.getColor(this, R.color.textColorPrimaryNight))
                        .textColorSecondaryInverse(ContextCompat.getColor(this, R.color.textColorSecondaryNight))
                        .apply();
            } else if (theme == THEME_AMOLED) {
                Aesthetic.Companion.get()
                        .isDark(true)
                        .lightStatusBarMode(AutoSwitchMode.OFF)
                        .colorPrimary(Color.BLACK)
                        .colorPrimaryDark(ContextCompat.getColor(this, R.color.colorWhite))
                        .colorStatusBar(ContextCompat.getColor(this, R.color.colorWhite))
                        .colorNavigationBar(Color.BLACK)
                        .colorAccent(Color.WHITE)
                        .colorCardViewBackground(Color.BLACK)
                        .colorWindowBackground(Color.BLACK)
                        .textColorPrimary(Color.WHITE)
                        .textColorSecondary(Color.WHITE)
                        .textColorPrimaryInverse(Color.BLACK)
                        .textColorSecondaryInverse(Color.BLACK)
                        .apply();
            }
        }
    }

    /**
     * Determine if the theme should be a night theme.
     *
     * @return True if the current theme is a night theme.
     */
    public boolean isNight() {
        int time = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        return ((time < getDayStart() || time > getDayEnd()) && getActivityTheme() == THEME_DAY_NIGHT) || getActivityTheme() == THEME_NIGHT;
    }

    /**
     * Get the theme to be used for activities and things. Despite
     * what the name implies, it does not return a theme resource,
     * but rather one of Alarmio.THEME_DAY_NIGHT, Alarmio.THEME_DAY,
     * Alarmio.THEME_NIGHT, or Alarmio.THEME_AMOLED.
     *
     * @return The theme to be used for activites.
     */
    public int getActivityTheme() {
        return PreferenceEntity.THEME.getValue(this);
    }

    /**
     * Determine if the sunrise/sunset stuff should occur automatically.
     *
     * @return True if the day/night stuff is automated.
     */
    public boolean isDayAuto() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && (boolean) PreferenceEntity.DAY_AUTO.getValue(this);
    }

    /**
     * @return the hour of the start of the day (24h), as specified by the user
     */
    public int getDayStart() {
        if (isDayAuto() && getSunsetCalculator() != null)
            return getSunsetCalculator().getOfficialSunriseCalendarForDate(Calendar.getInstance()).get(Calendar.HOUR_OF_DAY);
        else return PreferenceEntity.DAY_START.getValue(this);
    }

    /**
     * @return the hour of the end of the day (24h), as specified by the user
     */
    public int getDayEnd() {
        if (isDayAuto() && getSunsetCalculator() != null)
            return getSunsetCalculator().getOfficialSunsetCalendarForDate(Calendar.getInstance()).get(Calendar.HOUR_OF_DAY);
        else return PreferenceEntity.DAY_END.getValue(this);
    }

    /**
     * @return the hour of the calculated sunrise time, or null.
     */
    @Nullable
    public Integer getSunrise() {
        if (getSunsetCalculator() != null)
            return getSunsetCalculator().getOfficialSunsetCalendarForDate(Calendar.getInstance()).get(Calendar.HOUR_OF_DAY);
        else return null;
    }

    /**
     * @return the hour of the calculated sunset time, or null.
     */
    @Nullable
    public Integer getSunset() {
        if (getSunsetCalculator() != null)
            return getSunsetCalculator().getOfficialSunsetCalendarForDate(Calendar.getInstance()).get(Calendar.HOUR_OF_DAY);
        else return null;
    }

    /**
     * @return the current SunriseSunsetCalculator object, or null if it cannot
     * be instantiated.
     * @see [SunriseSunsetLib Repo](https://github.com/mikereedell/sunrisesunsetlib-java)
     */
    @Nullable
    private SunriseSunsetCalculator getSunsetCalculator() {
        if (sunsetCalculator == null && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            try {
                LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                android.location.Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(new Criteria(), false));
                sunsetCalculator = new SunriseSunsetCalculator(new Location(location.getLatitude(), location.getLongitude()), TimeZone.getDefault().getID());
            } catch (NullPointerException ignored) {
            }
        }

        return sunsetCalculator;
    }

    /**
     * Determine if a ringtone is currently playing.
     *
     * @return True if a ringtone is currently playing.
     */
    public boolean isRingtonePlaying() {
        return currentRingtone != null && currentRingtone.isPlaying();
    }

    /**
     * Get the currently playing ringtone.
     *
     * @return The currently playing ringtone, or null.
     */
    @Nullable
    public Ringtone getCurrentRingtone() {
        return currentRingtone;
    }

    public void playRingtone(Ringtone ringtone) {
        if (!ringtone.isPlaying()) {
            stopCurrentSound();
            ringtone.play();
        }
        currentRingtone = ringtone;
    }

    /**
     * Play a stream ringtone.
     *
     * @param url The URL of the stream to be passed to ExoPlayer.
     * @see [ExoPlayer Repo](https://github.com/google/ExoPlayer)
     */
    public void playStream(String url, String type) {
        stopCurrentSound();
        player.prepare(hlsMediaSourceFactory.createMediaSource(Uri.parse(url)));
        player.setPlayWhenReady(true);
        currentStream = url;
    }

    /**
     * Play a stream ringtone.
     *
     * @param url        The URL of the stream to be passed to ExoPlayer.
     * @param attributes The attributes to play the stream with.
     * @see [ExoPlayer Repo](https://github.com/google/ExoPlayer)
     */
    public void playStream(String url, String type, AudioAttributes attributes) {
        player.stop();
        player.setAudioAttributes(attributes);
        playStream(url, type);
    }

    /**
     * Stop the currently playing stream.
     */
    public void stopStream() {
        player.stop();
        currentStream = null;
    }

    /**
     * Determine if the passed url matches the stream that is currently playing.
     *
     * @param url The URL to match the current stream to.
     * @return True if the URL matches that of the currently playing
     * stream.
     */
    public boolean isPlayingStream(String url) {
        return currentStream != null && currentStream.equals(url);
    }

    /**
     * Stop the currently playing sound, regardless of whether it is a ringtone
     * or a stream.
     */
    public void stopCurrentSound() {
        if (isRingtonePlaying())
            currentRingtone.stop();
        stopStream();
    }

    public void addListener(AlarmListener listener) {
        listeners.add(listener);
    }

    public void removeListener(AlarmListener listener) {
        listeners.remove(listener);
    }

    public void setListener(ActivityListener listener) {
        this.listener = listener;
//
//        if (listener != null) {
//            updateTheme();
//        }
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {
    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
    }

    @Override
    public void onLoadingChanged(boolean isLoading) {
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        switch (playbackState) {
            case Player.STATE_BUFFERING:
            case Player.STATE_READY:
                break;
            default:
                currentStream = null;
                break;
        }
    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {
    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
        currentStream = null;
        Exception exception;
        switch (error.type) {
            case ExoPlaybackException.TYPE_RENDERER:
                exception = error.getRendererException();
                break;
            case ExoPlaybackException.TYPE_SOURCE:
                exception = error.getSourceException();
                break;
            case ExoPlaybackException.TYPE_UNEXPECTED:
                exception = error.getUnexpectedException();
                break;
            default:
                return;
        }

        exception.printStackTrace();
        Toast.makeText(this, exception.getClass().getName() + ": " + exception.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPositionDiscontinuity(int reason) {
    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
    }

    @Override
    public void onSeekProcessed() {
    }

    public void requestPermissions(String... permissions) {
        if (listener != null)
            listener.requestPermissions(permissions);
    }

    public FragmentManager getFragmentManager() {
        if (listener != null)
            return listener.gettFragmentManager();
        else return null;
    }

    public interface AlarmListener {
        void onAlarmsChanged();

        void onTimersChanged();
    }

    public interface ActivityListener {
        void requestPermissions(String... permissions);

        FragmentManager gettFragmentManager(); //help
    }

}
