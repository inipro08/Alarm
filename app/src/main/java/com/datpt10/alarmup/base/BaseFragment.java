package com.datpt10.alarmup.base;


import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.datpt10.alarmup.Alarmup;
import com.datpt10.alarmup.R;
import com.datpt10.alarmup.model.AlarmEntity;
import com.datpt10.alarmup.model.TimerEntity;
import com.datpt10.alarmup.presenter.BasePresenter;
import com.datpt10.alarmup.util.CommonUtil;
import com.datpt10.alarmup.util.StorageCommon;
import com.datpt10.alarmup.view.event.OnCallBackToView;
import com.datpt10.alarmup.widget.CustomTypefaceSpan;
import com.datpt10.alarmup.widget.ProgressLoading;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

/**
 * create by datpt on 3/28/2019.
 */
public abstract class BaseFragment<T extends BasePresenter, G extends OnCallBackToView> extends Fragment implements Alarmup.AlarmListener, Animation.AnimationListener, View.OnClickListener, OnCallBackToView, Alarmup.ActivityListener {
    public static final String TAG = BaseFragment.class.getName();
    private static final int LAYOUT_NONE = -1;
    protected final HashMap<String, BaseFragment> mFragChild = new HashMap<>();
    protected T mPresenter;
    protected View mRootView;
    protected Context mContext;
    protected G mCallBack;
    protected String mTagSource;
    protected String mTagCurrentChildSource;
    protected String mParentTag;
    protected boolean isAnimEnd = true;
    protected Animation mAnim;
    protected int mId;
    protected View mClickedView;
    private Alarmup alarmup;

    protected abstract void initViews();

    protected abstract int getLayoutId();

    protected abstract <T> T getPresenter();

    protected abstract String getTAG();

    protected final <G extends View> G findViewById(int id) {
        return mRootView.findViewById(id);
    }

    protected final <G extends View> G findViewById(int id, View.OnClickListener event) {
        return findViewById(id, event, true, null);
    }

    @Override
    public void showLockDialog() {
        ProgressLoading.show(mContext);
    }

    @Override
    public void onStart() {
        super.onStart();
        initData();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        alarmup = (Alarmup) mContext.getApplicationContext();
        alarmup.addListener(this);
    }

    protected void initData() {
    }

    @Nullable
    protected Alarmup getAlarmup() {
        return alarmup;
    }

    protected List<AlarmEntity> getAlarmList() {
        return alarmup.getAlarms();
    }

    protected List<TimerEntity> getTimerList() {
        return alarmup.getTimers();
    }

    public String getTagCurrentChildSource() {
        return mTagCurrentChildSource;
    }

    public void setTagCurrentChildSource(String mTagCurrentChildSource) {
        this.mTagCurrentChildSource = mTagCurrentChildSource;
    }

    protected final <G extends View> G findViewById(int id, Typeface typeFace) {
        return findViewById(id, null, true, typeFace);
    }

    protected final <G extends View> G findViewById(int id, View.OnClickListener event, Typeface typeFace) {
        return findViewById(id, event, true, typeFace);
    }

    protected final <G extends View> G findViewById(int id, View.OnClickListener event, boolean enable, Typeface typeFace) {
        G view = mRootView.findViewById(id);
        view.setOnClickListener(event);
        view.setEnabled(enable);
        if (typeFace != null && view instanceof TextView) {
            ((TextView) view).setTypeface(typeFace);
        }
        return view;
    }

    @Override
    public final void onClick(View v) {
        if (!isAnimEnd) return;
        isAnimEnd = false;
        mId = v.getId();
        mClickedView = v;
        v.startAnimation(mAnim);
    }

    public final void setOnCallBack(G event) {
        mCallBack = event;
    }

    private void setTagCurrentChildFrg(String tag) {
        mTagCurrentChildSource = tag;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView..." + getTAG());
        setTagCurrentFrg();
        setRetainInstance(true);
        if (mCallBack instanceof BaseFragment) {
            ((BaseFragment) mCallBack).setTagCurrentChildFrg(getTAG());
        }
        int layoutId = getLayoutId();
        mContext = getActivity();
        mPresenter = getPresenter();
        mRootView = inflater.inflate(layoutId, container, false);
        mAnim = AnimationUtils.loadAnimation(mContext, R.anim.alpha);
        mAnim.setAnimationListener(this);
        initViews();

        //add event to handle notification
        return mRootView;
    }

