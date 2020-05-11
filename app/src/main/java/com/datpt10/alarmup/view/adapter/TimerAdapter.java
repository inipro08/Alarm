package com.datpt10.alarmup.view.adapter;

import android.content.Context;
import android.graphics.Color;
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
import androidx.cardview.widget.CardView;

import com.datpt10.alarmup.Alarmup;
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
    private OnM004TimerCallBack timerCallBack;
    private String[] mColors = {"#42CDCA", "#4fa6d3", "#4879af", "#63539e", "#5e4270"};
    private List<TimerEntity> listTimerEntity;
    private LayoutInflater inflater;
    private ListView lvTimer;
    private Handler mHandler = new Handler();
    private Runnable updateRemainingTimeRunnable = new Runnable() {
        @Override
        public void run() {
            synchronized (lstHolders) {
                for (TimeHolder holder : lstHolders) {
                    holder.updateTimeRemaining();
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

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TimeHolder holder = null;
        if (convertView == null) {
            holder = new TimeHolder();
            convertView = inflater.inflate(R.layout.item_m004_timer, parent, false);
            holder.edContentTimer = convertView.findViewById(R.id.ed_m004_item_label);
            holder.edContentTimer.setTypeface(Alarmup.getInstance().getBoldFont());
            holder.tvTimer = convertView.findViewById(R.id.tv_m004_item_count_downn);
            holder.tvTimer.setTypeface(Alarmup.getInstance().getBoldFont());
            holder.progressBarTimer = convertView.findViewById(R.id.progressBar);
            holder.ivDelete = convertView.findViewById(R.id.iv_m004_item_delete);
            holder.cardView = convertView.findViewById(R.id.cardViewTimer);
            holder.cardView.setCardBackgroundColor(Color.parseColor(mColors[position % 5]));
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
        TimeHolder finalHolder = holder;
        holder.edContentTimer.addTextChangedListener(new TextAdapter() {
            @Override
            public void afterTextChanged(Editable editable) {
                finalHolder.edContentTimer.setBackgroundColor(getContext().getResources().getColor(R.color.colorBgEditText));
                listTimerEntity.get(position).setContentTimer(getContext(), editable.toString());
            }
        });
        holder.setData(listTimerEntity.get(position));
        return convertView;
    }

    private static class TimeHolder {
        EditText edContentTimer;
        TextView tvTimer;
        ImageView ivDelete;
        CardView cardView;
        ProgressBar progressBarTimer;
        TimerEntity timerEntity;

        void setData(TimerEntity item) {
            timerEntity = item;
            edContentTimer.setText(item.getContentTimer());
            updateTimeRemaining();
        }

        void updateTimeRemaining() {
            progressBarTimer.setMax((int) timerEntity.getDuration());
            long timeDiff = timerEntity.getRemainingMillis();
            if (timeDiff > 0) {
                int seconds = (int) (timeDiff / 1000) % 60;
                int minutes = (int) ((timeDiff / (1000 * 60)) % 60);
                int hours = (int) ((timeDiff / (1000 * 60 * 60)) % 24);

                progressBarTimer.setProgress((int) timeDiff - 670);
                String timeLeftFormatted = String.format(Locale.getDefault(), "%02d : %02d : %02d", hours, minutes, seconds);
                tvTimer.setText(timeLeftFormatted);
            } else {
                tvTimer.setText("00 : 00 : 00");
            }
        }
    }
}