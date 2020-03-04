package com.datpt10.alarmup;

import android.app.Application;
import android.graphics.Typeface;
import android.util.Log;

import com.datpt10.alarmup.util.StorageCommon;

import java.lang.ref.WeakReference;

/**
 * create by datpt on 10/22/2019.
 */
public class ANApplication extends Application {
    private static final String TAG = ANApplication.class.getName();
    private static ANApplication instance;
    private Typeface mBoldFont;
    private Typeface mRegularFont;
    private Typeface mItalicFont;
    private Typeface mBoldItalicFont;

    private StorageCommon storageCommon;

    public ANApplication() {
        if (instance == null) {
            instance = this;
        }
    }

    public static ANApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate.......");

        storageCommon = new StorageCommon(new WeakReference<>(this));
        mBoldFont = Typeface.createFromAsset(getAssets(), "font/Neo Sans Intel-Bold.ttf");
        mRegularFont = Typeface.createFromAsset(getAssets(), "font/Neo Sans Intel.ttf");
        mBoldItalicFont = Typeface.createFromAsset(getAssets(), "font/Neo Sans Intel-Bold Italic.ttf");
        mItalicFont = Typeface.createFromAsset(getAssets(), "font/Neo Sans Intel-Italic.ttf");
    }

    public StorageCommon getStorageCommon() {
        if (storageCommon == null) {
            storageCommon = new StorageCommon(new WeakReference<>(this));
        }
        return storageCommon;
    }

    public Typeface getRegularFont() {
        return mRegularFont;
    }

    public Typeface getBoldFont() {
        return mBoldFont;
    }

    public Typeface getItalicFont() {
        return mItalicFont;
    }

    public Typeface getBoldItalicFont() {
        return mBoldItalicFont;
    }

}
