package com.datpt10.alarmup;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.Ringtone;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import com.afollestad.aesthetic.Aesthetic;
import com.afollestad.aesthetic.AutoSwitchMode;
import com.datpt10.alarmup.activity.AlarmActivity;
import com.datpt10.alarmup.model.AlarmEntity;
import com.datpt10.alarmup.model.PreferenceEntity;
import com.datpt10.alarmup.model.SoundEntity;
import com.datpt10.alarmup.model.TimerEntity;
import com.datpt10.alarmup.service.AlarmService;
import com.datpt10.alarmup.service.SleepReminderService;
import com.datpt10.alarmup.util.StorageCommon;
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
import com.google.android.gms.ads.MobileAds;
import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Alarmup extends Application implements Player.EventListener {
    public static final int THEME_DEFAULT = 0;
    public static final int THEME_ONE = 1;
    public static final int THEME_TWO = 2;
    public static final int THEME_THREE = 3;

    public static final String NOTIFICATION_CHANNEL_TIMERS = "timers";
    private static final String TAG = Alarmup.class.getName();
    private static final String CHANNEL_ID = "CHANNEL_ID";
    private static Alarmup instance;
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
    private StorageCommon storageCommon;
    private Typeface mBoldFont;
    private Typeface mRegularFont;
    private Typeface mItalicFont;
    private Typeface mBoldItalicFont;

    private String vibrate;
    private SoundEntity sound;
    private Vibrator vibrator;
    private Handler handler;
    private Runnable runnable;
    private AudioManager audioManager;
    private int currentVolume;
    private int minVolume;
    private int originalVolume;
    private int volumeRange;
    private boolean isSlowWake;
    private long slowWakeMillis;

    public Alarmup() {
        if (instance == null) {
            instance = this;
        }
    }

    public static Alarmup getInstance() {
        return instance;
    }

    public void showNotification(AlarmEntity alarmEntity) {
        Log.i("Alarmup", "shownotification");
        WakeLocker.acquire(getApplicationContext());
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        isSlowWake = PreferenceEntity.SLOW_WAKE_UP.getValue(this);
        slowWakeMillis = PreferenceEntity.SLOW_WAKE_UP_TIME.getValue(this);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        assert audioManager != null;
        originalVolume = audioManager.getStreamVolume(AudioManager.STREAM_ALARM);
        if (isSlowWake) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                minVolume = audioManager.getStreamMinVolume(AudioManager.STREAM_ALARM);
            } else {
                minVolume = 0;
            }
            volumeRange = originalVolume - minVolume;
            currentVolume = minVolume;
            audioManager.setStreamVolume(AudioManager.STREAM_ALARM, minVolume, 0);
        }
        long elapsedMillis = System.currentTimeMillis() - System.currentTimeMillis();
        assert alarmEntity != null;
        vibrate = alarmEntity.getVibrate(getApplicationContext());
        sound = alarmEntity.getSoundAlarm();
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                if (vibrate.equalsIgnoreCase("Vibrate") && sound != null) {
                    sound.play(instance);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                        vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                    else vibrator.vibrate(500);
                } else {
                    assert sound != null;
                    sound.play(instance);
                }
                if (isSlowWake) {
                    float slowWakeProgress = (float) elapsedMillis / slowWakeMillis;
                    if (currentVolume < originalVolume) {
                        int newVolume = minVolume + (int) Math.min(originalVolume, slowWakeProgress * volumeRange);
                        if (newVolume != currentVolume) {
                            audioManager.setStreamVolume(AudioManager.STREAM_ALARM, newVolume, 0);
                            currentVolume = newVolume;
                        }
                    }
                }
                handler.postDelayed(this, 1000);
            }
        };
        handler.post(runnable);
        if (sound != null)
            sound.play(instance);

        Intent startAlarmService = new Intent(getApplicationContext(), AlarmService.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(AlarmActivity.EXTRA_ALARM, alarmEntity);
        startAlarmService.putExtras(bundle);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getApplicationContext().startForegroundService(startAlarmService);
        }
        WakeLocker.release();
    }

    public void stopAnnoyances() {
        if (handler != null) handler.removeCallbacks(runnable);
        if (sound.isPlaying(this)) {
            sound.stop(this);
            if (isSlowWake) {
                audioManager.setStreamVolume(AudioManager.STREAM_ALARM, originalVolume, 0);
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Log.i(TAG, "Alarmio---");
        listeners = new ArrayList<>();
        alarms = new ArrayList<>();
        timers = new ArrayList<>();

        MobileAds.initialize(this, "ca-app-pub-1636422389316045/4010716135");

        player = ExoPlayerFactory.newSimpleInstance(this, new DefaultTrackSelector());
        player.addListener(this);
        storageCommon = new StorageCommon(new WeakReference<>(this));

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

        mBoldFont = Typeface.createFromAsset(getAssets(), "font/Neo Sans Intel-Bold.ttf");
        mRegularFont = Typeface.createFromAsset(getAssets(), "font/Neo Sans Intel.ttf");
        mBoldItalicFont = Typeface.createFromAsset(getAssets(), "font/Neo Sans Intel-Bold Italic.ttf");
        mItalicFont = Typeface.createFromAsset(getAssets(), "font/Neo Sans Intel-Italic.ttf");

        SleepReminderService.refreshSleepTime(this);
    }

    public StorageCommon getStorageCommonAlarmUp() {
        if (storageCommon == null) {
            storageCommon = new StorageCommon(new WeakReference<>(this));
        }
        return storageCommon;
    }

    public Typeface getRegularFont() {
        return mRegularFont;
    }

    public Typeface getBoldFont() {
        return mBoldFont;
    }

    public Typeface getItalicFont() {
        return mItalicFont;
    }

    public Typeface getBoldItalicFont() {
        return mBoldItalicFont;
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
        if (Utility.getTheme(getApplicationContext()) == THEME_DEFAULT) {
            Aesthetic.Companion.get()
                    .isDark(false)
                    .lightStatusBarMode(AutoSwitchMode.OFF)
                    .colorStatusBar(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT ? Color.TRANSPARENT : ContextCompat.getColor(this, R.color.colorStatusBarDefault))
                    .colorPrimaryDark(ContextCompat.getColor(this, R.color.colorPrimaryDarkDefault))
                    .colorWindowBackground(ContextCompat.getColor(this, R.color.colorWindowBackgroundDefault))
                    .colorNavigationBar(ContextCompat.getColor(this, R.color.colorNavigationBarDefault))
                    .colorPrimary(ContextCompat.getColor(this, R.color.colorPrimaryDefault))
                    .colorAccent(ContextCompat.getColor(this, R.color.colorAccent))
                    .apply();
        } else if (Utility.getTheme(getApplicationContext()) == THEME_ONE) {
            Aesthetic.Companion.get()
                    .isDark(false)
                    .lightStatusBarMode(AutoSwitchMode.OFF)
                    .colorStatusBar(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT ? Color.TRANSPARENT : ContextCompat.getColor(this, R.color.colorStatusBarOne))
                    .colorPrimaryDark(ContextCompat.getColor(this, R.color.colorPrimaryDarkOne))
                    .colorWindowBackground(ContextCompat.getColor(this, R.color.colorWindowBackgroundOne))
                    .colorNavigationBar(ContextCompat.getColor(this, R.color.colorNavigationBarOne))
                    .colorPrimary(ContextCompat.getColor(this, R.color.colorPrimaryOne))
                    .colorAccent(ContextCompat.getColor(this, R.color.colorAccentOne))
                    .apply();
        } else if (Utility.getTheme(getApplicationContext()) == THEME_TWO) {
            Aesthetic.Companion.get()
                    .isDark(false)
                    .lightStatusBarMode(AutoSwitchMode.OFF)
                    .colorStatusBar(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT ? Color.TRANSPARENT : ContextCompat.getColor(this, R.color.colorStatusBarTwo))
                    .colorPrimaryDark(ContextCompat.getColor(this, R.color.colorPrimaryDarkTwo))
                    .colorWindowBackground(ContextCompat.getColor(this, R.color.colorWindowBackgroundTwo))
                    .colorNavigationBar(ContextCompat.getColor(this, R.color.colorNavigationBarTwo))
                    .colorPrimary(ContextCompat.getColor(this, R.color.colorPrimaryTwo))
                    .colorAccent(ContextCompat.getColor(this, R.color.colorAccentTwo))
                    .apply();
        } else {
            Aesthetic.Companion.get()
                    .isDark(false)
                    .lightStatusBarMode(AutoSwitchMode.OFF)
                    .colorStatusBar(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT ? Color.TRANSPARENT : ContextCompat.getColor(this, R.color.colorStatusBarThree))
                    .colorPrimaryDark(ContextCompat.getColor(this, R.color.colorPrimaryDarkThree))
                    .colorWindowBackground(ContextCompat.getColor(this, R.color.colorWindowBackgroundThree))
                    .colorNavigationBar(ContextCompat.getColor(this, R.color.colorNavigationBarThree))
                    .colorPrimary(ContextCompat.getColor(this, R.color.colorPrimaryThree))
                    .colorAccent(ContextCompat.getColor(this, R.color.colorAccentThree))
                    .apply();
        }
    }

    /**
     * Determine if a ringtone is currently playing.
     *
     * @return True if a ringtone is currently playing.
     */
    public boolean isRingtonePlaying() {
        return currentRingtone != null && currentRingtone.isPlaying();
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

        if (listener != null) {
            updateTheme();
        }
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
