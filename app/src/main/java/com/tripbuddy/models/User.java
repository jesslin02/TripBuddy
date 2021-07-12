package com.tripbuddy.models;

import org.parceler.Parcel;

@Parcel
public class User {
    /* user's display name */
    String name;
    /* unique username for login */
    String username;
    /* strong password for login */
    String password;

    public User() {
        // empty constructor for parceler
    }

    public User(String name, String username, String password) {
        this.name = name;
        this.username = username;
        this.password = password;
    }

}
