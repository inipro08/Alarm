package com.datpt10.alarmup.activity;

import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.afollestad.aesthetic.AestheticActivity;
import com.datpt10.alarmup.Alarmio;
import com.datpt10.alarmup.R;
import com.datpt10.alarmup.model.AlarmEntity;
import com.datpt10.alarmup.model.PreferenceEntity;
import com.datpt10.alarmup.model.SoundEntity;
import com.datpt10.alarmup.model.TimerEntity;
import com.datpt10.alarmup.service.SleepReminderService;
import com.datpt10.alarmup.util.CommonUtil;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AlarmActivity extends AestheticActivity implements View.OnClickListener {

    public static final String EXTRA_ALARM = "com.datpt10.alarmup.activity.AlarmActivity.EXTRA_ALARM";
    public static final String EXTRA_TIMER = "com.datpt10.alarmup.activity.AlarmActivity..EXTRA_TIMER";
    public static final String DATE_NOW_DY = "yyyy-MM-dd hh:mm a";

    private TextView date, contentAlarm;
    private TextView questionContent, content;
    private Button btnCancel, btnMoreTime;
    private ImageView idea;

    private Alarmio alarmio;
    private Vibrator vibrator;
    private AudioManager audioManager;

    private boolean isAlarm;
    private long triggerMillis;
    private AlarmEntity alarm;
    private TimerEntity timer;
    private SoundEntity sound;
    private boolean isVibrate;

    private boolean isSlowWake;
    private long slowWakeMillis;

    private int currentVolume;
    private int minVolume;
    private int originalVolume;
    private int volumeRange;

    private Handler handler;
    private Runnable runnable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        alarmio = (Alarmio) getApplicationContext();

        date = findViewById(R.id.date);
        idea = findViewById(R.id.idea);
        questionContent = findViewById(R.id.question_content);
        content = findViewById(R.id.content);
        contentAlarm = findViewById(R.id.content_alarm);

        btnMoreTime = findViewById(R.id.bt_more_time);
        btnCancel = findViewById(R.id.bt_cancel);

        btnMoreTime.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

        isSlowWake = PreferenceEntity.SLOW_WAKE_UP.getValue(this);
        slowWakeMillis = PreferenceEntity.SLOW_WAKE_UP_TIME.getValue(this);

        isAlarm = getIntent().hasExtra(EXTRA_ALARM);
        if (isAlarm) {
            alarm = getIntent().getParcelableExtra(EXTRA_ALARM);
            if (alarm.hasSound())
                sound = alarm.getSoundAlarm();
        } else if (getIntent().hasExtra(EXTRA_TIMER)) {
            timer = getIntent().getParcelableExtra(EXTRA_TIMER);
            sound = alarm.getSoundAlarm();
        } else finish();
        date.setText(CommonUtil.getDateNow(DATE_NOW_DY));
        contentAlarm.setText(alarm.getContent(getApplicationContext()));
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
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
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        triggerMillis = System.currentTimeMillis();
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                long elapsedMillis = System.currentTimeMillis() - triggerMillis;

                if (sound != null && !sound.isPlaying(alarmio)) {
                    sound.play(alarmio);
                }
                if (alarm != null && isSlowWake) {
                    float slowWakeProgress = (float) elapsedMillis / slowWakeMillis;

                    WindowManager.LayoutParams params = getWindow().getAttributes();
                    params.screenBrightness = Math.max(0.01f, Math.min(1f, slowWakeProgress));
                    getWindow().setAttributes(params);
                    getWindow().addFlags(WindowManager.LayoutParams.FLAGS_CHANGED);

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
            sound.play(alarmio);
        List<String> countryListArray = new ArrayList<>();
        try {
            JSONArray jArray = new JSONArray(readJsonFileFromAssets());
            for (int i = 0; i < jArray.length(); ++i) {
                String name = jArray.getJSONObject(i).getString("content");
                countryListArray.add(name);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Random random = new Random();
        content.setText(countryListArray.get(random.nextInt(countryListArray.size())));
        SleepReminderService.refreshSleepTime(alarmio);
    }

    public String readJsonFileFromAssets() {
        String json = null;
        try {
            InputStream inputStream = getAssets().open("data/maybe_en.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopAnnoyances();
    }

    private void stopAnnoyances() {
        if (handler != null)
            handler.removeCallbacks(runnable);
        if (sound.isPlaying(alarmio)) {
            sound.stop(alarmio);
            if (isSlowWake) {
                audioManager.setStreamVolume(AudioManager.STREAM_ALARM, originalVolume, 0);
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_more_time:
                AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarm.setTime(getApplicationContext(), manager, 5000);
                onDestroy();
                break;
            case R.id.bt_cancel:
                stopAnnoyances();
                onDestroy();
                break;
            default:
                break;
        }
    }
}

