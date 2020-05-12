package com.datpt10.alarmup.view.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.datpt10.alarmup.Alarmup;
import com.datpt10.alarmup.R;
import com.datpt10.alarmup.model.DBManager;
import com.datpt10.alarmup.model.TimeZoneEntity;
import com.datpt10.alarmup.util.CommonUtil;
import com.datpt10.alarmup.view.event.OnM003TimeZoneCallBack;

import java.util.List;

/**
 * create by datpt on 12/27/2019.
 */
public class TimeZoneAdapter extends BaseRecycleAdapter<OnM003TimeZoneCallBack, TimeZoneEntity, TimeZoneAdapter.TimeZoneHolder> {
    private String[] mColors = {"#42CDCA", "#4fa6d3", "#4879af", "#63539e", "#5e4270"};
    private DBManager dbManager = new DBManager(mContext);

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
        zoneHolder.cardView.setCardBackgroundColor(Color.parseColor(mColors[position % 5]));
        zoneHolder.tvCity.setText(timeZoneEntity.getCity().substring(timeZoneEntity.getCity().lastIndexOf("/") + 1));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            zoneHolder.tvTimeZone.setTimeZone(timeZoneEntity.getCity());
        }
        zoneHolder.tvDate.setText(CommonUtil.getInstance().getDateCity(timeZoneEntity.getCity()));
        zoneHolder.tvDelete.setOnClickListener(v -> {
            String name = mListData.get(position).getCity();
            dbManager.deleteTitle(name);
            removeItem(position);
            notifyDataSetChanged();
            updateListData(mListData);
            mCallBack.changedView();

        });
    }

    public void addItem(int pos, TimeZoneEntity entity) {
        mListData.add(pos, entity);
        notifyItemInserted(pos);
    }

    private void removeItem(int pos) {
        mListData.remove(pos);
        notifyItemRemoved(pos);
    }

    public void updateListData(List<TimeZoneEntity> entityList) {
        mListData = entityList;
        notifyDataSetChanged();
    }

    public List<TimeZoneEntity> getListData() {
        return mListData;
    }

    public static class TimeZoneHolder extends BaseHolder {
        TextView tvCity, tvDate, tvDelete;
        TextClock tvTimeZone;
        ImageView ivDelete;

        private CardView cardView;

        TimeZoneHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void initView() {
            tvCity = findViewById(R.id.tv_m003_item_city, Alarmup.getInstance().getBoldFont());
            tvTimeZone = findViewById(R.id.tv_m003_item_time_zone, Alarmup.getInstance().getBoldFont());
            tvDate = findViewById(R.id.tv_m003_item_date, Alarmup.getInstance().getRegularFont());
            cardView = findViewById(R.id.cardView);

            tvDelete = findViewById(R.id.tv_m003_item_delete, Alarmup.getInstance().getRegularFont());
            ivDelete = findViewById(R.id.iv_m003_item_delete);
        }
    }
}