    protected void setTagCurrentFrg() {
        ((BaseActivity) getActivity()).currentTag = getTAG();
    }

    @Override
    public final void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    public final String getTagSource() {
        return mTagSource;
    }

    public final void setTagSource(String mTagSource) {
        this.mTagSource = mTagSource;
        defineBackKey();
    }

    public void showNotify(int text) {
        Toast.makeText(Alarmup.getInstance(), text, Toast.LENGTH_SHORT).show();
    }

    public void showNotify(String text) {
        Toast.makeText(Alarmup.getInstance(), text, Toast.LENGTH_SHORT).show();
    }

    protected abstract void defineBackKey();

    public final String getParentTag() {
        return mParentTag;
    }

    public final void setParentTag(String mTagParent) {
        this.mParentTag = mTagParent;
    }

    @Override
    public final void onAnimationStart(Animation animation) {

    }

    @Override
    public final void onAnimationEnd(Animation animation) {
        onClickView(mId);
        isAnimEnd = true;
    }

    protected void onClickView(int idView) {

    }

    @Override
    public final void onAnimationRepeat(Animation animation) {

    }

    public final StorageCommon getStorage() {
        return Alarmup.getInstance().getStorageCommonAlarmUp();
    }

    @SuppressLint("ResourceType")
    protected void showChildFrgScreen(String tagSource, String tagChild) {
        if (getContentLayout() == LAYOUT_NONE) return;

        Log.d(TAG, "showChildFrgScreen...");
        Fragment frgSaved = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
            frgSaved = getChildFragmentManager().findFragmentByTag(tagChild);
        }
        if (frgSaved != null) {
            mFragChild.put(tagChild, (BaseFragment) frgSaved);
        } else {
            BaseFragment frg = mFragChild.get(tagChild);
            try {
                Class<?> clazz = Class.forName(tagChild);
                Constructor<?> constructor = clazz.getConstructor();
                frg = (BaseFragment) constructor.newInstance();
                frg.setOnCallBack(this);

                mFragChild.put(tagChild, frg);
            } catch (Exception e) {
                CommonUtil.wtfe(TAG, e.getLocalizedMessage());
            }
            assert frg != null;

            frg.setTagSource(tagSource);
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    getChildFragmentManager().beginTransaction()
                            .setCustomAnimations(R.animator.alpha_in, R.animator.alpha_out)
                            .replace(getContentLayout(), frg).commit();
                }
            } catch (Exception e) {
                CommonUtil.wtfe(TAG, e.getLocalizedMessage());
            }
        }
    }

    protected int getContentLayout() {
        return LAYOUT_NONE;
    }

    protected void closeSpinner(Spinner spinner) {
        try {
            Method method = Spinner.class.getDeclaredMethod("onDetachedFromWindow");
            method.setAccessible(true);
            method.invoke(spinner);
        } catch (Exception e) {
            CommonUtil.wtfe(TAG, e.getLocalizedMessage());
        }
    }

    @Override
    public void onDestroy() {
        alarmup.removeListener(this);
        alarmup = null;
        super.onDestroy();
    }

    protected void highLightText(TextView mTvTitle, int start, int end, int color) {
        highLightText(mTvTitle, start, end, color, false, 0, null);
    }

    protected void highLightText(TextView mTvTitle, int start, int end, int color, boolean isBold) {
        highLightText(mTvTitle, start, end, color, isBold, 0, null);
    }

    protected void highLightText(TextView mTvTitle, int start, int end, int color, int size) {
        highLightText(mTvTitle, start, end, color, false, size, null);
    }

    protected void highLightText(TextView mTvTitle, int start, int end, int color, boolean isBold, int size) {
        highLightText(mTvTitle, start, end, color, isBold, size, null);
    }

    protected void highLightText(TextView mTvTitle, int start, int end, int color, int size, Typeface typeface) {
        highLightText(mTvTitle, start, end, color, false, size, typeface);
    }

    protected void highLightText(TextView mTvTitle, int start, int end, int color, Typeface typeface) {
        highLightText(mTvTitle, start, end, color, false, 0, typeface);
    }

    protected void highLightText(TextView mTvTitle, int start, int end, int color, boolean isBold, int size, Typeface typeface) {
        try {
            Spannable textToSpan = new SpannableString(mTvTitle.getText().toString());
            if (isBold) {
                textToSpan.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else if (typeface != null) {
                textToSpan.setSpan(new CustomTypefaceSpan(typeface), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            if (size > 0) {
                textToSpan.setSpan(new RelativeSizeSpan((mTvTitle.getTextSize() + size) / mTvTitle.getTextSize()), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            textToSpan.setSpan(new ForegroundColorSpan(getResources().getColor(color)), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            mTvTitle.setText(textToSpan);
        } catch (Exception ignored) {
            //do nothing for temporary
        }
    }

    protected void highLightText(TextView mTvTitle, int start1, int end1, int start2, int end2,
                                 int color1, int color2, boolean isBold, int size, Typeface typeface) {
        try {
            Spannable textToSpan = new SpannableString(mTvTitle.getText().toString());
            if (isBold) {
                textToSpan.setSpan(new StyleSpan(Typeface.BOLD), start1, end1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                textToSpan.setSpan(new StyleSpan(Typeface.BOLD), start2, end2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else if (typeface != null) {
                textToSpan.setSpan(new CustomTypefaceSpan(typeface), start1, end1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                textToSpan.setSpan(new CustomTypefaceSpan(typeface), start2, end2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            if (size > 0) {
                textToSpan.setSpan(new RelativeSizeSpan((mTvTitle.getTextSize() + size) / mTvTitle.getTextSize()), start1, end1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                textToSpan.setSpan(new RelativeSizeSpan((mTvTitle.getTextSize() + size) / mTvTitle.getTextSize()), start2, end2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            textToSpan.setSpan(new ForegroundColorSpan(getResources().getColor(color1)), start1, end1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            textToSpan.setSpan(new ForegroundColorSpan(getResources().getColor(color2)), start2, end2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            mTvTitle.setText(textToSpan);
        } catch (Exception e) {
            CommonUtil.wtfe(TAG, e.getLocalizedMessage());
        }
    }


    protected void highLightText(TextView mTvTitle, String highLight, int color, int size, Typeface typeface) {
        highLightText(mTvTitle, highLight, color, size, false, typeface);
    }

    protected void highLightText(TextView mTvTitle, String highLight, int color, Typeface typeface) {
        highLightText(mTvTitle, highLight, color, 0, false, typeface);
    }

    protected void highLightText(TextView mTvTitle, String highLight, int color, boolean isBold) {
        highLightText(mTvTitle, highLight, color, 0, isBold, null);
    }

    protected void highLightText(TextView mTvTitle, String highLight, int color, int size) {
        highLightText(mTvTitle, highLight, color, size, false, null);
    }

    protected void highLightText(TextView mTvTitle, String highLight, int color, int size, boolean isBold) {
        highLightText(mTvTitle, highLight, color, size, isBold, null);
    }

    protected void highLightText(TextView mTvTitle, String highLight, int color) {
        highLightText(mTvTitle, highLight, color, 0, false, null);
    }

    protected void highLightText(TextView mTvTitle, String highLight1, String highLight2, int color1, int color2, boolean isBold) {
        highLightText(mTvTitle, highLight1, highLight2, color1, color2, 0, isBold, null);
    }

    private void highLightText(TextView mTvTitle, String highLight, int color, int size, boolean isBold, Typeface typeface) {
        if (mTvTitle == null || highLight == null) {
            CommonUtil.wtfe(TAG, "highLightText...Err: null TextView");
            return;
        }

        int start = mTvTitle.getText().toString().indexOf(highLight);
        int end = start + highLight.length();
        if (start < 0) return;

        highLightText(mTvTitle, start, end, color, isBold, size, typeface);
    }

    private void highLightText(TextView mTvTitle, String highLight1, String highLight2, int color1, int color2, int size, boolean isBold, Typeface typeface) {
        int start1 = mTvTitle.getText().toString().indexOf(highLight1);
        int start2 = mTvTitle.getText().toString().indexOf(highLight2);
        int end1 = start1 + highLight1.length();
        int end2 = start2 + highLight2.length();
        if (start1 < 0) return;

        highLightText(mTvTitle, start1, end1, start2, end2, color1, color2, isBold, size, typeface);
    }

    @Override
    public void onAlarmsChanged() {

    }

    @Override
    public void onTimersChanged() {

    }

    @Override
    public void requestPermissions(String... permissions) {
    }

    @Override
    public FragmentManager gettFragmentManager() {
        return gettFragmentManager();
    }
}