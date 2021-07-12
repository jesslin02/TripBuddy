package com.tripbuddy.models;

import org.parceler.Parcel;

import java.util.Date;

@Parcel
public class Event {
    /* title of event */
    String title;
    /* pointer to trip that contains this event */
    Trip trip;
    /* pointer to user who created the trip */
    User user;
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

    public Event(String title, String location) {
        this(title, null, null, null, null, location, 0, null, null);
    }

    public Event(String title, Trip trip, User user, Date start, Date end, String location, long phone, String website, String notes) {
        this.title = title;
        this.trip = trip;
        this.user = user;
        this.start = start;
        this.end = end;
        this.location = location;
        this.phone = phone;
        this.website = website;
        this.notes = notes;
    }

    public String getTitle() {
        return title;
    }

    public Date getStart() {
        return start;
    }

    public Date getEnd() {
        return end;
    }

    public String getLocation() {
        return location;
    }
}
