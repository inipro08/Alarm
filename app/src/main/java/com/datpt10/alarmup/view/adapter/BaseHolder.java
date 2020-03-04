package com.datpt10.alarmup.view.adapter;

import android.graphics.Typeface;
import android.view.View;
import android.view.animation.Animation;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.datpt10.alarmup.util.CommonUtil;


public abstract class BaseHolder extends RecyclerView.ViewHolder implements View.OnClickListener, Animation.AnimationListener {

    private static final String TAG = "HOLDER";
    private final View mRootView;
    protected boolean isAnimEnd = true;
    protected Animation mAnim;
    private int mId;

    public BaseHolder(View itemView) {
        super(itemView);
        mRootView = itemView;
//        mAnim = AnimationUtils.loadAnimation(ANApplication.getInstance(), R.anim.alpha);
//        mAnim.setAnimationListener(this);
        initView();
    }

    protected abstract void initView();

    @Override
    public final void onClick(View v) {
        if (!isAnimEnd) return;
        isAnimEnd = false;
        mId = v.getId();
//        v.startAnimation(mAnim);
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

    protected final <G extends View> G findViewById(int id) {
        return findViewById(id, null, true, null);
    }

    protected final <G extends View> G findViewById(int id, Typeface typeFace) {
        return findViewById(id, null, true, typeFace);
    }

    protected final <G extends View> G findViewById(int id, View.OnClickListener event) {
        return findViewById(id, event, null);
    }

    protected final <G extends View> G findViewById(int id, View.OnClickListener event, Typeface typeFace) {
        return findViewById(id, event, true, typeFace);
    }

    protected final <G extends View> G findViewById(int id, View.OnClickListener event, boolean enable, Typeface typeFace) {
        G view = mRootView.findViewById(id);
        if (view == null) return null;

        view.setOnClickListener(event);
        view.setEnabled(enable);
        if (typeFace != null && view instanceof TextView) {
            ((TextView) view).setTypeface(typeFace);
        }
        if (view.getContentDescription() == null) {
            CommonUtil.wtfe(TAG, "No. ContentDescription");
        }
        return view;
    }

}
