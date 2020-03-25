package com.datpt10.alarmup.base;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;

import com.afollestad.aesthetic.Aesthetic;
import com.datpt10.alarmup.ANApplication;
import com.datpt10.alarmup.Alarmio;
import com.datpt10.alarmup.presenter.BasePresenter;
import com.datpt10.alarmup.util.StorageCommon;

public abstract class BaseActivity<T extends BasePresenter> extends Activity implements Alarmio.ActivityListener {

    protected static final int NO_LAYOUT = -1;
    private static final String TAG = BaseActivity.class.getName();
    public transient String currentTag;
    protected T mPresenter;
    protected boolean isDestroyed;
    private Alarmio alarmio;

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
        Toast.makeText(ANApplication.getInstance(), text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public final void onCreate(Bundle savedInstanceState) {
        Aesthetic.Companion.attach(this);
        super.onCreate(savedInstanceState);
        Log.i(TAG, "BaseActivity: onCreate...");
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        int layoutId = getLayoutId();
        if (layoutId != NO_LAYOUT) {
            setContentView(layoutId);
        }
        mPresenter = getPresenter();
        alarmio = (Alarmio) getApplicationContext();
        alarmio.setListener(this);
        initViews();
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
        alarmio.stopCurrentSound();
    }

    protected void onClickView(int idView) {

    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "BaseActivity: onDestroy...");
        handleOnDestroy();
        super.onDestroy();
        currentTag = null;
        if (alarmio != null)
            alarmio.setListener(null);
        alarmio = null;
    }

    public StorageCommon getStorage() {
        return ANApplication.getInstance().getStorageCommon();
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