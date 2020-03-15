package com.datpt10.alarmup.view.fragment;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TableRow;
import android.widget.TextView;

import com.datpt10.alarmup.R;
import com.datpt10.alarmup.base.BaseFragment;
import com.datpt10.alarmup.model.TimerEntity;
import com.datpt10.alarmup.presenter.M004SettingPresenter;
import com.datpt10.alarmup.view.adapter.TimerAdapter;
import com.datpt10.alarmup.view.event.OnM001HomePageCallBack;
import com.datpt10.alarmup.view.event.OnM004TimerCallBack;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.concurrent.TimeUnit;

/**
 * create by datpt on 10/25/2019.
 */
public class M004TimerFrg extends BaseFragment<M004SettingPresenter, OnM001HomePageCallBack> implements OnM004TimerCallBack {
    public static final String TAG = M004TimerFrg.class.getName();
    private NumberPicker mNumberHour, mNumberMinute, mNumberSecond;
    private TextView tvHour, tvMinute, tvSecond;
    private TableRow trCountDown;
    private FloatingActionButton fl_Play_Pause;
    private TimerAdapter timerAdapter;
    private ImageButton imgAddTimer;
    private ListView lvTimer;
    private TimerEntity mTimerEntity;

    @Override
    protected void initViews() {

        fl_Play_Pause = findViewById(R.id.fl_m004_add_timer, this);
        tvHour = findViewById(R.id.tv_m004_hours);
        tvMinute = findViewById(R.id.tv_m004_minutes);
        tvSecond = findViewById(R.id.tv_m004_seconds);
        lvTimer = findViewById(R.id.lv_m004_timer);
        imgAddTimer = findViewById(R.id.ig_m004_add_timer, this);
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
        mNumberMinute.setOnScrollListener((numberPicker, i) -> {
            fl_Play_Pause.setVisibility(numberPicker.getValue() == 0 ? View.GONE : View.VISIBLE);
        });
        mNumberSecond.setOnScrollListener((numberPicker, i) -> {
            fl_Play_Pause.setVisibility(numberPicker.getValue() == 0 ? View.GONE : View.VISIBLE);
        });
        timerAdapter = new TimerAdapter(mContext, getTimerList(), this, lvTimer);
        lvTimer.setAdapter(timerAdapter);
    }

    @SuppressLint("RestrictedApi")
    private void showViewTimer(boolean isShow) {
        if (isShow) {
            lvTimer.setVisibility(View.GONE);
            imgAddTimer.setVisibility(View.GONE);
            trCountDown.setVisibility(View.VISIBLE);
            fl_Play_Pause.setVisibility(View.VISIBLE);

        } else {
            lvTimer.setVisibility(View.VISIBLE);
            imgAddTimer.setVisibility(View.VISIBLE);
            trCountDown.setVisibility(View.GONE);
            fl_Play_Pause.setVisibility(View.GONE);
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
        long mTimeInMillis = TimeUnit.HOURS.toMillis(mNumberHour.getValue())
                + TimeUnit.MINUTES.toMillis(mNumberMinute.getValue())
                + TimeUnit.SECONDS.toMillis(mNumberSecond.getValue());
        switch (idView) {
            case R.id.fl_m004_add_timer:
                if (mTimerEntity == null) {
                    TimerEntity timerEntity = getAlarmio().newTimer();
                    timerEntity.set(mContext, System.currentTimeMillis() + mTimeInMillis);
                    timerEntity.setDuration(mContext, mTimeInMillis);
                } else {
                    mTimerEntity.set(mContext, System.currentTimeMillis() + mTimeInMillis);
                    mTimerEntity.setDuration(mContext, mTimeInMillis);
                }
                timerAdapter.notifyDataSetChanged();
                showViewTimer(false);
                break;
            case R.id.ig_m004_add_timer:
                showViewTimer(true);
                break;
        }
    }

    @Override
    public void showNumberPicker(TimerEntity timerEntity) {
        mTimerEntity = timerEntity;
        showViewTimer(true);
    }

    @Override
    public void removeTimer(TimerEntity timerEntity) {
        assert getAlarmio() != null;
        getAlarmio().removeTimer(timerEntity);
        initViews();
        lvTimer.post(() -> timerAdapter.notifyDataSetChanged());
    }

    @Override
    public void onAlarmsChanged() {
    }

    @Override
    public void onTimersChanged() {
        if (lvTimer != null && timerAdapter != null) {
            lvTimer.post(() -> timerAdapter.notifyDataSetChanged());
        }
    }
}
