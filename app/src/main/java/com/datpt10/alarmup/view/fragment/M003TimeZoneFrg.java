package com.datpt10.alarmup.view.fragment;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.datpt10.alarmup.Alarmup;
import com.datpt10.alarmup.R;
import com.datpt10.alarmup.Utility;
import com.datpt10.alarmup.base.BaseFragment;
import com.datpt10.alarmup.model.DBManager;
import com.datpt10.alarmup.model.TimeZoneEntity;
import com.datpt10.alarmup.presenter.M003TimeZonePresenter;
import com.datpt10.alarmup.view.adapter.SearchAdapter;
import com.datpt10.alarmup.view.adapter.TimeZoneAdapter;
import com.datpt10.alarmup.view.event.OnM001HomePageCallBack;
import com.datpt10.alarmup.view.event.OnM003TimeZoneCallBack;
import com.datpt10.alarmup.widget.SearchViewAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.List;

/**
 * create by datpt on 10/25/2019.
 */
public class M003TimeZoneFrg extends BaseFragment<M003TimeZonePresenter, OnM001HomePageCallBack> implements OnM003TimeZoneCallBack {
    public static final String TAG = M003TimeZoneFrg.class.getName();
    private RecyclerView rlTimeZone, rlCity;
    private TimeZoneAdapter timeZoneAdapter;
    private List<TimeZoneEntity> mListTimeZone = new ArrayList<>();
    private DBManager dbManager;
    private View timezoneEmpty;
    private TextView timezoneTextEmpty;
    private SearchView searchView;
    private AdView mAdView;

    @Override
    protected void initViews() {
        mAdView = new AdView(mContext);
        mAdView.setAdUnitId("ca-app-pub-9133689301868303/7435904913");
        mAdView.setAdSize(AdSize.BANNER);
        LinearLayout layout = findViewById(R.id.admob_timeZone);
        layout.addView(mAdView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        dbManager = new DBManager(mContext);
        searchView = findViewById(R.id.sv_m003_search_city);
        rlCity = findViewById(R.id.rl_m003_city);
        rlCity.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        rlCity.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        rlCity.setHasFixedSize(false);

        searchView.setQueryHint(mContext.getResources().getString(R.string.txt_m003_item_search));
        timezoneEmpty = findViewById(R.id.timezone_empty);
        timezoneTextEmpty = findViewById(R.id.timezone_emptyText, Alarmup.getInstance().getRegularFont());
        timezoneTextEmpty.setText(R.string.txt_timezone_empty_text);

        rlTimeZone = findViewById(R.id.rl_m003_time_zone);
        rlTimeZone.setLayoutManager(new LinearLayoutManager(mContext));
        mListTimeZone = dbManager.getAllTimeZone();
        timeZoneAdapter = new TimeZoneAdapter(mContext, mListTimeZone, this);
        rlTimeZone.setAdapter(timeZoneAdapter);
        onTimeZoneChanged();

        searchView.setOnQueryTextListener(new SearchViewAdapter() {
            @Override
            public boolean onQueryTextChange(String newText) {
                setAdapterSearch(mPresenter.getItemSearch(newText));
                rlCity.setVisibility(newText.equalsIgnoreCase("") ? View.GONE : View.VISIBLE);
                return false;
            }
        });
    }

    public void setAdapterSearch(ArrayList<String> mListCity) {
        if (Utility.getTheme(mContext) == Alarmup.THEME_DEFAULT) {
            rlCity.setBackgroundColor(mContext.getResources().getColor(R.color.colorWindowBackgroundDefault));
        } else if (Utility.getTheme(mContext) == Alarmup.THEME_ONE) {
            rlCity.setBackgroundColor(mContext.getResources().getColor(R.color.colorWindowBackgroundOne));
        } else if (Utility.getTheme(mContext) == Alarmup.THEME_TWO) {
            rlCity.setBackgroundColor(mContext.getResources().getColor(R.color.colorWindowBackgroundTwo));
        } else {
            rlCity.setBackgroundColor(mContext.getResources().getColor(R.color.colorWindowBackgroundThree));
        }
        SearchAdapter searchAdapter = new SearchAdapter(mContext, mListCity, this);
        rlCity.setAdapter(searchAdapter);
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
    }

    private void onTimeZoneChanged() {
        if (timezoneEmpty != null && timeZoneAdapter != null)
            timezoneEmpty.setVisibility(timeZoneAdapter.getListData().size() > 0 ? View.GONE : View.VISIBLE);
    }

    @Override
    public void addTimeZone(String mNameCity) {
        TimeZoneEntity entity = new TimeZoneEntity(mNameCity);
        timeZoneAdapter.addItem(timeZoneAdapter.getListData().size(), entity);
        dbManager.addTimeZone(entity);
        rlCity.setVisibility(View.GONE);
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
