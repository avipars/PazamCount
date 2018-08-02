package com.aviparshan.pazamcount;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.TextView;

/**
 * Created by avipars on 8/2/18 on com.aviparshan.pazamcount
 */
public class Helper {

    public static void animateTextView(int initialValue, int finalValue, final TextView textview) {

        ValueAnimator valueAnimator = ValueAnimator.ofInt(initialValue, finalValue);
        valueAnimator.setDuration(2000);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                textview.setText(valueAnimator.getAnimatedValue().toString());
            }
        });
        valueAnimator.start();

    }

    static double Rounder(double a) {
        return Math.round(a * 100.0) / 100.0;
    }

    static int serviceTime(String time) {
        int months = 0; //retrieved from paths
        switch (time) {
            case "2 Years 8 Months":
                months = 32;
                break;
            case "2 Years 6 Months":
                months = 30;
                break;
            case "2 Years 4 Months":
                months = 28;
                break;
            case "2 Years":
                months = 24;
                break;
            case "1 Year 6 Months":
                months = 18;
                break;
            case "1 Year":
                months = 12;
                break;
            case "6 Months":
                months = 6;
                break;
        }
        return months;
    }

    static int serviceTime(int time) {
        int months = 0; //retrieved from paths
        switch (time) {
            case 0:
                months = 32;
                break;
            case 1:
                months = 30;
                break;
            case 2:
                months = 28;
                break;
            case 3:
                months = 24;
                break;
            case 4:
                months = 18;
                break;
            case 5:
                months = 12;
                break;
            case 6:
                months = 6;
                break;
        }
        return months;
    }

    public static void putPref(String key, long value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public static void putPref(String key, int value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static void putPref(String key, String value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static void putPref(String key, boolean value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static String getPref(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, null);
    }

    public static boolean getBoolPref(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(key, true);
    }

    public static int getIntPref(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getInt(key, 0);
    }

    public static long getLongPref(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getLong(key, 0);
    }

}
