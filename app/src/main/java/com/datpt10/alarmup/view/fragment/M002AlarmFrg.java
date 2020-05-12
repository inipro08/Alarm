package com.datpt10.alarmup.view.fragment;

import android.app.AlarmManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.aesthetic.Aesthetic;
import com.datpt10.alarmup.Alarmup;
import com.datpt10.alarmup.R;
import com.datpt10.alarmup.base.BaseFragment;
import com.datpt10.alarmup.model.AlarmEntity;
import com.datpt10.alarmup.presenter.M002AlarmPresenter;
import com.datpt10.alarmup.view.adapter.AlarmAdapter;
import com.datpt10.alarmup.view.event.OnM001HomePageCallBack;
import com.datpt10.alarmup.view.event.OnM002AlarmCallBack;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;

import io.reactivex.disposables.Disposable;


/**
 * create by datpt on 10/24/2019.
 */
public class M002AlarmFrg extends BaseFragment<M002AlarmPresenter, OnM001HomePageCallBack> implements OnM002AlarmCallBack, Alarmup.AlarmListener {
    public static final String TAG = M002AlarmFrg.class.getName();
    private AlarmAdapter alarmAdapter;
    private RecyclerView recyclerAlarm;
    private Disposable colorAccentSubscription;
    private Disposable colorForegroundSubscription;
    private Disposable textColorPrimarySubscription;
    private View empty;
    private TextView emptyText;
    private FloatingActionButton fladdAlarm;


    @Override
    protected void initViews() {
        recyclerAlarm = findViewById(R.id.rl_m002_list_alarm);
        fladdAlarm = findViewById(R.id.ig_m002_add_alarm, this);
        empty = findViewById(R.id.empty);
        emptyText = findViewById(R.id.emptyText, Alarmup.getInstance().getRegularFont());
        emptyText.setText(R.string.txt_alarm_empty_text);
        recyclerAlarm.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        recyclerAlarm.setHasFixedSize(true);
        recyclerAlarm.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        alarmAdapter = new AlarmAdapter(mContext, getAlarmList(), this, recyclerAlarm, getAlarmup());
        recyclerAlarm.setAdapter(alarmAdapter);

        colorAccentSubscription = Aesthetic.Companion.get()
                .colorAccent().doOnError(throwable -> Log.e(TAG, "Throwable " + throwable.getMessage()))
                .subscribe(integer -> alarmAdapter.setColorAccent(integer));

        colorForegroundSubscription = Aesthetic.Companion.get()
                .colorCardViewBackground().doOnError(throwable -> Log.e(TAG, "Throwable " + throwable.getMessage()))
                .subscribe(integer -> alarmAdapter.setColorForeground(integer));

        textColorPrimarySubscription = Aesthetic.Companion.get()
                .textColorPrimary().doOnError(throwable -> Log.e(TAG, "Throwable " + throwable.getMessage()))
                .subscribe(integer -> alarmAdapter.setTextColorPrimary(integer));
        onChanged();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.frg_m002_alarm;
    }

    @Override
    protected M002AlarmPresenter getPresenter() {
        return new M002AlarmPresenter(this);
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
        if (idView == R.id.ig_m002_add_alarm) {
            showTimePicker(mContext);
        }
    }

    @Override
    public void onDestroyView() {
        colorAccentSubscription.dispose();
        colorForegroundSubscription.dispose();
        textColorPrimarySubscription.dispose();
        super.onDestroyView();
    }

    @Override
    public void onAlarmsChanged() {
        if (recyclerAlarm != null && alarmAdapter != null) {
            recyclerAlarm.post(() -> alarmAdapter.notifyDataSetChanged());
            onChanged();
        }
    }

    private void onChanged() {
        if (empty != null && alarmAdapter != null)
            empty.setVisibility(alarmAdapter.getAlarmData().size() > 0 ? View.GONE : View.VISIBLE);
    }

    public void showTimePicker(Context mContext) {
        Calendar noteCal = Calendar.getInstance();
        int mHour = noteCal.get(Calendar.HOUR_OF_DAY);
        int mMin = noteCal.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(mContext, (timePicker, hourOfDay, minute) -> {
            noteCal.set(Calendar.HOUR_OF_DAY, hourOfDay);
            noteCal.set(Calendar.MINUTE, minute);
            AlarmManager manager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
            AlarmEntity alarm = getAlarmup().newAlarm();
            alarm.setTime(mContext, manager, noteCal.getTimeInMillis());
            alarm.setEnabled(mContext, manager, true);
            initViews();
        }, mHour, mMin, false);
        timePickerDialog.show();
    }

    @Override
    public void onTimersChanged() {
    }

    @Override
    public void removeAlarm(AlarmEntity alarmEntity) {
        assert getAlarmup() != null;
        getAlarmup().removeAlarm(alarmEntity);
    }

}
