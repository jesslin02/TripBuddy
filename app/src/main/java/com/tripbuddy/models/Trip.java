package com.tripbuddy.models;

import java.util.Date;

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

    // simplified constructor for testing purposes
    public Trip(String title, String destination) {
        this(title, null, destination, null, null, null);
    }

    public Trip(String title, User user, String destination, Date start, Date end, String notes) {
        this.title = title;
        this.user = user;
        this.destination = destination;
        this.start = start;
        this.end = end;
        this.notes = notes;
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

    public Date getStart() {
        return start;
    }

    public Date getEnd() {
        return end;
    }

    public String getNotes() {
        return notes;
    }
}
