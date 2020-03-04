package com.datpt10.alarmup.base;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.datpt10.alarmup.ANApplication;
import com.datpt10.alarmup.R;
import com.datpt10.alarmup.presenter.BasePresenter;
import com.datpt10.alarmup.util.CommonUtil;
import com.datpt10.alarmup.util.StorageCommon;
import com.datpt10.alarmup.view.event.OnCallBackToView;
import com.datpt10.alarmup.widget.CustomTypefaceSpan;
import com.datpt10.alarmup.widget.ProgressLoading;


public abstract class BaseDialog<T extends BasePresenter, H extends OnCallBackToView> extends
        Dialog implements View.OnClickListener, Animation.AnimationListener {
    private static final CharSequence PLACE_HOLDER = "PLACEHOLDER";
    private static final String TAG = "DIALOG";
    protected T mPresenter;
    protected H mCallBack;
    protected Context mContext;
    protected boolean isAnimEnd = true;
    protected Animation mAnim;
    protected int mId;

    public BaseDialog(Context context) {
        this(context, false);
    }

    public BaseDialog(Context context, int style) {
        this(context, true, style);
    }

    public BaseDialog(Context context, boolean isCancel, int style) {
        super(context, style);
        mContext = context;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(getLayoutId());
        setCancelable(isCancel);
        setCanceledOnTouchOutside(isCancel);
        mPresenter = getPresenter();
        mAnim = AnimationUtils.loadAnimation(mContext, R.anim.alpha);
        mAnim.setAnimationListener(this);
        initViews();
    }

    public BaseDialog(Context context, boolean isCancel) {
        super(context);
        mContext = context;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(getLayoutId());
        setCancelable(isCancel);
        setCanceledOnTouchOutside(isCancel);
        mPresenter = getPresenter();
        mAnim = AnimationUtils.loadAnimation(mContext, R.anim.alpha);
        mAnim.setAnimationListener(this);
        initViews();
    }

    public void showLockDialog() {
        ProgressLoading.show(mContext);
    }

    protected final <G extends View> G findViewById(int id, View.OnClickListener event) {
        return findViewById(id, event, true, null);
    }

    protected final <G extends View> G findViewById(int id, Typeface typeFace) {
        return findViewById(id, null, true, typeFace);
    }

    protected final <G extends View> G findViewById(int id, View.OnClickListener event, Typeface typeFace) {
        return findViewById(id, event, true, typeFace);
    }

    protected final <G extends View> G findViewById(int id, View.OnClickListener event, boolean enable, Typeface typeFace) {
        G view = findViewById(id);
        view.setOnClickListener(event);
        view.setEnabled(enable);
        if (typeFace != null && view instanceof TextView) {
            ((TextView) view).setTypeface(typeFace);
        }

        if (view.getContentDescription() == null) {
            CommonUtil.wtfe(TAG, "No. ContentDescription");
        } else if (view instanceof TextView) {
            String key = view.getContentDescription().toString();
//            if (LangMgr.getInstance().getCurrentLang() == null) {
//                CommonUtil.wtfe(TAG, "Err: No current language");
//            } else {
//                if (key.contains(PLACE_HOLDER)) {
//                    ((TextView) view).setHint(LangMgr.getInstance().getLangList().get(key));
//                } else {
//                    ((TextView) view).setText(LangMgr.getInstance().getLangList().get(key));
//                }
//            }
        }
        return view;
    }

    public void setOnCallBack(H event) {
        mCallBack = event;
    }

    protected abstract T getPresenter();

    protected abstract void initViews();

    public abstract int getLayoutId();

    @Override
    public void onClick(View v) {
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

    public final StorageCommon getStorage() {
        return ANApplication.getInstance().getStorageCommon();
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
            textToSpan.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(color)), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            mTvTitle.setText(textToSpan);
        } catch (Exception ignored) {
            //do nothing for temporary
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

    private void highLightText(TextView mTvTitle, String highLight, int color, int size, boolean isBold, Typeface typeface) {
        int start = mTvTitle.getText().toString().indexOf(highLight);
        int end = start + highLight.length();
        if (start < 0) return;

        highLightText(mTvTitle, start, end, color, isBold, size, typeface);
    }

    public void showNotify(int text) {
        Toast.makeText(ANApplication.getInstance(), text, Toast.LENGTH_SHORT).show();
    }

    public void showNotify(String text) {
        Toast.makeText(ANApplication.getInstance(), text, Toast.LENGTH_SHORT).show();
    }
}