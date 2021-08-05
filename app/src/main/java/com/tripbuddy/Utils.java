package com.tripbuddy;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.tripbuddy.models.Event;
import com.tripbuddy.models.Trip;
import com.tripbuddy.receivers.AlarmReceiver;

import org.parceler.Parcels;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * contains functions that are used across multiple classes
 */
public class Utils {
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMM d, yyyy");
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

    public static void setDateError(boolean valid, int color, TextInputLayout ... items) {
        for (TextInputLayout layout : items) {
            if (!valid) {
                layout.setErrorTextColor(ColorStateList.valueOf(color));
                layout.setError("End date cannot be before Start Date");
                layout.setErrorEnabled(true);
            } else {
                layout.setErrorEnabled(false);
            }
        }
    }

    /**
     * checks that all required input fields are filled out when user presses
     * the create button for a new trip or event
     * CreateTripActivity, CreateEventActivity
     */
    public static boolean checkRequiredInput(int color, TextInputLayout ... items) {
        boolean validInput = true;
        for (TextInputLayout layout : items) {
            TextInputEditText editText = (TextInputEditText) layout.getEditText();
            String editTextContent = editText.getText().toString();
            if (editTextContent.isEmpty()) {
                layout.setErrorTextColor(ColorStateList.valueOf(color));
                layout.setError("Required");
                layout.setErrorEnabled(true);
                validInput = false;
            } else {
                layout.setErrorEnabled(false);
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

    public static void createNotif(Context context, Event event) {
        Calendar notifTime = Calendar.getInstance();
        notifTime.setTime(event.getStartDate());
        if (notifTime.getTimeInMillis() < System.currentTimeMillis()) {
            return;
        }

        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        Bundle alarmExtras = new Bundle();
        alarmExtras.putString("title", event.getTitle());
        alarmExtras.putString("location", event.getLocation());
        alarmExtras.putInt("time", (int) notifTime.getTimeInMillis());
        alarmIntent.putExtras(alarmExtras);

        int requestID = (int) notifTime.getTimeInMillis(); //unique requestID to differentiate between various notification with same NotifId
        int flags = PendingIntent.FLAG_CANCEL_CURRENT; // cancel old intent and create new one
        PendingIntent pIntent = PendingIntent.getBroadcast(context, requestID, alarmIntent, flags);

        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmMgr.setExact(AlarmManager.RTC_WAKEUP, notifTime.getTimeInMillis(), pIntent);
        Log.i(context.getClass().getSimpleName(), "created notif for " + event.getTitle());
    }

    public static void deleteNotif(Context context, Event event, Calendar oldTime) {
        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        Bundle alarmExtras = new Bundle();
        alarmExtras.putString("title", event.getTitle());
        alarmExtras.putString("location", event.getLocation());
        alarmExtras.putInt("time", (int) oldTime.getTimeInMillis());
        alarmIntent.putExtras(alarmExtras);

        int requestID = (int) oldTime.getTimeInMillis(); //unique requestID to differentiate between various notification with same NotifId
        int flags = PendingIntent.FLAG_CANCEL_CURRENT; // cancel old intent and create new one
        PendingIntent pIntent = PendingIntent.getBroadcast(context, requestID, alarmIntent, flags);

        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmMgr.cancel(pIntent);
        pIntent.cancel();
        Log.i(context.getClass().getSimpleName(), "deleted notif for " + event.getTitle());
    }
}
