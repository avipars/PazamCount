package com.aviparshan.pazamcount;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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

public class Main extends AppCompatActivity {

    private static final String[] paths = {"2 Years 8 Months", "2 Years 6 Months", "2 Years 4 Months", "2 Years", "1 Year 6 Months", "1 Year", "6 Months"};
    final String welcomeScreenShownPref = "welcomeScreenShown";

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Window window = getWindow();
        Drawable background = getResources().getDrawable(R.drawable.bg_gradient); //bg_gradient is your gradient.
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(android.R.color.transparent));
        window.setBackgroundDrawable(background);

        checkFirstRun();

        JodaTimeAndroid.init(this);
        calendar = Calendar.getInstance();
        datePicker = findViewById(R.id.datePicker);
        spinner = findViewById(R.id.spinner);

        datePicker.setMaxDate(System.currentTimeMillis());
        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        if (!isFirstRun) {
            Date formattedDate = new Date(Helper.getLongPref("Date", getApplicationContext()));
            String dayString = new SimpleDateFormat("dd", Locale.ENGLISH).format(formattedDate);
            String monthString = new SimpleDateFormat("MM", Locale.ENGLISH).format(formattedDate);
            String yearString = new SimpleDateFormat("YYYY", Locale.ENGLISH).format(formattedDate);
            year = Integer.parseInt(yearString);
            month = Integer.parseInt(monthString) - 1;
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
                nextStep();
            }
        });


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(Main.this,
                android.R.layout.simple_spinner_item, paths);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        int timer = Helper.getIntPref("Time", getApplicationContext()); //gets results form shared prefs
        spinner.setSelection(timer); //sets Spinner to previous results

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //String itemText = spinner.getSelectedItem().toString();
                spinnerSet = true;
                Toast.makeText(Main.this, R.string.click, Toast.LENGTH_SHORT).show();
                nextStep();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }


    void setTimePref() {
        int itemPosition = spinner.getSelectedItemPosition();
        Helper.putPref("Time", itemPosition, getApplicationContext());
    }

    void setDatePref(Date startD) {
        try {
            long millis = startD.getTime();
            // String formatted = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(startD);
            Helper.putPref("Date", millis, getApplicationContext());
        } catch (NullPointerException npe) {

            Toast.makeText(this, npe.toString(), Toast.LENGTH_SHORT).show();
//            Calendar c = Calendar.getInstance();
//            long millis = c.getTimeInMillis();
//            Helper.putPref("Date", millis, getApplicationContext());
//        }
        }
    }

    void nextStep() {
        if (spinnerSet & dateSet) {
            setTimePref();
            setDatePref(startD);
            openResults();
        }
    }

    void openResults() {
        Intent intent = new Intent(Main.this, Results.class);
        startActivity(intent);
    }

    public void checkFirstRun() {

        isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("isFirstRun", true);
        if (isFirstRun) {
            // Place your dialog code here to display the dialog
            getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                    .edit()
                    .putBoolean("isFirstRun", false)
                    .apply();
        } else if (Helper.getBoolPref("goBack", getApplicationContext())) {
            Helper.putPref("goBack", false, getApplicationContext());

        } else {
            openResults();
        }
    }

}