package com.datpt10.alarmup.view.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.datpt10.alarmup.R;
import com.datpt10.alarmup.view.event.OnM003TimeZoneCallBack;

import java.util.List;

public class SearchAdapter extends BaseRecycleAdapter<OnM003TimeZoneCallBack, String, SearchAdapter.SearchHolder> {
    private List<String> stringList;

    public SearchAdapter(Context mContext, List<String> mListData, OnM003TimeZoneCallBack mCallBack) {
        super(mContext, mListData, mCallBack);
        this.stringList = mListData;
    }

    @Override
    protected int getLayoutId(int viewType) {
        return R.layout.item_m003_search;
    }

    @Override
    protected SearchHolder getViewHolder(int viewType, View itemView) {
        return new SearchHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        SearchHolder searchHolder = (SearchHolder) holder;
        searchHolder.tvCity.setText(stringList.get(position));
        searchHolder.tvCity.setOnClickListener(v -> mCallBack.addTimeZone(searchHolder.tvCity.getText().toString()));
        searchHolder.itemView.setOnClickListener(v -> mCallBack.addTimeZone(searchHolder.tvCity.getText().toString()));
    }

    public static class SearchHolder extends BaseHolder {
        TextView tvCity;

        SearchHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void initView() {
            tvCity = findViewById(R.id.tv_m003_item_search);
        }
    }
}
