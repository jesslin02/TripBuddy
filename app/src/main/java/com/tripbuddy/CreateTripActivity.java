package com.tripbuddy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.tripbuddy.databinding.ActivityCreateTripBinding;
import com.tripbuddy.databinding.ActivityLoginBinding;
import com.tripbuddy.models.Trip;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CreateTripActivity extends AppCompatActivity {
    public static final String TAG = "CreateTripActivity";
    ActivityCreateTripBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_create_trip);

        binding = ActivityCreateTripBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.etStart.setInputType(InputType.TYPE_NULL);
        binding.etStart.setOnTouchListener(new dateTouchListener(binding.etStart, this));

        binding.etEnd.setInputType(InputType.TYPE_NULL);
        binding.etEnd.setOnTouchListener(new dateTouchListener(binding.etEnd, this));

        binding.btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = binding.etTitle.getText().toString();
                String dest = binding.etDestination.getText().toString();
                String start = binding.etStart.getText().toString();
                String end = binding.etEnd.getText().toString();
                String notes = binding.etNotes.getText().toString();
                if (checkRequiredInput(title, dest, start, end)) {
                    if (checkDates(start,end)) {
                        ParseUser currentUser = ParseUser.getCurrentUser();
                        saveTrip(currentUser, title, dest, start, end, notes);
                    } else {
                        Toast.makeText(CreateTripActivity.this,
                                "Start date must be before end date", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CreateTripActivity.this,
                            "Please fill out all required fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * used as onTouchListener for inputting the start date and end date
     * allows popup calendar date picker
     * https://www.tutlane.com/tutorial/android/android-datepicker-with-examples
     */
    static class dateTouchListener implements View.OnTouchListener {
        EditText etDate;
        Activity activity;

        public dateTouchListener(EditText etDate, Activity activity) {
            super();
            this.etDate = etDate;
            this.activity = activity;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                hideKeyboard(activity);
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                DatePickerDialog picker = new DatePickerDialog(activity,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                etDate.setText((monthOfYear + 1) + "/" + dayOfMonth + "/" + year);
                            }
                        }, year, month, day);
                picker.show();
                return true;
            }
            return false;
        }
    }

    /**
     * checks if all required fields are filled out
     * if a required field isn't filled out, changes background color to red
     */
    private boolean checkRequiredInput(String title, String dest, String start, String end) {
        boolean validInput = true;
        if (title.isEmpty()) {
            binding.etTitle.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
            validInput = false;
        }
        if (dest.isEmpty()) {
            binding.etDestination.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
            validInput = false;
        }
        if (start.isEmpty()) {
            binding.etStart.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
            validInput = false;
        }
        if (end.isEmpty()) {
            binding.etEnd.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
            validInput = false;
        }
        return validInput;
    }

    /**
     * checks if start date and end date of trip are valid
     * (start date must be before end date)
     * @param start string in format mm/dd/yyyy
     * @param end string in format mm/dd/yyyy
     */
    static boolean checkDates(String start, String end) {
        List<Integer> startList = convertToDateList(start);
        List<Integer> endList = convertToDateList(end);
        // making sure to go from largest to smallest unit of time
        int[] yearMonthDay = new int[]{2, 0, 1};
        for (int i : yearMonthDay) {
            if (startList.get(i) > endList.get(i)) {
                return false;
            } else if (startList.get(i) < endList.get(i)) {
                return true;
            }
        }
        return true;
    }

    /**
     * creates and saves trip to Parse
     */
    private void saveTrip(ParseUser user, String title, String dest, String start, String end, String notes) {
        Trip trip = new Trip();
        trip.setUser(user);
        trip.setTitle(title);
        trip.setDestination(dest);
        if (!notes.isEmpty()) {
            trip.setNotes(notes);
        }
        trip.setStart(convertToDateList(start));
        trip.setEnd(convertToDateList(end));
        trip.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error while creating trip", e);
                    Toast.makeText(CreateTripActivity.this, "Error while creating trip!", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.i(TAG, "Trip creation was successful!");
                resetInput();
                goMainActivity();
            }
        });
    }

    /**
     * converts editText date input string into an int array
     * for use in the Trip setStart and setEnd methods
     * @param date in the form of "mm/dd/yyyy"
     * @return List<Integer> in the form of [mm, dd, yyyy]
     */
    static List<Integer> convertToDateList(String date) {
        List<Integer> converted = new ArrayList<>();
        String[] separated = date.split("/");
        for (int i = 0; i < separated.length; i++) {
            converted.add(i, Integer.valueOf(separated[i]));
        }
        return converted;
    }

    /**
     * changes all input fields to blank and restores original colors
     * after trip has been created
     */
    private void resetInput() {
        binding.etTitle.setText("");
        binding.etTitle.getBackground().clearColorFilter();
        binding.etDestination.setText("");
        binding.etDestination.getBackground().clearColorFilter();
        binding.etStart.setText("");
        binding.etStart.getBackground().clearColorFilter();
        binding.etEnd.setText("");
        binding.etEnd.getBackground().clearColorFilter();
        binding.etNotes.setText("");
    }

    private void goMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        // so that pressing the back button on the MainActivity doesn't go back to the login screen
        finish();
    }

    /**
     * for use when inputting start and end dates
     * https://stackoverflow.com/questions/1109022/how-do-you-close-hide-the-android-soft-keyboard-programmatically
     */
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}