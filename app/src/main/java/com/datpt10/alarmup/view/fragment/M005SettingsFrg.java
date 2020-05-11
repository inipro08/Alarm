package com.datpt10.alarmup.view.fragment;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.transition.TransitionManager;

import com.datpt10.alarmup.Alarmup;
import com.datpt10.alarmup.R;
import com.datpt10.alarmup.Utility;
import com.datpt10.alarmup.base.BaseFragment;
import com.datpt10.alarmup.presenter.M005SettingsPresenter;
import com.datpt10.alarmup.util.CommonUtil;
import com.datpt10.alarmup.view.event.OnM001HomePageCallBack;
import com.datpt10.alarmup.view.event.OnM005SettingCallBack;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

public class M005SettingsFrg extends BaseFragment<M005SettingsPresenter, OnM001HomePageCallBack> implements OnM005SettingCallBack, AdapterView.OnItemSelectedListener {
    public static final String TAG = M005SettingsFrg.class.getName();
    private static final String SPINNER_THEME = "SPINNER_THEME";
    private static final String[] mTypeTheme = {
            "Default", "Theme 1", "Theme 2", "Theme 3"
    };
    private TextView tvUpdateTheme, tvAbout, tvRate;
    private ImageView ivUpdateTheme, ivAbout, ivRate, ivAuthor;
    private Spinner spinnerTheme;
    private LinearLayout lnAbout;
    private boolean isClickAbout;
    private AdView mAdView;

    @Override
    protected void initViews() {
        mAdView = new AdView(mContext);
        mAdView.setAdUnitId("ca-app-pub-9133689301868303/4723680932");
        mAdView.setAdSize(AdSize.BANNER);
        LinearLayout layout = findViewById(R.id.layout_admob);
        layout.addView(mAdView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        tvUpdateTheme = findViewById(R.id.tv_m005_update_theme, Alarmup.getInstance().getRegularFont());
        ivUpdateTheme = findViewById(R.id.iv_m005_update_theme);
        tvAbout = findViewById(R.id.tv_m005_about, this, Alarmup.getInstance().getRegularFont());
        ivAbout = findViewById(R.id.iv_m005_about, this);
        tvRate = findViewById(R.id.tv_m005_rate, this, Alarmup.getInstance().getBoldFont());
        ivRate = findViewById(R.id.iv_m005_rate, this);
        ivAuthor = findViewById(R.id.iv_m005_author);
        lnAbout = findViewById(R.id.ln_m005_about);

        setSpinnerTheme();
    }

    private void setSpinnerTheme() {
        spinnerTheme = findViewById(R.id.spinner_m005);
        spinnerTheme.setOnItemSelectedListener(this);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, mTypeTheme);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTheme.setAdapter(spinnerAdapter);
        int index = -1;
        for (int i = 0; i < mTypeTheme.length; i++) {
            if (mTypeTheme[i].equalsIgnoreCase(CommonUtil.getInstance().getPrefContent(SPINNER_THEME))) {
                index = i;
                break;
            }
        }
        spinnerTheme.setSelection(index);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.frg_m005_settings;
    }

    @Override
    protected M005SettingsPresenter getPresenter() {
        return new M005SettingsPresenter(this);
    }

    @Override
    protected String getTAG() {
        return null;
    }

    @Override
    protected void defineBackKey() {

    }

    @Override
    protected void onClickView(int idView) {
        switch (idView) {
            case R.id.tv_m005_about:
            case R.id.iv_m005_about:
                TransitionManager.beginDelayedTransition(lnAbout);
                isClickAbout = !isClickAbout;
                lnAbout.setVisibility(isClickAbout ? View.VISIBLE : View.GONE);
                break;
            case R.id.iv_m005_rate:
            case R.id.tv_m005_rate:
                Uri uri = Uri.parse("market://details?id=" + mContext.getPackageName());
                Intent goMarket = new Intent(Intent.ACTION_VIEW, uri);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    goMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY
                            | Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                            Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                }
                try {
                    mContext.startActivity(goMarket);
                } catch (ActivityNotFoundException e) {
                    mContext.startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" + mContext.getPackageName())));
                }
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        String item = adapterView.getItemAtPosition(position).toString();
        assert getAlarmup() != null;
        switch (item) {
            case "Default":
                setSpinnerItemTheme(mContext, adapterView, 0, "Default");
                break;
            case "Theme 1":
                setSpinnerItemTheme(mContext, adapterView, 1, "Theme 1");
                break;
            case "Theme 2":
                setSpinnerItemTheme(mContext, adapterView, 2, "Theme 2");
                break;
            case "Theme 3":
                setSpinnerItemTheme(mContext, adapterView, 3, "Theme 3");
                break;
            default:
                break;
        }
    }

    public void setSpinnerItemTheme(Context context, AdapterView<?> adapterView, int theme, String item) {
        ((TextView) adapterView.getChildAt(0)).setTypeface(Alarmup.getInstance().getBoldFont());
        ((TextView) adapterView.getChildAt(0)).setTextColor(mContext.getResources().getColor(R.color.colorAccent));
        CommonUtil.getInstance().savePrefContent(SPINNER_THEME, item);
        assert getAlarmup() != null;
        Utility.setTheme(context, theme);
        getAlarmup().updateTheme();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }
}
