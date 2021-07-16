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

import java.text.SimpleDateFormat;
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
    Calendar start;
    String endDate;
    String endTime;
    Calendar end;
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
    class timeTouchListener implements View.OnTouchListener {
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
                TimePickerDialog picker = new TimePickerDialog(activity, 2,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                SimpleDateFormat sdFormat = new SimpleDateFormat("h:mm a");
                                Calendar cal = Calendar.getInstance();
                                cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                cal.set(Calendar.MINUTE, minute);
                                if (etTime == binding.etStartTime) {
                                    start = cal;
                                    etTime.setText(sdFormat.format(start.getTime()));
                                } else {
                                    end = cal;
                                    etTime.setText(sdFormat.format(end.getTime()));
                                }
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
        event.setStart(start);
        event.setEnd(end);
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
        convertToCalendar(start, startDate, startTime);
        convertToCalendar(end, endDate, endTime);
        return start.getTimeInMillis() < end.getTimeInMillis();
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

//    /**
//     * @param time string in the form "hh:mm"
//     * @return list of ints in the form [hh, mm]
//     */
//    static List<Integer> convertToTimeList(String time) {
//        String[] splitTime = time.split(":");
//        List<Integer> converted = new ArrayList<>();
//        int hour = Integer.parseInt(splitTime[0]);
//        int minute = Integer.parseInt(splitTime[1].substring(0, 2));
//        if (hour != 12 && splitTime[1].endsWith("PM")) {
//            hour += 12;
//        } else if (hour == 12 && splitTime[1].endsWith("AM")) {
//            hour -= 12;
//        }
//        converted.add(hour);
//        converted.add(minute);
//        return converted;
//    }

    static void convertToCalendar(Calendar cal, String date, String time) {
        List<Integer> converted = Utils.convertToDateList(date); // [mm, dd, yyyy]
        cal.set(Calendar.YEAR, converted.get(2));
        cal.set(Calendar.MONTH, converted.get(0) - 1);
        cal.set(Calendar.DAY_OF_MONTH, converted.get(1));
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