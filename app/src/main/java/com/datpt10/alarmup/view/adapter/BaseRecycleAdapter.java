package com.datpt10.alarmup.view.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.datpt10.alarmup.Alarmup;
import com.datpt10.alarmup.R;
import com.datpt10.alarmup.util.CommonUtil;
import com.datpt10.alarmup.util.StorageCommon;
import com.datpt10.alarmup.view.event.OnCallBackToView;
import com.datpt10.alarmup.widget.CustomTypefaceSpan;

import java.util.ArrayList;
import java.util.List;


/**
 * create by datpt on 6/5/2019.
 */
public abstract class BaseRecycleAdapter<T extends OnCallBackToView, E extends Object, G extends
        RecyclerView.ViewHolder> extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener, Animation.AnimationListener {

    private static final CharSequence PLACE_HOLDER = "PLACEHOLDER";
    private static final String TAG = "ADAPTER";
    protected T mCallBack;
    protected List<E> mListData;
    protected Context mContext;
    protected boolean isAnimEnd = true;
    protected Animation mAnim;
    private int mId;

    public BaseRecycleAdapter(Context mContext, List<E> mListData, T mCallBack) {
        this.mCallBack = mCallBack;
        if (mListData != null) {
            this.mListData = new ArrayList<>(mListData);
        }
        mAnim = AnimationUtils.loadAnimation(mContext, R.anim.alpha);
        mAnim.setAnimationListener(this);
        this.mContext = mContext;
    }

    public final StorageCommon getStorage() {
        return Alarmup.getInstance().getStorageCommonAlarmUp();
    }

    @Override
    public final G onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(getLayoutId(viewType), parent, false);
        return getViewHolder(viewType, itemView);
    }

    protected abstract int getLayoutId(int viewType);

    protected abstract G getViewHolder(int viewType, View itemView);

    @Override
    public final int getItemCount() {
        return mListData.size();
    }

    protected final <G extends View> G findViewById(View rootView, int id) {
        return findViewById(rootView, id, null, true, null);
    }

    protected final <G extends View> G findViewById(View rootView, int id, Typeface typeFace) {
        return findViewById(rootView, id, null, true, typeFace);
    }

    protected final <G extends View> G findViewById(View rootView, int id, View.OnClickListener event) {
        return findViewById(rootView, id, event, null);
    }

    protected final <G extends View> G findViewById(View rootView, int id, View.OnClickListener event, Typeface typeFace) {
        return findViewById(rootView, id, event, true, typeFace);
    }

    protected final <G extends View> G findViewById(View rootView, int id, View.OnClickListener event, boolean enable, Typeface typeFace) {
        G view = rootView.findViewById(id);
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

    protected void highLightText(TextView mTvTitle, String highLight, int color, boolean isBold) {
        highLightText(mTvTitle, highLight, color, 0, isBold, null);
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
            textToSpan.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(color)), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            mTvTitle.setText(textToSpan);
        } catch (Exception ignored) {
            //do nothing for temporary
        }
    }

    @Override
    public final void onClick(View v) {
        if (!isAnimEnd) return;
        isAnimEnd = false;
        mId = v.getId();
        v.startAnimation(mAnim);
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
}
