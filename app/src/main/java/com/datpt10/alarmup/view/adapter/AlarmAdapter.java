package com.datpt10.alarmup.view.adapter;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.AutoTransition;
import androidx.transition.TransitionManager;

import com.afollestad.aesthetic.Aesthetic;
import com.datpt10.alarmnow.widget.AestheticCheckBoxView;
import com.datpt10.alarmup.Alarmio;
import com.datpt10.alarmup.R;
import com.datpt10.alarmup.model.AlarmEntity;
import com.datpt10.alarmup.util.CommonUtil;
import com.datpt10.alarmup.util.FormatUtils;
import com.datpt10.alarmup.view.event.OnM002AlarmCallBack;
import com.datpt10.alarmup.widget.DayRepeat;
import com.datpt10.alarmup.widget.DimenUtils;
import com.datpt10.alarmup.widget.TextAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.functions.Consumer;
import kotlin.TypeCastException;

/**
 * create by datpt on 12/3/2019.
 */
public class AlarmAdapter extends BaseRecycleAdapter<OnM002AlarmCallBack, AlarmEntity, AlarmAdapter.AlarmHolder> {
    private String[] mColors = {"#42CDCA", "#4fa6d3", "#4879af", "#63539e", "#5e4270"};
    private AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
    private List<AlarmEntity> alarmData;
    private int expandedPosition = -1;
    private Alarmio mAlarmio;
    private int colorAccent = Color.WHITE;
    private int colorForeground = Color.TRANSPARENT;
    private int textColorPrimary = Color.RED;
    private RecyclerView recycler;

    public AlarmAdapter(Context mContext, List<AlarmEntity> mListData, OnM002AlarmCallBack mCallBack, RecyclerView recyclerAlarm, Alarmio alarmio) {
        super(mContext, mListData, mCallBack);
        this.alarmData = mListData;
        this.recycler = recyclerAlarm;
        this.mAlarmio = alarmio;
    }

    public List<AlarmEntity> getAlarmData() {
        return alarmData;
    }

    public final int getColorAccent() {
        return this.colorAccent;
    }

    public final void setColorAccent(int colorAccent) {
        this.colorAccent = colorAccent;
        if (expandedPosition > 0) {
            recycler.post(() -> notifyItemChanged(expandedPosition));
        }
    }

    public final int getColorForeground() {
        return this.colorForeground;
    }

    public final void setColorForeground(int colorForeground) {
        this.colorForeground = colorForeground;
        if (expandedPosition > 0) {
            recycler.post(() -> notifyItemChanged(expandedPosition));
        }
    }

    public final int getTextColorPrimary() {
        return this.textColorPrimary;
    }

    public final void setTextColorPrimary(int textColorPrimary) {
        this.textColorPrimary = textColorPrimary;
        if (expandedPosition > 0) {
            recycler.post(() -> notifyItemChanged(expandedPosition));
        }
    }

    @Override
    protected int getLayoutId(int viewType) {
        return R.layout.item_m002_alarm;
    }

    @Override
    protected AlarmHolder getViewHolder(int viewType, View itemView) {
        return new AlarmHolder(itemView);
    }

    private AlarmEntity getAlarm(int position) {
        int alarmSize = alarmData.size();
        AlarmEntity data;
        if (0 <= position) {
            if (alarmSize > position) {
                data = alarmData.get(position);
                return data;
            }
        }
        return null;
    }

    @SuppressLint("ResourceType")
    private void onBindAlarmViewHolderRepeat(final AlarmHolder holder, final AlarmEntity alarm) {
        holder.cbRepeat.setOnCheckedChangeListener(null);
        holder.cbRepeat.setChecked(alarm.isRepeat());
        holder.cbRepeat.setOnCheckedChangeListener(($noName_0, b) -> {
            int i = 0;
            for (int j = alarm.daysSelect.length; i < j; ++i) {
                alarm.daysSelect[i] = b;
            }
            alarm.setDays(mContext, alarm.daysSelect);
            AutoTransition transition = new AutoTransition();
            transition.setDuration(150L);
            TransitionManager.beginDelayedTransition(recycler, transition);
            recycler.post(this::notifyDataSetChanged);
        });
        holder.lnDay.setVisibility(alarm.isRepeat() ? View.VISIBLE : View.GONE);
        DayRepeat.OnCheckedChangeListener listener = (dayRepeat, b) -> {
            alarm.daysSelect[holder.lnDay.indexOfChild(dayRepeat)] = b;
            alarm.setDays(mContext, alarm.daysSelect);
            if (!alarm.isRepeat()) {
                notifyItemChanged(holder.getAdapterPosition());
            } else {
                onBindViewHolder(holder, holder.getAdapterPosition());
            }
        };
        for (int i = 0; i <= 6; ++i) {
            if (holder.lnDay.getChildAt(i) == null) {
                throw new TypeCastException("cannot be cast");
            }
            DayRepeat dayRepeat = (DayRepeat) holder.lnDay.getChildAt(i);
            dayRepeat.setChecked(alarm.daysSelect[i]);
            dayRepeat.onCheckedChangeListener(listener);
            switch (i) {
                case 0:
                case 6:
                    dayRepeat.setText("S");
                    break;
                case 1:
                    dayRepeat.setText("M");
                    break;
                case 2:
                case 4:
                    dayRepeat.setText("T");
                    break;
                case 3:
                    dayRepeat.setText("W");
                    break;
                case 5:
                    dayRepeat.setText("F");
                    break;
            }
        }
    }

