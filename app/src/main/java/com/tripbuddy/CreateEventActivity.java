package com.tripbuddy;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.tripbuddy.databinding.ActivityCreateEventBinding;
import com.tripbuddy.models.Event;
import com.tripbuddy.models.Trip;

import org.parceler.Parcels;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CreateEventActivity extends AppCompatActivity {
    public static final String TAG = "CreateEventActivity";
    ActivityCreateEventBinding binding;
    Intent intent;
    Trip trip;
    /* for use if user is editing an existing event */
    Event event;
    Calendar startCal;
    Calendar endCal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_create_event);

        intent = getIntent();
        trip = Parcels.unwrap(intent.getParcelableExtra(Trip.class.getSimpleName()));
        Log.d(TAG, String.format("Creating event for '%s'", trip.getTitle()));

        binding = ActivityCreateEventBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        startCal = Calendar.getInstance();
        endCal = Calendar.getInstance();
        event = new Event();

        if (intent.getBooleanExtra("edit", true)) {
            event = Parcels.unwrap(intent.getParcelableExtra(Event.class.getSimpleName()));
            startCal = Utils.toCalendar(event.getStartDate());
            endCal = Utils.toCalendar(event.getEndDate());
            populateItems();
        }

        binding.etStartDate.setInputType(InputType.TYPE_NULL);
        binding.etStartDate.setOnTouchListener(new Utils.dateTouchListener(binding.etStartDate, this, startCal));

        binding.etStartTime.setInputType(InputType.TYPE_NULL);
        binding.etStartTime.setOnTouchListener(new timeTouchListener(binding.etStartTime, this, startCal));

        binding.etEndDate.setInputType(InputType.TYPE_NULL);
        binding.etEndDate.setOnTouchListener(new Utils.dateTouchListener(binding.etEndDate, this, endCal));

        binding.etEndTime.setInputType(InputType.TYPE_NULL);
        binding.etEndTime.setOnTouchListener(new timeTouchListener(binding.etEndTime, this, endCal));

        binding.btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
     * populate fields with existing information if user is editing an event
     */
    private void populateItems() {
        binding.etTitle.setText(event.getTitle());
        binding.etLocation.setText(event.getLocation());
        binding.etPhone.setText(String.valueOf(event.getPhone()));
        binding.etWebsite.setText(event.getWebsite());
        binding.etNotes.setText(event.getNotes());
        SimpleDateFormat formatDate = new SimpleDateFormat("M/d/yyyy");
        SimpleDateFormat formatTime = new SimpleDateFormat("h:mm a");
        binding.etStartDate.setText(formatDate.format(startCal.getTime()));
        binding.etStartTime.setText(formatTime.format(startCal.getTime()));
        binding.etEndDate.setText(formatDate.format(endCal.getTime()));
        binding.etEndTime.setText(formatTime.format(endCal.getTime()));
        binding.btnCreate.setText("Update");
    }

    /**
     * used as onTouchListener for inputting the start time and end time
     * allows popup time picker
     */
    class timeTouchListener implements View.OnTouchListener {
        EditText etTime;
        Activity activity;
        Calendar cal;

        public timeTouchListener(EditText etTime, Activity activity, Calendar cal) {
            super();
            this.etTime = etTime;
            this.activity = activity;
            this.cal = cal;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                Utils.hideKeyboard(activity);
                final Calendar cldr = Calendar.getInstance();
                int hour = cldr.get(Calendar.HOUR_OF_DAY);
                int min = cldr.get(Calendar.MINUTE);
                TimePickerDialog picker = new TimePickerDialog(activity, 2,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                SimpleDateFormat sdFormat = new SimpleDateFormat("h:mm a");
                                cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                cal.set(Calendar.MINUTE, minute);
                                etTime.setText(sdFormat.format(cal.getTime()));
                            }
                        }, hour, min, false);
                picker.show();
                return true;
            }
            return false;
        }
    }

    private void saveTrip(ParseUser user) {
        event.setUser(user);
        event.setTrip(trip);
        event.setTitle(binding.etTitle.getText().toString());
        event.setLocation(binding.etLocation.getText().toString());
        event.setStart(startCal);
        event.setEnd(endCal);
        String phoneString = binding.etPhone.getText().toString();
        if (!phoneString.isEmpty()) {
            long phone = Long.parseLong(phoneString);
            event.setPhone(phone);
        }
        String website = binding.etWebsite.getText().toString();
        if (!website.isEmpty()) {
            event.setWebsite(website);
        }
        String notes = binding.etNotes.getText().toString();
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
                Utils.goItineraryActivity(CreateEventActivity.this, trip);
                finish();
            }
        });
    }

    private boolean checkDates() {
        return startCal.getTimeInMillis() < endCal.getTimeInMillis();
    }

    private boolean checkRequiredInput() {
        return Utils.checkRequiredInput(binding.etTitle, binding.etLocation, binding.etStartDate,
                binding.etStartTime, binding.etEndDate, binding.etEndTime);
    }

    private void resetInput() {
        Utils.resetInput(binding.etTitle, binding.etLocation, binding.etStartDate,
                binding.etStartTime, binding.etEndDate, binding.etEndTime, binding.etPhone,
                binding.etWebsite, binding.etNotes);
    }
}