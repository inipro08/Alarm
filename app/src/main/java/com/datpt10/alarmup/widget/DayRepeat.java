package com.datpt10.alarmup.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.Nullable;

import com.afollestad.aesthetic.Aesthetic;
import com.datpt10.alarmup.view.event.Subscribblable;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import kotlin.jvm.internal.Intrinsics;

public final class DayRepeat extends View implements View.OnClickListener, Subscribblable {

    @Nullable
    public OnCheckedChangeListener onCheckedChangeListener;
    private Paint accentPaint;
    private Paint textPaint;
    private Paint textPaintInverse;
    private Disposable colorAccentSubscription;
    private Disposable textColorPrimarySubscription;
    private Disposable textColorPrimaryInverseSubscription;
    private float checked;
    private boolean isChecked;
    private int textColorPrimary;
    private int textColorPrimaryInverse;
    private String text;
    private HashMap findViewCache;

    public DayRepeat(Context context) {
        super(context);
        Intrinsics.checkParameterIsNotNull(context, "context");
        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#006bbd"));
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        accentPaint = paint;
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTextSize((float) DimenUtils.dpToPx(14.0F));
        paint.setTextAlign(Paint.Align.CENTER);
        textPaint = paint;
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTextSize((float) DimenUtils.dpToPx(14.0F));
        paint.setTextAlign(Paint.Align.CENTER);
        textPaintInverse = paint;
        setOnClickListener(this);
    }

    public DayRepeat(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        Intrinsics.checkParameterIsNotNull(context, "context");
        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#006bbd"));
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
       accentPaint = paint;
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTextSize((float) DimenUtils.dpToPx(14.0F));
        paint.setTextAlign(Paint.Align.CENTER);
       textPaint = paint;
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTextSize((float) DimenUtils.dpToPx(14.0F));
        paint.setTextAlign(Paint.Align.CENTER);
       textPaintInverse = paint;
        setOnClickListener(this);
    }

    public DayRepeat(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Intrinsics.checkParameterIsNotNull(context, "context");
        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#242E42"));
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
     accentPaint = paint;
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTextSize((float) DimenUtils.dpToPx(14.0F));
        paint.setTextAlign(Paint.Align.CENTER);
        textPaint = paint;
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTextSize((float) DimenUtils.dpToPx(14.0F));
        paint.setTextAlign(Paint.Align.CENTER);
       textPaintInverse = paint;
       setOnClickListener(this);
    }

    private static void accesssetChecked(DayRepeat dayRepeat, float var1) {
        dayRepeat.checked = var1;
    }

    public final void setText(@NotNull String text) {
        this.text = text;
       invalidate();
    }

    public final boolean isChecked() {
        return this.isChecked;
    }

    public final void setChecked(boolean isChecked) {
        if (isChecked != this.isChecked) {
            this.isChecked = isChecked;
            textPaint.setColor(isChecked ? textColorPrimaryInverse : textColorPrimary);
            ValueAnimator valueAnimator = isChecked ? ValueAnimator.ofFloat(0.0F, 1.0F) : ValueAnimator.ofFloat(1.0F, 0.0F);
            valueAnimator.setInterpolator(isChecked ? new DecelerateInterpolator() : new AnticipateOvershootInterpolator());
            valueAnimator.addUpdateListener(new DayRepeatisCheckedinlinedapplylambda1(this, isChecked));
            valueAnimator.start();
        }
    }

    public View findCachedViewById(int var1) {
        if (findViewCache == null) {
            findViewCache = new HashMap();
        }

        View var2 = (View) findViewCache.get(var1);
        if (var2 == null) {
            var2 = this.findViewById(var1);
            findViewCache.put(var1, var2);
        }
        return var2;
    }

