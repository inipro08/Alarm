package com.datpt10.alarmup.view.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextClock;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.datpt10.alarmup.R;
import com.datpt10.alarmup.model.DBManager;
import com.datpt10.alarmup.model.entities.TimeZoneEntity;
import com.datpt10.alarmup.util.CommonUtil;
import com.datpt10.alarmup.view.event.OnM003TimeZoneCallBack;

import java.util.List;

/**
 * create by datpt on 12/27/2019.
 */
public class TimeZoneAdapter extends BaseRecycleAdapter<OnM003TimeZoneCallBack, TimeZoneEntity, TimeZoneAdapter.TimeZoneHolder> {

    public static final String NOTE_TIME_FORMAT = "hh:mm";
    DBManager dbManager = new DBManager(mContext);

    public TimeZoneAdapter(Context mContext, List<TimeZoneEntity> mListData, OnM003TimeZoneCallBack mCallBack) {
        super(mContext, mListData, mCallBack);
    }

    @Override
    protected int getLayoutId(int viewType) {
        return R.layout.item_m003_time_zone;
    }

    @Override
    protected TimeZoneHolder getViewHolder(int viewType, View itemView) {
        return new TimeZoneHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        TimeZoneHolder zoneHolder = (TimeZoneHolder) holder;
        TimeZoneEntity timeZoneEntity = mListData.get(position);
        zoneHolder.itemView.setTag(timeZoneEntity);
        zoneHolder.tvCity.setText(timeZoneEntity.getCity().substring(timeZoneEntity.getCity().lastIndexOf("/") + 1));
        zoneHolder.tvTimeZone.setTimeZone(timeZoneEntity.getCity());
        zoneHolder.tvDate.setText(CommonUtil.getInstance().getDateCity(timeZoneEntity.getCity()));
    }

    public void addItem(int pos, TimeZoneEntity entity) {
        mListData.add(pos, entity);
        notifyItemInserted(pos);
    }

    public void removeItem(int pos) {
        mListData.remove(pos);
        notifyItemRemoved(pos);
    }

    public void updateListData(List<TimeZoneEntity> entityList) {
        mListData = entityList;
        notifyDataSetChanged();
    }

    public class TimeZoneHolder extends BaseHolder {
        TextView tvCity, tvDate;
        TextClock tvTimeZone;

        public TimeZoneHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String name = mListData.get(getAdapterPosition()).getCity();
                    dbManager.deleteTitle(name);
                    removeItem(getAdapterPosition());
                    notifyDataSetChanged();
                    updateListData(mListData);
                }
            });
        }

        @Override
        protected void initView() {
            tvCity = findViewById(R.id.tv_m003_item_city);
            tvTimeZone = findViewById(R.id.tv_m003_item_time_zone);
            tvDate = findViewById(R.id.tv_m003_item_date);
        }
    }
}
