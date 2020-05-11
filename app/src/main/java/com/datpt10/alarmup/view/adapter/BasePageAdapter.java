package com.datpt10.alarmup.view.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.datpt10.alarmup.Alarmup;
import com.datpt10.alarmup.util.CommonUtil;
import com.datpt10.alarmup.util.StorageCommon;
import com.datpt10.alarmup.view.event.OnCallBackToView;

import java.util.ArrayList;
import java.util.List;

public abstract class BasePageAdapter<T extends OnCallBackToView, G> extends PagerAdapter
        implements View.OnClickListener {
    private static final CharSequence PLACE_HOLDER = "PLACEHOLDER";
    private static final String TAG = "BASE ADAPTER";
    protected final T mCallBack;
    protected final ArrayList<G> listData = new ArrayList<>();
    protected final Context mContext;

    public BasePageAdapter(List<G> listData, Context mContext, T callBack) {
        this.listData.addAll(listData);
        this.mContext = mContext;
        this.mCallBack = callBack;
    }

    public void showNotify(int text) {
        Toast.makeText(Alarmup.getInstance(), text, Toast.LENGTH_SHORT).show();
    }

    public void showNotify(String text) {
        Toast.makeText(Alarmup.getInstance(), text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public final int getCount() {
        return listData.size();
    }

    protected final <K extends View> K findViewById(View view, int id, View.OnClickListener event, Typeface typeface) {
        K childView = view.findViewById(id);
        childView.setEnabled(true);
        if (childView instanceof TextView && typeface != null) {
            ((TextView) childView).setTypeface(typeface);
        }
        if (childView.getContentDescription() == null) {
            CommonUtil.wtfe(TAG, "No. ContentDescription");
        }
        if (event != null) {
            childView.setOnClickListener(event);
        }
        return childView;
    }

    protected final <K extends View> K findViewById(View view, int id, View.OnClickListener event) {
        return findViewById(view, id, event, null);
    }

    protected final <K extends View> K findViewById(View view, int id, Typeface typeface) {
        return findViewById(view, id, null, typeface);
    }

    protected final <K extends View> K findViewById(View view, int id) {
        return findViewById(view, id, null, null);
    }

    @Override
    public final boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @NonNull
    @Override
    public final View instantiateItem(ViewGroup container, int position) {
        View view = View.inflate(mContext, getLayoutId(position), null);
        G data = listData.get(position);
        initViews(view, data, position);
        view.setTag(data);
        view.setOnClickListener(this);
        container.addView(view);
        return view;
    }

    protected abstract void initViews(View rootView, G data, int pos);

    protected abstract int getLayoutId(int pos);

    @Override
    public final void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    public final StorageCommon getStorage() {
        return Alarmup.getInstance().getStorageCommonAlarmUp();
    }
}
