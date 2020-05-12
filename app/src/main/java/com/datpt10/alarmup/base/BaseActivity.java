package com.datpt10.alarmup.base;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.afollestad.aesthetic.Aesthetic;
import com.datpt10.alarmup.Alarmup;
import com.datpt10.alarmup.R;
import com.datpt10.alarmup.presenter.BasePresenter;
import com.datpt10.alarmup.util.CommonUtil;
import com.datpt10.alarmup.util.StorageCommon;

import java.util.Locale;

public abstract class BaseActivity<T extends BasePresenter> extends AppCompatActivity implements Alarmup.ActivityListener {

    public static final String LANGUAGE_KEY = "LANGUAGE_KEY";
    protected static final int NO_LAYOUT = -1;
    private static final String TAG = BaseActivity.class.getName();
    public transient String currentTag;
    protected T mPresenter;
    protected boolean isDestroyed;
    private Alarmup alarmup;

    private int STORAGE_PERMISSION_CODE = 1;

    protected abstract int getLayoutId();

    protected abstract void initViews();

    protected abstract void initData();

    protected void handleOnDestroy() {
        isDestroyed = true;
    }

    protected abstract <G> G getPresenter();

    protected final <G extends View> G findViewById(int id, View.OnClickListener event) {
        G view = findViewById(id);
        view.setOnClickListener(event);
        return view;
    }

    protected void showNotify(String text) {
        Toast.makeText(Alarmup.getInstance(), text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public final void onCreate(Bundle savedInstanceState) {
        Aesthetic.Companion.attach(this);
        super.onCreate(savedInstanceState);
        String languageKey = CommonUtil.getInstance().getPrefContent(LANGUAGE_KEY);
        if (languageKey == null || languageKey.equals("vi")) setLang("vi");
        else setLang("en");

        Log.i(TAG, "BaseActivity: onCreate...");
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        int layoutId = getLayoutId();
        if (layoutId != NO_LAYOUT) {
            setContentView(layoutId);
        }
        mPresenter = getPresenter();
        alarmup = (Alarmup) getApplicationContext();
        alarmup.setListener(this);
        initViews();
        getSupportActionBar().hide();
    }

    public void setLang(String lang) {
        Locale myLocale = new Locale(lang);
        Resources resources = getResources();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        Configuration configuration = resources.getConfiguration();
        configuration.locale = myLocale;
        resources.updateConfiguration(configuration, displayMetrics);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        initData();
    }

    @Override
    protected final void onResume() {
        super.onResume();
        Aesthetic.Companion.resume(this);
    }

    @Override
    public final void onPause() {
        Aesthetic.Companion.pause(this);
        super.onPause();
        alarmup.stopCurrentSound();
    }

    protected void onClickView(int idView) {
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "BaseActivity: onDestroy...");
        handleOnDestroy();
        super.onDestroy();
        currentTag = null;
//        if (alarmup != null)
//            alarmup.setListener(null);
//        alarmup = null;
    }

    public StorageCommon getStorage() {
        return Alarmup.getInstance().getStorageCommonAlarmUp();
    }

    public void setTagCurrentFrg(String tag) {
        currentTag = tag;
    }

    @Override
    public boolean isDestroyed() {
        return isDestroyed;
    }

    @Override
    public void requestPermissions(String... permissions) {
    }

    @Override
    public FragmentManager gettFragmentManager() {
        return null;
    }
}