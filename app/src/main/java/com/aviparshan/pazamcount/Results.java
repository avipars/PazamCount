package com.aviparshan.pazamcount;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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

public class Results extends AppCompatActivity {

    ProgressBar prog;
    TextView stats, daysLeftTextView, percentDone, pazamDays;
    TextView draft, release;
    TextView pazamStats;
    TextView subDaysLeft, subPercentDone, subDaysServed;
    Calendar calendar;
    CardView card, card3;
    boolean isTimerRunning;
    Date f;
    int progress;
    boolean released = false;
    private Handler handler = new Handler();
    private Runnable runnable;
    Toolbar toolbar;
    Helper help = new Helper();

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void setStatusBarGradiant(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            Drawable background = activity.getResources().getDrawable(R.drawable.bg_gradient);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(activity.getResources().getColor(android.R.color.transparent));
            window.setNavigationBarColor(activity.getResources().getColor(android.R.color.transparent));
            window.setBackgroundDrawable(background);
        }
    }


    void setProcessBar(int progress) {
        ProgressBarAnimation anim = new ProgressBarAnimation(prog, 0, progress);
        anim.setDuration(1500);
        prog.startAnimation(anim);
    }

    Date getFutureDate(Date currentDate, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentDate);
        cal.add(Calendar.DATE, days);
        return cal.getTime();
    }

    void setDiff() {
        Date current_date = new Date();
        int timer = Helper.getIntPref(help.spinnerPreferenceKey, getApplicationContext()); //gets results form shared prefs
        int service = Helper.serviceTime(timer);
        Date startD = new Date(Helper.getLongPref(help.datePreferenceKey, getApplicationContext()));
        int days = (int) (service * 30.4167);
        f = getFutureDate(startD, days);
        DateFormat df = new SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.ENGLISH);
        DateFormat simple = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);

        String reportDate = df.format(f);
        String startDate = df.format(startD);
        String draftDate = simple.format(startD);
        String releaseDate = simple.format(f);

        DateTime now = new DateTime();
        LocalDate today = now.toLocalDate();
        int days_left = Days.daysBetween(new LocalDate(today), new LocalDate(f)).getDays();
        int total_days = Days.daysBetween(new LocalDate(startD), new LocalDate(f)).getDays();
        int served = total_days - days_left;
        double monthsLeft = (days_left) / 30.4375;
        double weeksLeft = (days_left) / 7;
        double hoursLeft = (days_left) * 24;
        double minutesLeft = (days_left) * 1440;
        double secondsLeft = (days_left) * 86400;

        double monthsDone = served / 30.4375;
        double weeksDone = served / 7;
        try {
            progress = 100 * served / total_days; //don't mess with this
            if (progress < 0) {
                progress = abs(progress);
            }
        } catch (ArithmeticException e) {
            Toast.makeText(this, "Error: Divide by zero " + e.getMessage(), Toast.LENGTH_SHORT).show();
            progress = 0;
        }
        setProcessBar(progress);
        if (!current_date.after(f)) //already released, because otherwise math is messed up
        {
            released = false;
            stats.setText(total_days + " Total Days\n" + Helper.Rounder(monthsLeft) + " Months Left\n" +
                    weeksLeft + " Weeks Left\n" + hoursLeft + " Hours Left\n" + minutesLeft + " Minutes Left\n"
                    + secondsLeft + " Seconds Left\n");
            Helper.animateTextView(0, served, pazamDays);
            pazamStats.setText(Helper.Rounder(monthsDone) + " Months Served\n" + Helper.Rounder(weeksDone) + " Weeks Served\n");

        } else {
            released = true;
            Toast.makeText(this, "Service Finished", Toast.LENGTH_SHORT).show();
            subDaysServed.setText("Service Days");
            subDaysLeft.setText(" Days out of Army");
            stats.setText(Helper.Rounder(abs(monthsLeft)) + " Months Out of Army\n" +
                    abs(weeksLeft) + " Weeks Out of Army\n" + abs(hoursLeft) + " Hours Out of Army\n" + abs(minutesLeft) + " Minutes Out of Army\n"
                    + abs(secondsLeft) + " Seconds Out of Army\n");
            Helper.animateTextView(0, total_days, pazamDays);

        }

        draft.setText(draftDate);
        release.setText(releaseDate);
        Helper.animateTextView(0, abs(days_left), daysLeftTextView);
        Helper.animateTextView(0, progress, percentDone);

        // perc.setText(progress + "%");
        //dayleft.setText(String.valueOf(abs(days_left)));
        //progressText.setText(progress + "%");
        // printResults(reportDate, total_days, days_left, served, progress);
        //  //prog.setProgress(progress); //days done / total days

    }

    //     void printResults(String reportDate, int total_days, int days_left, int served, int progress) {
