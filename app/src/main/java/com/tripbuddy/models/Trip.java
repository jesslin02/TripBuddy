package com.tripbuddy.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.parceler.Parcel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Parcel(analyze = {Trip.class})
@ParseClassName("Trip")
public class Trip extends ParseObject {
    public static final String KEY_TITLE = "title";
    public static final String KEY_USER = "user";
    public static final String KEY_DEST = "destination";
    public static final String KEY_START = "start";
    public static final String KEY_END = "end";
    public static final String KEY_NOTES = "notes";

    /* title of trip */
    public String getTitle() {
        return getString(KEY_TITLE);
    }

    public void setTitle(String title) {
        put(KEY_TITLE, title);
    }

    /* city where trip is located */
    public String getDestination() {
        return getString(KEY_DEST);
    }

    public void setDestination(String dest) {
        put(KEY_DEST, dest);
    }

    /* start date of trip */
    public String getStart() {
        Date parseDate = getDate(KEY_START);
        return formatDate(parseDate);
    }

    /* end date of trip */
    public String getEnd() {
        Date parseDate = getDate(KEY_END);
        return formatDate(parseDate);
    }

    private static String formatDate(Date date) {
        String pattern = "MMMM d";
        SimpleDateFormat sdFormat = new SimpleDateFormat(pattern);
        return sdFormat.format(date);
    }

    private static Date newDate(int year, int month, int date) {
        // REMINDER: month value is 0 indexed (0 for january, 1 for february, etc)
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(year, month, date);
        return cal.getTime();
    }

    public void setStart(int year, int month, int date) {
        Date start = newDate(year, month, date);
        put(KEY_START, start);
    }

    public void setEnd(int year, int month, int date) {
        Date end = newDate(year, month, date);
        put(KEY_END, end);
    }

    /* OPTIONAL: short notes about trip */
    public String getNotes() {
        return getString(KEY_NOTES);
    }

    public void setNotes(String notes) {
        put(KEY_NOTES, notes);
    }

    /* pointer to user who created the trip */
    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }
}
