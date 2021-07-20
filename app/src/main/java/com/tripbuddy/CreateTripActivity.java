package com.tripbuddy;

import androidx.annotation.NonNull;
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

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.tripbuddy.databinding.ActivityCreateTripBinding;
import com.tripbuddy.databinding.ActivityLoginBinding;
import com.tripbuddy.models.Trip;

import org.parceler.Parcels;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class CreateTripActivity extends AppCompatActivity {
    public static final String TAG = "CreateTripActivity";
    public ActivityCreateTripBinding binding;
    String API_KEY = BuildConfig.API_KEY;
    AutocompleteSupportFragment autocompleteFragment;
    /* if user is editing trip or not */
    boolean edit;
    /* existing trip if editing */
    Trip trip;
    Intent intent;
    String tripDestination;
    Calendar startCal;
    Calendar endCal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_create_trip);

        binding = ActivityCreateTripBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        Places.initialize(this, API_KEY);
        PlacesClient placesClient = Places.createClient(this);

        startCal = Calendar.getInstance();
        endCal = Calendar.getInstance();
        trip = new Trip();

        // Initialize the AutocompleteSupportFragment.
        autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.fragLocation);

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));
        autocompleteFragment.setTypeFilter(TypeFilter.CITIES);

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                // TODO: Get info about the selected place.
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
                tripDestination = place.getName();
            }


            @Override
            public void onError(@NonNull Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });

        intent = getIntent();
        if (intent.getBooleanExtra("edit", true)) {
            trip = Parcels.unwrap(intent.getParcelableExtra(Trip.class.getSimpleName()));
            startCal = Utils.toCalendar(trip.getStartDate());
            endCal = Utils.toCalendar(trip.getEndDate());
            populateItems();
        }

        binding.etStart.setInputType(InputType.TYPE_NULL);
        binding.etStart.setOnTouchListener(new Utils.dateTouchListener(binding.etStart, this, startCal));

        binding.etEnd.setInputType(InputType.TYPE_NULL);
        binding.etEnd.setOnTouchListener(new Utils.dateTouchListener(binding.etEnd, this, endCal));

        binding.btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkRequiredInput()) {
                    if (checkDates()) {
                        ParseUser currentUser = ParseUser.getCurrentUser();
                        saveTrip(currentUser);
                    } else {
                        Toast.makeText(CreateTripActivity.this,
                                "Start date cannot be after end date", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CreateTripActivity.this,
                            "Please fill out all required fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * populate fields with existing information if user is editing a trip
     */
    private void populateItems() {
        binding.etTitle.setText(trip.getTitle());
        autocompleteFragment.setText(trip.getDestination());
        tripDestination = trip.getDestination();
        binding.etNotes.setText(trip.getNotes());
        SimpleDateFormat sdFormat = new SimpleDateFormat("M/d/yyyy");
        binding.etStart.setText(sdFormat.format(startCal.getTime()));
        binding.etEnd.setText(sdFormat.format(endCal.getTime()));
        binding.btnCreate.setText("Update");
    }

    /**
     * checks if all required fields are filled out
     * if a required field isn't filled out, changes background color to red
     */
    private boolean checkRequiredInput() {
        boolean locationFilled = true;
        if (tripDestination == null) {
            binding.tvLocation.setTextColor(Color.RED);
            locationFilled = false;
        }
        return Utils.checkRequiredInput(binding.etTitle, binding.etStart, binding.etEnd) && locationFilled;
    }

    /**
     * checks if start date and end date of trip are valid
     * (start date must be before end date)
     */
    private boolean checkDates() {
        return startCal.getTimeInMillis() < endCal.getTimeInMillis();
    }

    /**
     * creates and saves trip to Parse
     */
    private void saveTrip(ParseUser user) {
        trip.setUser(user);
        trip.setTitle(binding.etTitle.getText().toString());
        trip.setDestination(tripDestination);
        String notes = binding.etNotes.getText().toString();
        if (!notes.isEmpty()) {
            trip.setNotes(notes);
        }
        trip.setStart(startCal);
        trip.setEnd(endCal);
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
                Utils.goMainActivity(CreateTripActivity.this);
            }
        });
    }

    /**
     * changes all input fields to blank and restores original colors
     * after trip has been created
     */
    private void resetInput() {
        autocompleteFragment.onResume();
        Utils.resetInput(binding.etTitle, binding.etStart, binding.etEnd, binding.etNotes);
    }
}