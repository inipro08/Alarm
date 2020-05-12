package com.datpt10.alarmup.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * create by datpt on 11/21/2019.
 */
public class DBManager extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "alarm_list";
    private static final int DATABASE_VERSION = 2;

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
        String sqlTimeZone = " CREATE TABLE IF NOT EXISTS " + TABLE_TIME_ZONE + " ("
                + TIME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
                + TIME_CITY + " TEXT)";
        db.execSQL(sqlTimeZone);
        Log.d("DBManager", "Create:-----");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TIME_ZONE);
        onCreate(db);
        Toast.makeText(mContext, "Drop successfylly", Toast.LENGTH_SHORT).show();
    }

    public boolean deleteTitle(String name) {
        SQLiteDatabase database = this.getWritableDatabase();
        return database.delete(TABLE_TIME_ZONE, TIME_CITY + "=?", new String[]{name}) > 0;
    }

    public void addTimeZone(TimeZoneEntity timeZoneEntity) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TIME_CITY, timeZoneEntity.getCity());
        database.insert(TABLE_TIME_ZONE, null, values);
        database.close();
        Log.d("DBManager", "addTimeZone successfylly:-----");
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
    public void deleteTimeZone(TimeZoneEntity timeZoneEntity) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(TABLE_TIME_ZONE, TIME_ID + "=?",
                new String[]{String.valueOf(timeZoneEntity.getId())});
        database.close();
    }
}