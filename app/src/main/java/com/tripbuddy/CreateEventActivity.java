package com.tripbuddy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.tripbuddy.databinding.ActivityCreateEventBinding;
import com.tripbuddy.listeners.DateTouchListener;
import com.tripbuddy.listeners.TimeTouchListener;
import com.tripbuddy.models.Event;
import com.tripbuddy.models.Trip;

import org.parceler.Parcels;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;

public class CreateEventActivity extends AppCompatActivity {
    public static final String TAG = "CreateEventActivity";
    String API_KEY = BuildConfig.API_KEY;
    ActivityCreateEventBinding binding;
    AutocompleteSupportFragment autocompleteFragment;
    Intent intent;
    Trip trip;
    /* for use if user is editing an existing event */
    Event event;
    String eventLocation;
    Calendar startCal;
    Calendar endCal;
    /* for use to update notifications */
    Calendar prevStart;
    int SALMON;
    int GREEN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_create_event);

        SALMON = getResources().getColor(R.color.salmon);
        GREEN = getResources().getColor(R.color.green);

        intent = getIntent();
        trip = Parcels.unwrap(intent.getParcelableExtra(Trip.class.getSimpleName()));
        Log.d(TAG, String.format("Creating event for '%s'", trip.getTitle()));

        binding = ActivityCreateEventBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        Places.initialize(this, API_KEY);
        PlacesClient placesClient = Places.createClient(this);

        startCal = Calendar.getInstance();
        endCal = Calendar.getInstance();
        event = new Event();

        // Initialize the AutocompleteSupportFragment.
        autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.fragLocation);

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                // TODO: Get info about the selected place.
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
                eventLocation = place.getName();
            }


            @Override
            public void onError(@NonNull Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });

        if (intent.getBooleanExtra("edit", true)) {
            event = Parcels.unwrap(intent.getParcelableExtra(Event.class.getSimpleName()));
            prevStart = Utils.toCalendar(event.getStartDate());
            startCal = Utils.toCalendar(event.getStartDate());
            endCal = Utils.toCalendar(event.getEndDate());
            populateItems();
        }

        binding.etStartDate.setInputType(InputType.TYPE_NULL);
        binding.etStartDate.setOnTouchListener(new DateTouchListener(binding.etStartDate,
                this, startCal, getSupportFragmentManager()));

        binding.etStartTime.setInputType(InputType.TYPE_NULL);
        binding.etStartTime.setOnTouchListener(new TimeTouchListener(binding.etStartTime,
                this, startCal, getSupportFragmentManager()));

        binding.etEndDate.setInputType(InputType.TYPE_NULL);
        binding.etEndDate.setOnTouchListener(new DateTouchListener(binding.etEndDate,
                this, endCal, getSupportFragmentManager()));

        binding.etEndTime.setInputType(InputType.TYPE_NULL);
        binding.etEndTime.setOnTouchListener(new TimeTouchListener(binding.etEndTime,
                this, endCal, getSupportFragmentManager()));

        binding.btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkRequiredInput()) {
                    if (checkDates()) {
                        ParseUser currentUser = ParseUser.getCurrentUser();
                        saveTrip(currentUser);
                    }
                }
            }
        });
    }

    /**
     * populate fields with existing information if user is editing an event
     */
    private void populateItems() {
        binding.etTitle.setText(event.getTitle());
        autocompleteFragment.setText(event.getLocation());
        eventLocation = event.getLocation();
        long phone = event.getPhone();
        if (phone != 0) {
            binding.etPhone.setText(String.valueOf(phone));
        }
        binding.etWebsite.setText(event.getWebsite());
        binding.etNotes.setText(event.getNotes());
        SimpleDateFormat formatDate = new SimpleDateFormat("M/d/yyyy");
        SimpleDateFormat formatTime = new SimpleDateFormat("h:mm a");
        binding.etStartDate.setText(formatDate.format(startCal.getTime()));
        binding.etStartTime.setText(formatTime.format(startCal.getTime()));
        binding.etEndDate.setText(formatDate.format(endCal.getTime()));
        binding.etEndTime.setText(formatTime.format(endCal.getTime()));
        binding.tvAdd.setText("Edit this event");
        binding.btnCreate.setText("Update");
    }

    private void saveTrip(ParseUser user) {
        event.setUser(user);
        event.setTrip(trip);
        event.setTitle(binding.etTitle.getText().toString());
        event.setLocation(eventLocation);
        event.setStart(startCal);
        event.setEnd(endCal);
        String phoneString = binding.etPhone.getText().toString();
        if (!phoneString.isEmpty()) {
            if (phoneString.matches("^[0-9]*$") && phoneString.length() == 10) {
                long phone = Long.parseLong(phoneString);
                event.setPhone(phone);
            } else {
                binding.phoneLayout.setErrorTextColor(ColorStateList.valueOf(SALMON));
                binding.phoneLayout.setError("Phone number is invalid");
                binding.phoneLayout.setErrorEnabled(true);
                return;
            }
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
                // Utils.createNotif(CreateEventActivity.this, event);
                resetInput();
                updateNotif();
                // Utils.goItineraryActivity(CreateEventActivity.this, trip);
                Intent i = new Intent(CreateEventActivity.this, EventDetailActivity.class);
                i.putExtra(Event.class.getSimpleName(), Parcels.wrap(event));
                startActivity(i);
                finish();
            }
        });
    }

    private void updateNotif() {
        SharedPreferences sharedPref = getSharedPreferences("Notifs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        String id = event.getId();
        if (sharedPref.getBoolean(id, false)) { // alarm exists for this event
            // delete old alarm
            Utils.deleteNotif(this, event, prevStart);
        }
        // create new alarm
        Utils.createNotif(this, event);
        editor.putBoolean(id, true);
        editor.commit();
    }

    private boolean checkDates() {
        boolean valid = startCal.getTimeInMillis() <= endCal.getTimeInMillis();
        Utils.setDateError(valid, SALMON, binding.startLayout, binding.startTimeLayout,
                binding.endLayout, binding.endTimeLayout);
        return valid;
    }

    private boolean checkRequiredInput() {
        boolean locationFilled = true;
        if (eventLocation == null) {
            binding.tvLocation.setTextColor(SALMON);
            locationFilled = false;
        } else {
            binding.tvLocation.setTextColor(GREEN);
        }
        return Utils.checkRequiredInput(SALMON, binding.titleLayout, binding.startLayout,
                binding.startTimeLayout, binding.endLayout, binding.endTimeLayout) && locationFilled;
    }

    private void resetInput() {
        autocompleteFragment.onResume();
        binding.tvLocation.setTextColor(GREEN);
        Utils.resetInput(binding.titleLayout, binding.startLayout, binding.startTimeLayout,
                binding.endLayout, binding.endTimeLayout, binding.phoneLayout, binding.websiteLayout,
                binding.notesLayout);
    }
}