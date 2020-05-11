package com.datpt10.alarmup.widget;

import android.widget.SearchView;

public abstract class SearchViewAdapter implements SearchView.OnQueryTextListener {
    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}
