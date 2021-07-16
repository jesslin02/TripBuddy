package com.tripbuddy;

import android.app.Activity;
import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

public class Utils {

    /**
     * converts editText date input string into an int array
     * for use in the Trip setStart and setEnd methods
     * @param date in the form of "mm/dd/yyyy"
     * @return List<Integer> in the form of [mm, dd, yyyy]
     */
    public static List<Integer> convertToDateList(String date) {
        List<Integer> converted = new ArrayList<>();
        String[] separated = date.split("/");
        for (int i = 0; i < separated.length; i++) {
            converted.add(i, Integer.valueOf(separated[i]));
        }
        return converted;
    }

    public static void goMainActivity(Activity current) {
        Intent i = new Intent(current, MainActivity.class);
        current.startActivity(i);
        // so that pressing the back button on the MainActivity doesn't go back to the login screen
        current.finish();
    }
}
