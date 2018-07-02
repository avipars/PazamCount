package com.aviparshan.pazamcount;


import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static java.lang.Math.abs;

public class Main extends AppCompatActivity {
    private Spinner spinner;
    private Button save, date;
    Calendar calendar;
    String start_date, release_date;
    Date startD;
    ProgressBar prog;
    Boolean startSet = false, endSet = false;
    private static final String[] paths = {"2 Years 8 Months", "2 Years 4 Months", "2 Years", "1 Year 6 Months", "1 Year", "6 Months"};
    TextView release, left, count, progre, serv;
    Calendar selectedDate = Calendar.getInstance();
    DatePickerDialog sdatePickerDialog;
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

    public static String getPref(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, null);
    }

    public static int getIntPref(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getInt(key, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        JodaTimeAndroid.init(this);
        calendar = Calendar.getInstance();
        prog = findViewById(R.id.progressBar);
        date = findViewById(R.id.datepicker);
        spinner = findViewById(R.id.spinner1);
        release = findViewById(R.id.releasedate);
        left = findViewById(R.id.daysleft);
        count = findViewById(R.id.count);
        progre = findViewById(R.id.progress);
        serv = findViewById(R.id.served);
        prog.setProgress(0);   // Main Progress
        prog.setSecondaryProgress(100); // Secondary Progress
        prog.setMax(100); // Maximum Progress
        Resources res = getResources();
        Drawable drawable = res.getDrawable(R.drawable.circular);
        prog.setProgressDrawable(drawable);

        // setSpinner();
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sdatePickerDialog = new DatePickerDialog(v.getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        startD = calendar.getTime();
                        start_date = DateFormat.getDateInstance(DateFormat.MEDIUM).format(startD);
                        date.setText(start_date); //show on button
                        endSet = true;
                        setDiff();
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                sdatePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                sdatePickerDialog.show();
            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(Main.this,
                android.R.layout.simple_spinner_item, paths);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        int timer = getIntPref("Time", getApplicationContext()); //gets results form shared prefs
        spinner.setSelection(timer); //sets Spinner to previous results

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //String itemText = spinner.getSelectedItem().toString();
                startSet = true;
                setDiff();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setSharedPreferences() {
        int itemPosition = spinner.getSelectedItemPosition();
        putPref("Time", itemPosition, getApplicationContext());
        putPref("Date", date.getText().toString(), getApplicationContext());
        Toast.makeText(this, "Set Shared Prefs: " + itemPosition + date.getText().toString(), Toast.LENGTH_SHORT).show();
    }

    private void setDiff() {
        if (startSet && endSet) {
            setSharedPreferences();
            String itemText = spinner.getSelectedItem().toString();
            int month = serviceTime(itemText);
            int days = (int) (month * 30.4167);
            Date f = getFutureDate(startD, days);
            DateFormat df = new SimpleDateFormat("EEEE, MMMM d, yyyy");
            String reportDate = df.format(f);
            DateTime now = new DateTime();
            LocalDate today = now.toLocalDate();
            int days_left = Days.daysBetween(new LocalDate(f), new LocalDate(today)).getDays();
            int total_days = Days.daysBetween(new LocalDate(startD), new LocalDate(f)).getDays();
            int served = total_days - abs(days_left);
            int progress = 0;
            try {
                progress = 100 * served / total_days; //don't mess with this
                if (progress >= 100) {
                    progress = 100;
                } else if (progress < 0) {
                    progress = abs(progress);
                }
            } catch (ArithmeticException e) {
                Toast.makeText(this, "Divide by zero " + e.getMessage(), Toast.LENGTH_SHORT).show();
                progress = 0;
            }
            release.setText("Release Date: " + reportDate);
            left.setText("Total Service Days:  " + total_days);
            count.setText("Days Left: " + abs(days_left));
            serv.setText("Days Served: " + served);
            progre.setText("Progress: " + progress + "%");

            ProgressBarAnimation anim = new ProgressBarAnimation(prog, 0, progress);
            anim.setDuration(1000);
            prog.startAnimation(anim);
            //prog.setProgress(progress); //days done / total days
        }
    }

    public Date getFutureDate(Date currentDate, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentDate);
        cal.add(Calendar.DATE, days);
        return cal.getTime();
    }

    private int serviceTime(String time) {
        int months = 0;
        switch (time) {
            case "2 Years 8 Months":
                months = 32;
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


}