//        release.setText("Release Date: " + reportDate);
//        left.setText("Total Service Days:  " + total_days);
//        count.setText("Days Left: " + abs(days_left));
//        serv.setText("Days Served: " + served);
//        progre.setText("Progress: " + progress + "%");
//    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarGradiant(this);
        setContentView(R.layout.activity_results);

        toolbar = findViewById(R.id.tool);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            Drawable gradientBG = getResources().getDrawable(R.drawable.bg_transparent);
            ab.setBackgroundDrawable(gradientBG);
        }

        card3 = findViewById(R.id.card3);
        pazamStats = findViewById(R.id.pazamStats);
        card = findViewById(R.id.card);
        draft = findViewById(R.id.draft);
        release = findViewById(R.id.release);
        subDaysLeft = findViewById(R.id.subhead);
        subDaysServed = findViewById(R.id.subheadPazam);
        subPercentDone = findViewById(R.id.subheadPercent);
        prog = findViewById(R.id.progressBar);
        stats = findViewById(R.id.stats);
        daysLeftTextView = findViewById(R.id.days);
        percentDone = findViewById(R.id.percentService);
        pazamDays = findViewById(R.id.pazam);

        prog.setProgress(0);   // Main Progress
        prog.setSecondaryProgress(50); // Secondary Progress
        prog.setMax(100); // Maximum Progress

        setDiff();
        if (released) {
            card3.setVisibility(View.GONE);
        }
//        card.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (!released) {
//                    String done = percentDone.getText().toString();
//                    int left = 100 - Integer.valueOf(done);
//                    percentDone.setText(left + "");
//                    if (subPercentDone.getText().toString().equalsIgnoreCase("% Done")) {
//                        subPercentDone.setText("% Left");
//                        Helper.animateTextView(Integer.valueOf(done), left, percentDone);
//
//                    } else {
//                        Helper.animateTextView(Integer.valueOf(done), left, percentDone);
//                        subPercentDone.setText("% Done");
//
//                    }
//                }
//            }
//        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case 1:
                Helper.putPref(help.goBackKey, true, getApplicationContext());
                Intent settings = new Intent(this, Main.class);
                Bundle bundle = ActivityOptions.makeCustomAnimation(this,
                        android.R.anim.fade_in, android.R.anim.fade_out).toBundle();
                this.startActivity(settings, bundle); //issue wiht other transitions b/c it's a fragment
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_patient_home_screen, menu);

        menu.add(0, 1, 1, menuIconWithText(getResources().getDrawable(R.drawable.ic_date_), getResources().getString(R.string.go_back)));
        return true;
    }

    private void gradient() {
        Window window = getWindow();
        Drawable background = getResources().getDrawable(R.drawable.bg_gradient); //bg_gradient is your gradient.
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(android.R.color.transparent));
        window.setBackgroundDrawable(background);
    }

    private CharSequence menuIconWithText(Drawable r, String title) {

        r.setBounds(0, 0, r.getIntrinsicWidth(), r.getIntrinsicHeight());
        SpannableString sb = new SpannableString("    " + title);
        ImageSpan imageSpan = new ImageSpan(r, ImageSpan.ALIGN_BOTTOM);
        sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return sb;
    }
    protected void onStop() {
        super.onStop();
        stopUpdates();
    }

    @Override
    public void onDestroy() {
        stopUpdates();
        super.onDestroy();

    }

    private void stopUpdates() {
        if (isTimerRunning) {
            handler.removeCallbacksAndMessages(null);
            isTimerRunning = false;
        }
    }
}


