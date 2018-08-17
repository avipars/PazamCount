package com.aviparshan.pazamcount;


import android.app.ActivityOptions;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.Toast;

import net.danlew.android.joda.JodaTimeAndroid;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class Main extends AppCompatActivity {

    private static final String[] paths = {"2 Years 8 Months", "2 Years 6 Months", "2 Years 4 Months", "2 Years", "1 Year 6 Months", "1 Year", "6 Months"};
    private Spinner spinner;
    DatePicker datePicker;
    String start_date;
    Calendar calendar;
    Boolean spinnerSet = false, dateSet = false;
    Date startD;
    DatePickerDialog datePickerDialog;
    boolean isFirstRun;
    int year, month, day;
    private Button date;
    FloatingActionButton fab;

    Helper help = new Helper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkFirstRun();
        gradient();

        JodaTimeAndroid.init(this);
        calendar = Calendar.getInstance();
        datePicker = findViewById(R.id.datePicker);
        spinner = findViewById(R.id.spinner);
        fab = findViewById(R.id.floating);

        showFAB();

        datePicker.setMaxDate(System.currentTimeMillis());
        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        if (!isFirstRun) {
            Date formattedDate = new Date(Helper.getLongPref(help.datePreferenceKey, getApplicationContext()));
            String dayString = new SimpleDateFormat("dd", Locale.ENGLISH).format(formattedDate);
            String monthString = new SimpleDateFormat("MM", Locale.ENGLISH).format(formattedDate);
            String yearString = new SimpleDateFormat("YYYY", Locale.ENGLISH).format(formattedDate);
            year = Integer.parseInt(yearString);
            month = Integer.parseInt(monthString) - 1; //because it starts at 0
            day = Integer.parseInt(dayString);
        } else {
            Calendar c = Calendar.getInstance();
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);
        }

        datePicker.init(year, month, day, new DatePicker.OnDateChangedListener() {

            @Override
            public void onDateChanged(DatePicker datePicker, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                startD = calendar.getTime();
                start_date = DateFormat.getDateInstance(DateFormat.MEDIUM).format(startD);
                dateSet = true;
            }
        });


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(Main.this,
                android.R.layout.simple_spinner_item, paths);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        int timer = Helper.getIntPref(help.spinnerPreferenceKey, getApplicationContext()); //gets results form shared prefs
        spinner.setSelection(timer); //sets Spinner to previous results

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerSet = true;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideFAB();
                nextStep();
            }
        });
    }

    private void gradient() {
        Window window = getWindow();
        Drawable background = getResources().getDrawable(R.drawable.bg_gradient); //bg_gradient is your gradient.
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(android.R.color.transparent));
        window.setBackgroundDrawable(background);
    }

    void setTimePref() {
        int itemPosition = spinner.getSelectedItemPosition();
        Helper.putPref(help.spinnerPreferenceKey, itemPosition, getApplicationContext());
    }

    void setDatePref(Date startD) {
        try {
            long millis = startD.getTime();
            // String formatted = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(startD);
            Helper.putPref(help.datePreferenceKey, millis, getApplicationContext());
        } catch (NullPointerException npe) {
            Toast.makeText(this, npe.toString() + " Please report to developer", Toast.LENGTH_SHORT).show();
//            Calendar c = Calendar.getInstance();
//            long millis = c.getTimeInMillis();
//            Helper.putPref("Date", millis, getApplicationContext());
//        }
        }
    }

    /*
    saves preferences based on what has changed
     */
    void nextStep() {

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if (spinnerSet & dateSet) {
                    setTimePref();
                    setDatePref(startD);
                    openResultsAnimation();
                } else if (spinnerSet & !dateSet) {
                    setTimePref();
                    openResultsAnimation();
                } else if (!spinnerSet & dateSet) {
                    setDatePref(startD);
                    openResultsAnimation();
                } else //no changes
                {
                    openResultsAnimation();
                }
            }
        }, 800);
    }

    void openResultsAnimation() {
        Intent settings = new Intent(this, Results.class);
        Bundle bundle = ActivityOptions.makeCustomAnimation(this,
                android.R.anim.fade_in, android.R.anim.fade_out).toBundle();
        this.startActivity(settings, bundle); //issue wiht other transitions b/c it's a fragment
    }

    void openResults() {
        Intent intent = new Intent(Main.this, Results.class);
        startActivity(intent);
    }

    public void checkFirstRun() {
        isFirstRun = getSharedPreferences(help.prefKey, MODE_PRIVATE).getBoolean(help.firstRunKey, true);
        if (isFirstRun) {
            getSharedPreferences(help.prefKey, MODE_PRIVATE)
                    .edit()
                    .putBoolean(help.firstRunKey, false)
                    .apply();
        } else if (Helper.getBoolPref(help.goBackKey, getApplicationContext())) {
            Helper.putPref(help.goBackKey, false, getApplicationContext());
        } else {
            openResults();
        }
    }

    void hideFAB() {
        Animation hide_fab = AnimationUtils.loadAnimation(getApplication(), R.anim.fab_hide);
        fab.startAnimation(hide_fab);
    }

    void showFAB() {
        fab.setVisibility(View.VISIBLE);
        Animation show_fab = AnimationUtils.loadAnimation(getApplication(), R.anim.fab_show);
        fab.startAnimation(show_fab);
    }

}