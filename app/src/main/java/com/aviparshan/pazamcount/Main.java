package com.aviparshan.pazamcount;


import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
    public static final String CHANNEL_ID = "PazamOlam_Channel";
    private Spinner spinner;
    DatePicker datePicker;
    String start_date;
    Calendar calendar;
    Boolean spinnerSet = false, dateSet = false;
    Date startD;
    boolean isFirstRun;
    int year, month, day;
    FloatingActionButton fab;
    Toolbar toolbar;
    Helper help = new Helper();
    Boolean fabShowing = false;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void setStatusBarGradient(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            Drawable background = activity.getResources().getDrawable(R.drawable.bg_gradient);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(activity.getResources().getColor(android.R.color.transparent));
            window.setNavigationBarColor(activity.getResources().getColor(android.R.color.transparent));
            window.setBackgroundDrawable(background);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarGradient(this);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.tool);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            Drawable gradientBG = getResources().getDrawable(R.drawable.bg_transparent);
            ab.setBackgroundDrawable(gradientBG);
        }

        checkFirstRun();

        JodaTimeAndroid.init(this);

        datePicker = findViewById(R.id.datePicker);
        spinner = findViewById(R.id.spinner);
        fab = findViewById(R.id.floating);

        calendar = Calendar.getInstance();

        Calendar calendarA = Calendar.getInstance();
        calendarA.add(Calendar.YEAR, -5);
        datePicker.setMaxDate(System.currentTimeMillis());
        datePicker.setMinDate(calendarA.getTimeInMillis());
        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        if (!isFirstRun) {
            Date formattedDate = new Date(Helper.getLongPref(help.datePreferenceKey, getApplicationContext()));
            String dayString = new SimpleDateFormat("dd", Locale.ENGLISH).format(formattedDate);
            String monthString = new SimpleDateFormat("MM", Locale.ENGLISH).format(formattedDate);
            String yearString = new SimpleDateFormat("yyyy", Locale.ENGLISH).format(formattedDate);
            year = Integer.parseInt(yearString);
            month = Integer.parseInt(monthString) - 1; //because it starts at 0
            day = Integer.parseInt(dayString);
            showFAB();
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
                if (spinnerSet) {
                    showFAB();
                }
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
                if (dateSet) {
                    showFAB();
                }
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
            Toast.makeText(this, npe.toString() + getString(R.string.error_report), Toast.LENGTH_SHORT).show();
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
                if (isFirstRun) {
                    if (spinnerSet & dateSet) {
                        setTimePref();
                        setDatePref(startD);
                        openResultsAnimation();
                    }
                } else {
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
            createNotificationChannel();
        } else if (Helper.getBoolPref(help.goBackKey, getApplicationContext())) {
            Helper.putPref(help.goBackKey, false, getApplicationContext());
        } else {
            openResults();
        }
    }

    void hideFAB() {
        if (fabShowing) {
            fabShowing = false;
            Animation hide_fab = AnimationUtils.loadAnimation(getApplication(), R.anim.fab_hide);
            fab.startAnimation(hide_fab);
        }
    }

    void showFAB() {
        if (!fabShowing) {
            fabShowing = true;
            fab.setVisibility(View.VISIBLE);
            Animation show_fab = AnimationUtils.loadAnimation(getApplication(), R.anim.fab_show);
            fab.startAnimation(show_fab);
        }
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

    }
}