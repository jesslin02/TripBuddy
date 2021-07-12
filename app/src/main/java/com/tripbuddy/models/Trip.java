package com.tripbuddy.models;

import org.parceler.Parcel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Parcel
public class Trip {
    /* title of trip */
    String title;
    /* pointer to user who created the trip */
    User user;
    /* city where trip is located */
    String destination;
    /* start date of trip */
    Date start;
    /* end date of trip */
    Date end;
    /* OPTIONAL: short notes about trip */
    String notes;

    public Trip() {
        // empty constructor for parceler
    }

    // simplified constructor for testing purposes
    public Trip(String title, String destination) {
        this(title, null, destination);
    }

    public Trip(String title, User user, String destination) {
        this.title = title;
        this.user = user;
        this.destination = destination;
    }

    private static Date newDate(int year, int month, int date) {
        // REMINDER: month value is 0 indexed (0 for january, 1 for february, etc)
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(year, month, date);
        return cal.getTime();
    }

    public void setStart(int year, int month, int date) {
        this.start = newDate(year, month, date);
    }

    public void setEnd(int year, int month, int date) {
        this.end = newDate(year, month, date);
    }

    public String getTitle() {
        return title;
    }

    public User getUser() {
        return user;
    }

    public String getDestination() {
        return destination;
    }

    public String getStart() {
        return formatDate(start);
    }

    public String getEnd() {
        return formatDate(end);
    }

    private static String formatDate(Date date) {
        String pattern = "MMMM d";
        SimpleDateFormat sdFormat = new SimpleDateFormat(pattern);
        return sdFormat.format(date);
    }

    public String getNotes() {
        return notes;
    }
}