    public void clearFindViewByIdCache() {
        if (findViewCache != null) {
            findViewCache.clear();
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        Intrinsics.checkParameterIsNotNull(canvas, "canvas");
        super.onDraw(canvas);
        if (text != null) {
            canvas.drawText(text, (float) (getWidth() / 2), (float) (getHeight() / 3) - (textPaint.descent() + textPaint.ascent()) / (float) 2, textPaint);
        }
        Path circlePath = new Path();
        circlePath.addCircle((float) (getWidth() / 2), (float) (getHeight() / 3), checked * (float) DimenUtils.dpToPx(14.0F), Path.Direction.CW);
        circlePath.close();
        canvas.drawPath(circlePath, accentPaint);
        if (text != null) {
            canvas.drawText(text, (float) (getWidth() / 2), (float) (getHeight() / 3) - (textPaint.descent() + textPaint.ascent()) / (float) 2, textPaint);
            canvas.clipPath(circlePath);
            canvas.drawText(text, (float) (getWidth() / 2), (float) (getHeight() / 3) - (textPaint.descent() + textPaint.ascent()) / (float) 2, textPaintInverse);
        }

    }

    @Override
    public void onClick(View view) {
        setChecked(!isChecked);
        if (onCheckedChangeListener != null) {
            onCheckedChangeListener.onCheckedChanged(this, isChecked);
        }
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        subscribe();
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        unsubscribe();
    }

    @Override
    public void subscribe() {
        colorAccentSubscription = Aesthetic.Companion.get().colorAccent().subscribe(new Consumer() {
            // $FF: synthetic method
            // $FF: bridge method
            public void accept(Object var1) {
                this.accept((Integer) var1);
            }

            public final void accept(Integer integer) {
                accentPaint.setColor(Color.parseColor("#009FDA"));
                invalidate();
            }
        });
        textColorPrimarySubscription = Aesthetic.Companion.get().textColorPrimary().subscribe(new Consumer() {
            // $FF: synthetic method
            // $FF: bridge method
            public void accept(Object var1) {
                this.accept((Integer) var1);
            }

            public final void accept(Integer integer) {
                textColorPrimary = integer;
                textPaint.setColor(Color.parseColor("#ffffff"));
                invalidate();
            }
        });
        textColorPrimaryInverseSubscription = Aesthetic.Companion.get().textColorPrimaryInverse().subscribe(new Consumer() {
            // $FF: synthetic method
            // $FF: bridge method
            public void accept(Object var1) {
                this.accept((Integer) var1);
            }

            public final void accept(Integer integer) {
                textColorPrimaryInverse = integer;
                textPaintInverse.setColor(Color.WHITE);
                invalidate();
            }
        });
    }

    @Override
    public void unsubscribe() {
        if (colorAccentSubscription != null) {
            colorAccentSubscription.dispose();
        }

        if (textColorPrimarySubscription != null) {
            textColorPrimarySubscription.dispose();
        }

        if (textColorPrimaryInverseSubscription != null) {
            textColorPrimaryInverseSubscription.dispose();
        }
    }

    public void onCheckedChangeListener(OnCheckedChangeListener listener) {
        this.onCheckedChangeListener = listener;
    }

    public interface OnCheckedChangeListener {
        void onCheckedChanged(DayRepeat var1, boolean var2);
    }


    private static class DayRepeatisCheckedinlinedapplylambda1 implements ValueAnimator.AnimatorUpdateListener {
        // $FF: synthetic field
        final DayRepeat repeat;
        // $FF: synthetic field
        final boolean isCheckedinlined;

        DayRepeatisCheckedinlinedapplylambda1(DayRepeat dayRepeat, boolean isChecked) {
            this.repeat = dayRepeat;
            this.isCheckedinlined = isChecked;
        }

        @Override
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            Object var10001 = valueAnimator.getAnimatedValue();
            if (!(var10001 instanceof Float)) {
                var10001 = null;
            }
            accesssetChecked(repeat, var10001 != null ? (Float) var10001 : 0.0F);
            repeat.invalidate();
        }
    }
}
