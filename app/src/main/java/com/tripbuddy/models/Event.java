package com.tripbuddy.models;

import java.util.Date;

public class Event {
    /* title of event */
    String title;
    /* pointer to trip that contains this event */
    Trip trip;
    /* pointer to user who created the trip */
    User user;
    /* short notes about trip */
    String notes;
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
    String event;

}
