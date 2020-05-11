package com.datpt10.alarmup.view.fragment;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TableRow;
import android.widget.TextView;

import com.datpt10.alarmup.Alarmup;
import com.datpt10.alarmup.R;
import com.datpt10.alarmup.base.BaseFragment;
import com.datpt10.alarmup.model.TimerEntity;
import com.datpt10.alarmup.presenter.M004TimerPresenter;
import com.datpt10.alarmup.service.TimerService;
import com.datpt10.alarmup.util.CommonUtil;
import com.datpt10.alarmup.view.adapter.TimerAdapter;
import com.datpt10.alarmup.view.event.OnM001HomePageCallBack;
import com.datpt10.alarmup.view.event.OnM004TimerCallBack;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.concurrent.TimeUnit;

/**
 * create by datpt on 10/25/2019.
 */
public class M004TimerFrg extends BaseFragment<M004TimerPresenter, OnM001HomePageCallBack> implements OnM004TimerCallBack {
    public static final String TAG = M004TimerFrg.class.getName();
    private NumberPicker mNumberHour, mNumberMinute, mNumberSecond;
    private TextView tvHour, tvMinute, tvSecond;
    private TableRow trCountDown;
    private FloatingActionButton fl_Play_Pause;
    private TimerAdapter timerAdapter;
    private ImageButton imgAddTimer;
    private ListView lvTimer;
    private AdView mAdView;

    @Override
    protected void initViews() {
        mAdView = new AdView(mContext);
        mAdView.setAdUnitId("ca-app-pub-9133689301868303/6140254676");
        mAdView.setAdSize(AdSize.BANNER);
        LinearLayout layout = findViewById(R.id.admob_timer);
        layout.addView(mAdView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        fl_Play_Pause = findViewById(R.id.fl_m004_add_timer, this);
        tvHour = findViewById(R.id.tv_m004_hours, Alarmup.getInstance().getBoldFont());
        tvMinute = findViewById(R.id.tv_m004_minutes, Alarmup.getInstance().getBoldFont());
        tvSecond = findViewById(R.id.tv_m004_seconds, Alarmup.getInstance().getBoldFont());
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
        isVisibleListTimer();
        timerAdapter = new TimerAdapter(mContext, getTimerList(), this, lvTimer);
        lvTimer.setAdapter(timerAdapter);
    }

    private void isVisibleListTimer() {
        if (getTimerList().size() == 0) showViewTimer(true);
        else showViewTimer(false);
    }

    @SuppressLint("RestrictedApi")
    private void showViewTimer(boolean isShow) {
        lvTimer.setVisibility(isShow ? View.GONE : View.VISIBLE);
        imgAddTimer.setVisibility(isShow ? View.GONE : View.VISIBLE);
        trCountDown.setVisibility(isShow ? View.VISIBLE : View.GONE);
        fl_Play_Pause.setVisibility(isShow ? View.VISIBLE : View.GONE);
        lvTimer.setVisibility(!isShow ? View.VISIBLE : View.GONE);
        imgAddTimer.setVisibility(!isShow ? View.VISIBLE : View.GONE);
        trCountDown.setVisibility(!isShow ? View.GONE : View.VISIBLE);
        fl_Play_Pause.setVisibility(!isShow ? View.GONE : View.VISIBLE);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.frg_m004_timer;
    }

    @Override
    protected M004TimerPresenter getPresenter() {
        return new M004TimerPresenter(this);
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
                if (mTimeInMillis != 0 && mTimeInMillis <= TimeUnit.SECONDS.toMillis(86400)) {
                    TimerEntity timerEntity = getAlarmup().newTimer();
                    timerEntity.set(mContext, (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE), System.currentTimeMillis() + mTimeInMillis);
                    timerEntity.setDuration(mContext, mTimeInMillis);
                    timerEntity.setContentTimer(mContext, "");
                    mContext.startService(new Intent(mContext, TimerService.class));
                    timerAdapter.notifyDataSetChanged();
                    showViewTimer(false);
                } else {
                    CommonUtil.getInstance().showDialog(mContext, mContext.getString(R.string.txt_dialog_timer), null);
                }
                break;
            case R.id.ig_m004_add_timer:
                showViewTimer(true);
                mContext.stopService(new Intent(mContext, TimerService.class));
                setNumberPickerDefault();
                break;
        }
    }

    void setNumberPickerDefault() {
        mNumberHour.setValue(0);
        mNumberMinute.setValue(0);
        mNumberSecond.setValue(0);
    }

    @Override
    public void removeTimer(TimerEntity timerEntity) {
        assert getAlarmup() != null;
        getAlarmup().removeTimer(timerEntity);
        lvTimer.post(() -> timerAdapter.notifyDataSetChanged());
        isVisibleListTimer();
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
