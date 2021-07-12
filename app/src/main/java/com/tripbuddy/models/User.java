package com.tripbuddy.models;

public class User {
    /* user's display name */
    String name;
    /* unique username for login */
    String username;
    /* strong password for login */
    String password;

    public User(String name, String username, String password) {
        this.name = name;
        this.username = username;
        this.password = password;
    }

}
