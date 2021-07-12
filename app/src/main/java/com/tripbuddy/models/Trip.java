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
}
