package com.tripbuddy;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.parse.Parse;
import com.parse.ParseObject;
import com.tripbuddy.models.Event;
import com.tripbuddy.models.Trip;

public class ParseApplication extends Application {
    /**
     * initializes Parse SDK as soon as the application is created
     */
    @Override
    public void onCreate() {
        super.onCreate();

        // register parse models BEFORE initializing
        ParseObject.registerSubclass(Trip.class);
        ParseObject.registerSubclass(Event.class);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("snXIjMUOp4bhWmLsiEWX0IYtCuxOLARY4xumpCJA")
                .clientKey("gYkCYZ47Pdgln4fdHeOwBaZBZEyxvyn5urVENvZ4")
                .server("https://parseapi.back4app.com")
                .build()
        );

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
    }
}
