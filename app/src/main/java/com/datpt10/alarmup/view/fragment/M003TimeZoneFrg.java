package com.datpt10.alarmup.view.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.datpt10.alarmup.R;
import com.datpt10.alarmup.base.BaseFragment;
import com.datpt10.alarmup.model.DBManager;
import com.datpt10.alarmup.model.entities.TimeZoneEntity;
import com.datpt10.alarmup.presenter.M003TimeZonePresenter;
import com.datpt10.alarmup.view.adapter.TimeZoneAdapter;
import com.datpt10.alarmup.view.event.OnM001HomePageCallBack;
import com.datpt10.alarmup.view.event.OnM003TimeZoneCallBack;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

/**
 * create by datpt on 10/25/2019.
 */
public class M003TimeZoneFrg extends BaseFragment<M003TimeZonePresenter, OnM001HomePageCallBack> implements OnM003TimeZoneCallBack {
    public static final String TAG = M003TimeZoneFrg.class.getName();
    public String mNameCity, mHourCity;
    private RecyclerView rlTimeZone;
    private TimeZoneAdapter timeZoneAdapter;
    private List<TimeZoneEntity> mListTimeZone = new ArrayList<>();
    private DBManager dbManager;
    private View timezoneEmpty;
    private TextView timezoneTextEmpty;

    @Override
    protected void initViews() {
        dbManager = new DBManager(mContext);
        findViewById(R.id.ib_m003_add_time_zone, this);
        timezoneEmpty = findViewById(R.id.timezone_empty);
        timezoneTextEmpty = findViewById(R.id.timezone_emptyText);
        timezoneTextEmpty.setText(R.string.txt__timezone_empty_text);
        rlTimeZone = findViewById(R.id.rl_m003_time_zone);
        rlTimeZone.setLayoutManager(new LinearLayoutManager(mContext));
        rlTimeZone.setHasFixedSize(true);
        mListTimeZone = dbManager.getAllTimeZone();
        timeZoneAdapter = new TimeZoneAdapter(mContext, mListTimeZone, this);
        rlTimeZone.setAdapter(timeZoneAdapter);
        onTimeZoneChanged();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.frg_m003_time_zone;
    }

    @Override
    protected M003TimeZonePresenter getPresenter() {
        return new M003TimeZonePresenter(this);
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
        if (idView == R.id.ib_m003_add_time_zone) {
            showListTimeZone(mContext, "", "");
        }
    }

    private void onTimeZoneChanged() {
        if (timezoneEmpty != null && timeZoneAdapter != null)
            timezoneEmpty.setVisibility(dbManager.getAllTimeZone().size() > 0 ? View.GONE : View.VISIBLE);
    }

    public void showListTimeZone(Context context, String city, String hour) {
        mNameCity = city;
        mHourCity = hour;
        ArrayList<String> mListId = new ArrayList<>();
        ArrayAdapter<String> arrayAdapter;
        int[] item = new int[1];
        String[] ids = TimeZone.getAvailableIDs();
        for (String id : ids) {
            String id2 = TimeZone.getTimeZone(id).getID();
            mListId.add(id2);
        }
        arrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_expandable_list_item_1, mListId);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose a city...");
        builder.setAdapter(arrayAdapter, null);
        AlertDialog alertDialog = builder.create();
        alertDialog.getListView().setOnItemClickListener((parent, view, position, id) -> item[0] = position);
        alertDialog.getListView().setDividerHeight(2);
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OKAY", (dialog, position) -> {
            CharSequence[] sequences = mListId.toArray(new String[mListId.size()]);
            mNameCity = sequences[item[0]].toString();
            addItem(mNameCity);
        });
        alertDialog.show();
    }

    private void addItem(String mNameCity) {
        TimeZoneEntity entity = new TimeZoneEntity(mNameCity);
        timeZoneAdapter.addItem(0, entity);
        dbManager.addTimeZone(entity);
        onTimeZoneChanged();
    }

    @Override
    public void changedView() {
        onTimeZoneChanged();
    }

    @Override
    public void onAlarmsChanged() {
    }

    @Override
    public void onTimersChanged() {
    }
}
