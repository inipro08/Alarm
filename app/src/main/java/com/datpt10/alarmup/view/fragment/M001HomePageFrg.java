package com.datpt10.alarmup.view.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.FrameLayout;

import androidx.core.content.ContextCompat;

import com.datpt10.alarmup.R;
import com.datpt10.alarmup.base.BaseFragment;
import com.datpt10.alarmup.presenter.M001HomePagePresenter;
import com.datpt10.alarmup.view.event.OnHomeBackToView;
import com.datpt10.alarmup.view.event.OnM001HomePageCallBack;

import jahirfiquitiva.libs.fabsmenu.FABsMenu;
import jahirfiquitiva.libs.fabsmenu.FABsMenuListener;
import jahirfiquitiva.libs.fabsmenu.TitleFAB;


/**
 * create by datpt on 10/22/2019.
 */
public class M001HomePageFrg extends BaseFragment<M001HomePagePresenter, OnHomeBackToView> implements OnM001HomePageCallBack {
    public static final String TAG = M001HomePageFrg.class.getName();
    private FABsMenu menu;
    private TitleFAB timerFab;
    private TitleFAB timeZoneFab;
    private TitleFAB alarmFab;
    private FrameLayout mLnBottomBar;

    @Override
    protected void initViews() {
        mLnBottomBar = findViewById(R.id.ln_m001_bottom_bar);

        menu = findViewById(R.id.fabsMenu, this);
        timerFab = findViewById(R.id.tv_m001_bottom_bar_timer, this);
        timeZoneFab = findViewById(R.id.tv_m001_bottom_bar_time_zone, this);
        alarmFab = findViewById(R.id.tv_m001_bottom_bar_alarm, this);

        menu.setMenuListener(new FABsMenuListener() {
            @Override
            public void onMenuExpanded(FABsMenu fabsMenu) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.FOREGROUND_SERVICE) != PackageManager.PERMISSION_GRANTED)
                        requestPermissions(new String[]{Manifest.permission.FOREGROUND_SERVICE}, 0);
                    else fabsMenu.collapseImmediately();
                }
            }
        });
        showFragmentDefault();
    }

    private void showFragmentDefault() {
        showChildFrgScreen(TAG, M002AlarmFrg.TAG);
    }

    @Override
    protected int getContentLayout() {
        return R.id.ln_m001_content_layout;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.frg_m001_home_page;
    }

    @Override
    protected M001HomePagePresenter getPresenter() {
        return new M001HomePagePresenter(this);
    }

    @Override
    protected String getTAG() {
        return TAG;
    }

    @Override
    protected void defineBackKey() {

    }

    @Override
    public void showChildFrgScreen(String tagSource, String tagChild) {
        super.showChildFrgScreen(tagSource, tagChild);
    }

    @Override
    protected void onClickView(int idView) {
        switch (idView) {
            case R.id.tv_m001_bottom_bar_alarm:
                showChildFrgScreen(TAG, M002AlarmFrg.TAG);
                menu.collapseImmediately();
                break;
            case R.id.tv_m001_bottom_bar_time_zone:
                showChildFrgScreen(TAG, M003TimeZoneFrg.TAG);
                menu.collapseImmediately();
                break;
            case R.id.tv_m001_bottom_bar_timer:
                showChildFrgScreen(TAG, M004TimerFrg.TAG);
                menu.collapseImmediately();
                break;
            default:
                break;
        }
    }

    @Override
    public void onAlarmsChanged() {

    }

    @Override
    public void onTimersChanged() {

    }
}
