package com.datpt10.alarmup.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.datpt10.alarmup.model.entities.AlarmData;
import com.datpt10.alarmup.model.entities.TimeZoneEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * create by datpt on 11/21/2019.
 */
public class DBManager extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "alarm_list";
    private static final int DATABASE_VERSION = 2;

    private static final String TABLE_NAME_ALARM = "alarm";
    private static final String ALARM_ID = "id";
    private static final String ALARM_TIME = "alarmTime";
    private static final String ALARM_REPEAT = "alarmRepeat";
    private static final String ALARM_RING = "alarmRing";
    private static final String ALARM_CONTENT = "alarmContent";
    private static final String ALARM_TOGGLE = "toggle";

    private static final String TABLE_TIME_ZONE = "timezone";
    private static final String TIME_ID = "id";
    private static final String TIME_CITY = "city";
    private static final String TIME_HOUR_CITY = "hour";

    private Context mContext;

    public DBManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.d("DBManager", "DBManager:-----");
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sqlQuery = " CREATE TABLE IF NOT EXISTS " + TABLE_NAME_ALARM + " ("
                + ALARM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
                + ALARM_TIME + " TEXT, "
                + ALARM_REPEAT + " TEXT, "
                + ALARM_CONTENT + " TEXT, "
                + ALARM_RING + " TEXT, "
                + ALARM_TOGGLE + " INTEGER) ";
        db.execSQL(sqlQuery);
        String sqlTimeZone = " CREATE TABLE IF NOT EXISTS " + TABLE_TIME_ZONE + " ("
                + TIME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
                + TIME_CITY + " TEXT)";
        db.execSQL(sqlTimeZone);
        Log.d("DBManager", "Create:-----");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_ALARM);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TIME_ZONE);
        onCreate(db);
        Toast.makeText(mContext, "Drop successfylly", Toast.LENGTH_SHORT).show();
    }

    public boolean deleteTitle(String name) {
        SQLiteDatabase database = this.getWritableDatabase();
        return database.delete(TABLE_TIME_ZONE, TIME_CITY + "=?", new String[]{name}) > 0;
    }

    public boolean deleteItemAlarm(String time) {
        SQLiteDatabase database = this.getWritableDatabase();
        return database.delete(TABLE_NAME_ALARM, ALARM_TIME + "=?", new String[]{time}) > 0;
    }

    public void addAlarm(AlarmData alarmData) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ALARM_TIME, alarmData.getAlarmTime());
        values.put(ALARM_REPEAT, alarmData.getAlarmRepeat());
        values.put(ALARM_CONTENT, alarmData.getAlarmContent());
        values.put(ALARM_RING, alarmData.getAlarmRing());
        values.put(ALARM_TOGGLE, alarmData.getOnOff());

        database.insert(TABLE_NAME_ALARM, null, values);
        database.close();
        Log.d("DBManager", "addAlarm successfylly:-----");
    }

    //Add new a alarm
    // select a alarm by ID
    // Update time of alarm
    public int updateAlarm(AlarmData alarmData) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ALARM_TIME, alarmData.getAlarmTime());
        values.put(ALARM_REPEAT, alarmData.getAlarmRepeat());
        values.put(ALARM_CONTENT, alarmData.getAlarmContent());
        values.put(ALARM_RING, alarmData.getAlarmRing());
        values.put(ALARM_TOGGLE, alarmData.getOnOff());
        return database.update(TABLE_NAME_ALARM, values, ALARM_ID + "=?", new String[]{String.valueOf(alarmData.getId())});
    }

    public int updateTime(AlarmData alarmData) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ALARM_TIME, alarmData.getAlarmTime());
        return database.update(TABLE_NAME_ALARM, values, ALARM_ID + "=?", new String[]{String.valueOf(alarmData.getId())});
    }

    public int updateRepeat(AlarmData alarmData) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ALARM_REPEAT, alarmData.getAlarmRepeat());
        return database.update(TABLE_NAME_ALARM, values, ALARM_ID + "=?", new String[]{String.valueOf(alarmData.getId())});
    }

    public int updateToggle(AlarmData alarmData) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ALARM_TOGGLE, alarmData.getOnOff());
        return database.update(TABLE_NAME_ALARM, values, ALARM_ID + "=?", new String[]{String.valueOf(alarmData.getId())});
    }

    public int updateContent(AlarmData alarmData) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ALARM_CONTENT, alarmData.getAlarmContent());
        return database.update(TABLE_NAME_ALARM, values, ALARM_ID + "=?", new String[]{String.valueOf(alarmData.getId())});
    }

    public int updateRing(AlarmData alarmData) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ALARM_RING, alarmData.getAlarmRing());
        return database.update(TABLE_NAME_ALARM, values, ALARM_ID + "=?", new String[]{String.valueOf(alarmData.getId())});
    }


    public void addTimeZone(TimeZoneEntity timeZoneEntity) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TIME_CITY, timeZoneEntity.getCity());
        database.insert(TABLE_TIME_ZONE, null, values);
        database.close();
        Log.d("DBManager", "addTimeZone successfylly:-----");
    }

    public AlarmData getAlarmById(int id) {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.query(TABLE_NAME_ALARM, new String[]{ALARM_ID, ALARM_TIME, ALARM_REPEAT, ALARM_CONTENT, ALARM_RING, ALARM_TOGGLE},
                ALARM_ID + "=?", new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();
        AlarmData alarmData = new AlarmData(cursor.getString(1)
                , cursor.getString(2), cursor.getString(3),
                cursor.getString(4), cursor.getInt(5));
        cursor.close();
        database.close();
        return alarmData;
    }

    public TimeZoneEntity getTimeZoneById(int id) {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.query(TABLE_TIME_ZONE, new String[]{TIME_ID, TIME_CITY, TIME_HOUR_CITY},
                TIME_ID + "=?", new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();
        TimeZoneEntity timeZoneEntity = new TimeZoneEntity(cursor.getString(1));
        cursor.close();
        database.close();
        return timeZoneEntity;
    }


    /*
     Getting All Alarm
      */

    public List<AlarmData> getAllAlarm() {
        List<AlarmData> listAlarm = new ArrayList<>();
        //Select all query
        String selectQuery = "SELECT * FROM " + TABLE_NAME_ALARM;
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                AlarmData alarmData = new AlarmData();
                alarmData.setId(cursor.getInt(0));
                alarmData.setAlarmTime(cursor.getString(1));
                alarmData.setAlarmRepeat(cursor.getString(2));
                alarmData.setAlarmContent(cursor.getString(3));
                alarmData.setAlarmRing(cursor.getString(4));
                alarmData.setOnOff(cursor.getInt(5));
                listAlarm.add(alarmData);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return listAlarm;
    }

    public List<TimeZoneEntity> getAllTimeZone() {
        List<TimeZoneEntity> listTimeZone = new ArrayList<>();
        //Select all query
        String selectQuery = "SELECT * FROM " + TABLE_TIME_ZONE;
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                TimeZoneEntity timeZoneEntity = new TimeZoneEntity();
                timeZoneEntity.setId(cursor.getInt(0));
                timeZoneEntity.setCity(cursor.getString(1));
                listTimeZone.add(timeZoneEntity);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return listTimeZone;
    }

    // Delete a alarm by Id
    public void deleteAlarm(AlarmData alarmData) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(TABLE_NAME_ALARM, ALARM_ID + "=?",
                new String[]{String.valueOf(alarmData.getId())});
        database.close();
    }

    // Delete a alarm by Id
    public void deleteTimeZone(TimeZoneEntity timeZoneEntity) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(TABLE_TIME_ZONE, TIME_ID + "=?",
                new String[]{String.valueOf(timeZoneEntity.getId())});
        database.close();
    }
}