package de.nilsstrelow.vplan.activities;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;

import java.util.Date;

import de.nilsstrelow.vplan.R;
import de.nilsstrelow.vplan.constants.Hours;
import de.nilsstrelow.vplan.helpers.Entry;
import de.nilsstrelow.vplan.services.ReminderService;
import de.nilsstrelow.vplan.utils.DateUtils;

public class AddReminderActivity extends ActionBarActivity {

    public static final String TAG = AddReminderActivity.class.getSimpleName();
    public static final String ENTRY_KEY = "entry_key";
    private boolean DEBUG = false;
    private Entry selectedEntry;
    private Date timeToNotify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_reminder_activity);

        selectedEntry = (Entry) getIntent().getSerializableExtra(ENTRY_KEY);
        timeToNotify = selectedEntry.day;

        if (DEBUG)
            Log.e(TAG, timeToNotify.toString());

        final TimePicker timePicker = (TimePicker) findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);

        final DatePicker datePicker = (DatePicker) findViewById(R.id.datePicker);

        /* Add predefined time */
        String stunde = selectedEntry.stunde;
        final String title = "Erinnerung für " + stunde + ". Stunde";
        //getDialog().setTitle(title);
        int hour = Integer.valueOf((stunde.matches("[1][0-2].*") ? stunde.substring(0, 2) : stunde.substring(0, 1)));
        String[] times = Hours.getHour(hour).split(":");
        timePicker.setCurrentHour(Integer.valueOf(times[0]));
        timePicker.setCurrentMinute(Integer.valueOf(times[1]));

        datePicker.init(DateUtils.getYear(timeToNotify), DateUtils.getMonth(timeToNotify), DateUtils.getDay(timeToNotify), new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

            }
        });

        View actionBarButtons = getLayoutInflater().inflate(R.layout.done_cancel_bar,
                new LinearLayout(this), false);
        View cancelActionView = actionBarButtons.findViewById(R.id.action_cancel);
        cancelActionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        View doneActionView = actionBarButtons.findViewById(R.id.action_done);
        doneActionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                timeToNotify.setYear(datePicker.getYear());
                timeToNotify.setMonth(datePicker.getMonth());
                timeToNotify.setDate(datePicker.getDayOfMonth());
                timeToNotify.setHours(timePicker.getCurrentHour());
                timeToNotify.setMinutes(timePicker.getCurrentMinute());

                Log.e(TAG, timeToNotify.toString());

                if (DateUtils.isFuture(timeToNotify)) {
                    addReminder();
                    Toast.makeText(AddReminderActivity.this, title + " erstellt", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(AddReminderActivity.this, "Hmmm... Woher hast du die Zeitmaschine ?", Toast.LENGTH_LONG).show();
                }
            }
        });

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(actionBarButtons);

        TextView txtVertreter = (TextView) findViewById(R.id.rVertreter);
        txtVertreter.setText(selectedEntry.vertreter);
        TextView txtFach = (TextView) findViewById(R.id.rFach);
        txtFach.setText(selectedEntry.fach);
        TextView txtRaum = (TextView) findViewById(R.id.rRaum);
        txtRaum.setText(selectedEntry.raum);
        TextView txtStattLehrer = (TextView) findViewById(R.id.rStattLehrer);
        txtStattLehrer.setText(selectedEntry.stattLehrer);
        TextView txtStattFach = (TextView) findViewById(R.id.rStattFach);
        txtStattFach.setText(selectedEntry.stattFach);
        TextView txtStattRaum = (TextView) findViewById(R.id.rStattRaum);
        txtStattRaum.setText(selectedEntry.stattRaum);
        TextView txtBemerkung = (TextView) findViewById(R.id.rBemerkung);
        txtBemerkung.setText(selectedEntry.bemerkung);

    }

    private void addReminder() {

        Intent i = new Intent(this, ReminderService.class);
        i.putExtra(ReminderService.SELECTED_ENTRY_KEY, selectedEntry);
        PendingIntent pendingIntent = PendingIntent.getService(this,
                5546, i, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager am = (AlarmManager) getSystemService(Activity.ALARM_SERVICE);

        Log.e(TAG, timeToNotify.toString());

        if (DEBUG)
            am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 2000, pendingIntent);
        else
            am.set(AlarmManager.RTC_WAKEUP, timeToNotify.getTime(), pendingIntent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EasyTracker.getInstance(this).activityStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EasyTracker.getInstance(this).activityStop(this);
    }
}
