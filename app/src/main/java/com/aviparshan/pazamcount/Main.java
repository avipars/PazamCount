package com.aviparshan.pazamcount;


import android.app.DatePickerDialog;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

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
    TextView release, left, count, progre;

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

        prog.setProgress(0);   // Main Progress
        prog.setSecondaryProgress(100); // Secondary Progress
        prog.setMax(100); // Maximum Progress
        Resources res = getResources();
        Drawable drawable = res.getDrawable(R.drawable.circular);
        prog.setProgressDrawable(drawable);

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog sdatePickerDialog = new DatePickerDialog(v.getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        startD = calendar.getTime();
                        start_date = DateFormat.getDateInstance(DateFormat.MEDIUM).format(calendar.getTime());
                        date.setText(start_date); //show on button
                        endSet = true;
                        setDiff();
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                sdatePickerDialog.show();
            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(Main.this,
                android.R.layout.simple_spinner_item, paths);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String itemText = spinner.getSelectedItem().toString();
                startSet = true;
                setDiff();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setDiff() {
        if (startSet && endSet) {

            String itemText = spinner.getSelectedItem().toString();
            int month = serviceTime(itemText);
            int days = (int) (month * 30.4167);
            Date f = getFutureDate(startD, days);
            DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
            String reportDate = df.format(f);
            DateTime now = new DateTime();
            LocalDate today = now.toLocalDate();
            int days_left = Days.daysBetween(new LocalDate(f), new LocalDate(today)).getDays();
            int total_days = Days.daysBetween(new LocalDate(startD), new LocalDate(f)).getDays();
            release.setText("Release Date: " + reportDate);
            left.setText("Total Service Days:  " + total_days);
            count.setText("Days Left: " + abs(days_left));

           // int progress =  /total_days;
           // progre.setText(progress + "%");
           // prog.setProgress(progress); //days done / total days

        }
    }

    public Date getFutureDate(Date currentDate, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentDate);
        cal.add(Calendar.DATE, days);

        Date futureDate = cal.getTime();
        return futureDate;
    }

    private int serviceTime(String time) {
        int months = 0;
        //{"2 Years 8 Months", "2 Years 4 Months", "2 Years", "1 Year 6 Months", "1 Year", "6 Months"
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
            case "1 Years 6 Months":
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