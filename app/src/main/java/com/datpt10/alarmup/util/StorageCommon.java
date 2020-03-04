package com.datpt10.alarmup.util;

import com.datpt10.alarmup.ANApplication;
import com.datpt10.alarmup.model.entities.TimeZoneEntity;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.List;

/**
 * create by datpt on 3/28/2019.
 */
public class StorageCommon implements Serializable {

    private boolean mIsActive;
    private String mFlag;

    private transient WeakReference<ANApplication> reference;
    private String preTAG;
    private String mSnooze;
    private String mCity;
    private String mHourCity;
    private List<TimeZoneEntity> mList;

    public StorageCommon(WeakReference<ANApplication> reference) {
        this.reference = reference;
    }

    public WeakReference<ANApplication> getReference() {
        return reference;
    }

    public void setReference(WeakReference<ANApplication> reference) {
        this.reference = reference;
    }

    public void setFlagM001Active(boolean isActive) {
        mIsActive = isActive;
    }

    public boolean isIsActive() {
        return mIsActive;
    }

    public String getPreTAG() {
        return preTAG;
    }

    public void setPreTAG(String preTAG) {
        this.preTAG = preTAG;
    }

    public String getFlag() {
        return mFlag;
    }

    public void setmFlag(String mFlag) {
        this.mFlag = mFlag;
    }

    public String getSnooze() {
        return mSnooze;
    }

    public void setSnooze(String snooze) {
        this.mSnooze = snooze;
    }

    public void setM003NameCity(String city) {
        this.mCity = city;
    }

    public String getCity() {
        return mCity;
    }

    public void setM003HourCity(String hour) {
        this.mHourCity = hour;
    }

    public String getHourCity() {
        return mHourCity;
    }

    public List<TimeZoneEntity> getListTimeZone() {
        return mList;
    }

    public void setListTimeZone(List<TimeZoneEntity> mListTimeZone) {
        this.mList = mListTimeZone;
    }
}
