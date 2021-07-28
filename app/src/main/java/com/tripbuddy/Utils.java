package com.tripbuddy;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;

import com.tripbuddy.models.Event;
import com.tripbuddy.models.Trip;
import com.tripbuddy.receivers.AlarmReceiver;

import org.parceler.Parcels;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * contains functions that are used across multiple classes
 */
public class Utils {

    /**
     * go to MainActivity from current activity
     * LoginActivity, SignUpActivity, CreateTripActivity
     */
    public static void goMainActivity(Activity current) {
        Intent i = new Intent(current, MainActivity.class);
        current.startActivity(i);
        // so that pressing the back button on the MainActivity doesn't go back to the login screen
        current.finish();
    }

    /**
     * go to ItineraryActivity from current activity
     * TripDetailActivity, CreateEventActivity
     */
    public static void goItineraryActivity(Activity current, Trip trip) {
        Intent i = new Intent(current, ItineraryActivity.class);
        i.putExtra(Trip.class.getSimpleName(), Parcels.wrap(trip));
        current.startActivity(i);
    }

    public static Calendar toCalendar(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }

    /**
     * clears text input fields after creating a trip or an event
     * CreateTripActivity, CreateEventActivity
     * @param items array of EditText objects to be cleared
     */
    public static void resetInput(EditText ... items) {
        for (EditText et : items) {
            et.setText("");
            et.getBackground().clearColorFilter();
        }
    }

    /**
     * checks that all required input fields are filled out when user presses
     * the create button for a new trip or event
     * CreateTripActivity, CreateEventActivity
     */
    public static boolean checkRequiredInput(EditText ... items) {
        boolean validInput = true;
        for (EditText et : items) {
            if (et.getText().toString().isEmpty()) {
                et.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
                validInput = false;
            }
        }
        return validInput;
    }

    /**
     * for use when inputting start and end dates/times
     * CreateTripActivity, CreateEventActivity
     * https://stackoverflow.com/questions/1109022/how-do-you-close-hide-the-android-soft-keyboard-programmatically
     */
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * used as onTouchListener for inputting the start date and end date
     * allows popup calendar date picker
     * https://www.tutlane.com/tutorial/android/android-datepicker-with-examples
     * CreateTripActivity, CreateEventActivity
     */
    static class dateTouchListener implements View.OnTouchListener {
        EditText etDate;
        Activity activity;
        Calendar cal;

        public dateTouchListener(EditText etDate, Activity activity, Calendar cal) {
            super();
            this.etDate = etDate;
            this.activity = activity;
            this.cal = cal;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                Utils.hideKeyboard(activity);
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                DatePickerDialog picker = new DatePickerDialog(activity,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                SimpleDateFormat sdFormat = new SimpleDateFormat("M/d/yyyy");
                                cal.set(Calendar.YEAR, year);
                                cal.set(Calendar.MONTH, monthOfYear);
                                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                etDate.setText(sdFormat.format(cal.getTime()));
                            }
                        }, year, month, day);
                picker.show();
                return true;
            }
            return false;
        }
    }

    public static void createNotif(Context context, Event event) {
        Calendar notifTime = Calendar.getInstance();
        notifTime.setTime(event.getStartDate());
        if (notifTime.getTimeInMillis() < System.currentTimeMillis()) {
            return;
        }

        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        Bundle alarmExtras = new Bundle();
        alarmExtras.putString("event", event.getTitle());
        alarmExtras.putString("location", event.getLocation());
        alarmExtras.putInt("time", (int) notifTime.getTimeInMillis());
        alarmIntent.putExtras(alarmExtras);

        int requestID = (int) System.currentTimeMillis(); //unique requestID to differentiate between various notification with same NotifId
        int flags = PendingIntent.FLAG_CANCEL_CURRENT; // cancel old intent and create new one
        PendingIntent pIntent = PendingIntent.getBroadcast(context, requestID, alarmIntent, flags);

        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmMgr.set(AlarmManager.RTC_WAKEUP, notifTime.getTimeInMillis(), pIntent);
        Log.i(context.getClass().getSimpleName(), "created notif for " + event.getTitle());
    }
}
