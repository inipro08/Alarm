package com.datpt10.alarmup.view.adapter;

import android.content.Context;
import android.os.Build;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.datpt10.alarmup.Alarmio;
import com.datpt10.alarmup.R;
import com.datpt10.alarmup.model.TimerEntity;
import com.datpt10.alarmup.view.event.OnM004TimerCallBack;
import com.datpt10.alarmup.widget.TextAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class TimerApdater extends BaseRecycleAdapter<OnM004TimerCallBack, TimerEntity, TimerApdater.TimerHolder> {
    private Alarmio alarmio;
    private RecyclerView recycler;
    private List<TimerEntity> timerData;


    public TimerApdater(Context mContext, List<TimerEntity> mListData, OnM004TimerCallBack mCallBack, RecyclerView recyclerTimer) {
        super(mContext, mListData, mCallBack);
        this.recycler = recyclerTimer;
        this.timerData = mListData;
    }

    @Override
    protected int getLayoutId(int viewType) {
        return R.layout.item_m004_timer;
    }

    @Override
    protected TimerHolder getViewHolder(int viewType, View itemView) {
        return new TimerHolder(itemView);
    }

    private TimerEntity getTimer(int position) {
        int timerSize = timerData.size();
        TimerEntity data;
        if (0 <= position) {
            if (timerSize > position) {
                data = timerData.get(position);
                return data;
            }
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        TimerHolder timerHolder = (TimerHolder) holder;
        TimerEntity timerEntity = getTimer(position);
        timerHolder.edContentTimer.setCursorVisible(false);
        timerHolder.edContentTimer.clearFocus();
        timerHolder.edContentTimer.setText(timerEntity.getContentTimer());
        timerHolder.edContentTimer.setOnClickListener(it -> holder.itemView.callOnClick());

        timerHolder.tvTimer.setText("??????");
        timerHolder.progressBarTimer.setMax(Math.toIntExact(timerEntity.getRemainingMillis()));

        timerHolder.actionPlayCancel.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_cancel));
        timerHolder.actionPlayCancel.setOnClickListener(view -> {
            timerEntity.onRemoved(mContext);
            mCallBack.removeTimer(timerEntity);
        });
        timerHolder.actionAddTimer.setOnClickListener(view -> mCallBack.addTimer());
    }

    public class TimerHolder extends BaseHolder {
        private EditText edContentTimer;
        private TextView tvTimer;
        private ProgressBar progressBarTimer;
        private FloatingActionButton actionAddTimer;
        private FloatingActionButton actionPlayCancel;

        public TimerHolder(View itemView) {
            super(itemView);
            edContentTimer.addTextChangedListener(new TextAdapter() {
                @Override
                public void afterTextChanged(Editable editable) {
                    timerData.get(getAdapterPosition()).setContentTimer(editable.toString(), mContext);
                }
            });
        }

        @Override
        protected void initView() {
            edContentTimer = findViewById(R.id.ed_m004_item_label);
            tvTimer = findViewById(R.id.tv_m004_item_count_downn);
            progressBarTimer = findViewById(R.id.progressBar);
            actionAddTimer = findViewById(R.id.fl_m004_item_add_timer);
            actionPlayCancel = findViewById(R.id.fl_m004_item_play_cancel);
        }
    }
}
