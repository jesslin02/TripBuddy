package com.tripbuddy;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.tripbuddy.databinding.ActivityCreateEventBinding;
import com.tripbuddy.CreateTripActivity.dateTouchListener;
import com.tripbuddy.models.Event;
import com.tripbuddy.models.Trip;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CreateEventActivity extends AppCompatActivity {
    public static final String TAG = "CreateEventActivity";
    ActivityCreateEventBinding binding;
    Trip trip;
    String title;
    String location;
    String startDate;
    String startTime;
    /* list of values in the start date [hh, mm, yyyy, MM, dd] */
    List<Integer> startList;
    String endDate;
    String endTime;
    /* list of values in the end date [hh, mm, yyyy, MM, dd] */
    List<Integer> endList;
    long phone;
    String website;
    String notes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_create_event);

        trip = Parcels.unwrap(getIntent().getParcelableExtra(Trip.class.getSimpleName()));
        Log.d(TAG, String.format("Creating event for '%s'", trip.getTitle()));

        binding = ActivityCreateEventBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.etStartDate.setInputType(InputType.TYPE_NULL);
        binding.etStartDate.setOnTouchListener(new dateTouchListener(binding.etStartDate, this));

        binding.etStartTime.setInputType(InputType.TYPE_NULL);
        binding.etStartTime.setOnTouchListener(new timeTouchListener(binding.etStartTime, this));

        binding.etEndDate.setInputType(InputType.TYPE_NULL);
        binding.etEndDate.setOnTouchListener(new dateTouchListener(binding.etEndDate, this));

        binding.etEndTime.setInputType(InputType.TYPE_NULL);
        binding.etEndTime.setOnTouchListener(new timeTouchListener(binding.etEndTime, this));

        binding.btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title = binding.etTitle.getText().toString();
                location = binding.etLocation.getText().toString();
                startDate = binding.etStartDate.getText().toString();
                startTime = binding.etStartTime.getText().toString();
                endDate = binding.etEndDate.getText().toString();
                endTime = binding.etEndTime.getText().toString();
                String phoneString = binding.etPhone.getText().toString();
                if (!phoneString.isEmpty()) {
                    phone = Long.valueOf(phoneString);
                }
                website = binding.etWebsite.getText().toString();
                notes = binding.etNotes.getText().toString();
                if (checkRequiredInput()) {
                    if (checkDates()) {
                        ParseUser currentUser = ParseUser.getCurrentUser();
                        saveTrip(currentUser);
                    } else {
                        Toast.makeText(CreateEventActivity.this,
                                "Start date and time must be before end date and time", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CreateEventActivity.this,
                            "Please fill out all required fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * used as onTouchListener for inputting the start time and end time
     * allows popup time picker
     */
    static class timeTouchListener implements View.OnTouchListener {
        EditText etTime;
        Activity activity;

        public timeTouchListener(EditText etTime, Activity activity) {
            super();
            this.etTime = etTime;
            this.activity = activity;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                CreateTripActivity.hideKeyboard(activity);
                final Calendar cldr = Calendar.getInstance();
                int hour = cldr.get(Calendar.HOUR_OF_DAY);
                int min = cldr.get(Calendar.MINUTE);
                TimePickerDialog picker = new TimePickerDialog(activity,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                String timeOfDay = "AM";
                                String extraZero = "";
                                if (hourOfDay > 12) {
                                    hourOfDay = hourOfDay % 12;
                                    timeOfDay = "PM";
                                }
                                if (minute < 10) {
                                    extraZero = "0";
                                }
                                etTime.setText(hourOfDay + ":" + extraZero + minute + " " + timeOfDay);
                            }
                        }, hour, min, false);
                picker.show();
                return true;
            }
            return false;
        }
    }

    private void saveTrip(ParseUser user) {
        Event event = new Event();
        event.setUser(user);
        event.setTrip(trip);
        event.setTitle(title);
        event.setLocation(location);
        event.setStart(startList);
        event.setEnd(endList);
        if (phone != 0) {
            event.setPhone(phone);
        }
        if (!website.isEmpty()) {
            event.setWebsite(website);
        }
        if (!notes.isEmpty()) {
            event.setNotes(notes);
        }
        event.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error while creating event", e);
                    Toast.makeText(CreateEventActivity.this, "Error while creating event!", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.i(TAG, "Event creation was successful!");
                resetInput();
                goItineraryActivity();
            }
        });
    }

    /**
     * uses checkDates() from CreateTripActivity to check dates then checks times
     */
    private boolean checkDates() {
        startList = convertToDateTimeList(startDate, startTime);
        endList = convertToDateTimeList(endDate, endTime);
        // making sure to go from largest to smallest unit of time
        // year, month, day, hour, min
        int[] yearMonthDay = new int[]{2, 0, 1, 3, 4};
        for (int i : yearMonthDay) {
            if (startList.get(i) > endList.get(i)) {
                return false;
            } else if (startList.get(i) < endList.get(i)) {
                return true;
            }
        }
       return true;
    }

    private boolean checkRequiredInput() {
        boolean validInput = true;
        if (title.isEmpty()) {
            binding.etTitle.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
            validInput = false;
        }
        if (location.isEmpty()) {
            binding.etLocation.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
            validInput = false;
        }
        if (startDate.isEmpty()) {
            binding.etStartDate.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
            validInput = false;
        }
        if (startTime.isEmpty()) {
            binding.etStartTime.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
            validInput = false;
        }
        if (endDate.isEmpty()) {
            binding.etEndDate.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
            validInput = false;
        }
        if (endTime.isEmpty()) {
            binding.etEndTime.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
            validInput = false;
        }
        return validInput;
    }

    /**
     * @param time string in the form "hh:mm"
     * @return list of ints in the form [hh, mm]
     */
    static List<Integer> convertToTimeList(String time) {
        String[] splitTime = time.split(":");
        List<Integer> converted = new ArrayList<>();
        int hour = Integer.valueOf(splitTime[0]);
        if (splitTime[1].endsWith("PM")) {
            hour += 12;
        }
        converted.add(hour);
        converted.add(Integer.valueOf(splitTime[1].substring(0, 2)));
        return converted;
    }

    static List<Integer> convertToDateTimeList(String date, String time) {
        List<Integer> converted = CreateTripActivity.convertToDateList(date);
        converted.addAll(convertToTimeList(time));
        return converted;
    }

    private void resetInput() {
        binding.etTitle.setText("");
        binding.etTitle.getBackground().clearColorFilter();
        binding.etLocation.setText("");
        binding.etLocation.getBackground().clearColorFilter();
        binding.etStartDate.setText("");
        binding.etStartDate.getBackground().clearColorFilter();
        binding.etStartTime.setText("");
        binding.etStartTime.getBackground().clearColorFilter();
        binding.etEndDate.setText("");
        binding.etEndDate.getBackground().clearColorFilter();
        binding.etEndTime.setText("");
        binding.etEndTime.getBackground().clearColorFilter();
        binding.etPhone.setText("");
        binding.etWebsite.setText("");
        binding.etNotes.setText("");
    }

    private void goItineraryActivity() {
        Intent i = new Intent(this, ItineraryActivity.class);
        i.putExtra(Trip.class.getSimpleName(), Parcels.wrap(trip));
        startActivity(i);
        finish();
    }
}