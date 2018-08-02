package com.aviparshan.pazamcount;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static java.lang.Math.abs;

/**
 * Created by avipars on 8/1/18 on com.aviparshan.pazamcount
 */
public class Results extends AppCompatActivity {

    ProgressBar prog;
    TextView percentageTitle, perc, dayleft;
    Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        dayleft = findViewById(R.id.days_left);
        perc = findViewById(R.id.percentage);
        percentageTitle = findViewById(R.id.title);
        prog = findViewById(R.id.progressBar);
        prog.setProgress(0);   // Main Progress
        prog.setSecondaryProgress(50); // Secondary Progress
        prog.setMax(100); // Maximum Progress

        setDiff();

    }

//     void printResults(String reportDate, int total_days, int days_left, int served, int progress) {
//        release.setText("Release Date: " + reportDate);
//        left.setText("Total Service Days:  " + total_days);
//        count.setText("Days Left: " + abs(days_left));
//        serv.setText("Days Served: " + served);
//        progre.setText("Progress: " + progress + "%");
//    }

    //  printResults(reportDate, total_days, days_left, served, progress);
    void setProressBar(int progress) {
        ProgressBarAnimation anim = new ProgressBarAnimation(prog, 0, progress);
        anim.setDuration(1000);
        prog.startAnimation(anim);
    }

    Date getFutureDate(Date currentDate, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentDate);
        cal.add(Calendar.DATE, days);
        return cal.getTime();
    }

    void setDiff() {
        int timer = Helper.getIntPref("Time", getApplicationContext()); //gets results form shared prefs
        int service = Helper.serviceTime(timer);
        Date startD = new Date(Helper.getLongPref("Date", getApplicationContext()));
        int days = (int) (service * 30.4167);
        Date f = getFutureDate(startD, days);
        DateFormat df = new SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.ENGLISH);
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

        setProressBar(progress);
        perc.setText(progress + "%");
        dayleft.setText(String.valueOf(abs(days_left)));
        //progressText.setText(progress + "%");
        // printResults(reportDate, total_days, days_left, served, progress);
        //  //prog.setProgress(progress); //days done / total days
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_results, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.back:
                Helper.putPref("goBack", true, getApplicationContext());
                Intent intent = new Intent(Results.this, Main.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}


