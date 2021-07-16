package com.tripbuddy.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.parceler.Parcel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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

    private static Date newDate(int month, int date, int year) {
        // REMINDER: month value is 0 indexed (0 for january, 1 for february, etc)
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(year, month - 1, date);
        return cal.getTime();
    }

    public void setStart(Calendar cal) {
        // Date start = newDate(dateList.get(0), dateList.get(1), dateList.get(2));
        put(KEY_START, cal.getTime());
    }

    public void setEnd(Calendar cal) {
        // Date end = newDate(dateList.get(0), dateList.get(1), dateList.get(2));
        put(KEY_END, cal.getTime());
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
