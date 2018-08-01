package com.aviparshan.pazamcount;


import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.tsongkha.spinnerdatepicker.DatePicker;
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import static java.lang.Math.abs;

public class Main extends AppCompatActivity implements com.tsongkha.spinnerdatepicker.DatePickerDialog.OnDateSetListener {

    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
    private static final String[] paths = {"2 Years 8 Months", "2 Years 6 Months", "2 Years 4 Months", "2 Years", "1 Year 6 Months", "1 Year", "6 Months"};
    final String welcomeScreenShownPref = "welcomeScreenShown";

    private Spinner spinner;
    private Button save, date;
    Calendar calendar;
    String start_date, release_date;
    Date startD;
    ProgressBar prog;
    SharedPreferences mPrefs;
    Boolean startSet = false, endSet = false;
    SimpleDateFormat simpleDateFormat;
    TextView release, left, count, progre, serv;
    Calendar selectedDate = Calendar.getInstance();
    Calendar maxDate;
    DatePickerDialog sdatePickerDialog;
    String datePref;
    SimpleDateFormat sdf = new SimpleDateFormat("EE MMM dd HH:mm:ss z yyyy",
            Locale.ENGLISH);

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

        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        // second argument is the default to use if the preference can't be found
        Boolean welcomeScreenShown = mPrefs.getBoolean(welcomeScreenShownPref, false);

        if (!welcomeScreenShown) {
            // here you can launch another activity if you like
            SharedPreferences.Editor editor = mPrefs.edit();
            editor.putBoolean(welcomeScreenShownPref, true);
            editor.apply(); // Very important to save the preference

            int timer = getIntPref("Time", getApplicationContext()); //gets results form shared prefs
            Intent intent = new Intent(this, Results.class);
            intent.putExtra(EXTRA_MESSAGE, timer);
        }


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

        simpleDateFormat = new SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.US);

        try {
            setDiff();
        } catch (Error e) {
            e.printStackTrace();
        }


        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String datePref = getPref("Date", getApplicationContext()); //gets results form shared prefs
                    if (!datePref.isEmpty()) {
                        Date date = sdf.parse(datePref);
                        SimpleDateFormat yearFormat = new SimpleDateFormat("YYYY");
                        SimpleDateFormat monthFormat = new SimpleDateFormat("MM");
                        SimpleDateFormat dayFormat = new SimpleDateFormat("dd");
                        int year = Integer.parseInt(yearFormat.format(date));
                        int month = Integer.parseInt(monthFormat.format(date));
                        int day = Integer.parseInt(dayFormat.format(date));
                        showDate(year, month - 1, day, R.style.NumberPickerStyle); //month is 0 -12 day is 1-31
                    }
                } catch (NullPointerException npe) {
                    showDate(2017, 7, 8, R.style.NumberPickerStyle); //month is 0 -12 day is 1-31
                } catch (ParseException e) {
                    e.printStackTrace();
                }


//                sdatePickerDialog = new DatePickerDialog(v.getContext(), new DatePickerDialog.OnDateSetListener() {
//                    @Override
//                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//                        calendar.set(Calendar.YEAR, year);
//                        calendar.set(Calendar.MONTH, monthOfYear);
//                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
//                        startD = calendar.getTime();
//                        start_date = DateFormat.getDateInstance(DateFormat.MEDIUM).format(startD);
//                        date.setText(start_date); //show on button
//                        endSet = true;
//                        setDiff();
//                    }
//                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
//                sdatePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
//                sdatePickerDialog.show();

            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(Main.this,
                android.R.layout.simple_spinner_item, paths);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        int timer = getIntPref("Time", getApplicationContext()); //gets results form shared prefs
        spinner.setSelection(timer); //sets Spinner to previous results

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()

        {
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

    void showDate(int year, int monthOfYear, int dayOfMonth, int spinnerTheme) {
        new SpinnerDatePickerDialogBuilder()
                .context(Main.this)
                .callback(Main.this)
                .spinnerTheme(spinnerTheme)
                .defaultDate(year, monthOfYear, dayOfMonth)
                .build()
                .show();
        //maxDate
    }

    public Date getFutureDate(Date currentDate, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentDate);
        cal.add(Calendar.DATE, days);
        return cal.getTime();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Calendar calendar = new GregorianCalendar(year, monthOfYear, dayOfMonth);
        startD = calendar.getTime();
        date.setText(simpleDateFormat.format(calendar.getTime()));
        endSet = true;
        setDiff();
    }

    private void setSharedPreferences() {
        int itemPosition = spinner.getSelectedItemPosition();
        putPref("Time", itemPosition, getApplicationContext());
        String prefData = new SimpleDateFormat("EE MMM dd HH:mm:ss z yyyy",
                Locale.ENGLISH).format(startD);
        putPref("Date", prefData, getApplicationContext());
        Toast.makeText(this, "Set Shared Prefs: " + itemPosition + " " + date.getText().toString(), Toast.LENGTH_SHORT).show();
    }

    private void setDiff() {
        if (startSet && endSet) {
            setSharedPreferences();
            String itemText = spinner.getSelectedItem().toString();
            int month = serviceTime(itemText);
            int days = (int) (month * 30.4167);
            Date f = getFutureDate(startD, days);
            DateFormat df = new SimpleDateFormat("EEEE, MMMM dd, yyyy");
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

            printResults(reportDate, total_days, days_left, served, progress);
            ProgressBarAnimation anim = new ProgressBarAnimation(prog, 0, progress);
            anim.setDuration(1000);
            prog.startAnimation(anim);
            //prog.setProgress(progress); //days done / total days
        }
    }

    public void printResults(String reportDate, int total_days, int days_left, int served, int progress) {
        release.setText("Release Date: " + reportDate);
        left.setText("Total Service Days:  " + total_days);
        count.setText("Days Left: " + abs(days_left));
        serv.setText("Days Served: " + served);
        progre.setText("Progress: " + progress + "%");
    }

    private int serviceTime(String time) {
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
}