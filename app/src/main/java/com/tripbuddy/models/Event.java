package com.tripbuddy.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.parceler.Parcel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Parcel(analyze = {Event.class})
@ParseClassName("Event")
public class Event extends ParseObject {
    public static final String KEY_TITLE = "title";
    public static final String KEY_TRIP = "trip";
    public static final String KEY_USER = "user";
    public static final String KEY_LOC = "location";
    public static final String KEY_START = "start";
    public static final String KEY_END = "end";
    public static final String KEY_PHONE = "phone";
    public static final String KEY_WEB = "website";
    public static final String KEY_NOTES = "notes";

//    private static Date newDate(int month, int date, int year, int hrs, int min) {
//        // REMINDER: month value is 0 indexed (0 for january, 1 for february, etc)
//        Calendar cal = Calendar.getInstance();
//        cal.clear();
//        cal.set(year, month - 1, date, hrs, min);
//        return cal.getTime();
//    }

    /* title of event */
    public String getTitle() {
        return getString(KEY_TITLE);
    }

    public void setTitle(String title) {
        put(KEY_TITLE, title);
    }

    /* pointer to trip that contains this event */
    public Trip getTrip() {
        return (Trip) getParseObject(KEY_TRIP);
    }

    public void setTrip(Trip trip) {
        put(KEY_TRIP, trip);
    }

    /* pointer to user who created the trip */
    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    public void setStart(Calendar cal) {
        put(KEY_START, cal.getTime());
    }

    public void setEnd(Calendar cal) {
        put(KEY_END, cal.getTime());
    }

    public String getStart() {
        return formatDate(getDate(KEY_START));
    }

    public Date getStartDate() {
        return getDate(KEY_START);
    }

    public String getEnd() {
        return formatDate(getDate(KEY_END));
    }

    public Date getEndDate() {
        return getDate(KEY_END);
    }

    private static String formatDate(Date date) {
        String pattern = "MMM d h:mm a";
        SimpleDateFormat sdFormat = new SimpleDateFormat(pattern);
        return sdFormat.format(date);
    }

    public String getLocation() {
        return getString(KEY_LOC);
    }

    public void setLocation(String loc) {
        put(KEY_LOC, loc);
    }

    /* OPTIONAL: phone number of venue */
    public long getPhone() {
        return getLong(KEY_PHONE);
    }

    public String getPhoneString() {
        long phone = getLong(KEY_PHONE);
        if (phone == 0) {
            return null;
        }
        String phoneStr = String.valueOf(phone);
        String formatted = phoneStr.replaceFirst("(\\d{3})(\\d{3})(\\d+)", "($1) $2-$3");
        return formatted;
    }

    public void setPhone(long phone) {
        put(KEY_PHONE, phone);
    }

    /* OPTIONAL: website of venue */
    public String getWebsite() {
        return getString(KEY_WEB);
    }

    public void setWebsite(String web) {
        put(KEY_WEB, web);
    }

    /* OPTIONAL: short notes about event */
    public String getNotes() {
        return getString(KEY_NOTES);
    }

    public void setNotes(String notes) {
        put(KEY_NOTES, notes);
    }

    public String getId() {
        return getObjectId();
    }
}
