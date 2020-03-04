package com.datpt10.alarmup.model.entities;

/**
 * create by datpt on 11/18/2019.
 */
public class AlarmData {
  private int id;
  private String alarmTime;
  private String alarmRepeat;
  private String alarmContent;
  private String alarmRing;
  private int onOff;

  public AlarmData(int id, String alarmTime, String alarmRepeat, String alarmContent, String alarmRing, int onOff) {
    this.id = id;
    this.alarmTime = alarmTime;
    this.alarmRepeat = alarmRepeat;
    this.alarmContent = alarmContent;
    this.alarmRing = alarmRing;
    this.onOff = onOff;
  }

  public AlarmData(String alarmTime, String alarmRepeat, String alarmContent, String alarmRing, int onOff) {
    this.alarmTime = alarmTime;
    this.alarmRepeat = alarmRepeat;
    this.alarmContent = alarmContent;
    this.alarmRing = alarmRing;
    this.onOff = onOff;
  }

  public AlarmData() {
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getAlarmTime() {
    return alarmTime;
  }

  public void setAlarmTime(String alarmTime) {
    this.alarmTime = alarmTime;
  }

  public String getAlarmRepeat() {
    return alarmRepeat;
  }

  public void setAlarmRepeat(String alarmRepeat) {
    this.alarmRepeat = alarmRepeat;
  }

  public String getAlarmRing() {
    return alarmRing;
  }

  public void setAlarmRing(String alarmRing) {
    this.alarmRing = alarmRing;
  }

  public String getAlarmContent() {
    return alarmContent;
  }

  public void setAlarmContent(String alarmContent) {
    this.alarmContent = alarmContent;
  }

  public int getOnOff() {
    return onOff;
  }

  public void setOnOff(int onOff) {
    this.onOff = onOff;
  }
}