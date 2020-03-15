package com.datpt10.alarmup.view.adapter;

import android.content.Context;
import android.os.Handler;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.datpt10.alarmup.R;
import com.datpt10.alarmup.model.TimerEntity;
import com.datpt10.alarmup.view.event.OnM004TimerCallBack;
import com.datpt10.alarmup.widget.TextAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class TimerAdapter extends ArrayAdapter<TimerEntity> {
    private static final String TAG = TimerAdapter.class.getName();
    private final List<TimeHolder> lstHolders;
    private List<TimerEntity> listTimerEntity;
    private LayoutInflater inflater;
    private ListView lvTimer;
    private OnM004TimerCallBack timerCallBack;
    private Handler mHandler = new Handler();
    private Runnable updateRemainingTimeRunnable = new Runnable() {
        @Override
        public void run() {
            synchronized (lstHolders) {
                long currentTime = System.currentTimeMillis();
                for (TimeHolder holder : lstHolders) {
                    holder.updateTimeRemaining(currentTime);
                }
            }
        }
    };

    public TimerAdapter(@NonNull Context context, List<TimerEntity> timerEntityList, OnM004TimerCallBack callBack, ListView rlTimer) {
        super(context, 0, timerEntityList);
        this.listTimerEntity = timerEntityList;
        this.lvTimer = rlTimer;
        this.timerCallBack = callBack;
        inflater = LayoutInflater.from(context);
        lstHolders = new ArrayList<>();
        startUpdateTimer();
    }

    private void startUpdateTimer() {
        Timer tmr = new Timer();
        tmr.schedule(new TimerTask() {
            @Override
            public void run() {
                mHandler.post(updateRemainingTimeRunnable);
            }
        }, 1000, 1000);
    }

    private TimerEntity getTimer(int position) {
        int alarmSize = listTimerEntity.size();
        TimerEntity data;
        if (0 <= position) {
            if (alarmSize > position) {
                data = listTimerEntity.get(position);
                return data;
            }
        }
        return null;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TimeHolder holder = null;
        if (convertView == null) {
            holder = new TimeHolder();
            convertView = inflater.inflate(R.layout.item_m004_timer, parent, false);
            holder.edContentTimer = convertView.findViewById(R.id.ed_m004_item_label);
            holder.tvTimer = convertView.findViewById(R.id.tv_m004_item_count_downn);
            holder.progressBarTimer = convertView.findViewById(R.id.progressBar);
            holder.ivDelete = convertView.findViewById(R.id.iv_m004_item_delete);
            convertView.setTag(holder);
            synchronized (lstHolders) {
                lstHolders.add(holder);
            }
        } else {
            holder = (TimeHolder) convertView.getTag();
        }
        holder.ivDelete.setOnClickListener(v -> {
            timerCallBack.removeTimer(listTimerEntity.get(position));
            lvTimer.invalidateViews();
        });
        holder.edContentTimer.addTextChangedListener(new TextAdapter() {
            @Override
            public void afterTextChanged(Editable s) {
                listTimerEntity.get(position).setContentTimer(s.toString(), getContext());
            }
        });
        holder.tvTimer.setOnClickListener(v -> timerCallBack.showNumberPicker(listTimerEntity.get(position)));
        holder.setData(listTimerEntity.get(position));
        return convertView;
    }

    static class TimeHolder {
        EditText edContentTimer;
        TextView tvTimer;
        ImageView ivDelete;
        ProgressBar progressBarTimer;
        TimerEntity timerEntity;

        void setData(TimerEntity item) {
            timerEntity = item;
            edContentTimer.setText(item.getContentTimer());
            updateTimeRemaining(System.currentTimeMillis());
        }

        void updateTimeRemaining(long currentTime) {
            progressBarTimer.setMax((int) timerEntity.getDuration());
            long timeDiff = timerEntity.getRemainingMillis() - currentTime;
            if (timeDiff > 0) {
                int seconds = (int) (timeDiff / 1000) % 60;
                int minutes = (int) ((timeDiff / (1000 * 60)) % 60);
                int hours = (int) ((timeDiff / (1000 * 60 * 60)) % 24);

                progressBarTimer.setProgress((int) timeDiff);
                String timeLeftFormatted = null;
                if (hours > 0) {
                    timeLeftFormatted = String.format(Locale.getDefault(), "%02d : %02d : %02d", hours, minutes, seconds);
                } else if (minutes > 0) {
                    timeLeftFormatted = String.format(Locale.getDefault(), "%02d : %02d", minutes, seconds);
                } else if (seconds > 0) {
                    timeLeftFormatted = String.format(Locale.getDefault(), "%02d", seconds);
                }
                tvTimer.setText(timeLeftFormatted);
            } else {
                tvTimer.setText("Done");
            }
        }
    }
}