package com.datpt10.alarmup.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.datpt10.alarmup.Alarmup;
import com.datpt10.alarmup.R;
import com.datpt10.alarmup.base.BaseActivity;
import com.datpt10.alarmup.base.BaseFragment;
import com.datpt10.alarmup.model.TimerEntity;
import com.datpt10.alarmup.presenter.HomePresenter;
import com.datpt10.alarmup.util.CommonUtil;
import com.datpt10.alarmup.view.event.OnHomeBackToView;
import com.datpt10.alarmup.view.event.OnLanguageCallBack;
import com.datpt10.alarmup.view.fragment.M001HomePageFrg;

import java.lang.reflect.Constructor;
import java.util.HashMap;

public class HomeActivity extends BaseActivity<HomePresenter> implements OnHomeBackToView, OnLanguageCallBack {
    public static final String TAG = HomeActivity.class.getName();
    public static final String EXTRA_TIMER = "EXTRA_TIMER";
    public static final String TIMER_ID = "TIMER_ID";
    private static final String ALREADY_VALUE = "YES";
    private final HashMap<String, BaseFragment> mFrags = new HashMap<>();
    private AlertDialog mAlertDialog;
    private Alarmup alarmup;

    @Override
    protected int getLayoutId() {
        return R.layout.act_home;
    }

    @Override
    protected void initViews() {
        Log.d(TAG, "initViews...");
        Intent intent = this.getIntent();
        if (getIntent().hasExtra(EXTRA_TIMER)) {
            TimerEntity timerEntity = getIntent().getParcelableExtra(EXTRA_TIMER);
            assert timerEntity != null;
            int duration = (int) timerEntity.getDuration();
            int idEntity = timerEntity.getId();
            CommonUtil.getInstance().savePrefContent(EXTRA_TIMER, duration);
            CommonUtil.getInstance().savePrefContent(TIMER_ID, idEntity);
        }
        String timerFrg = intent.getStringExtra("EXTRA_TIMER_IN");
        CommonUtil.getInstance().savePrefContent("TIMER_EXTRA", timerFrg);
        showFrgScreen(TAG, M001HomePageFrg.TAG);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    protected void initData() {
        Log.d(TAG, "innitData...");
    }

    @Override
    protected HomePresenter getPresenter() {
        return new HomePresenter(this);
    }

    @Override
    public void backToPreviousScreen() {
        super.onBackPressed();
    }

    @Override
    protected void handleOnDestroy() {
        super.handleOnDestroy();
    }

    @SuppressLint("ResourceType")
    @Override
    public void showFrgScreen(String tagSource, String tagChild) {
        Log.d(TAG, "showChildFrgScreen...");
        Fragment frgSaved = getFragmentManager().findFragmentByTag(tagChild);
        if (frgSaved != null) {
            mFrags.put(tagChild, (BaseFragment) frgSaved);
        } else {
            BaseFragment frg = mFrags.get(tagChild);
            try {
                Class<?> clazz = Class.forName(tagChild);
                Constructor<?> constructor = clazz.getConstructor();
                frg = (BaseFragment) constructor.newInstance();
                frg.setOnCallBack(this);

                mFrags.put(tagChild, frg);
            } catch (Exception e) {
                e.printStackTrace();
            }
            assert frg != null;

            frg.setTagSource(tagSource);
            try {
                getFragmentManager().beginTransaction()
                        .setCustomAnimations(R.animator.alpha_in, R.animator.alpha_out)
                        .replace(R.id.content, frg).commit();
            } catch (Exception ignore) {
                ignore.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        Log.i(TAG, "Current fragment is : ");
        if (currentTag == null) {
            super.onBackPressed();
            return;
        }
        BaseFragment frg = mFrags.get(currentTag);
        if (frg == null) {
            super.onBackPressed();
            return;
        }
        frg.backToPreviousScreen();
    }

    @Override
    public void closeApp() {
        finish();
    }

    @Override
    public void hideBottomBar() {
        hideSystemUI();
    }

    private void hideSystemUI() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    @Override
    public void showWaringDialog(String txt, boolean isActive) {

    }

    @Override
    public void showM001Landing(int isActive) {
        Log.i(TAG, "showM001Landing : ");
        if (mAlertDialog != null && mAlertDialog.isShowing()) return;
        if (isActive == CommonUtil.GREY_LIST) {
            getStorage().setFlagM001Active(true);
            String flagAlreadyCheck = getStorage().getFlag();
            if (flagAlreadyCheck == null || !flagAlreadyCheck.equals(ALREADY_VALUE)) {
                getStorage().setmFlag(ALREADY_VALUE);
            }
            showFrgScreen(TAG, M001HomePageFrg.TAG);
        }
    }

    @Override
    public void filterLanguage(String key) {
    }

    @Override
    public void showM001LandingPageFrg() {
    }
}
