package com.datpt10.alarmup.util;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.datpt10.alarmup.Alarmup;
import com.datpt10.alarmup.R;
import com.datpt10.alarmup.activity.AlarmActivity;
import com.datpt10.alarmup.model.AlarmEntity;
import com.datpt10.alarmup.model.SoundEntity;
import com.datpt10.alarmup.view.event.OnOKDialogCallBack;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import static android.content.Context.MODE_PRIVATE;

public class CommonUtil {
    public static final int GREY_LIST = 1;
    public static final String TAG = CommonUtil.class.getName();
    private static final String SAVE_DATA = "save_data";
    private static final String TIME_FORMAT = "HH : mm";
    private static final String DATE_FORMAT = "dd/MM/yyyy";
    private static final String DATE_NOW_DY = "yyyy-MM-dd";
    private static LOG_TYPE mDebugType = LOG_TYPE.INFO;

    private static CommonUtil instance;
    private Map<String, String> mBackFlow;

    /**
     * CommonUtil
     * private constructor for singleton
     */
    private CommonUtil() {
        mBackFlow = new HashMap<>();
    }

    /**
     * getInstance
     * return instance for singleton pattern
     *
     * @return instance
     */
    public static CommonUtil getInstance() {
        if (instance == null) {
            instance = new CommonUtil();
        }
        return instance;
    }

    public static void wtfi(String tag, String text) {
        if (text == null) return;
        if (mDebugType == LOG_TYPE.INFO || mDebugType == LOG_TYPE.DEBUG) {
            //Log.i(tag, text);
        } else {
            System.out.println(tag + ":" + text);
        }
    }

    public static void wtfe(String tag, String text) {
        if (text == null) return;
        if (mDebugType == LOG_TYPE.INFO || mDebugType == LOG_TYPE.DEBUG) {
            Log.e(tag, text);
        } else {
            System.out.println(tag + ":" + text);
        }
    }

    public static void wtfd(String tag, String text) {
        if (text == null) return;
        if (mDebugType == LOG_TYPE.INFO || mDebugType == LOG_TYPE.DEBUG) {
            Log.d(tag, text);
        } else {
            System.out.println(tag + ":" + text);
        }
    }

