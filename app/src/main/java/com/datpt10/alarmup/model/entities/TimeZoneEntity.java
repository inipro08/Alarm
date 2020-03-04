package com.datpt10.alarmup.model.entities;

/**
 * create by datpt on 12/27/2019.
 */
public class TimeZoneEntity {
    int id;
    String city;

    public TimeZoneEntity(int id, String city) {
        this.id = id;
        this.city = city;
    }

    public TimeZoneEntity(String city) {
        this.city = city;
    }

    public TimeZoneEntity() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
