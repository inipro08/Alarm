package com.datpt10.alarmup.view.fragment;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TableRow;
import android.widget.TextView;

import com.datpt10.alarmup.R;
import com.datpt10.alarmup.base.BaseFragment;
import com.datpt10.alarmup.model.TimerEntity;
import com.datpt10.alarmup.presenter.M004SettingPresenter;
import com.datpt10.alarmup.service.TimerService;
import com.datpt10.alarmup.view.event.OnM001HomePageCallBack;
import com.datpt10.alarmup.view.event.OnM004TimerCallBack;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Locale;

import me.tankery.lib.circularseekbar.CircularSeekBar;

/**
 * create by datpt on 10/25/2019.
 */
public class M004TimerFrg extends BaseFragment<M004SettingPresenter, OnM001HomePageCallBack> implements OnM004TimerCallBack {
    public static final String TAG = M004TimerFrg.class.getName();
    private static final String TIMER_LEFT = "TIMER_LEFT";
    private static final String TIME_LEFT_PAUSE = "TIME_LEFT_PAUSE";
    private NumberPicker mNumberHour, mNumberMinute, mNumberSecond;
    private TextView tvHour, tvMinute, tvSecond, tvCountDownTimer;
    private FloatingActionButton fl_Cancel, fl_Play_Pause;
    private CircularSeekBar mCircularSeekBar;
    private TableRow trCountDown;
    private boolean isTimerRunning;
    private long mTimerLeftInMillis;
    private long mTimerContinue;
    private String timeLeftFormatted;
    private BroadcastReceiver timerReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long mTimerLeft = intent.getExtras().getLong("CountDown");
            updateCountDownText(mTimerLeft);
            mTimerContinue = intent.getExtras().getLong("TimerLeft");
        }
    };

    @Override
    protected void initViews() {
        fl_Play_Pause = findViewById(R.id.fl_m004_play_pause, this);
        numberPicker();
    }

    @SuppressLint("RestrictedApi")
    private void numberPicker() {
        tvHour = findViewById(R.id.tv_m004_hours);
        tvMinute = findViewById(R.id.tv_m004_minutes);
        tvSecond = findViewById(R.id.tv_m004_seconds);

        mNumberHour = findViewById(R.id.number_m004_hour);
        mNumberHour.setMaxValue(24);
        mNumberHour.setMinValue(0);

        mNumberMinute = findViewById(R.id.number_m004_minute);
        mNumberMinute.setMaxValue(60);
        mNumberMinute.setMinValue(0);

        mNumberSecond = findViewById(R.id.number_m004_second);
        mNumberSecond.setMaxValue(60);
        mNumberSecond.setMinValue(0);
        trCountDown = findViewById(R.id.tr_m004_countDown);

        mNumberHour.setOnScrollListener((numberPicker, i) -> {
            fl_Play_Pause.setVisibility(numberPicker.getValue() == 0 ? View.GONE : View.VISIBLE);
        });
    }

    @SuppressLint("RestrictedApi")
    private void showTimerSelect(boolean isShow) {
        if (isShow) {
            mCircularSeekBar.setVisibility(View.GONE);
            tvCountDownTimer.setVisibility(View.GONE);
            fl_Cancel.setVisibility(View.GONE);
            trCountDown.setVisibility(View.VISIBLE);
            fl_Play_Pause.setImageDrawable(getResources().getDrawable(R.drawable.ic_play));
        } else {
            mCircularSeekBar.setVisibility(View.VISIBLE);
            tvCountDownTimer.setVisibility(View.VISIBLE);
            fl_Cancel.setVisibility(View.VISIBLE);
            trCountDown.setVisibility(View.GONE);
            fl_Play_Pause.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause));
        }

    }

    @Override
    protected int getLayoutId() {
        return R.layout.frg_m004_timer;
    }

    @Override
    protected M004SettingPresenter getPresenter() {
        return new M004SettingPresenter(this);
    }

    @Override
    protected String getTAG() {
        return TAG;
    }

    @Override
    protected void defineBackKey() {
    }

    @Override
    protected void onClickView(int idView) {
        switch (idView) {
            case R.id.fl_m004_play_pause:
                break;
        }
    }

    private void updateCountDownText(long mTimerLeft) {
        long Hours = (mTimerLeft / (60 * 60 * 1000)) % 24;
        long Minutes = (mTimerLeft / (60 * 1000)) % 60;
        long Seconds = (mTimerLeft / 1000) % 60;

        if (mNumberHour.getValue() > 0) {
            timeLeftFormatted = String.format(Locale.getDefault(), "%02d : %02d : %02d", Hours, Minutes, Seconds);
        } else if (mNumberMinute.getValue() > 0) {
            timeLeftFormatted = String.format(Locale.getDefault(), "%02d : %02d", Minutes, Seconds);
        } else if (mNumberSecond.getValue() > 0) {
            timeLeftFormatted = String.format(Locale.getDefault(), "%02d", Seconds);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mContext.unregisterReceiver(timerReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        mContext.registerReceiver(timerReceiver, new IntentFilter(TimerService.COUNTDOWN_BR));
    }

    @Override
    public void onAlarmsChanged() {

    }

    @Override
    public void onTimersChanged() {

    }

    @Override
    public void removeTimer(TimerEntity timerEntity) {
        assert getAlarmio() != null;
        getAlarmio().removeTimer(timerEntity);
    }

    @Override
    public void addTimer() {
        assert getAlarmio() != null;
        numberPicker();
    }
}
