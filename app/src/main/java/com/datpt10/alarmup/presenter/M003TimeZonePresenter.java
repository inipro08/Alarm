package com.datpt10.alarmup.presenter;


import com.datpt10.alarmup.view.event.OnM003TimeZoneCallBack;

import java.util.ArrayList;
import java.util.TimeZone;

/**
 * create by datpt on 10/25/2019.
 */
public class M003TimeZonePresenter extends BasePresenter<OnM003TimeZoneCallBack> {
    public M003TimeZonePresenter(OnM003TimeZoneCallBack event) {
        super(event);
    }

    public ArrayList<String> getItemSearch(String content) {
        ArrayList<String> mListId = new ArrayList<>();
        ArrayList<String> result = new ArrayList<>();
        String[] ids = TimeZone.getAvailableIDs();
        for (String id : ids) {
            String id2 = TimeZone.getTimeZone(id).getID();
            mListId.add(id2);
        }
        for (int i = 0; i < mListId.size(); i++) {
            if (mListId.get(i).toUpperCase().contains(content.toUpperCase())) {
                result.add(mListId.get(i));
            }
        }
        return result;
    }
}
