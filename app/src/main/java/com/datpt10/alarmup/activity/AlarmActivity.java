package com.datpt10.alarmup.activity;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.content.Context;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.datpt10.alarmup.Alarmup;
import com.datpt10.alarmup.R;
import com.datpt10.alarmup.model.AlarmEntity;
import com.datpt10.alarmup.model.PreferenceEntity;
import com.datpt10.alarmup.model.SoundEntity;
import com.datpt10.alarmup.service.SleepReminderService;
import com.datpt10.alarmup.util.CommonUtil;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AlarmActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String EXTRA_ALARM = "com.datpt10.alarmup.activity.AlarmActivity.EXTRA_ALARM";
    public static final String EXTRA_TIMER = "com.datpt10.alarmup.activity.AlarmActivity.EXTRA_TIMER";
    public static final String DATE_NOW_DY = "yyyy-MM-dd hh:mm a";
    private TextView date, titleAlarm;
    private TextView questionContent, content;
    private Button btnCancel, btnMoreTime;
    private ImageView idea;
    private Alarmup alarmup;
    private AudioManager audioManager;
    private boolean isAlarm;
    private Vibrator vibrator;
    private long triggerMillis;
    private AlarmEntity alarm;
    private SoundEntity sound;
    private boolean isSlowWake;
    private long slowWakeMillis;
    private int currentVolume;
    private int minVolume;
    private int originalVolume;
    private int volumeRange;
    private Handler handler;
    private Runnable runnable;
    private AdView adView;
    private String vibrate;

    @SuppressLint("InvalidWakeLockTag")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        CommonUtil.wtfi("AlarmActivity","OnCreate---");
        adView = new AdView(getApplicationContext());
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId("ca-app-pub-9133689301868303/1877778327");
        LinearLayout layout = findViewById(R.id.layout_ads_alarm);
        layout.addView(adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        alarmup = (Alarmup) getApplicationContext();
        date = findViewById(R.id.date);
        idea = findViewById(R.id.idea);
        questionContent = findViewById(R.id.tv_question_content);
        content = findViewById(R.id.tv_content);
        titleAlarm = findViewById(R.id.content_alarm);
        btnMoreTime = findViewById(R.id.bt_more_time);
        btnCancel = findViewById(R.id.bt_cancel);
        btnMoreTime.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        isSlowWake = PreferenceEntity.SLOW_WAKE_UP.getValue(this);
        slowWakeMillis = PreferenceEntity.SLOW_WAKE_UP_TIME.getValue(this);

        isAlarm = getIntent().hasExtra(EXTRA_ALARM);
        if (isAlarm) {
            alarm = getIntent().getParcelableExtra(EXTRA_ALARM);
            assert alarm != null;
            vibrate = alarm.getVibrate(getApplicationContext());
            if (alarm.hasSound()) {
                sound = alarm.getSoundAlarm();
            }
        } else finish();
        date.setText(CommonUtil.getDateNow(DATE_NOW_DY));
        titleAlarm.setText(alarm.getContent(getApplicationContext()));
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
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        triggerMillis = System.currentTimeMillis();
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                long elapsedMillis = System.currentTimeMillis() - triggerMillis;
                if (vibrate.equalsIgnoreCase("Vibrate") && sound != null) {
                    sound.play(alarmup);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                        vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                    else vibrator.vibrate(500);
                } else {
                    assert sound != null;
                    sound.play(alarmup);
                }
                if (alarm != null && isSlowWake) {
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
        if (sound != null) sound.play(alarmup);
        showContentRandom();
    }

    @Override
    protected void onPause() {
        if (adView != null) adView.pause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adView != null) adView.resume();

    }

    public void showContentRandom() {
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
        SleepReminderService.refreshSleepTime(alarmup);
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
                | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopAnnoyances();
    }

    public void stopAnnoyances() {
        if (handler != null) handler.removeCallbacks(runnable);
        if (sound.isPlaying(alarmup)) {
            sound.stop(alarmup);
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
                alarm.setTime(getApplicationContext(), manager, System.currentTimeMillis() + 5 * 60 * 1000);
                stopAnnoyances();
                finish();
                break;
            case R.id.bt_cancel:
                stopAnnoyances();
                finish();
                break;
        }
    }
}

