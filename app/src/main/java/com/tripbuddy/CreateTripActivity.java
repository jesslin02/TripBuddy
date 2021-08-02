package com.tripbuddy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
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
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.tripbuddy.databinding.ActivityCreateTripBinding;
import com.tripbuddy.databinding.ActivityLoginBinding;
import com.tripbuddy.listeners.DateRangeTouchListener;
import com.tripbuddy.models.Event;
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
    int SALMON;
    int GREEN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_create_trip);

        SALMON = getResources().getColor(R.color.salmon);
        GREEN = getResources().getColor(R.color.green);

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

        binding.etDates.setOnTouchListener(new DateRangeTouchListener(binding.etDates,
                this, startCal, endCal, getSupportFragmentManager()));

        binding.btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkRequiredInput()) {
                    ParseUser currentUser = ParseUser.getCurrentUser();
                    saveTrip(currentUser);
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
        SimpleDateFormat sdFormat = new SimpleDateFormat("MMM d, yyyy");
        binding.etDates.setText(sdFormat.format(startCal.getTime()) + " - "
                                    + sdFormat.format(endCal.getTime()));
        binding.tvAdd.setText("Edit this trip");
        binding.btnCreate.setText("Update");
    }

    /**
     * checks if all required fields are filled out
     * if a required field isn't filled out, changes background color to red
     */
    private boolean checkRequiredInput() {
        boolean locationFilled = true;
        if (tripDestination == null) {
            binding.tvLocation.setTextColor(SALMON);
            locationFilled = false;
        } else {
            binding.tvLocation.setTextColor(GREEN);
        }
        return Utils.checkRequiredInput(SALMON, binding.titleLayout, binding.dateLayout) && locationFilled;
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
                Intent i = new Intent(CreateTripActivity.this, TripDetailActivity.class);
                i.putExtra(Trip.class.getSimpleName(), Parcels.wrap(trip));
                startActivity(i);
                finish();
            }
        });
    }

    /**
     * changes all input fields to blank and restores original colors
     * after trip has been created
     */
    private void resetInput() {
        autocompleteFragment.onResume();
        binding.tvLocation.setTextColor(GREEN);
        Utils.resetInput(binding.titleLayout, binding.notesLayout);
    }
}