    @SuppressLint({"CheckResult"})
    private void onBindAlarmViewHolderExpansion(final AlarmHolder holder, int position) {
        final boolean isExpanded = position == expandedPosition;
        int visibility = isExpanded ? View.VISIBLE : View.GONE;
        if (visibility != holder.lnExpand.getVisibility()) {
            holder.lnExpand.setVisibility(visibility);
            Aesthetic.Companion.get().colorPrimary().take(1L).subscribe(new Consumer() {
                // $FF: synthetic method
                // $FF: bridge method
                public void accept(Object var1) {
                    this.accept((Integer) var1);
                }

                public final void accept(Integer integer) {
                    ValueAnimator valueAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), isExpanded ? integer : getColorForeground(), isExpanded ? getColorForeground() : integer);
                    valueAnimator.addUpdateListener((valueAnimator1 -> {
                        Object var10000 = valueAnimator1.getAnimatedValue();
                        if (!(valueAnimator1.getAnimatedValue() instanceof Integer)) {
                            var10000 = null;
                        }
                        if (var10000 != null) {
//                            holder.itemView.setBackgroundColor(Color.parseColor(mColors[position % 4]));
                        }
                    }));
                    valueAnimator.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animator) {
                        }

                        @Override
                        public void onAnimationEnd(Animator animator) {
//                            holder.itemView.setBackgroundColor(isExpanded ? Color.parseColor(mColors[position % 4]) : Color.TRANSPARENT);
                        }

                        @Override
                        public void onAnimationCancel(Animator animator) {
                        }

                        @Override
                        public void onAnimationRepeat(Animator animator) {
                        }
                    });
                    valueAnimator.start();
                }
            });
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(isExpanded ? 0.0F : (float) DimenUtils.dpToPx(2.0F), isExpanded ? (float) DimenUtils.dpToPx(2.0F) : 0.0F);
            valueAnimator.addUpdateListener(valueAnimator12 -> {
                Object var10000 = valueAnimator12.getAnimatedValue();
                if (!(var10000 instanceof Float)) {
                    var10000 = null;
                }
                Float var5 = (Float) var10000;
                if (var5 != null) {
                    float elevation = ((Number) var5).floatValue();
                    ViewCompat.setElevation(holder.itemView, elevation);
                }
            });
            valueAnimator.start();
        } else {
            holder.itemView.setBackgroundColor(Color.parseColor(mColors[position % 5]));
        }
        holder.itemView.setOnClickListener(it -> {
            expandedPosition = isExpanded ? -1 : holder.getAdapterPosition();
            AutoTransition transition = new AutoTransition();
            transition.setDuration(200);
            TransitionManager.beginDelayedTransition(recycler, transition);
            recycler.post(this::notifyDataSetChanged);
        });
    }

    @Override
    public void onBindViewHolder(@NotNull RecyclerView.ViewHolder holder, int position) {
        AlarmHolder alarmHolder = (AlarmHolder) holder;
        boolean isExpanded = position == expandedPosition;
        AlarmEntity alarmEntity1 = getAlarm(position);
        alarmHolder.itemView.setBackgroundColor(Color.parseColor(mColors[position % 5]));
        alarmHolder.edContent.setFocusableInTouchMode(isExpanded);
        alarmHolder.edContent.setCursorVisible(false);
        alarmHolder.edContent.clearFocus();
        alarmHolder.edContent.setText(alarmEntity1.getContent(mContext));
        if (isExpanded) {
            alarmHolder.edContent.setOnClickListener(null);
        } else {
            alarmHolder.edContent.setOnClickListener(it -> holder.itemView.callOnClick());
        }
        alarmHolder.edContent.setOnFocusChangeListener(($noName_0, hasFocus) -> alarmHolder.edContent.setCursorVisible(hasFocus && holder.getAdapterPosition() == expandedPosition));

        alarmHolder.mSwitch.setOnCheckedChangeListener(null);
        alarmHolder.mSwitch.setChecked(alarmEntity1.isEnabledToggle);

        alarmHolder.mSwitch.setOnCheckedChangeListener(($noName_0, b) -> {
            alarmEntity1.setEnabled(mContext, alarmManager, b);
            AutoTransition transition = new AutoTransition();
            transition.setDuration(200);
            TransitionManager.beginDelayedTransition(recycler, transition);
            recycler.post(this::notifyDataSetChanged);
        });
        alarmHolder.tvTime.setText(FormatUtils.formatShort(mContext, alarmEntity1.timerAlarm.getTime()));
        alarmHolder.tvTime.setOnClickListener(view -> CommonUtil.getInstance().showTimePicker(mContext, alarmHolder.tvTime, alarmEntity1, alarmManager));

        alarmHolder.tvNextTime.setVisibility(alarmEntity1.isEnabledToggle ? View.VISIBLE : View.GONE);
        alarmHolder.ivNextTime.setVisibility(alarmEntity1.isEnabledToggle ? View.VISIBLE : View.GONE);

        Calendar nextAlarm = alarmEntity1.getNext();
        if (alarmEntity1.isEnabledToggle && nextAlarm != null) {
            int minutes = (int) TimeUnit.MILLISECONDS.toMinutes(nextAlarm.getTimeInMillis() - Calendar.getInstance().getTimeInMillis());
            alarmHolder.tvNextTime.setText(String.format("%s from now.", FormatUtils.formatUnit(mContext, minutes)));
        }
        alarmHolder.tvRingFile.setText(alarmEntity1.hasSound() ? alarmEntity1.getSoundAlarm().getName() : "None");
        alarmHolder.tvRingFile.setOnClickListener(view -> {
            CommonUtil.getInstance().showListRingTone(mContext, alarmHolder.tvRingFile, alarmEntity1, mAlarmio);
        });
        if (isExpanded) {
            onBindAlarmViewHolderRepeat(alarmHolder, alarmEntity1);
        } else {
            alarmHolder.cbRepeat.setAlpha(alarmEntity1.isRepeat() ? 1.0F : 1.0F);
            alarmHolder.tvSound.setAlpha(alarmEntity1.hasSound() ? 1.0F : 1.0F);
        }
        alarmHolder.ivExpand.animate().rotationX((float) (isExpanded ? 180 : 0)).start();
        alarmHolder.tvDelete.setOnClickListener(view -> {
            mListData.remove(alarmEntity1);
            mCallBack.removeAlarm(alarmEntity1);
        });
        onBindAlarmViewHolderExpansion(alarmHolder, position);
    }

    public class AlarmHolder extends BaseHolder {
        private TextView tvTime;
        private ImageView ivExpand;
        private EditText edContent;
        private Switch mSwitch;
        private LinearLayout lnExpand, lnDay;
        private TextView tvRingFile;
        private TextView tvSound;
        private TextView tvDelete;
        private AestheticCheckBoxView cbRepeat;
        private TextView tvNextTime;
        private ImageView ivNextTime;

        AlarmHolder(View itemView) {
            super(itemView);
            edContent.addTextChangedListener(new TextAdapter() {
                @Override
                public void afterTextChanged(Editable editable) {
                    edContent.setBackgroundColor(mContext.getResources().getColor(R.color.colorBgEditText));
                    alarmData.get(getAdapterPosition()).setContent(mContext, editable.toString());
                }
            });
        }

        @Override
        protected void onClickView(int idView) {
            switch (idView) {
                case R.id.tv_m002_view_set_alarm:
                case R.id.iv_m002_item_expand:
                case R.id.tv_m002_item_file:
                case R.id.tv_m002_item_delete:
                    break;
            }
        }

        @SuppressLint("ResourceType")
        @Override
        protected void initView() {
            tvTime = findViewById(R.id.tv_m002_view_set_alarm, this);
            tvNextTime = findViewById(R.id.nextTime);
            mSwitch = findViewById(R.id.s_m002_item_alarm, this);

            ivNextTime = findViewById(R.id.iv_m002_item_next_time);
            findViewById(R.id.iv_m002_item_sound);
            findViewById(R.id.iv_m002_item_label);
            findViewById(R.id.iv_m002_item_delete);

            tvSound = findViewById(R.id.tv_m002_item_sound);
            tvRingFile = findViewById(R.id.tv_m002_item_file);

            tvDelete = findViewById(R.id.tv_m002_item_delete, this);
            ivExpand = findViewById(R.id.iv_m002_item_expand, this);
            edContent = findViewById(R.id.ed_m002_item_content);
            lnExpand = findViewById(R.id.ln_m002_view_expand, this);
            lnDay = findViewById(R.id.days, this);
            cbRepeat = findViewById(R.id.repeat, this);
        }
    }
}
