package com.tripbuddy.models;

import com.parse.ParseUser;

import org.parceler.Parcel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Parcel
public class Event {
    /* title of event */
    String title;
    /* pointer to trip that contains this event */
    Trip trip;
    /* pointer to user who created the trip */
    ParseUser user;
    /* start date/time of trip */
    Date start;
    /* end date/time of trip */
    Date end;
    /* city where trip is located */
    String location;
    /* OPTIONAL: phone number of venue */
    long phone;
    /* OPTIONAL: website of venue */
    String website;
    /* OPTIONAL: short notes about event */
    String notes;

    public Event() {
        // empty constructor for parceler
    }

    // simplified constructor for testing purposes
    public Event(String title, String location) {
        this(title, null, location);
    }

    public Event(String title, Trip trip, String location) {
        this.title = title;
        this.trip = trip;
        this.location = location;
    }

    private static Date newDate(int year, int month, int date, int hrs, int min) {
        // REMINDER: month value is 0 indexed (0 for january, 1 for february, etc)
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(year, month, date, hrs, min);
        return cal.getTime();
    }

    public void setStart(int year, int month, int date, int hrs, int min) {
        this.start = newDate(year, month, date, hrs, min);
    }

    public void setEnd(int year, int month, int date, int hrs, int min) {
        this.end = newDate(year, month, date, hrs, min);
    }

    public String getTitle() {
        return title;
    }

    public String getStart() {
        return formatDate(start);
    }

    public String getEnd() {
        return formatDate(end);
    }

    private static String formatDate(Date date) {
        String pattern = "MMMM d h:mm a";
        SimpleDateFormat sdFormat = new SimpleDateFormat(pattern);
        return sdFormat.format(date);
    }

    public String getLocation() {
        return location;
    }
}
