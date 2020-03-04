package com.datpt10.alarmup.view.fragment;

import android.util.Log;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.aesthetic.Aesthetic;
import com.datpt10.alarmup.Alarmio;
import com.datpt10.alarmup.R;
import com.datpt10.alarmup.base.BaseFragment;
import com.datpt10.alarmup.model.AlarmEntity;
import com.datpt10.alarmup.presenter.M002AlarmPresenter;
import com.datpt10.alarmup.view.adapter.AlarmAdapter;
import com.datpt10.alarmup.view.event.OnM001HomePageCallBack;
import com.datpt10.alarmup.view.event.OnM002AlarmCallBack;
import com.datpt10.alarmup.view.adapter.AlarmAdapter;

import io.reactivex.disposables.Disposable;


/**
 * create by datpt on 10/24/2019.
 */
public class M002AlarmFrg extends BaseFragment<M002AlarmPresenter, OnM001HomePageCallBack> implements OnM002AlarmCallBack, Alarmio.AlarmListener {
    public static final String TAG = M002AlarmFrg.class.getName();
    private AlarmAdapter alarmAdapter;
    private RecyclerView recyclerAlarm;

    private Disposable colorAccentSubscription;
    private Disposable colorForegroundSubscription;
    private Disposable textColorPrimarySubscription;


    @Override
    protected void initViews() {
        recyclerAlarm = findViewById(R.id.rl_m002_list_alarm);
        recyclerAlarm.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerAlarm.setHasFixedSize(true);
        recyclerAlarm.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        alarmAdapter = new AlarmAdapter(mContext, getAlarmList(), this, recyclerAlarm);
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
//        set background alarm while alarm empty
//        if (empty != null && alarmAdapter != null)
//            empty.setVisibility(alarmAdapter.getItemCount() > 0 ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onTimersChanged() {
        if (recyclerAlarm != null && alarmAdapter != null) {
            recyclerAlarm.post(() -> alarmAdapter.notifyDataSetChanged());

            onChanged();
        }
    }

    @Override
    public void removeAlarm(AlarmEntity alarmEntity) {
        assert getAlarmio() != null;
        getAlarmio().removeAlarm(alarmEntity);
    }
}