    public static String getDateNow(String dateStyle) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(dateStyle, Locale.getDefault());
            return dateFormat.format(new Date(System.currentTimeMillis()));
        } catch (Exception e) {
            CommonUtil.wtfe(TAG, e.getLocalizedMessage());
        }
        return null;
    }

    public static String getDateNow(String dateStyle, int nextDays) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(dateStyle, Locale.getDefault());
            Date dt = new Date(System.currentTimeMillis());
            Calendar c = Calendar.getInstance();
            c.setTime(dt);
            c.add(Calendar.DAY_OF_YEAR, nextDays);
            return dateFormat.format(c.getTime().getTime());
        } catch (Exception e) {
            CommonUtil.wtfe(TAG, e.getLocalizedMessage());
        }
        return null;
    }

    public static String getDateNow(String dateStyle, String time, int nextDays) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(dateStyle, Locale.getDefault());
            Date dt = dateFormat.parse(time);
            Calendar c = Calendar.getInstance();
            c.setTime(dt);
            c.add(Calendar.DAY_OF_YEAR, nextDays);
            return dateFormat.format(c.getTime().getTime());
        } catch (Exception e) {
            CommonUtil.wtfe(TAG, e.getLocalizedMessage());
        }
        return null;
    }

    public static int getInt(String value) {
        return (int) getDouble(value);
    }

    private static double getDouble(String value) {
        try {
            return Double.parseDouble(value);
        } catch (Exception e) {
            CommonUtil.wtfe(TAG, e.getLocalizedMessage());
        }
        return 0;
    }

    public static String getTimeNow(String noteTimeFormat) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(noteTimeFormat);
        dateFormat.setTimeZone(TimeZone.getDefault());
        Date currentTime = Calendar.getInstance().getTime();
        return dateFormat.format(currentTime);
    }

    public static Date stringToDateLocal(String inputStrDate, String dateFormat) {

        return stringToDate(inputStrDate, dateFormat, TimeZone.getDefault());
    }

    private static Date stringToDate(String inputStrDate, String dateFormat, TimeZone timeZone) {
        if (inputStrDate == null) return null;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.getDefault());
        simpleDateFormat.setTimeZone(timeZone);
        try {
            return simpleDateFormat.parse(inputStrDate);
        } catch (Exception e) {

            return null;
        }
    }

    public void savePrefContent(Context context, String key, String text) {
        SharedPreferences.Editor editor = context.getSharedPreferences(SAVE_DATA, MODE_PRIVATE).edit();
        editor.putString(key, text);
        editor.apply();
    }

    public void savePrefContent(String key, int value) {
        savePrefContent(Alarmup.getInstance(), key, value);
    }

    public void savePrefContent(Context context, String key, int value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(SAVE_DATA, MODE_PRIVATE).edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public void savePrefContent(String key, String text) {
        savePrefContent(Alarmup.getInstance(), key, text);
    }

    public String getPrefContent(String key) {
        return getPrefContent(key, false);
    }

    public String getPrefContent(String key, boolean isDeleted) {
        String result = Alarmup.getInstance()
                .getSharedPreferences(SAVE_DATA, MODE_PRIVATE).getString(key, null);
        if (result != null && isDeleted) {
            SharedPreferences.Editor editor = Alarmup.getInstance()
                    .getSharedPreferences(SAVE_DATA, MODE_PRIVATE).edit();
            editor.remove(key);
            editor.apply();
        }
        return result;
    }

    public int getIntPrefContent(String key, boolean isDeleted) {
        int result = Alarmup.getInstance()
                .getSharedPreferences(SAVE_DATA, MODE_PRIVATE).getInt(key, 0);
        if (result != 0 && isDeleted) {
            SharedPreferences.Editor editor = Alarmup.getInstance()
                    .getSharedPreferences(SAVE_DATA, MODE_PRIVATE).edit();
            editor.remove(key);
            editor.apply();
        }
        return result;
    }

    public int getIntPrefContent(String key) {
        return getIntPrefContent(key, false);
    }

    public void removePrefContent(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(SAVE_DATA, MODE_PRIVATE);
        preferences.edit().clear().apply();
    }

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    public void showDialog(Context context, String title, String message, String bt1Name, String
            bt2Name, final OnOKDialogCallBack onOKDialogCallBack) {
        AlertDialog mAlert = new AlertDialog.Builder(context).create();
        doKeepDialog(mAlert);
        mAlert.setTitle(title);
        mAlert.setMessage(message);
        if (bt1Name != null) {
            mAlert.setButton(AlertDialog.BUTTON_POSITIVE, bt1Name, (dialogInterface, i) -> {
                if (onOKDialogCallBack == null) return;
                onOKDialogCallBack.handleOKButton1();
            });
        }
        if (bt2Name != null) {
            mAlert.setButton(AlertDialog.BUTTON_POSITIVE, bt2Name, (dialogInterface, i) -> {
                if (onOKDialogCallBack == null) return;
                onOKDialogCallBack.handleOKButton2();
            });
        }
        mAlert.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    public void showDialog(Context context, int message, final OnOKDialogCallBack onOKDialogCallBack) {
        AlertDialog mAlert = new AlertDialog.Builder(context).create();
        doKeepDialog(mAlert);
        mAlert.setTitle(R.string.app_name);
        mAlert.setMessage(Alarmup.getInstance().getString(message));
        mAlert.setButton(AlertDialog.BUTTON_POSITIVE, context.getString(R.string.txt_got_it), (dialogInterface, i) -> {
            if (onOKDialogCallBack == null) return;
            onOKDialogCallBack.handleOKButton1();
        });
        mAlert.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    public AlertDialog showDialog(Context context, String message, final OnOKDialogCallBack onOKDialogCallBack) {
        AlertDialog mAlert = new AlertDialog.Builder(context).create();
        doKeepDialog(mAlert);
        mAlert.setTitle(R.string.app_name);
        mAlert.setCancelable(false);
        mAlert.setMessage(message);
        mAlert.setButton(AlertDialog.BUTTON_POSITIVE, context.getString(R.string.txt_got_it), (dialogInterface, i) -> {
            if (onOKDialogCallBack == null) return;
            onOKDialogCallBack.handleOKButton1();
        });
        mAlert.show();
        return mAlert;
    }

    // Prevent dialog dismiss when orientation changes
    public void doKeepDialog(Dialog dialog) {
        if (dialog.getWindow() == null) return;
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(lp);
    }

    public void savePreferenceString(Context context, String key, String text) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, text);
        editor.apply();
    }

    public void savePreferenceInt(Context context, String key, int value) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public void showListRingTone(Context context, TextView tvRingFile, AlarmEntity alarmEntity1, Alarmup mAlarmup) {
        ArrayList<SoundEntity> sounds = new ArrayList<>();
        ArrayAdapter<String> adapter;
        List<String> soundName;
        int[] item = new int[1];
        RingtoneManager manager = new RingtoneManager(context);
        manager.setType(RingtoneManager.TYPE_ALARM);
        Cursor cursor = manager.getCursor();
        int count = cursor.getCount();
        if (count > 0 && cursor.moveToFirst()) {
            do {
                sounds.add(new SoundEntity(cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX), SoundEntity.TYPE_RINGTONE, cursor.getString(RingtoneManager.URI_COLUMN_INDEX) + "/" + cursor.getString(RingtoneManager.ID_COLUMN_INDEX)));
            } while (cursor.moveToNext());
        }
        soundName = new ArrayList<>();
        for (int i = 0; i < sounds.size(); i++) {
            soundName.add(sounds.get(i).getName());
        }
        adapter = new ArrayAdapter<>(context, android.R.layout.simple_expandable_list_item_1, soundName);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose a ringtone alarm...");
        builder.setAdapter(adapter, null);
        AlertDialog alertDialog = builder.create();
        alertDialog.getListView().setOnItemClickListener((parent, view, position, id) -> {
            SoundEntity soundEntity = sounds.get(position);
            if (soundEntity.isPlaying(mAlarmup)) {
                soundEntity.stop(mAlarmup);
            } else {
                soundEntity.preview(mAlarmup);
            }
            item[0] = position;
        });
        alertDialog.getListView().setDividerHeight(2);
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OKAY", (dialog, position) -> {
            sounds.get(item[0]).stop(mAlarmup);
            tvRingFile.setText(sounds.get(item[0]).getName());
            alarmEntity1.setSound(context, sounds.get(item[0]));
        });
        alertDialog.show();
    }

    public String getFirstRingtone(Context context, AlarmEntity alarmEntity1) {
        ArrayList<SoundEntity> sounds = new ArrayList<>();
        RingtoneManager manager = new RingtoneManager(context);
        manager.setType(RingtoneManager.TYPE_ALARM);
        Cursor cursor = manager.getCursor();
        int count = cursor.getCount();
        if (count > 0 && cursor.moveToFirst()) {
            do {
                sounds.add(new SoundEntity(cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX), SoundEntity.TYPE_RINGTONE, cursor.getString(RingtoneManager.URI_COLUMN_INDEX) + "/" + cursor.getString(RingtoneManager.ID_COLUMN_INDEX)));
            } while (cursor.moveToNext());
        }
        String firstRingtone = sounds.get(0).getName();
        alarmEntity1.setSound(context, sounds.get(0));

        return firstRingtone;
    }

    public String getDateCity(String zoneId) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        dateFormat.setTimeZone(TimeZone.getTimeZone(zoneId));
        String date = dateFormat.format(calendar.getTime());
        return date;
    }

    public void showTimePicker(Context mContext, TextView tvTime, AlarmEntity alarmEntity1, AlarmManager alarmManager) {
        Calendar noteCal = Calendar.getInstance();
        int mHour = noteCal.get(Calendar.HOUR_OF_DAY);
        int mMin = noteCal.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(mContext, (timePicker, hourOfDay, minute) -> {
            noteCal.set(Calendar.HOUR_OF_DAY, hourOfDay);
            noteCal.set(Calendar.MINUTE, minute);
            tvTime.setText(new SimpleDateFormat(TIME_FORMAT, Locale.getDefault()).format(noteCal.getTime()));
            alarmEntity1.setTime(mContext, alarmManager, noteCal.getTimeInMillis());
        }, mHour, mMin, false);
        timePickerDialog.show();
    }

    public enum LOG_TYPE {
        INFO, DEBUG
    }
}